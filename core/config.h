//
// Created by Ragnarr Ivarssen on 13.07.18.
//

#ifndef RIVERCOIN_CPP_CONFIG_H
#define RIVERCOIN_CPP_CONFIG_H

#include "defines.h"
#include "security/security.h"
#include <string>

#include <boost/property_tree/xml_parser.hpp>
#include <boost/property_tree/ptree.hpp>
#include <boost/foreach.hpp>
#include "security/security.h"
#include "logger.h"

#define MAX_RIVERCOIN_AMT 180000000
#define FIRST_REWARD 50

/** block interval in milliseconds **/
#define BLOCK_INTERVAL 30000
#define BLOCK_MAX_TRANSACTIONS 12500

#define secondToMillis(x) (x * 1000)
#define millisToSeconds(x) (x / 1000)

#define STRUCTURE_DB_NAME "structure"
#define BLOCKCHAIN_DB_NAME "blockchain"
#define DEFAULT_PORT 5110
#define CONFIG_DB_NAME "config"
#ifdef WINDOWS_PLATFORM
#define PATH_SEPARATOR '\\'
#else
#define PATH_SEPARATOR '/'
#endif

struct config{
    char BASE_TARGET_DIFFICULTY[32] = {15, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127};
    char MIN[32] = {0, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127, 127};
    char CURRENT[32];
    Address address;
    bool    mining_allowed;
    bool    use_gpu;
    unsigned char cpu_thread_count;
    unsigned short max_outbound_connections;
    unsigned short max_inbound_connections;
    bool           enable_crash_logs;
    unsigned long  memdump_interval;
    config(boost::property_tree::ptree& tree)
    {
        using boost::property_tree::ptree;

        BOOST_FOREACH(boost::property_tree::ptree::value_type const& v, tree.get_child("Config") )
        {
            if(v.first == "Mining") {
                const char* mining_address_string = (v.second.get<std::string>("Address").c_str());

                if(Address::__check_address_valid(mining_address_string))
                    address = Address(mining_address_string);
                else{
                    logger::err((std::string("mining address '") + std::string(mining_address_string) + std::string("' incorrect.")).c_str());
//                    exit(0);
                }
                mining_allowed = v.second.get<bool>("Enabled");
                use_gpu        = v.second.get<bool>("GPUMining");
                cpu_thread_count        = v.second.get<unsigned char>("CPUThreadCount");
            } else if(v.first == "Network") {
                max_outbound_connections        = v.second.get<unsigned short>("MaxOutboundConnections");
                max_inbound_connections         = v.second.get<unsigned short>("MaxInboundConnections");
            } else if(v.first == "Cycles")
            {
                enable_crash_logs               = v.second.get<bool>("EnableSystemCrashLogs");
                memdump_interval                = v.second.get<unsigned long>("MemoryDumpInterval");
            }
        }
    }

    const char * calculateDifficulty()
    {
        return BASE_TARGET_DIFFICULTY;
    }
};

#endif //RIVERCOIN_CPP_CONFIG_H
