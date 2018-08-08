//
// Created by Abdullah Fadhil on 01.08.18.
//

#include "ecdsa.h"
#include "../base58.h"
#include "../math/math.h"
#include <stdio.h>
#include <assert.h>
#include <stdbool.h>
#include <openssl/sha.h>
#include <openssl/ssl.h>
#include <openssl/ec.h>
#include <openssl/ripemd.h>
#include <iostream>

ECDSA::Keypair::Keypair(eckeypriv_t* P, eckeypubl_t* p, ecbtcaddr_t* a) : _private_(P), _public_(p), _addrss_(a)
{
}

ECDSA::Keypair::Keypair(const ECDSA::Keypair &o) : _private_(o._private_), _public_(o._public_), _addrss_(o._addrss_)
{
}

ECDSA::Keypair::~Keypair()
{
    delete (_private_);
    delete (_public_);
    delete (_addrss_);
}

#ifndef __TEMP_METHODS_ECDSA_ADDRESSING__
#define __TEMP_METHODS_ECDSA_ADDRESSING__

bool encapsulated(unsigned char * key, unsigned char length)
{
    unsigned char* CHECKSUM_A = (unsigned char *) calloc(32, 32);
    unsigned char* CHECKSUM_B = (unsigned char *) calloc(32, 32);

    SHA256(key + 2, length - 6, CHECKSUM_A);
    SHA256(CHECKSUM_A, 32, CHECKSUM_B);
//    std::cout << std::endl;
//
//    for (int i = 0; i < length; i ++)
//        std::cout << (int) key[i] << " ";
//
//    std::cout << std::endl;
//
//    for (int i = 0; i < 4; i ++)
//        std::cout << (int) CHECKSUM_B[i] << " ";
//
//        std::cout << std::endl;

    for (int i = 0; i < 4; i ++)
        if (key[(length - 4) + i] != CHECKSUM_B[i])
        {
            free (CHECKSUM_A);
            free (CHECKSUM_B);
            return false;
        }

    free (CHECKSUM_A);
    free (CHECKSUM_B);

    return true;
}

void encapsulate(unsigned char* data, unsigned char length, unsigned short HEADER, unsigned char* out)
{
    unsigned char* CHECKSUM_A = (unsigned char *) calloc(32, 32);
    unsigned char* CHECKSUM_B = (unsigned char *) calloc(32, 32);

    SHA256(data, length, CHECKSUM_A);
    SHA256(CHECKSUM_A, 32, CHECKSUM_B);

    out[0] = ((unsigned char *) &HEADER)[0];
    out[1] = ((unsigned char *) &HEADER)[1];

    memcpy(out + 2, data, length);
    out[2 + length] = CHECKSUM_B[0];
    out[3 + length] = CHECKSUM_B[1];
    out[4 + length] = CHECKSUM_B[2];
    out[5 + length] = CHECKSUM_B[3];

    free (CHECKSUM_A);
    free (CHECKSUM_B);
}

#endif

ECDSA::ecbtcaddr_t* ECDSA::Keypair::getAddress()
{
    ECDSA::ecbtcaddr_t* ecbtcaddr = ECDSA::bitcoin_address(_public_, NETWORK_ADDRESS_PREFIX);

    if (ecbtcaddr) return ecbtcaddr;
}

