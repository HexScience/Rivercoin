//
// Created by Ragnarr Ivarssen on 19.07.18.
//

#ifndef RIVERCOIN_CPP_MINER_H
#define RIVERCOIN_CPP_MINER_H

class Block;

class RiverMiner {
public:
    void mine(Block* block);
    bool busy();
    void interrupt();
};


#endif //RIVERCOIN_CPP_MINER_H
