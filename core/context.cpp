//
// Created by Ragnarr Ivarssen on 13.07.18.
//

#include "context.h"
#include <chrono>
#include "miner.h"

const config& Context::getConfig() const { return _config; }

long long Context::timestamp()
{
    return std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count();
}

bool Context::lastTransactionWas(long i)
{
    return false;
}

bool Context::keepAlive()
{
    return _keepAlive;
}

void Context::execute()
{
}

RiverMiner* Context::getMiner()
{
    return nullptr;
}

//u_int256 Context::getDifficulty()
//{
//    return uint256::fromBytes256("11111111111111111111111111111111111111");
//}
//Server& Context::getServer() const { return server; }
