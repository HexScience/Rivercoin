//
// Created by Ragnarr Ivarssen on 09.07.18.
//

#ifndef RIVERCOIN_CPP_SECURITY_H
#define RIVERCOIN_CPP_SECURITY_H

#include <cstring>
#include "../math/math.h"
#include <vector>

#define MAIN_NETWORK_HEADER 0
#define TEST_NETWORK_HEADER 1
#define ADDRESS_SIZE 25
#define CONTRACT_ADDRESS_SIZE 37
#define DEFAULT_ADDRESS "1NwxDTT8gywVMmALW3n98sWTCoWyBNWZhQ"

class CompressedPublicKey{
public:
    bool operator== (const CompressedPublicKey& o) const
    {
        return false;
    }

    bool operator!= (const CompressedPublicKey& o) const
    {
        return !(*this == o);
    }
};

class Address{
private:
     char address_[ADDRESS_SIZE];
public:
    Address()
    {
        setAddress(DEFAULT_ADDRESS);
    }
    Address(const char* address) {setAddress(address);}
    Address(CompressedPublicKey key) {setAddress(DEFAULT_ADDRESS);}
    Address(const Address& o) {setAddress(o.address_);}
    static bool __check_address_valid(const char* addr_, bool DECODE_B58 = true);
    bool compare(const Address& o) const;
    uint256 asuint256() const;
    bool operator< (const Address& a) const;
    bool operator> (const Address& a) const;
    bool operator== (const Address& a) const;
    bool operator<= (const Address& a) const;
    bool operator>= (const Address& a) const;
    const char* addr();
    void setAddress(const char address[ADDRESS_SIZE]);
};

#define EC_KEY_SIZE 65

//template <unsigned char T>
struct Key{
    char key[EC_KEY_SIZE];
    Key();
    void set(char m[EC_KEY_SIZE]);
    bool empty();
    bool verify(char* signature, unsigned int length, char* data, unsigned int dlength);
    void sign(char* data, unsigned int length, char* out);
    void derivePublic();
};

class Keypair{
private:
    Key priv;
    Key publ;
public:
    Keypair();
    Keypair(char priv[EC_KEY_SIZE]);
    Key getPublic();
    Key getPrivate();
};

class Wallet{
private:
    Keypair     keypair;
    Address     address;
public:
    Wallet();
    bool genKeyPair(char* seed);
    Keypair getPair();
    Address getAddress();
};

#endif //RIVERCOIN_CPP_SECURITY_H
