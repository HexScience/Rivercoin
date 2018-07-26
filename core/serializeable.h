//
// Created by Ragnarr Ivarssen on 18.07.18.
//

#ifndef RIVERCOIN_CPP_SERIALIZEABLE_H
#define RIVERCOIN_CPP_SERIALIZEABLE_H

#include "block.h"

#define BLOCK_MAGIC_HEADER 0

template <typename T> struct serializeable{
    const short magic_header;
    const T serializeable_data;

    serializeable(const short m, const T& s) : magic_header(m), serializeable_data(s) {}
    serializeable(const serializeable& o) : magic_header(o.magic_header), serializeable_data(o.serializeable_data) {}
};

#endif //RIVERCOIN_CPP_SERIALIZEABLE_H
