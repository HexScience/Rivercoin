//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#ifndef RIVERCOIN_CPP_BLOCK_H
#define RIVERCOIN_CPP_BLOCK_H

#include "transaction.h"
#include "../external/uint256_t.h"
#include "tree.h"
#include "config.h"
#include "context.h"
#include "security/security.h"

struct BlockHeader {
    const unsigned long long            __block_number__;
    const uint256_t                     __parent_hash__;
    const uint256_t                     __block_hash__;
    const Address                       __miner__;
    const unsigned long long            __block_time__;
    const unsigned long long            __nonce__;
};

struct StoredBlock{
    BlockHeader header;
};

class Block{
private:
    const unsigned long long index;
    const unsigned long long times;
    const Address            miner;
    const uint256_t          parent;
    TransactionTree          tree;
    Context                  context;
public:
    Block(unsigned long long i, unsigned long long t, Address m, uint256_t p, Context& c) : index(i), times(t), miner(m), parent(p), context(c) {}
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
