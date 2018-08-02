//
// Created by Abdullah Fadhil on 01.08.18.
//

#ifndef RIVERCOIN_CPP_ECDSA_H
#define RIVERCOIN_CPP_ECDSA_H

#include <string>

#define NETWORK_ADDRESS_PREFIX 0
#define TESTNET_ADDRESS_PREFIX 1

namespace ECDSA{
    template <unsigned short L> struct eckey_t{
        unsigned char key[L];

        eckey_t();
        eckey_t(const eckey_t<L>& o);
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

    typedef eckey_t<32> eckeypriv_t;
    typedef eckey_t<33> eckeypubl_t;
    typedef eckey_t<25> ecbtcaddr_t;

    struct Keypair{
        eckeypriv_t _private_;
        eckeypubl_t _public_;

        Keypair(eckeypriv_t P, eckeypubl_t p);
        Keypair(const Keypair& o);
        ~Keypair();
    };
    ecbtcaddr_t*        bitcoin_address(eckeypubl_t* _public_, unsigned char PREFIX);
    Keypair*            ecdsa_new(const std::string& seed);
    eckeypriv_t*        derive_private(const std::string& seed);
    eckeypubl_t*        derive_public(eckeypriv_t* _private_);
}

#endif //RIVERCOIN_CPP_ECDSA_H
