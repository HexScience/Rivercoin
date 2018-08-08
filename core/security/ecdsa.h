//
// Created by Abdullah Fadhil on 01.08.18.
//

#ifndef RIVERCOIN_CPP_ECDSA_H
#define RIVERCOIN_CPP_ECDSA_H

#include <string>
#include "../base58.h"

#define NETWORK_ADDRESS_PREFIX 0
#define TESTNET_ADDRESS_PREFIX 1

namespace ECDSA{
//    template <size_t L> struct eckey_t{
//        unsigned char key[L];
//
//        eckey_t();
//        eckey_t(const eckey_t<L>& o);
//        std::string base58();
//    };
//
//    template<size_t L>
//    eckey_t<L>::eckey_t()
//    {
//        for (int i = 0; i < L; i ++) key[i] = 0;
//    }
//
//    template<size_t L>
//    eckey_t<L>::eckey_t(const eckey_t<L> &o)
//    {
//        for (int i = 0; i < L; i ++) key[i] = o.key[i];
//    }
//
//    template<size_t L>
//    std::string eckey_t<L>::base58()
//    {
//        return Base58::quick_encode((char *) key, L);
//    }

#define PRIVATE_KEY_SIZE 38
#define PUBLIC_KEY_SIZE 39
#define ADDRESS_KEY_SIZE 25

    struct eckeypriv_t
    {
        unsigned char * key;

        eckeypriv_t();
        eckeypriv_t(const eckeypriv_t& o);
        std::string base58();
        ~eckeypriv_t();
    };

    struct eckeypubl_t
    {
        unsigned char * key;

        eckeypubl_t();
        eckeypubl_t(const eckeypriv_t& o);
        std::string base58();
        ~eckeypubl_t();
    };

    struct ecbtcaddr_t
    {
        unsigned char * key;

        ecbtcaddr_t();
        ecbtcaddr_t(const ecbtcaddr_t& o);
        std::string base58();
        ~ecbtcaddr_t();
    };

    struct Keypair{
        eckeypriv_t* _private_;
        eckeypubl_t* _public_;
        ecbtcaddr_t* _addrss_;

        Keypair(eckeypriv_t* P, eckeypubl_t* p, ecbtcaddr_t* a);
        Keypair(const Keypair& o);
        ~Keypair();
        ecbtcaddr_t* getAddress();
    };
    ecbtcaddr_t*        bitcoin_address(eckeypubl_t* _public_, unsigned char PREFIX);
    Keypair*            ecdsa_new(const std::string& seed);
    eckeypriv_t*        derive_private(const std::string& seed);
    eckeypubl_t*        derive_public(eckeypriv_t* _private_);
}

typedef ECDSA::Keypair*     eckeypair_t;
typedef ECDSA::eckeypriv_t* ecdsapriv_t;
typedef ECDSA::eckeypubl_t* ecdsapubl_t;
typedef ECDSA::ecbtcaddr_t* ecdsaaddr_t;

#endif //RIVERCOIN_CPP_ECDSA_H
