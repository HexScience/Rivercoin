//
// Created by Ragnarr Ivarssen on 18.07.18.
//

#ifndef RIVERCOIN_CPP_BLOCKCHAIN_H
#define RIVERCOIN_CPP_BLOCKCHAIN_H

#include <vector>
#include <map>
#include "serializeable.h"
#include "fileutils.h"
#include <string>
#include "security/security.h"

#define GENESIS 1
#define NO_CHAIN 0
#define INVALID_CHAIN 2

class Block;
class Context;

class BlockChain{
private:
    std::vector<Block*>     orphanedBlocks;
    std::vector<Block*>     downloadedBlocks;
    Context*                context;
    Block*                  current;
public:
    BlockChain(Context* c);

    void loadAllBlocks();
    void downloadAllMissingBlocks();
    void queOrphaned(Block* block);
    void download(Block* block);
    void continueChain();

    void serialize();

    void sendSolution();

    BlockIndex activeBlock();

    void removeOldOrphandedBlocks();

    void removeInvalidOrphandedBlocks();

    std::vector<Block*> getLongest(std::vector<std::vector<Block*>> subchains);

    void updateChain(std::vector<Block*> longChain);

    void checkForValidSolutions();

    void execute();
};

#endif //RIVERCOIN_CPP_BLOCKCHAIN_H
