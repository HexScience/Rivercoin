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

#include "compute/MochaPP/VM/tinyvm.h"
#include "block.h"
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

int char2int(char input)
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
void hexToBytes(const char* src, char* target)
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

int main(int arg_l, const char* args[]) {
//    logger::alert("----------------------------------------");
//    rvc my_balance = rvc(50.05020102);
//    logger::alert(std::string(std::string("Mining Address: ") + mining_address).c_str());
//    logger::alert("if address is incorrect, please restart the client.");
//

    /* Load the human readable error strings for libcrypto */
    ERR_load_crypto_strings();

    /* Load all digest and cipher algorithms */
    OpenSSL_add_all_algorithms();

    /* Load config file, and other important initialisation */
    OPENSSL_config(NULL);

    using boost::property_tree::ptree;

//    logger::alert(std::to_string(ack(8, 3)).c_str());

    unsigned char ERROR = 0;

    ptree pTree;
    std::istringstream istringstream(file::readUTF(std::string(".") + PATH_SEPARATOR + "structure" + PATH_SEPARATOR + "config.xml", ERROR));

    boost::property_tree::read_xml(istringstream, pTree);

    Context* context = new Context(pTree);

//    char buf[32];
//    char dta[] = "hello world test";
//
//    unsigned long long nonce = 1000000;
//
//    unsigned long long now = context->timestamp();
//
//    RiverHash::apply_cpu_variant(dta, nonce, sizeof(dta), buf);
//
//    unsigned long long time = context->timestamp() - now;
//
//    unsigned char program[] = {push_i_8, 15, push_i_8, 50, op_add, char_, char_, int_, stack_load, int_, print, int_, inc, int_, goto_, 0};
//    unsigned char program[] = {push_i_32, 15, 0, 0, 0, stack_load, int_, print, int_, inc, int_, goto_, 5};
//
//    MochaVM vm;
//
//    unsigned int index = 0;
//    vm.execute(program, sizeof(program), index);


//    boost::multiprecision::uint256_t t("7009962265102230458240694435574978735234727012846377283761280208322191916927");
//    boost::multiprecision::uint256_t b = uint256::fromBytes256(context->getConfig().BASE_TARGET_DIFFICULTY);
//
//    std::cout << t << std::endl;
//    std::cout << b << std::endl;
//    std::cout << (t>b) << std::endl;
//
//    std::string difficulty = "000000004EAADBD5513E67397611CE4D22D434577161FC6EDBA6F3E6DA8ECCCD";
//    const char difficulty_bytes[32]     = {0, 0, 0, 0, 78, -86, -37, -43, 81, 62, 103, 57, 118, 17, -50, 77, 34, -44, 52, 87, 113, 97, -4, 110, -37, -90, -13, -26, -38, -114, -52, -51};
//    const char difficulty_bytes2[32]    = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 34, -44, 52, 87, 113, 97, -4, 110, -37, -90, -13, -26, -38, -114, -52, -51};
//
//    u_int256 i256   = uint256::fromBytes256(difficulty_bytes);
//    u_int256 i2562  = uint256::fromBytes256(difficulty_bytes2);
//
//    std::cout << toHex(difficulty_bytes2, 32) << std::endl;
//
//    std::cout << i256 << "\n" << i2562 << "\n" << (i256 > i2562) << std::endl;
//    std::cout << (uint256::fromBytes256(context->getConfig().BASE_TARGET_DIFFICULTY) > i256) << std::endl;



        Block block(0,0, context->getDifficulty(), *context);
        unsigned long long nonce = 0;
        char out[32];

        RiverHash::mine(0, (char* )(block.toStoredBlock()), nonce, sizeof(StoredBlock), out, context->getDifficulty());

        std::cout << nonce << " " << toHex(out, 32).c_str() << "\n" << ByteUtil::fromBytes256(out, 32) << std::endl;

    /* Clean up */

    /* Removes all digests and ciphers */
    EVP_cleanup();

    /* if you omit the next, a small leak may be left when you make use of the BIO (low level API) for e.g. base64 transformations */
    CRYPTO_cleanup_all_ex_data();

    /* Remove error strings */
    ERR_free_strings();

    return 0;
}