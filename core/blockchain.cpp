//
// Created by Ragnarr Ivarssen on 19.07.18.
//

#include "blockchain.h"
#include <vector>
#include "math/math.h"
#include "miner.h"
#include "block.h"
#include "context.h"
#include <memory>

BlockInfo::BlockInfo() {}
BlockInfo::BlockInfo(const BlockInfo &o) : data(o.data) {}

bool BlockInfo::read()
{
    unsigned char ERROR = 0;
    data = file::read(std::string(".") + PATH_SEPARATOR + "structure" + PATH_SEPARATOR + "config.xml", ERROR);

    return ERROR == 0;
}

bool BlockInfo::dump()
{
    unsigned char ERROR = 0;
    file::write<const char*>(std::string(".") + PATH_SEPARATOR + "structure" + PATH_SEPARATOR + "config.xml", data.data(), ERROR);

    return ERROR == 0;
}

void BlockInfo::allocate()
{
    for (int i = 0; i < BLOCKINFO_SIZE; i ++)
        data.push_back(0);
}

void BlockInfo::setLatestBlock(BlockIndex i)
{
    if (data.size() == 0) return;
    ((BlockIndex *)data.data())[0] = i;
}

BlockIndex BlockInfo::getLatestBlock()
{
    if (data.size() == 0) return 0;
    return ((BlockIndex *)data.data())[0];
}

BlockChain::BlockChain(Context *c) : context(c)
{
}
void BlockChain::loadAllBlocks()
{
    BlockInfo info;
    if (info.read())
    {
        BlockIndex i = info.getLatestBlock();
        unsigned char ERROR = 0;
        std::vector<char> data = file::read(std::string(".") + PATH_SEPARATOR + "structure" + PATH_SEPARATOR + "config.xml", ERROR);

        if (ERROR != 0)
        {
            serializeable<StoredBlock> serialized = ((serializeable<StoredBlock> *) data.data())[0];

            current = new Block(serialized.serializeable_data, *context);
        }
    }
}
void BlockChain::downloadAllMissingBlocks() {}
void BlockChain::queOrphaned(Block *block)
{
    orphanedBlocks.push_back(block);
}
void BlockChain::queTransaction(Transaction transaction)
{
    transactionStream.push_back(transaction);
}
void BlockChain::download(Block *block)
{
    downloadedBlocks.push_back(block);
}

void BlockChain::continueChain()
{
    Block* temp = current;

    temp->removeTransactions(transactionStream);

    current = new Block(temp->getIndex() + 1, context->timestamp(), temp->getHash(), *context);

    delete(temp);
}

void BlockChain::serialize()
{
    using std::string;
    StoredBlock block = *(current->toStoredBlock().get());
    serializeable<StoredBlock> s(BLOCK_MAGIC_HEADER, block);

    unsigned char ERROR = 0;

    file::write<serializeable<StoredBlock>>((string(STRUCTURE_DB_NAME) + string(BLOCKCHAIN_DB_NAME) + string("block[") + std::to_string(current->getIndex()) + string("].blk")).c_str(), s, ERROR);

    if(ERROR != 0)
    {
        logger::err(string(string("couldn't export block '") + std::to_string(current->getIndex()) + string("'.")));
    } else {
        BlockInfo info;
        if (info.read())
        {
            info.setLatestBlock(current->getIndex());

            if (!info.dump())
                logger::err(string(string("couldn't export block info '") + std::to_string(current->getIndex()) + string("'.")));
        } else {
            info.allocate();
        }
    }
}

void BlockChain::sendSolution()
{
    std::shared_ptr<StoredBlock> block = current->toStoredBlock();

    Message msg(BLOCK_MAGIC_HEADER, (char *) block.get());

    context->sendMessageToNetwork(msg);
}

BlockIndex BlockChain::activeBlock()
{
    if (current) return current->getIndex();
    else return 0;
}

void BlockChain::removeOldOrphandedBlocks()
{
    orphanedBlocks.erase(std::remove_if(orphanedBlocks.begin(), orphanedBlocks.end(), [this](Block* b) { return b->getIndex() < this->activeBlock();/**subtractUnsigned(this->activeBlock(), 1);**/}), orphanedBlocks.end());
}

void BlockChain::removeInvalidOrphandedBlocks()
{
    orphanedBlocks.erase(std::remove_if(orphanedBlocks.begin(), orphanedBlocks.end(), [](Block* b) { return !b->checkSolutionValid(); }), orphanedBlocks.end());
}

std::vector<Block*> BlockChain::getLongest(std::vector<std::vector<Block* >> subchains)
{
    std::sort(subchains.begin(), subchains.end(), [](const std::vector<Block* > & a, const std::vector<Block* > & b){ return a.size() > b.size(); });
    return subchains[0];
}

void BlockChain::updateChain(std::vector<Block *> longChain)
{
    if (context->getMiner()->busy())
        context->getMiner()->interrupt();

    /** might want to check if our block is done mining but that's sort of useless? **/
//    else {
//        if (longChain.size() == 1)
//
//    }

    for (BlockIndex i = 0; i < longChain.size(); i ++)
    {
        current = longChain[i];

        serialize();
    }

    continueChain();
}

void BlockChain::checkForValidSolutions()
{
    if (orphanedBlocks.size() == 0) return;
    typedef std::vector<Block*>     subchain;

    /** formulate temporary chains and choose the longest one **/
    std::vector<Address>    subchainclients;

    for (unsigned long i = 0; i < orphanedBlocks.size(); i ++)
        if (std::find(subchainclients.begin(), subchainclients.end(), orphanedBlocks[i]->getMiner()) != subchainclients.end()) {}
        else subchainclients.push_back(orphanedBlocks[i]->getMiner());

    std::vector<subchain>   subchains;

    for (unsigned long i = 0; i < subchainclients.size(); i ++)
    {
        Address miner = subchainclients[i];

        for(unsigned long j = 0; j < orphanedBlocks.size(); j ++)
            if (orphanedBlocks[j]->getMiner() == miner)
                subchains[i].push_back(orphanedBlocks[j]);
        /** we could insteade add the above code into the erase code but it COULD have some undefined or unexpected behaviour so instead two loops should suffice **/

        orphanedBlocks.erase(std::remove_if(orphanedBlocks.begin(), orphanedBlocks.end(), [](Block* b) { return !b->checkSolutionValid(); }), orphanedBlocks.end());
    }

    updateChain(getLongest(subchains));
}

void BlockChain::createGenesisBlock()
{
    char PARENT_HASH[32] = GENESIS_HASH;
    current = new Block(GENESIS, context->timestamp(), ByteUtil::fromBytes256(PARENT_HASH), *context);
}

void BlockChain::execute()
{
    loadAllBlocks();
    downloadAllMissingBlocks();

    if (activeBlock() == 0)
        createGenesisBlock();

    while (context->keepAlive())
    {
        /** remove any blocks that are too old to be added to the chain **/
        removeOldOrphandedBlocks();
        /** remove any blocks that have invalid solutions or unacceptable behaviour **/
        removeInvalidOrphandedBlocks();
        /** check the remaining blocks for any valid solutions and add them to the chain **/
        checkForValidSolutions();

        /** check to mine the block **/
        if (current->ready() && !context->getMiner()->busy())
            context->getMiner()->mine(current, uint256(context->getDifficulty()));

        if (current->finished())
        {
            sendSolution();
            logger::alert("finished mining block[" + std::to_string(current->getIndex()) + "].");
            serialize();
            continueChain();
        }
    }
}