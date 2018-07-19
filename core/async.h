//
// Created by Ragnarr Ivarssen on 19.07.18.
//

#ifndef RIVERCOIN_CPP_ASYNC_H
#define RIVERCOIN_CPP_ASYNC_H

#include <boost/thread.hpp>
#include <boost/bind.hpp>

class Async{
private:
public:
    Async(){
        boost::thread thread;
    }
};

#endif //RIVERCOIN_CPP_ASYNC_H
