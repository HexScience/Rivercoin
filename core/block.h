//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#ifndef RIVERCOIN_CPP_BLOCK_H
#define RIVERCOIN_CPP_BLOCK_H

#include "transaction.h"
#include "math/math.h"
#include "tree.h"
#include "config.h"
#include "context.h"
#include "security/security.h"

struct BlockHeader {
    BlockIndex                      __block_number__;
    uint256                         __parent_hash__;
    uint256                         __block_hash__;
    Address                         __miner__;
    unsigned long long              __block_time__;
    unsigned long long              __nonce__;

    BlockHeader(BlockIndex number, uint256 parentHash, uint256 hash, Address miner, unsigned long long time, unsigned long long nonce)
            : __block_number__(number),
              __parent_hash__(parentHash),
              __block_hash__(hash),

    {
    }

    BlockHeader(const BlockHeader& o)
            : __block_number__(o.__block_number__),
              __parent_hash__(o.__parent_hash__),
              __block_hash__(o.__block_hash__),
              __miner__(o.__miner__),
              __block_time__(o.__block_time__),
              __nonce__(o.__nonce__)
    {
    }
};

struct StoredBlock{
    BlockHeader     header;
    TransactionTree tree;

    StoredBlock(BlockHeader h, TransactionTree t) : header(h), tree(t)
    {
    }

    StoredBlock(const StoredBlock& o)
            : header(o.header), tree(o.tree)
    {
    }
};

class Block{
private:
    BlockHeader              header;
    TransactionTree          tree;
    Context                  context;
public:
    Block(unsigned long long index, unsigned long long time, uint256 parentHash, Context& c) : context(c)
    {
        header.__block_number__ = index;
        header.__block_hash__   = uint256(0);
        header.__parent_hash__  = parentHash;
        header.__block_time__   = time;
        header.__nonce__        = 0;
        header.__miner__        = Address();
    }
    Block(const StoredBlock& s, Context& c)
            : header(s.header), context(c)
    {
    }
    void setMiner(const Address address)
    {
        header.__miner__ = address;
    }
    void add(Transaction& transaction)
    {
        if (transaction.valid(context))
            tree.add(transaction);
    }
    bool full()
    {
        return tree.length() >= BLOCK_MAX_TRANSACTIONS;
    }
    bool ready()
    {
        return full() || context.lastTransactionWas((long)(BLOCK_INTERVAL * 0.75));
    }
    void mine()
    {
    }
    BlockIndex getIndex()
    {
        return header.__block_number__;
    }
    uint256 getHash()
    {
        return header.__block_hash__;
    }
    uint256 getParentHash()
    {
        return header.__parent_hash__;
    }

    bool valid()
    {
        return false;
    }
    StoredBlock* toStoredBlock()
    {
        return new StoredBlock(header, tree);
    }
    ~Block() {}

    bool checkSolutionValid() {
        return false;
    }

    Address getMiner()
    {
        return header.__miner__;
    }

    bool finished()
    {
        return false;
    }
};

struct BlockPacket{
    Block block;
    long long timeStamp;
};

#endif //RIVERCOIN_CPP_BLOCK_H
