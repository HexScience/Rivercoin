//
// Created by Ragnarr Ivarssen on 19.07.18.
//

#include "miner.h"
#include "block.h"
#include "math/riverhash.h"
//#include <boost/thread.hpp>
//#include <boost/thread/future.hpp>
//#include <boost/thread/tss.hpp>

void RiverMiner::mine(Block* block, const uint256& difficulty)
{
    StoredBlock* storedBlock = block->toStoredBlock();
    const char* in = (char* ) storedBlock;
    unsigned long long nonce = 0;
    char out[32];

//    boost::thread_group group;
//
//    group.create_thread(boost::bind(RiverHash::mine, RiverHash::RiverHash_13_v4, in, nonce, sizeof(StoredBlock), out, difficulty));

    delete storedBlock;
}

bool RiverMiner::busy()
{
    return false;
}

void RiverMiner::interrupt()
{
}
