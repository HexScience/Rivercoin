//
// Created by Ragnarr Ivarssen on 13.07.18.
//

#ifndef RIVERCOIN_CPP_CONTEXT_H
#define RIVERCOIN_CPP_CONTEXT_H

#include "config.h"
#include "network.h"
#include <boost/property_tree/xml_parser.hpp>
#include <boost/property_tree/ptree.hpp>
#include <boost/foreach.hpp>
#include "security/security.h"
#include <string>
#include "logger.h"
#include "math/math.h"

class RiverMiner;

class Context {
private:
    config          _config;
    bool            _keepAlive;
    RiverMiner*     miner;
public:
    Context(boost::property_tree::ptree& tree) : _config(tree)
    {
    }
    config& getConfig();
//    Server& getServer() const;
    bool lastTransactionWas(long i);
    long long timestamp();
    virtual void execute();
    RiverMiner* getMiner();
    uint256 getDifficulty();
    bool keepAlive();
    void sendMessageToNetwork(const Message& msg) const;
};


#endif //RIVERCOIN_CPP_CONTEXT_H
