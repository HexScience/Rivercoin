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
    template <unsigned int L> std::string base32(const Array<T, L>& a);
    template <unsigned int L> std::string base58(const Array<T, L>& a);
};

namespace algorithms{
    namespace sha256{
        static template <unsigned int L> std::string base32(const Array<T, L>& a);
        static template <unsigned int L> std::string base58(const Array<T, L>& a);
    };
    namespace sha512{
        static template <unsigned int L> std::string base32(const Array<T, L>& a);
        static template <unsigned int L> std::string base58(const Array<T, L>& a);
    };
    namespace ripemd160{
        static template <unsigned int L> std::string base32(const Array<T, L>& a);
        static template <unsigned int L> std::string base58(const Array<T, L>& a);
    }

    static template <unsigned int L> std::string base32(const Array<T, L>& a);
    static template <unsigned int L> std::string base58(const Array<T, L>& a);
}


#endif //RIVERCOIN_CPP_HASHALGORITHM_H
