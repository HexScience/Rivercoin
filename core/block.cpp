//
// Created by Ragnarr Ivarssen on 26.07.18.
//

#include "block.h"
#include "context.h"

BlockHeader::BlockHeader(BlockIndex number, uint256 parentHash, uint256 hash, Address miner, unsigned long long time,
                         unsigned long long nonce)
        : __block_number__(number),
          __parent_hash__(parentHash),
          __block_hash__(hash),
          __miner__(miner),
          __block_time__(time),
          __nonce__(nonce)
{
}

BlockHeader::BlockHeader(const BlockHeader &o)
        : __block_number__(o.__block_number__),
          __parent_hash__(o.__parent_hash__),
          __block_hash__(o.__block_hash__),
          __miner__(o.__miner__),
          __block_time__(o.__block_time__),
          __nonce__(o.__nonce__)
{
}

StoredBlock::StoredBlock(BlockHeader h, TransactionTree t) : header(h), tree(t)
{
}

StoredBlock::StoredBlock(const StoredBlock &o)
        : header(o.header), tree(o.tree)
{
}

Block::Block(unsigned long long index, unsigned long long time, uint256 parentHash, Context* c)
        : header(index, parentHash, ByteUtil::fromBytes256(EMPTY_HASH), Address(), time, 0), context(c)
{
//        header.__block_number__ = index;
//        header.__block_hash__   = uint256(0);
//        header.__parent_hash__  = parentHash;
//        header.__block_time__   = time;
//        header.__nonce__        = 0;
//        header.__miner__        = Address();
}

Block::Block(const StoredBlock &block, Context* c) : header(block.header), tree(block.tree), context(c) {}

void Block::setMiner(const Address address) {}
void Block::add(Transaction &transaction)
{
    if (transaction.valid(context))
        tree.add(transaction);
}
bool Block::full()
{
    return tree.length() >= BLOCK_MAX_TRANSACTIONS;
}
bool Block::ready()
{
    return full() || context->lastTransactionWas((long)(BLOCK_INTERVAL * 0.75));
}
BlockIndex Block::getIndex()
{
    return header.__block_number__;
}
uint256 Block::getHash()
{
    return header.__block_hash__;
}
uint256 Block::getParentHash()
{
    return header.__parent_hash__;
}
bool Block::valid()
{
    return false;
}
std::shared_ptr<StoredBlock> Block::toStoredBlock()
{
    return std::shared_ptr<StoredBlock>(new StoredBlock(header, tree));
}
bool Block::checkSolutionValid()
{
    return false;
}
Address Block::getMiner()
{
    return header.__miner__;
}
bool Block::finished()
{
    return false;
}
void Block::removeTransactions(std::vector<Transaction> &vector)
{
    for (auto transaction : tree.get())
    {
        std::vector<Transaction>::iterator i = std::find(vector.begin(), vector.end(), transaction);

        if (i != vector.end())
            vector.erase(i);
    }
}
Block::~Block() {}