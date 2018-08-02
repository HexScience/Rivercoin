//
// Created by Abdullah Fadhil on 01.08.18.
//

#include "ecdsa.h"
#include "../base58.h"
#include <stdio.h>
#include <assert.h>
#include <stdbool.h>
#include <openssl/sha.h>
#include <openssl/ssl.h>
#include <openssl/ec.h>
#include <openssl/ripemd.h>
#include <iostream>

ECDSA::Keypair::Keypair(eckeypriv_t P, eckeypubl_t p) : _private_(P), _public_(p)
{
}

ECDSA::Keypair::Keypair(const ECDSA::Keypair &o) : _private_(o._private_), _public_(o._public_)
{
}

ECDSA::Keypair::~Keypair()
{
}

std::shared_ptr<ECDSA::ecbtcaddr_t> ECDSA::Keypair::getAddress()
{
    ECDSA::ecbtcaddr_t* ecbtcaddr = ECDSA::bitcoin_address(&_public_, NETWORK_ADDRESS_PREFIX);

    if (ecbtcaddr) return std::shared_ptr<ECDSA::ecbtcaddr_t>(ecbtcaddr);

    return std::shared_ptr<ECDSA::ecbtcaddr_t>(new ECDSA::ecbtcaddr_t);
}

ECDSA::ecbtcaddr_t* ECDSA::bitcoin_address(eckeypubl_t *_public_, unsigned char PREFIX)
{
    ecbtcaddr_t* address = 0;
    EC_GROUP * pgroup = EC_GROUP_new_by_curve_name(NID_secp256k1);

    EC_POINT * ecpoint = EC_POINT_new(pgroup);


    if (!EC_POINT_oct2point(pgroup, ecpoint, (unsigned char *) _public_, 33, NULL))
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

    memcpy(address, final_address, 25);

    EC_POINT_free(ecpoint);
    EC_GROUP_free(pgroup);
    free(buffer);

    return address;
}

ECDSA::Keypair* ECDSA::ecdsa_new(const std::string &seed)
{
    eckeypriv_t* priv = derive_private(seed);
    eckeypubl_t* publ = derive_public(priv);

    eckeypriv_t key_a = eckeypriv_t(*priv);
    eckeypubl_t key_b = eckeypubl_t(*publ);

    delete (priv);
    delete (publ);

    return new ECDSA::Keypair(key_a, key_b);
}

ECDSA::eckeypubl_t* ECDSA::derive_public(eckeypriv_t *_private_)
{
    eckeypubl_t* publ = 0;
    EC_GROUP * pgroup = EC_GROUP_new_by_curve_name(NID_secp256k1);

    BIGNUM * nmodorder = BN_new();
    nmodorder = BN_bin2bn((unsigned char *) _private_, SHA256_DIGEST_LENGTH, nmodorder);

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

//    std::cout << len << " public " << std::endl;

    publ = new eckeypubl_t();

    memcpy(publ, buffer, len);

    BN_free(nmodorder);
    EC_POINT_free(ecpoint);
    EC_GROUP_free(pgroup);
    free(buffer);

    return publ;
}

ECDSA::eckeypriv_t *ECDSA::derive_private(const std::string &seed)
{
    unsigned char * hash = (unsigned char *) malloc(SHA256_DIGEST_LENGTH);
    BIGNUM * n = BN_new();

    //first we hash the string
    SHA256 ((const unsigned char *) seed.c_str(), seed.length(), hash);
    //then we convert the hash to the BIGNUM n
    n = BN_bin2bn(hash, SHA256_DIGEST_LENGTH, n);

    BIGNUM * order = BN_new();
    BIGNUM * nmodorder = BN_new();
    BN_CTX *bnctx;
    bnctx = BN_CTX_new();

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
    unsigned char * buf = (unsigned char *) malloc(buflen);
    int datalen;

    //nmodorder is converted to binary representation
    datalen = BN_bn2bin(nmodorder, buf);

    eckeypriv_t *priv = new eckeypriv_t();

    memcpy(priv, buf, datalen);

    free(hash);
    BN_free(n);
    BN_free(order);
    BN_free(nmodorder);
    EC_GROUP_free(pgroup);
    BN_CTX_free(bnctx);

    return priv;
}
