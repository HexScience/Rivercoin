//
// Created by Ragnarr Ivarssen on 30.07.18.
//

#ifndef RIVERCOIN_CPP_ECRF_H
#define RIVERCOIN_CPP_ECRF_H

#include "math/math.h"
#include <openssl/sha.h>
#include <string>

//template <typename hasher, typename curver>
class ecrf{
    static void digest(unsigned int t, const std::string& a, unsigned char* out)
    {
        switch (t)
        {
            case SHA_256:
                SHA256((unsigned char *) a.c_str(), a.size(), out);
                break;
        }
    }
public:
    enum{SHA_256 = 32};
    static void gen_private(const std::string& seed, unsigned int t, char* priv, char* pub)
    {
        unsigned char out[t];

        digest(t, seed, out);

        unsigned short privKey[t];

        for (int i = 0; i < t; i ++) privKey[i] = out[i] * 1.61803398875;

        unsigned short publKey[t / 2];

        for (int i = 0; i < t / 2; i ++) publKey[i] = 19 * (privKey[i] ^ privKey[t / 2]);

        memcpy(priv, (char *) privKey, t * 2);
        memcpy(pub, (char *) publKey, t);
    }
};

#endif //RIVERCOIN_CPP_ECRF_H
