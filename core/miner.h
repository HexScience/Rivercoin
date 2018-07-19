//
// Created by Ragnarr Ivarssen on 19.07.18.
//

#ifndef RIVERCOIN_CPP_MINER_H
#define RIVERCOIN_CPP_MINER_H

#include "math/math.h"

class Block;

class RiverMiner {
public:
    void mine(Block* block, const uint256& difficulty);
    bool busy();
    void interrupt();
};


#endif //RIVERCOIN_CPP_MINER_H
