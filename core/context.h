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

class Context {
private:
    config _config;
public:
    Context(boost::property_tree::ptree& tree) : _config(tree)
    {
    }
    const config& getConfig() const;
//    Server& getServer() const;
    bool lastTransactionWas(long i);

    long long timestamp();

//    u_int256 getDifficulty();
};


#endif //RIVERCOIN_CPP_CONTEXT_H
