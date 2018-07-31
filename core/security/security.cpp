//
// Created by Ragnarr Ivarssen on 19.07.18.
//

#include "security.h"
#include "../base58.h"
#include <openssl/sha.h>

#include <iomanip>
#include <string>
#include <string.h>
#include <iostream>
#include <openssl/pem.h>
#include <openssl/x509.h>
#include "ecad.h"

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

//void ECDSA::generate_private()
//{
//    EC_KEY* key;
//
//    if(NULL == (key = EC_KEY_new_by_curve_name(NID_secp256k1)))
//        return;
//
//    BIGNUM *prv;
//    EC_POINT *pub;
//
//    if(1 != EC_KEY_set_private_key(key, prv)) return;
//    if(1 != EC_KEY_set_public_key(key, pub)) return;
//}

bool Wallet::genKeyPair(char *seed)
{
    char priv_key[64];
    char address[64];

    if (create_address_from_string((unsigned char *) seed, (unsigned char *) address, (unsigned char *) priv_key, true, false, NULL) == 1)
    {
        printf("address: %s\n", address);
        printf("private key: %s\n", priv_key);

        this->address = Address(address);
        this->keypair = Keypair(priv_key);
        return true;
    }
    else
        printf("Something went wrong :(\n");

    return false;
}

Keypair Wallet::getPair()
{
    return keypair;
}

Address Wallet::getAddress()
{
    return address;
}

Wallet::Wallet()
{
}

Key<EC_KEY_SIZE> Keypair::getPrivate()
{
    return priv;
}

Key<EC_KEY_SIZE> Keypair::getPublic()
{
    return publ;
}

Keypair::Keypair(char *priv)
{
    this->priv.set(priv);
}

Keypair::Keypair()
{
}



template<unsigned char T>
void Key<T>::derivePublic()
{
    BIGNUM* bignum = BN_new();

    char debased[64];

    Base58::decode((unsigned char *) key, 64, (unsigned char *) debased);

    BN_bin2bn((unsigned char *) debased + 1, 32, bignum);
    EC_POINT* pub_key;

    EC_GROUP* pgroup = EC_GROUP_new_by_curve_name(NID_secp256k1);

    if (!EC_POINT_mul(pgroup, pub_key, bignum, NULL, NULL, NULL));

    unsigned int bufsize = EC_POINT_point2oct (pgroup, pub_key, POINT_CONVERSION_UNCOMPRESSED, NULL, 0, NULL);
    u_int8_t * buffer = (u_int8_t *) malloc(bufsize);
    //then we place the data in the buffer
    int len = EC_POINT_point2oct (pgroup, pub_key, POINT_CONVERSION_UNCOMPRESSED, buffer, bufsize, NULL);
    if (len == 0) {
        printf("ERROR: Couldn't convert point to octet string.");
    }

    Key<64> pub;

    std::cout << bufsize << " " << T << std::endl;

    pub.set((char *) buffer);

    BN_free(bignum);
    EC_POINT_free(pub_key);
    EC_GROUP_free(pgroup);
    delete(buffer);

//    return pub;
}

template<unsigned char T>
void Key<T>::sign(char *data, unsigned int length, char *out)
{
}

template<unsigned char T>
bool Key<T>::verify(char *signature, unsigned int length, char *data, unsigned int dlength) {
    return false;
}

template<unsigned char T>
bool Key<T>::empty()
{
    for (int i = 0; i < T; i ++)
        if (key[i] != 0) return false;

    return true;
}

template<unsigned char T>
void Key<T>::set(char *m)
{
    memcpy(key, m, T);
}

template<unsigned char T>
Key<T>::Key()
{
    for (int i = 0; i < T; i++) key[i] = 0;
}