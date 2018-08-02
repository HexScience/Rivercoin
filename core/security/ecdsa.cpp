////
//// Created by Abdullah Fadhil on 01.08.18.
////
//
//#include "ecdsa.h"
//#include <stdio.h>
//#include <assert.h>
//#include <stdbool.h>
//#include <openssl/sha.h>
//#include <openssl/ssl.h>
//#include <openssl/ec.h>
//#include <openssl/ripemd.h>
//#include <iostream>
//
//Keypair::Keypair(Key P, Key p) : _private_(P), _public_(p) {}
//
//Key* ECDSA::bitcoin_address(Key *_public_)
//{
//    return nullptr;
//}
//
//Keypair* ECDSA::ecdsa_new(const std::string &seed)
//{
//    return nullptr;
//}
//
//Key* ECDSA::derive_public(Key *_private_)
//{
//    return nullptr;
//}
//
//Key *ECDSA::derive_private(const std::string &seed)
//{
//    unsigned char * hash = (unsigned char *) malloc(SHA256_DIGEST_LENGTH);
//    BIGNUM * n = BN_new();
//
//    //first we hash the string
//    SHA256 ((const unsigned char *) seed.c_str(), seed.length(), hash);
//    //then we convert the hash to the BIGNUM n
//    n = BN_bin2bn(hash, SHA256_DIGEST_LENGTH, n);
//
//    BIGNUM * order = BN_new();
//    BIGNUM * nmodorder = BN_new();
//    BN_CTX *bnctx;
//    bnctx = BN_CTX_new();
//
//    //then we create a new EC group with the curve secp256k1
//    EC_GROUP * pgroup = EC_GROUP_new_by_curve_name(NID_secp256k1);
//
//    if (!pgroup) {
//        printf("ERROR: Couldn't get new group\n");
//        return 0;
//    }
//
//    //now we need to get the order of the group, and make sure that
//    //the number we use for the private key is less than or equal to
//    //the group order by using "nmodorder = n % order"
//    EC_GROUP_get_order(pgroup, order, NULL);
//    BN_mod(nmodorder, n, order, bnctx);
//
//    if (BN_is_zero(nmodorder)) {
//        printf("ERROR: SHA256(string) %% order == 0. Pick another string.\n");
//        return 0;
//    }
//
//    int buflen = BN_num_bytes(nmodorder);
//    unsigned char * buf = (unsigned char *) malloc(buflen);
//    int datalen;
//
//    //nmodorder is converted to binary representation
//    datalen = BN_bn2bin(nmodorder, buf);
//
//    std::cout << datalen << " " << buflen << std::endl;
//
////    memcpy(priv_key, buf, datalen);
//
//    free(hash);
//    BN_free(n);
//    BN_free(order);
//    BN_free(nmodorder);
//    EC_GROUP_free(pgroup);
//    BN_CTX_free(bnctx);
//}
