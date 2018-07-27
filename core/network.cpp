//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#include "network.h"
#include "context.h"
#include "config.h"

#include <boost/asio.hpp>
#include <boost/array.hpp>
#include <iostream>

#ifndef BOOST_IOS
#define BOOST_IOS
boost::asio::io_service ios;
#endif

SocketConnection::SocketConnection(const std::string _ip_, unsigned short _port_) : ip(_ip_), port(_port_), socket(ios)
{
}

void SocketConnection::makeConnection()
{
    boost::asio::io_service ios;

    boost::asio::ip::tcp::endpoint endpoint(boost::asio::ip::address::from_string(ip), port);

    socket = boost::asio::ip::tcp::socket(ios);

    socket.connect(endpoint);
}

bool SocketConnection::isConnected()
{
    return socket.is_open();
}

void SocketConnection::disconnect()
{
    socket.close();
}

void SocketConnection::send(const std::string& msg, boost::system::error_code& error)
{
    boost::array<char, msg.size()> buf;
    std::copy(msg.begin(), msg.end(), buf);

    socket.async_write_some(boost::asio::buffer(buf, msg.size()), error);
}

void SocketConnection::receive(char *out, const unsigned long &length)
{
}


