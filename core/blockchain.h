//
// Created by Ragnarr Ivarssen on 18.07.18.
//

#ifndef RIVERCOIN_CPP_BLOCKCHAIN_H
#define RIVERCOIN_CPP_BLOCKCHAIN_H

#include <vector>
#include <map>
#include "block.h"
#include "serializeable.h"
#include "fileutils.h"
#include <string>

#define GENESIS 1
#define NO_CHAIN 0
#define INVALID_CHAIN 2

class BlockChain{
private:
    std::vector<Block*>     orphanedBlocks;
    std::vector<Block*>     downloadedBlocks;
    Context                 context;
    Block*                  current;
public:
    BlockChain(Context& c) : context(c)
    {
    }

    void loadAllBlocks();
    void downloadAllMissingBlocks();
    void queOrphaned(Block* block);
    void download(Block* block);
    Block* continueChain()
    {
        Block* temp = current;

        current = new Block(temp->getIndex() + 1, context.timestamp(), temp->getHash(), context);

        delete(temp);
    }

    void serialize()
    {
        using std::string;
        serializeable<Block> s(BLOCK_MAGIC_HEADER, *current);

        unsigned char ERROR = 0;

        file::write((string(STRUCTURE_DB_NAME) + string(BLOCKCHAIN_DB_NAME) + string("block[") + std::to_string(current->getIndex()) + string("].blk")).c_str(), s, ERROR);

        if(ERROR != 0)
        {
            logger::err(string(string("couldn't export block '") + std::to_string(current->getIndex()) + string("'.")));
        }
    }

    void execute()
    {
        loadAllBlocks();
        downloadAllMissingBlocks();
    }
};

#endif //RIVERCOIN_CPP_BLOCKCHAIN_H
