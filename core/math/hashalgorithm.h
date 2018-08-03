//
// Created by Abdullah Fadhil on 03.08.18.
//

#ifndef RIVERCOIN_CPP_HASHALGORITHM_H
#define RIVERCOIN_CPP_HASHALGORITHM_H

#include <string>
#include "math.h"

#include <openssl/sha.h>
#include <openssl/ripemd.h>

class HashAlgorithm {
private:
public:
    template <unsigned int L> std::string base32(const Array<char, L>& a);
    template <unsigned int L> std::string base58(const Array<char, L>& a);
};

namespace algorithms{
    namespace sha256{
        static template <unsigned int L> std::string base32(const Array<char, L>& a);
        static template <unsigned int L> std::string base58(const Array<char, L>& a);
    };
    namespace sha512{
        static template <unsigned int L> std::string base32(const Array<char, L>& a);
        static template <unsigned int L> std::string base58(const Array<char, L>& a);
    };
    namespace ripemd160{
        static template <unsigned int L> std::string base32(const Array<char, L>& a);
        static template <unsigned int L> std::string base58(const Array<char, L>& a);
    }

    static template <unsigned int L> std::string base32(const Array<char, L>& a);
    static template <unsigned int L> std::string base58(const Array<char, L>& a);
}



#include <openssl/sha.h>
#include <openssl/ripemd.h>
#include "../base58.h"

#ifndef _base32_
#define _base32_

static int ___char2int(char input)
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
static void ___hexToBytes(const char* src, char* target)
{
    while(*src && src[1])
    {
        *(target++) = ___char2int(*src)*16 + ___char2int(src[1]);
        src += 2;
    }
}

static std::string ___toHex(const char * data, int length)
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

template <unsigned int L>
std::string algorithms::sha256::base32(const Array<char, L> &a)
{
    char _32b[32];

    SHA256((unsigned char *) a.array, a.length(), (unsigned char *) _32b);

    return ___toHex(_32b, 32);
}

template <unsigned int L>
std::string algorithms::sha256::base58(const Array<char, L> &a)
{
    char _32b[32];

    SHA256((unsigned char *) a.array, a.length(), (unsigned char *) _32b);

    return Base58::quick_encode(_32b, 32);
}

template <unsigned int L>
std::string algorithms::base32(const Array<char, L> &a)
{
    return ___toHex(a.array, a.length());
}

template <unsigned int L>
std::string algorithms::base58(const Array<char, L> &a)
{
    return Base58::quick_encode(a.array, a.length());
}

template <unsigned int L>
std::string algorithms::sha512::base32(const Array<char, L> &a)
{
    char _64b[64];

    SHA256((unsigned char *) a.array, a.length(), (unsigned char *) _64b);

    return ___toHex(_64b, 64);
}

template <unsigned int L>
std::string algorithms::sha512::base58(const Array<char, L> &a)
{
    char _64b[64];

    SHA512((unsigned char *) a.array, a.length(), (unsigned char *) _64b);

    return Base58::quick_encode(_64b, 64);
}

template <unsigned int L>
std::string algorithms::ripemd160::base32(const Array<char, L> &a)
{
    char _20b[20];

    RIPEMD160((unsigned char *) a.array, a.length(), (unsigned char *) _20b);

    return ___toHex(_20b, 20);
}

template <unsigned int L>
std::string algorithms::ripemd160::base58(const Array<char, L> &a)
{
    char _20b[20];

    RIPEMD160((unsigned char *) a.array, a.length(), (unsigned char *)_20b);

    return Base58::quick_encode(_20b, 20);
}

#endif //RIVERCOIN_CPP_HASHALGORITHM_H
