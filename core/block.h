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
    using boost::multiprecision::uint256_t;
    unsigned long long            __block_number__;
    uint256_t                     __parent_hash__;
    uint256_t                     __block_hash__;
    Address                       __miner__;
    unsigned long long            __block_time__;
    unsigned long long            __nonce__;
};

struct StoredBlock{
    BlockHeader header;

    StoredBlock(BlockHeader h) : header(h)
    {
    }

    StoredBlock(const StoredBlock& o)
            : header(o.header)
    {
    }
};

class Block{
private:
    using boost::multiprecision::uint256_t;
    BlockHeader              header;
    TransactionTree          tree;
    Context                  context;
public:
    Block(unsigned long long index, unsigned long long time, uint256_t parentHash, Context& c) : context(c)
    {
        header.__block_number__ = index;
        header.__parent_hash__  = parentHash;
        header.__block_time__   = time;
    }
    Block(const StoredBlock& s, Context& c)
            : header(s.header), context(c)
    {
    }
    bool add(Transaction& transaction)
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
    unsigned long long getIndex()
    {
        return header.__block_number__;
    }
    uint256_t getHash()
    {
        return header.__block_hash__;
    }
    uint256_t getParentHash()
    {
        return header.__parent_hash__;
    }

    bool valid()
    {
    }
    StoredBlock* toStoredBlock()
    {
        return new StoredBlock(header);
    }
    ~Block() {}

    bool checkSolutionValid() {
        return false;
    }

    Address getMiner()
    {
        return header.__miner__;
    }
};

#endif //RIVERCOIN_CPP_BLOCK_H
