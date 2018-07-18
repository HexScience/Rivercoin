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

    void mine();

    bool valid();
    StoredBlock* toStoredBlock();
    ~Block() {}
};

#endif //RIVERCOIN_CPP_BLOCK_H
