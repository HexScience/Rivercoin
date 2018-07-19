//
// Created by Ragnarr Ivarssen on 16.07.18.
//

#ifndef RIVERCOIN_CPP_HASHUTIL_H
#define RIVERCOIN_CPP_HASHUTIL_H

#include <iostream>
#include "../base58.h"

class HashUtil{
public:
    static std::string toHex(const char * data, int length)
    {
        std::string string = "";
        char const hex_chars[16] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

        for( int i = 0; i < length; i ++)
        {
            char const byte = data[i];

            string += hex_chars[ ( byte & 0xF0 ) >> 4 ];
            string += hex_chars[ ( byte & 0x0F ) >> 0 ];
        }

        return string;
    }
};

#endif //RIVERCOIN_CPP_HASHUTIL_H
