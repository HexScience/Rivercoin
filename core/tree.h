//
// Created by Ragnarr Ivarssen on 13.07.18.
//

#ifndef RIVERCOIN_CPP_TREE_H
#define RIVERCOIN_CPP_TREE_H

#include "transaction.h"
#include <vector>
#include <boost/multiprecision/cpp_int.hpp>

struct TransactionTreeElement{
    TransactionTreeElement* child_a;
    TransactionTreeElement* child_b;
    Transaction*            elemt;

    TransactionTreeElement(Transaction* c) : child_a(0), child_b(0), elemt(c) {}
    TransactionTreeElement(TransactionTreeElement* a, TransactionTreeElement* b) : child_a(a), child_b(b), elemt(0) {}

    uint256 hash()
    {
//        if(elemt)
//            return sha_256((const char *) elemt);
//        else return uint256_t(20);
        return uint256(0);
    }

    ~TransactionTreeElement()
    {
        delete(child_a);
        delete(child_b);
        delete(elemt);
    }
};

class TransactionTree{
private:
    TransactionTreeElement* root;
    std::vector<Transaction> list;
public:
    TransactionTree() : root(0)
    {
    }

    void add(Transaction& transaction)
    {
        list.push_back(transaction);
    }

    unsigned long length()
    {
        return list.size();
    }

    void build()
    {
    }
};

#endif //RIVERCOIN_CPP_TREE_H
