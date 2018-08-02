//
// Created by Ragnarr Ivarssen on 31.07.18.
//

#ifndef RIVERCOIN_CPP_ECAD_H
#define RIVERCOIN_CPP_ECAD_H

//
// Created by Ragnarr Ivarssen on 31.07.18.
//
#include <stdio.h>
#include <assert.h>
#include <stdbool.h>
#include <openssl/sha.h>
#include <openssl/ssl.h>
#include <openssl/ec.h>
#include <openssl/ripemd.h>

int create_address_from_string(const unsigned char *string,
                unsigned char *address,
                unsigned char *priv_key,
                bool base58,
                bool debug,
                EC_GROUP *precompgroup);
void print_hex(u_int8_t * buffer, unsigned int len);
void base58_encode(unsigned char *data, unsigned int len, char *result);
void prepare_for_address(unsigned char *data, int datalen, char start_byte);

#endif //RIVERCOIN_CPP_ECAD_H
