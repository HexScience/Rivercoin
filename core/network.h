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
#include <string>
#include <vector>
#include <set>

#include <boost/asio.hpp>
#include <boost/array.hpp>
#include <iostream>

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
class Context;

/** this is a container for all the TCP socket code **/
struct SocketConnection{
    const std::string               ip;
    const unsigned short            port;
    bool                            connected;
    boost::asio::io_service         ios;
    boost::asio::ip::tcp::socket    socket;
    std::vector<Message>            messages;
    SocketConnection(const std::string _ip_, unsigned short _port_);// : ip(_ip_), port(_port_) {}
    void makeConnection();
    bool isConnected();
    void disconnect();
    void send(const std::string& msg, boost::system::error_code& error);
    void receive();
};

/** this class should handle a single TCPConnectionSocket to send and receive high level data (Messages) **/
class Client{
private:
    SocketConnection connection;
    static Message interpretMessage(unsigned char msg);
public:
    void send(serializeable<char*> msg);
    void receive(Context* context);
    void connect();
    void disconnect();
    void execute();
};

/** this class should handle multiple clients by sending and receiving data to and from the client set **/
class Server{
private:
    std::set<Client>        clients;
    std::set<std::string>   ipAddresses;
    Context*                context;
    void makeConnection(const std::string& ip);
    void acceptConnections();
    void readAllIpAddresses();
    void writeAllIpAddresses();
public:
    Server(Context* context);
    void send(Message msg);
    void close();
};

#endif //RIVERCOIN_CPP_NETWORK_H
