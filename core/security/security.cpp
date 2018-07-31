//
// Created by Ragnarr Ivarssen on 19.07.18.
//

#include "security.h"
#include "../base58.h"
#include <openssl/sha.h>

bool Address::operator< (const Address& a) const
{
    return asuint256() < a.asuint256();
}

bool Address::operator>(const Address &a) const
{
    return asuint256() > a.asuint256();
}

bool Address::operator== (const Address& a) const
{
    return asuint256() == a.asuint256();
}

bool Address::operator<= (const Address& a) const
{
    return asuint256() <= a.asuint256();
}

bool Address::operator>= (const Address& a) const
{
    return asuint256() >= a.asuint256();
}

uint256 Address::asuint256() const
{
    return ByteUtil::fromBytes256(address_, ADDRESS_SIZE);
}

bool Address::compare(const Address &o) const
{
    for (int i = 0; i < ADDRESS_SIZE; i ++)
        if(address_[i] != o.address_[i]) return false;

    return true;
}

bool Address::__check_address_valid(const char *addr_, bool DECODE_B58)
{
    const unsigned char NETWORK_ADDRESS = 0;

    char ADDRESS[ADDRESS_SIZE];

    if (DECODE_B58)
        Base58::decode((unsigned char *)addr_, strlen(addr_), (unsigned char *)ADDRESS);

    if (ADDRESS[0] != NETWORK_ADDRESS) return false;

    char CHECKSUM0[32];
    char CHECKSUM[32];

    SHA256((unsigned char *)ADDRESS, 21, (unsigned char *)CHECKSUM0);
    SHA256((unsigned char *)CHECKSUM0, 32, (unsigned char *)CHECKSUM);

    for (int i = 0; i < 4; i ++)
        if (ADDRESS[ADDRESS_SIZE - 4 + i] != CHECKSUM[i]) return false;

    return true;
}

const char* Address::addr()
{
    return address_;
}

void Address::setAddress(const char *address)
{
    for (int i = 0; i < ADDRESS_SIZE; i ++)
        address_[i] = address[i];
}

#include <iomanip>
#include <string>
#include <string.h>
#include <iostream>
#include <openssl/pem.h>
#include <openssl/x509.h>

void ECDSA::generate_private()
{
    EC_KEY* key;

    if(NULL == (key = EC_KEY_new_by_curve_name(NID_secp256k1)))
        return;

    BIGNUM *prv;
    EC_POINT *pub;

    if(1 != EC_KEY_set_private_key(key, prv)) return;
    if(1 != EC_KEY_set_public_key(key, pub)) return;

    std::ostream s();
}
