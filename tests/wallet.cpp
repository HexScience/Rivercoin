//
// Created by Abdullah Fadhil on 03.08.18.
//

#include <iostream>
#include <string>
#include "../core/security/ecdsa.h";

int wallet_test(int arg_l, const char* args[])
{
    ECDSA::Keypair* my_first_keypair = ECDSA::ecdsa_new("my cool wallet seed");

    const std::string my_address = my_first_keypair->getAddress()->base58();

    std::cout <<
    "my address: " <<
    my_address <<
    std::endl;

    return 0;
}