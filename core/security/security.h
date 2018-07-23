//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#ifndef RIVERCOIN_CPP_SECURITY_H
#define RIVERCOIN_CPP_SECURITY_H

#include <cstring>
#include "../math/math.h"
#include <vector>
#include <openssl/ecdsa.h>

#define MAIN_NETWORK_HEADER 0
#define TEST_NETWORK_HEADER 1
#define ADDRESS_SIZE 25
#define CONTRACT_ADDRESS_SIZE 37
#define DEFAULT_ADDRESS "1NwxDTT8gywVMmALW3n98sWTCoWyBNWZhQ"

class CompressedPublicKey{
};

class Address{
private:
     char address_[ADDRESS_SIZE];
public:
    Address()
    {
        setAddress(DEFAULT_ADDRESS);
    }
    Address(const char* address) {setAddress(address);}
    Address(CompressedPublicKey key) {setAddress(DEFAULT_ADDRESS);}
    Address(const Address& o) {setAddress(o.address_);}
    static bool __check_address_valid(const char* addr_);
    bool compare(const Address& o) const;
    uint256 asuint256() const;
    bool operator< (const Address& a) const;
    bool operator> (const Address& a) const;
    bool operator== (const Address& a) const;
    bool operator<= (const Address& a) const;
    bool operator>= (const Address& a) const;
    const char* addr();
    void setAddress(const char address[ADDRESS_SIZE]);
};

//class PublicKey{
//private:
//    EC_KEY* key;
//public:
//    Address asAddress();
//    CompressedPublicKey getCompressed();
//    template <typename T> std::vector sign(T& o);
//
//    ~PublicKey()
//    {
//        delete key;
//    }
//};
//
//class PrivateKey{
//private:
//    EC_KEY* key;
//public:
//};
//
//class Keypair{
//private:
//    PublicKey publicKey;
//    PrivateKey privateKey;
//public:
//    Keypair(PrivateKey a, PublicKey b) : publicKey(b), privateKey(a) {}
//    Keypair(const char* prng)
//    {
//    }
//};

class Wallet{
private:
    EC_KEY* publicKey;
    EC_KEY* privateKey;
public:
    Wallet()
    {
    }
};

#endif //RIVERCOIN_CPP_SECURITY_H
