//
// Created by Ragnarr Ivarssen on 18.07.18.
//

#ifndef RIVERCOIN_CPP_BLOCKCHAIN_H
#define RIVERCOIN_CPP_BLOCKCHAIN_H

#include <vector>
#include <map>
#include "block.h"
#include "serializeable.h"
#include "fileutils.h"
#include <string>
#include "security/security.h"

#define GENESIS 1
#define NO_CHAIN 0
#define INVALID_CHAIN 2

class BlockChain{
private:
    std::vector<Block*>     orphanedBlocks;
    std::vector<Block*>     downloadedBlocks;
    Context                 context;
    Block*                  current;
public:
    BlockChain(Context& c) : context(c)
    {
    }

    void loadAllBlocks();
    void downloadAllMissingBlocks();
    void queOrphaned(Block* block);
    void download(Block* block);
    Block* continueChain()
    {
        Block* temp = current;

        current = new Block(temp->getIndex() + 1, context.timestamp(), temp->getHash(), context);

        delete(temp);
    }

    void serialize()
    {
        using std::string;
        serializeable<Block> s(BLOCK_MAGIC_HEADER, *current);

        unsigned char ERROR = 0;

        file::write((string(STRUCTURE_DB_NAME) + string(BLOCKCHAIN_DB_NAME) + string("block[") + std::to_string(current->getIndex()) + string("].blk")).c_str(), s, ERROR);

        if(ERROR != 0)
        {
            logger::err(string(string("couldn't export block '") + std::to_string(current->getIndex()) + string("'.")));
        }
    }

    void sendSolution()
    {
    }

    unsigned long long activeBlock()
    {
        return current->getIndex();
    }

    void removeOldOrphandedBlocks()
    {
        orphanedBlocks.erase(std::remove_if(orphanedBlocks.begin(), orphanedBlocks.end(), [](Block* b) { return b->getIndex() < (activeBlock() - 1); }), orphanedBlocks.end());
    }

    void removeInvalidOrphandedBlocks()
    {
        orphanedBlocks.erase(std::remove_if(orphanedBlocks.begin(), orphanedBlocks.end(), [](Block* b) { return !b->checkSolutionValid(); }), orphanedBlocks.end());
    }

    std::vector<Block*> getLongest(std::vector<std::vector<Block*>> subchains)
    {
        std::vector<Block*> longest;
        return longest;
    }

    void updateChain(std::vector<Block*> longChain)
    {
    }

    void checkForValidSolutions()
    {
        if (orphanedBlocks.size() == 0) return;
        typedef std::vector<Block*>     subchain;

        /** formulate temporary chains and choose the longest one **/
        std::vector<Address>    subchainclients;

        for (unsigned long i = 0; i < orphanedBlocks.size(); i ++)
            if (std::find(subchainclients.begin(), subchainclients.end(), orphanedBlocks[i]->getMiner() != subchainclients.end())) {}
            else subchainclients.push_back(orphanedBlocks[i]->getMiner());

        std::vector<subchain>   subchains;

        for (unsigned long i = 0; i < subchainclients.size(); i ++)
        {
            u_int256 miner = subchainclients[i];

            for(unsigned long j = 0; j < orphanedBlocks.size(); j ++)
                if (orphanedBlocks[j]->getMiner() == miner)
                    subchains[i].push_back(orphanedBlocks[j]);
            /** we could insteade add the above code into the erase code but it COULD have some undefined or unexpected behaviour so instead to loops should suffice **/

            orphanedBlocks.erase(std::remove_if(orphanedBlocks.begin(), orphanedBlocks.end(), [](Block* b) { return !b->checkSolutionValid(); }), orphanedBlocks.end());
        }

        updateChain(getLongest(subchains));
    }

    void execute()
    {
        loadAllBlocks();
        downloadAllMissingBlocks();

        while (context.keepAlive())
        {
            /** remove any blocks that are too old to be added to the chain **/
            removeOldOrphandedBlocks();
            /** remove any blocks that have invalid solutions or unacceptable behaviour **/
            removeInvalidOrphandedBlocks();
            /** check the remaining blocks for any valid solutions and add them to the chain **/
            checkForValidSolutions();
        }
    }
};

#endif //RIVERCOIN_CPP_BLOCKCHAIN_H
