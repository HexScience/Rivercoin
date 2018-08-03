//
// Created by Abdullah Fadhil on 03.08.18.
//

#include "hashalgorithm.h"

#include <openssl/sha.h>
#include <openssl/ripemd.h>
#include "../base58.h"

#ifndef _base32_
#define _base32_

static int char2int(char input)
{
    if(input >= '0' && input <= '9')
        return input - '0';
    if(input >= 'A' && input <= 'F')
        return input - 'A' + 10;
    if(input >= 'a' && input <= 'f')
        return input - 'a' + 10;
    throw std::invalid_argument("Invalid input string");
}

// This function assumes src to be a zero terminated sanitized string with
// an even number of [0-9a-f] characters, and target to be sufficiently large
static void hexToBytes(const char* src, char* target)
{
    while(*src && src[1])
    {
        *(target++) = char2int(*src)*16 + char2int(src[1]);
        src += 2;
    }
}

static std::string toHex(const char * data, int length)
{
    std::string string = "";
    char const hex_chars[16] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    for (int i = 0; i < length; i++) {
        char const byte = data[i];

        string += hex_chars[(byte & 0xF0) >> 4];
        string += hex_chars[(byte & 0x0F) >> 0];
    }

    return string;
}

#endif

template <typename T, unsigned int L>
std::string algorithms::sha256::base32(const Array<T, L> &a)
{
    char _32b[32];

    Array<char, L * sizeof(T)> ca = a.toCharArray().array;

    unsigned char len = SHA256(ca.array, ca.length(), _32b);

    return toHex(_32b, len);
}

template <typename T, unsigned int L>
std::string algorithms::sha256::base58(const Array<T, L> &a)
{
    char _32b[32];

    Array<char, L * sizeof(T)> ca = a.toCharArray().array;

    unsigned char len = SHA256(ca.array, ca.length(), _32b);

    return Base58::quick_encode(_32b, len);
}

template <typename T, unsigned int L>
std::string algorithms::base32(const Array<T, L> &a)
{
    Array<char, L * sizeof(T)> ca = a.toCharArray().array;

    return toHex(ca.array, ca.length());
}

template <typename T, unsigned int L>
std::string algorithms::base58(const Array<T, L> &a)
{
    return std::string();
}