ECDSA::ecbtcaddr_t* ECDSA::bitcoin_address(eckeypubl_t *_public_, unsigned char PREFIX)
{
    if (_public_ == nullptr) return nullptr;

    if (!encapsulated(_public_->key, PUBLIC_KEY_SIZE))
    {
        std::cout << "incorrect public key format, are you using a raw public key?" << std::endl;
        exit(0);
    }

    ecbtcaddr_t* address = nullptr;
    EC_GROUP * pgroup = EC_GROUP_new_by_curve_name(NID_secp256k1);

    EC_POINT * ecpoint = EC_POINT_new(pgroup);

    if (!EC_POINT_oct2point(pgroup, ecpoint, _public_->key + 2, 33, NULL))
    {
        printf("ERROR: Couldn't multiply the generator point with n\n");

        EC_POINT_free(ecpoint);
        EC_GROUP_free(pgroup);

        return address;
    }

    unsigned int bufsize = EC_POINT_point2oct (pgroup, ecpoint, POINT_CONVERSION_UNCOMPRESSED, NULL, 0, NULL);
    unsigned char * buffer = (unsigned char *) malloc(bufsize);
    //then we place the data in the buffer
    int len = EC_POINT_point2oct (pgroup, ecpoint, POINT_CONVERSION_UNCOMPRESSED, buffer, bufsize, NULL);
    if (len == 0) {
        printf("ERROR: Couldn't convert point to octet string.");

        EC_POINT_free(ecpoint);
        EC_GROUP_free(pgroup);
        free(buffer);

        return address;
    }

//    char hash[100];
//
//    Base58::encode(buffer, bufsize, (unsigned char *) hash);
//
//    std::cout << hash << " address : " << std::endl;

    address = new ecbtcaddr_t();

    char final_address[25];
    final_address[0] = PREFIX;

    unsigned char sha256[32];

    SHA256(buffer, len, sha256);
    char sha_md[20];

    RIPEMD160(sha256, 32, (unsigned char *) sha_md);

    memcpy(final_address + 1, sha_md, 20);

    SHA256((unsigned char *) final_address, 21, sha256);
    unsigned char checksum[32];
    SHA256(sha256, 32, checksum);

    memcpy(final_address + 21, checksum, 4);

    memcpy(address->key, final_address, 25);

    EC_POINT_free(ecpoint);
    EC_GROUP_free(pgroup);
    free(buffer);

    return address;
}

ECDSA::Keypair* ECDSA::ecdsa_new(const std::string &seed)
{
    eckeypriv_t* priv = derive_private(seed);
    eckeypubl_t* publ = derive_public(priv);

    if (priv == NULL || publ == NULL) return NULL;

    return new ECDSA::Keypair(priv, publ, bitcoin_address(publ, NETWORK_ADDRESS_PREFIX));
}

ECDSA::eckeypubl_t* ECDSA::derive_public(eckeypriv_t *_private_)
{
    eckeypubl_t* publ = nullptr;

    if (_private_ == nullptr) return publ;

    unsigned short version_ = ((unsigned short *) _private_->key)[0];

    if (!encapsulated(_private_->key, PRIVATE_KEY_SIZE))
    {
        std::cout << "incorrect private key format, are you using a raw private key?" << std::endl;
        exit(0);
    }

    EC_GROUP * pgroup = EC_GROUP_new_by_curve_name(NID_secp256k1);

    BIGNUM * nmodorder = BN_new();
    nmodorder = BN_bin2bn(_private_->key + 2, SHA256_DIGEST_LENGTH, nmodorder);

    EC_POINT * ecpoint = EC_POINT_new(pgroup);

    if (!EC_POINT_mul(pgroup, ecpoint, nmodorder, NULL, NULL, NULL))
    {
        printf("ERROR: Couldn't multiply the generator point with n\n");

        BN_free(nmodorder);
        EC_POINT_free(ecpoint);
        EC_GROUP_free(pgroup);

        return publ;
    }

//    {
//        unsigned int bufsize = EC_POINT_point2oct (pgroup, ecpoint, POINT_CONVERSION_UNCOMPRESSED, NULL, 0, NULL);
//        unsigned char * buffer = (unsigned char *) malloc(bufsize);
//        //then we place the data in the buffer
//        int len = EC_POINT_point2oct (pgroup, ecpoint, POINT_CONVERSION_UNCOMPRESSED, buffer, bufsize, NULL);
//        if (len == 0) {
//            printf("ERROR: Couldn't convert point to octet string.");
//
//            BN_free(nmodorder);
//            EC_POINT_free(ecpoint);
//            EC_GROUP_free(pgroup);
//            free(buffer);
//
//            return publ;
//        }
//
//        char hash[100];
//
//        Base58::encode(buffer, len, (unsigned char *) hash);
//
//        std::cout << hash << std::endl;
//    }

    unsigned int bufsize = EC_POINT_point2oct (pgroup, ecpoint, POINT_CONVERSION_COMPRESSED, NULL, 0, NULL);
    unsigned char * buffer = (unsigned char *) malloc(bufsize);
    //then we place the data in the buffer
    int len = EC_POINT_point2oct (pgroup, ecpoint, POINT_CONVERSION_COMPRESSED, buffer, bufsize, NULL);
    if (len == 0) {
        printf("ERROR: Couldn't convert point to octet string.");

        BN_free(nmodorder);
        EC_POINT_free(ecpoint);
        EC_GROUP_free(pgroup);
        free(buffer);

        return publ;
    }

    publ = new eckeypubl_t();

    unsigned short version = version_;

    encapsulate(buffer, len, version, publ->key);

    BN_free(nmodorder);
    EC_POINT_free(ecpoint);
    EC_GROUP_free(pgroup);
    free(buffer);

    return publ;
}

