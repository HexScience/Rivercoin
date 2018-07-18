//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#ifndef RIVERCOIN_CPP_TRANSACTION_H
#define RIVERCOIN_CPP_TRANSACTION_H

#include "hash.h"
#include "rivercoin.h"
#include "security/security.h"
#include "context.h"
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

class Transaction{
private:
    unsigned char type;
    CompressedPublicKey sender;
    Address             receiver;
    unsigned char       transactionData[96];
    unsigned char       transactionInputs[256];
public:
    Transaction(unsigned char t,
                CompressedPublicKey s,
                Address r) : type(t), sender(s), receiver(r) {}

    bool valid(Context& context)
    {
        return false;
    }
};

#endif //RIVERCOIN_CPP_TRANSACTION_H
