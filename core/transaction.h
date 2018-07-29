//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#ifndef RIVERCOIN_CPP_TRANSACTION_H
#define RIVERCOIN_CPP_TRANSACTION_H

#include "defines.h"
#include "rivercoin.h"
#include "security/security.h"
#include <vector>
#include <map>
#include <set>
#include "math/math.h"

#define TRANSACTION_TRANSACTION 0
#define REWARD_TRANSACTION 1
#define CONTRACT_TRANSACTION 2
#define INVOKE_TRANSACTION 3

struct Output{
    const Address owner;
    const rvc     amount;
    const uint256 hash;

    Output(const Address o, const rvc a, uint256 h) : owner(o), amount(a), hash(h) {}
    Output(const Output& o) : owner(o.owner), amount(o.amount), hash(o.hash) {}

    bool compare(const Output& output) const
    {
        return owner.compare(output.owner) && amount == output.amount && hash == output.hash;
    }
};

class Ledger{
private:
    std::map<const Address, std::vector<const Output>> ledger;
public:
    Ledger() {}
    Ledger(const Ledger& o) : ledger(o.ledger) {}
    void add(const Output output)
    {
        ledger[output.owner].push_back(output);
    }

    bool remove(const Output& compare)
    {
//        std::vector<const Output>::const_iterator index;
//        for (std::vector<const Output>::const_iterator i; i < ledger[compare.owner].size(); i ++)
//            if(ledger[compare.owner][i].compare(compare))
//                index = i;
//
//        ledger[compare.owner].erase(index);
        return false;
    }
};

class Context;

class Transaction{
private:
    enum {FUND_TRANSFER, REWARD, CONTRACT, CONTRACT_INVOKE};
    unsigned char type;
    CompressedPublicKey sender;
    Address             receiver;
    unsigned char       transactionData[256];
public:
    Transaction();
    Transaction(unsigned char t,
                CompressedPublicKey s,
                Address r);
    bool valid(Context* context);
    bool operator== (const Transaction& o) const;
    CompressedPublicKey getSender();
    Address getReceiver();
    unsigned char* getTransactionData();
    bool operator< (const Transaction& o) const;
};

class FundTransfer {
private:
public:
//    virtual void createOutputs(Transaction& t, std::vector<Output>& out)
//    {
//        rvc amount = ((rvc *)t.getTransactionData())[0];
//        unsigned long long nonce = ((unsigned long long *) t.getTransactionData() + sizeof(rvc))[0];
//    }
//    virtual void generateOutputs(Transaction& t, Ledger& ledger)
//    {
//    }
//    virtual void undoGenerateOutputs(Transaction& t, Ledger& ledger)
//    {
//    }
};

#endif //RIVERCOIN_CPP_TRANSACTION_H
