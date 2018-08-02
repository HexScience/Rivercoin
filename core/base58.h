//
// Created by Ragnarr Ivarssen on 13.07.18.
//

#ifndef RIVERCOIN_CPP_BASE58_H
#define RIVERCOIN_CPP_BASE58_H

#include <string>

class Base58 {
    static const char *ALPHABET;
    static const char ALPHABET_MAP[128];
public:
    static std::string quick_encode(char* data, int length);
// result must be declared: char result[len * 137 / 100];
    static int encode(const unsigned char *bytes, int len, unsigned char result[]);

// result must be declared (for the worst case): char result[len * 2];
    static int decode(
            const unsigned char *str, int len, unsigned char *result);
};
#endif //RIVERCOIN_CPP_BASE58_H
