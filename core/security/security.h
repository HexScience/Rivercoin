//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#ifndef RIVERCOIN_CPP_SECURITY_H
#define RIVERCOIN_CPP_SECURITY_H

#include <cstring>
#include "../../external/uint256_t.h"

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

    uint256_t asuint256() const
    {
        uint256_t self = address_[0];

        for(int i = 1; i < ADDRESS_SIZE; i ++)
            self *= address_[i];
        return self;
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

class Wallet{
};

#endif //RIVERCOIN_CPP_SECURITY_H
