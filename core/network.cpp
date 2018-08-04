//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#include "network.h"
#include "context.h"
#include "config.h"

#include <boost/asio.hpp>
#include <boost/array.hpp>
#include <iostream>

#ifndef SOCKETS_
#define SOCKETS_

struct Socket{
    boost::asio::io_service                 ios;
    boost::asio::ip::tcp::socket            socket;

    Socket() : socket(ios) {}
//    Socket(const Socket& o) : socket(o.socket) { ios = socket.get_io_context(); }
};

struct ServerSocket{
};

#endif

SocketConnection::SocketConnection(const std::string _ip_, unsigned short _port_) : ip(_ip_), port(_port_)
{
}

void SocketConnection::makeConnection()
{
    boost::asio::ip::tcp::endpoint endpoint(boost::asio::ip::address::from_string(ip), port);

    socket->socket.connect(endpoint);
}

bool SocketConnection::isConnected()
{
    return socket->socket.is_open();
}

void SocketConnection::disconnect()
{
    socket->socket.close();
}

void SocketConnection::send(const std::string& msg)
{
//    boost::array<char, 4> buf;
//    std::copy(msg.begin(), msg.end(), buf);
//
//    boost::system::error_code e;
//
//    socket->socket.async_write_some(boost::asio::buffer(buf, msg.size()), e);
}

void SocketConnection::receive()
{
//    socket->socket.read_some(out);
}


