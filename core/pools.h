//
// Created by Ragnarr Ivarssen on 26.07.18.
//

#ifndef RIVERCOIN_CPP_POOLS_H
#define RIVERCOIN_CPP_POOLS_H

#include <vector>
#include <map>
#include <set>
#include "transaction.h"

class Context;

class TransactionPool{
private:
    Context*                context;
    std::set<Transaction>   transactions;
    unsigned long long      timestamp;
public:
    TransactionPool(Context* context);
    void insert(Transaction& transaction);
    void relay(Transaction& transaction);
    void introduce(Transaction& transaction);
    Transaction fetch();
    unsigned long size();
    unsigned long long lastTransactionTimestamp();
};


#endif //RIVERCOIN_CPP_POOLS_H
