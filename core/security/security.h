//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#ifndef RIVERCOIN_CPP_SECURITY_H
#define RIVERCOIN_CPP_SECURITY_H

#include <cstring>
#include "../math/math.h"
#include <vector>

#define MAIN_NETWORK_HEADER 0
#define TEST_NETWORK_HEADER 1
#define ADDRESS_SIZE 25

class CompressedPublicKey{
};

class Address{
private:
    const char* address_;
public:
    Address() : address_("0xC")
    {
    }
    Address(const char* address) : address_(address) {}
    Address(CompressedPublicKey key) : address_("0xC")
    {
    }
    Address(const Address& o) : address_(o.address_) {}
    static bool __check_address_valid(const char* addr_)
    {
        return false;
    }

    bool compare(const Address& o) const
    {
        for (int i = 0; i < ADDRESS_SIZE; i ++)
            if(address_[i] != o.address_[i]) return false;

        return true;
    }

    u_int256 asuint256() const
    {
        return uint256::fromBytes256(address_, ADDRESS_SIZE);
    }

    bool operator< (const Address& a)
    {
        return asuint256() < a.asuint256();
    }

    bool operator> (const Address& a)
    {
        return asuint256() > a.asuint256();
    }

    bool operator== (const Address& a)
    {
        return asuint256() == a.asuint256();
    }

    bool operator<= (const Address& a)
    {
        return asuint256() <= a.asuint256();
    }

    bool operator>= (const Address& a)
    {
        return asuint256() >= a.asuint256();
    }
};

class PublicKey{
public:
    Address asAddress();
    CompressedPublicKey getCompressed();
    template <typename T> std::vector sign(T& o);
};

class PrivateKey{
};

class Keypair{
private:
    PublicKey publicKey;
    PrivateKey privateKey;
public:
    Keypair(PrivateKey a, PublicKey b) : publicKey(b), privateKey(a) {}
};

class Wallet{
};

#endif //RIVERCOIN_CPP_SECURITY_H
