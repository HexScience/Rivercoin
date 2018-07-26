//
// Created by Ragnarr Ivarssen on 26.07.18.
//

#include "transaction.h"

Transaction::Transaction() {}
Transaction::Transaction(unsigned char t, CompressedPublicKey s, Address r) : type(t), sender(s), receiver(r) {}
bool Transaction::valid(Context &context)
{
    if (type != FUND_TRANSFER && type != REWARD && type != CONTRACT && type != CONTRACT_INVOKE) return false;

    return false;
}
bool Transaction::operator==(const Transaction &o)
{
    if (type != o.type) return false;

    if (sender != o.sender) return false;

    for (unsigned int i = 0; i < 256; i ++)
        if (transactionData[i] != o.transactionData[i]) return false;

//        for (unsigned int i = 0; i < 256; i ++)
//            if (transactionInputs[i] != o.transactionInputs[i]) return false;
}
CompressedPublicKey Transaction::getSender()
{
    return sender;
}
Address Transaction::getReceiver()
{
    return receiver;
}
unsigned char* Transaction::getTransactionData()
{
    return transactionData;
}