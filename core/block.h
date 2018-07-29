//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#ifndef RIVERCOIN_CPP_BLOCK_H
#define RIVERCOIN_CPP_BLOCK_H

#include "transaction.h"
#include "math/math.h"
#include "tree.h"
#include "config.h"
#include "security/security.h"
#include <memory>
#include <algorithm>

struct BlockHeader {
    const BlockIndex                      __block_number__;
    const uint256                         __parent_hash__;
    uint256                               __block_hash__;
    const Address                         __miner__;
    const unsigned long long              __block_time__;
    unsigned long long                    __nonce__;

    BlockHeader(BlockIndex number, uint256 parentHash, uint256 hash, Address miner, unsigned long long time, unsigned long long nonce);
    BlockHeader(const BlockHeader& o);
};

class Block;

struct StoredBlock{
    BlockHeader     header;
    TransactionTree tree;
//    StoredBlock() : header(0, ByteUtil::fromBytes256(EMPTY_HASH), ByteUtil::fromBytes256(EMPTY_HASH), Address(), 0, 0)
//    {
//    }
    StoredBlock(BlockHeader h, TransactionTree t);
    StoredBlock(const StoredBlock& o);
};

#define GENESIS 1
#define GENESIS_HASH {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

class Context;

class Block{
private:
    char EMPTY_HASH[32] = GENESIS_HASH;
    BlockHeader              header;
    TransactionTree          tree;
    Context*                 context;
public:
    Block(unsigned long long index, unsigned long long time, uint256 parentHash, Context* c);
    Block(const StoredBlock& block, Context* c);
    void setMiner(const Address address);
    void add(Transaction& transaction);
    bool full();
    bool ready();
    BlockIndex getIndex();
    uint256 getHash();
    uint256 getParentHash();
    bool valid();
    std::shared_ptr<StoredBlock> toStoredBlock();
    bool checkSolutionValid();
    Address getMiner();
    bool finished();
    void removeTransactions(std::vector<Transaction> &vector);
    ~Block();
};

struct BlockPacket{
    Block block;
    long long timeStamp;
};

#endif //RIVERCOIN_CPP_BLOCK_H
