//
// Created by Ragnarr Ivarssen on 26.07.18.
//

#include "pools.h"
#include "context.h"

TransactionPool::TransactionPool(Context* c) : context(c)
{
}
void TransactionPool::insert(Transaction &transaction)
{
    transactions.insert(transaction);
    timestamp = context->timestamp();
}
void TransactionPool::relay(Transaction& transaction)
{
}
void TransactionPool::introduce(Transaction& transaction)
{
}
Transaction TransactionPool::fetch()
{
    return Transaction();
}
unsigned long TransactionPool::size()
{
    transactions.size();
}
unsigned long long TransactionPool::lastTransactionTimestamp()
{
    timestamp;
}
