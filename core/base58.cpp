//
// Created by Ragnarr Ivarssen on 13.07.18.
//

#include "base58.h"
#include "math/math.h"
#include "btc/base58.h"
#include <vector>

//const char* Base58::ALPHABET =
//        "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
//
//const char Base58::ALPHABET_MAP[128] = {
//        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
//        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
//        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
//        -1,  0,  1,  2,  3,  4,  5,  6,  7,  8, -1, -1, -1, -1, -1, -1,
//        -1,  9, 10, 11, 12, 13, 14, 15, 16, -1, 17, 18, 19, 20, 21, -1,
//        22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, -1, -1, -1, -1, -1,
//        -1, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, -1, 44, 45, 46,
//        47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, -1, -1, -1, -1, -1
//};
//
//int Base58::encode(const unsigned char *bytes, int len, unsigned char *result)
//{
//    unsigned char digits[len * 137 / 100];
//    int digitslen = 1;
//    for (int i = 0; i < len; i++) {
//        unsigned int carry = (unsigned int) bytes[i];
//        for (int j = 0; j < digitslen; j++) {
//            carry += (unsigned int) (digits[j]) << 8;
//            digits[j] = (unsigned char) (carry % 58);
//            carry /= 58;
//        }
//        while (carry > 0) {
//            digits[digitslen++] = (unsigned char) (carry % 58);
//            carry /= 58;
//        }
//    }
//    int resultlen = 0;
//     leading zero bytes
//    for (; resultlen < len && bytes[resultlen] == 0;)
//        result[resultlen++] = '1';
//     reverse
//    for (int i = 0; i < digitslen; i++)
//        result[resultlen + i] = ALPHABET[digits[digitslen - 1 - i]];
//    result[digitslen + resultlen] = 0;
//    return digitslen + resultlen;
//}
//
//int Base58::decode(const unsigned char *str, int len, unsigned char *result)
//{
//    result[0] = 0;
//    int resultlen = 1;
//    for (int i = 0; i < len; i++) {
//        unsigned int carry = (unsigned int) ALPHABET_MAP[str[i]];
//        for (int j = 0; j < resultlen; j++) {
//            carry += (unsigned int) (result[j]) * 58;
//            result[j] = (unsigned char) (carry & 0xff);
//            carry >>= 8;
//        }
//        while (carry > 0) {
//            result[resultlen++] = (unsigned int) (carry & 0xff);
//            carry >>= 8;
//        }
//    }
//
//    for (int i = 0; i < len && str[i] == '1'; i++)
//        result[resultlen++] = 0;
//
//     Poorly coded, but guaranteed to work.
//    for (int i = resultlen - 1, z = (resultlen >> 1) + (resultlen & 1);
//         i >= z; i--) {
//        int k = result[i];
//        result[i] = result[resultlen - i - 1];
//        result[resultlen - i - 1] = k;
//    }
//    return resultlen;
//}
//
std::string Base58::quick_encode(char *data, int length)
{
//    char* _return_ = (char *) calloc(length * 137 / 100, length * 137 / 100);
//    encode((unsigned char *) data, length, (unsigned char *) _return_);
//    std::string string(_return_);
//    free(_return_);
    return encode(data, length);//std::string(string);
}

//int Base58::length(int len)
//{
//    return (len * 137 / 100);
//}
//
std::string Base58::encode(void *data, int length)
{
//    std::string ALPHABET("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");
//
//    int base_count = ALPHABET.length();
//    std::string encoded = "";
//
//    boost::multiprecision::uint1024_t num = ByteUtil::fromBytes1024((char *) data, length);
//
//    while (num >= base_count)
//    {
//        boost::multiprecision::uint1024_t div = num / base_count;
//        boost::multiprecision::uint1024_t mod = (num - (base_count * (div)));
//        encoded = ALPHABET[static_cast<unsigned int>(mod)] + encoded;
//        num = (div);
//    }
//
//    if (num > 0)
//        encoded = ALPHABET[static_cast<unsigned int>(num)] + encoded;

//    std::vector<unsigned char> vector;
//    for (int i = 0; i < length; i ++)
//        vector.push_back((unsigned char) data[i]);//((unsigned char *) data, length);
//
    return EncodeBase58((unsigned char *) data, ((unsigned char *) data) + length);
}

bool Base58::decode(std::string base58, std::vector<unsigned char>& result)
{
//    std::string ALPHABET("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");
//    int len = base58.length();
//    uint1024 decoded = 0;
//    uint1024 multi = 1;
//
//    for (int i = len - 1; i >= 0; i--)
//    {
//        decoded += multi * ALPHABET.find(base58[i]);
//        multi = multi * ALPHABET.length();
//    }

    return DecodeBase58(base58, result);
}
