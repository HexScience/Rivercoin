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
    template <unsigned short L> struct eckey_t{
        unsigned char key[L];

        eckey_t();
        eckey_t(const eckey_t<L>& o);
        std::string base58();
    };

    template<unsigned short L>
    eckey_t<L>::eckey_t()
    {
    }

    template<unsigned short L>
    eckey_t<L>::eckey_t(const eckey_t<L> &o)
    {
        for (int i = 0; i < L; i ++) key[i] = o.key[i];
    }

    template<unsigned short L>
    std::string eckey_t<L>::base58()
    {
        return Base58::quick_encode((char *) key, L);
    }

    typedef eckey_t<32> eckeypriv_t;
    typedef eckey_t<33> eckeypubl_t;
    typedef eckey_t<25> ecbtcaddr_t;

    struct Keypair{
        eckeypriv_t _private_;
        eckeypubl_t _public_;

        Keypair(eckeypriv_t P, eckeypubl_t p);
        Keypair(const Keypair& o);
        ~Keypair();
        std::shared_ptr<ecbtcaddr_t> getAddress();
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
