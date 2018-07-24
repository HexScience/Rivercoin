////
//// Created by Ragnarr Ivarssen on 09.07.18.
////
//
#ifndef RIVERCOIN_CPP_NETWORK_H
#define RIVERCOIN_CPP_NETWORK_H
//
//#include <cstdlib>
//#include <iostream>
//#include <memory>
//#include <utility>
//#include <boost/asio.hpp>
//#include <ctime>
//#include <string>
//#include <boost/bind.hpp>
//#include <boost/shared_ptr.hpp>
//#include <boost/enable_shared_from_this.hpp>
//#include <boost/asio.hpp>
//
//struct DataPacket{
//    unsigned long length;
//    const char *  data;
//
//    DataPacket(unsigned long l, const char * d) : length(l), data(d) {}
//};
//
//class BasicMessage{
//    unsigned char op;
//    DataPacket    packet;
//};
//

#include "serializeable.h"

#define MSG_RECEIVED_SUCCESSFULLY 0
#define MSG_CORRUPTED 1
#define MSG_BLOCK_SOLUTION 2
#define MSG_FULL_BLOCK_GET 3
#define MSG_TRANSACTION 4
#define MSG_BLOCKCHAIN_SIZE 5
#define MSG_GREET 6
#define MSG_GREET_BACK 7
#define MSG_GOODBYE 8
#define MSG_FULL_BLOCK_SET 9

typedef serializeable<char*> Message;

class Client{
private:
public:
    void send(serializeable<char*> msg);
    void connect();
    void disconnect();
};

#endif //RIVERCOIN_CPP_NETWORK_H
