#include <iostream>

#include "defines.h"
#include "logger.h"
#include "rivercoin.h"
#include <string>
#include "context.h"
#include <boost/property_tree/xml_parser.hpp>
#include <boost/property_tree/ptree.hpp>
#include <istream>
#include "fileutils.h"
#include "config.h"
#include "math/riverhash.h"
#include "base58.h"
#include "compute/MochaPP/VM/instructions.h"
#include <ctype.h>
#include <openssl/conf.h>
#include <openssl/evp.h>
#include <openssl/err.h>

#include <openssl/ecdsa.h>
#include <openssl/ec.h>
#include <openssl/engine.h>
#include <openssl/asn1.h>
#include <openssl/crypto.h>

#include "compute/MochaPP/VM/tinyvm.h"
#include "block.h"
#include "security/ecad.h"
//#include "network.h"

int ack(int m, int n)
{
    if (m == 0) return n+1;
    if (n == 0) return ack( m - 1, 1 );
    return ack( m - 1, ack( m, n - 1 ) );
}

std::string toHex58(const char * data, int length)
{
    char result[length * 2];
    unsigned int rlength = Base58::encode((unsigned char *)data, length, (unsigned char *)result);

    return std::string(result).substr(0, rlength);
}

std::string fmHex58(const char * data, int length)
{
    char result[length];
    unsigned int rlength = Base58::decode((unsigned char *)data, length, (unsigned char *)result);

    return std::string(result).substr(0, rlength);
}

std::string toHex(const char * data, int length)
{
    std::string string = "";
    char const hex_chars[16] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    for( int i = 0; i < length; i ++)
    {
        char const byte = data[i];

        string += hex_chars[ ( byte & 0xF0 ) >> 4 ];
        string += hex_chars[ ( byte & 0x0F ) >> 0 ];
    }

    return string;
}

static int char2int(char input)
{
    if(input >= '0' && input <= '9')
        return input - '0';
    if(input >= 'A' && input <= 'F')
        return input - 'A' + 10;
    if(input >= 'a' && input <= 'f')
        return input - 'a' + 10;
    throw std::invalid_argument("Invalid input string");
}

// This function assumes src to be a zero terminated sanitized string with
// an even number of [0-9a-f] characters, and target to be sufficiently large
static void hexToBytes(const char* src, char* target)
{
    while(*src && src[1])
    {
        *(target++) = char2int(*src)*16 + char2int(src[1]);
        src += 2;
    }
}

static void hexStringToByteArray(std::string s, const char* data)
{
}

static void address_test()
{
//    unsigned char message[32] = "p1assword";
//    unsigned char address[64];
//    unsigned char priv_key[64];
//
//    if (create_address_from_string(message, address, priv_key, true, false, NULL) == 1)
//    {
//        printf("address: %s\n", address);
//        printf("private key: %s\n", priv_key);
//    }
//    else {
//        printf("Something went wront :(\n");
//    }

    Wallet wallet;

    wallet.genKeyPair("hello world");
    wallet.getPair().getPrivate().derivePublic();
}

int main(int arg_l, const char* args[]) {
    logger::alert("----------------------------------------");

    /* Load the human readable error strings for libcrypto */
    ERR_load_crypto_strings();

    /* Load all digest and cipher algorithms */
    OpenSSL_add_all_algorithms();

    /* Load config file, and other important initialisation */
    OPENSSL_config(NULL);

    using boost::property_tree::ptree;

//    logger::alert(std::to_string(ack(8, 3)).c_str());

    address_test();

    unsigned char ERROR = 0;

    ptree pTree;
    std::istringstream istringstream(file::readUTF(std::string(".") + PATH_SEPARATOR + "structure" + PATH_SEPARATOR + "config.xml", ERROR));

    boost::property_tree::read_xml(istringstream, pTree);

    Context* context = new Context(pTree);

        Block block(150, 125124, context->getDifficulty(), context);
        char out[32];

        hexToBytes("000A13334EAADBD5513E67397611CE4D22D434577161FC6EDBA6F3E6DA8ECCCD", out);

        RiverHash::mine(RiverHash::RiverHash_256_variant, block.toStoredBlock().get(), sizeof(StoredBlock), out, ByteUtil::fromBytes256(out));

    /* Clean up */

    /* Removes all digests and ciphers */
    EVP_cleanup();

    /* if you omit the next, a small leak may be left when you make use of the BIO (low level API) for e.g. base64 transformations */
    CRYPTO_cleanup_all_ex_data();

    /* Remove error strings */
    ERR_free_strings();

    return 0;
}