ECDSA::eckeypriv_t *ECDSA::derive_private(const std::string &seed)
{
    unsigned char * hash = (unsigned char *) calloc(SHA256_DIGEST_LENGTH, SHA256_DIGEST_LENGTH);
    BIGNUM * n = BN_new();

    //first we hash the string
    SHA256 ((const unsigned char *) seed.c_str(), seed.length(), hash);
    //then we convert the hash to the BIGNUM n
    n = BN_bin2bn(hash, SHA256_DIGEST_LENGTH, n);

    BIGNUM * order = BN_new();
    BIGNUM * nmodorder = BN_new();
    BN_CTX *bnctx = BN_CTX_new();

    //then we create a new EC group with the curve secp256k1
    EC_GROUP * pgroup = EC_GROUP_new_by_curve_name(NID_secp256k1);

    if (!pgroup) {
        printf("ERROR: Couldn't get new group\n");
        return 0;
    }

    //now we need to get the order of the group, and make sure that
    //the number we use for the private eckey_t is less than or equal to
    //the group order by using "nmodorder = n % order"
    EC_GROUP_get_order(pgroup, order, NULL);
    BN_mod(nmodorder, n, order, bnctx);

    if (BN_is_zero(nmodorder)) {
        printf("ERROR: SHA256(string) %% order == 0. Pick another string.\n");
        return 0;
    }

    int buflen = BN_num_bytes(nmodorder);
    unsigned char * buf = (unsigned char *) calloc(buflen, buflen);

    //nmodorder is converted to binary representation
    int datalen = BN_bn2bin(nmodorder, buf);

    unsigned short version = 0 | NETWORK_ADDRESS_PREFIX;
    eckeypriv_t *priv = new eckeypriv_t();

    encapsulate(buf, datalen, version, priv->key);

    free(hash);
    BN_free(n);
    BN_free(order);
    BN_free(nmodorder);
    EC_GROUP_free(pgroup);
    BN_CTX_free(bnctx);

    return priv;
}

ECDSA::eckeypriv_t::eckeypriv_t() : key((unsigned char *) calloc(PRIVATE_KEY_SIZE, PRIVATE_KEY_SIZE))
{
}

ECDSA::eckeypriv_t::eckeypriv_t(const eckeypriv_t &o) : key((unsigned char *) calloc(PRIVATE_KEY_SIZE, PRIVATE_KEY_SIZE))
{
    memcpy(key, o.key, PRIVATE_KEY_SIZE);
}

std::string ECDSA::eckeypriv_t::base58()
{
    return Base58::quick_encode((char *) key, PRIVATE_KEY_SIZE);
}

ECDSA::eckeypriv_t::~eckeypriv_t()
{
    free (key);
}

ECDSA::eckeypubl_t::eckeypubl_t() : key((unsigned char *) calloc(PUBLIC_KEY_SIZE, PUBLIC_KEY_SIZE))
{
}

ECDSA::eckeypubl_t::eckeypubl_t(const eckeypriv_t &o) : key((unsigned char *) calloc(PUBLIC_KEY_SIZE, PUBLIC_KEY_SIZE))
{
    memcpy(key, o.key, PUBLIC_KEY_SIZE);
}

std::string ECDSA::eckeypubl_t::base58()
{
    return Base58::quick_encode((char *) key, PUBLIC_KEY_SIZE);
}

ECDSA::eckeypubl_t::~eckeypubl_t()
{
    free (key);
}

ECDSA::ecbtcaddr_t::ecbtcaddr_t() : key((unsigned char *) calloc(ADDRESS_KEY_SIZE, ADDRESS_KEY_SIZE))
{
}

ECDSA::ecbtcaddr_t::ecbtcaddr_t(const ECDSA::ecbtcaddr_t &o) : key((unsigned char *) calloc(ADDRESS_KEY_SIZE, ADDRESS_KEY_SIZE))
{
    memcpy(key, o.key, ADDRESS_KEY_SIZE);
}

std::string ECDSA::ecbtcaddr_t::base58()
{
    return Base58::quick_encode((char *) key, ADDRESS_KEY_SIZE);
}

ECDSA::ecbtcaddr_t::~ecbtcaddr_t()
{
    free (key);
}
