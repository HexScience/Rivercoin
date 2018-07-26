//
// Created by Ragnarr Ivarssen on 26.07.18.
//

#include "pools.h"

TransactionPool::TransactionPool(Context& c) : context(c)
{
}
void TransactionPool::insert(Transaction &transaction)
{
    transactions.insert(transaction);
    timestamp = context.timestamp();
}
void TransactionPool::relay(Transaction& transaction)
{
}
void TransactionPool::introduce(Transaction& transaction)
{
}
Transaction TransactionPool::fetch()
{
}
unsigned long TransactionPool::size()
{
}
unsigned long long TransactionPool::lastTransactionTimestamp()
{
}
