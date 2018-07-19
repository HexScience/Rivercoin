//
// Created by Ragnarr Ivarssen on 14.07.18.
//

#ifndef RIVERCOIN_CPP_RIVERHASH_H
#define RIVERCOIN_CPP_RIVERHASH_H

#include "math.h"
#include <openssl/sha.h>
#include <openssl/aes.h>
#include <vector>
#include <boost/algorithm/hex.hpp>
#include <algorithm>
#include "../context.h"
#include "hashutil.h"

namespace memory{
    template <typename T>
    struct buffer{
        unsigned int specific_length;
        unsigned int index;
        T* data;

        buffer(unsigned int length) : specific_length(length), index(0), data(new T[length]) {}
        buffer(const buffer& o) : specific_length(o.specific_length), index(o.index), data(new T[o.specific_length]) { memcpy(data, o.data, sizeof(T) * specific_length); }
        ~buffer() { delete data; }

        void put(T byte)
        {
            if(specific_length > index)
                data[index ++] = byte;
        }

        void put(T* bytes, unsigned int length)
        {
            unsigned int i = 0;

            while(remaining() && length > i)
                put(bytes[i ++]);
        }

        void next(T* bytes, unsigned int length)
        {
            memcpy(bytes, data, length);
        }

        bool remaining()
        {
            return specific_length > index;
        }

        void rewind()
        {
            index = 0;
        }
    };
}

class RiverHash{
private:
    static void xor_(const char* a, const char* b, char* c, unsigned int length)
    {
        for (int i = 0; i < length; i++)
            c[i] = a[i] ^ b[i];
    }

    static void digest(const char* i, char* buf, long length)
    {
        SHA512((unsigned char *)i, length, (unsigned char *)buf);
    }

    static void fill_mat(mat4& m, char buf[64])
    {
        float* data = (float*)buf;

        m.set(data);
    }

    static void hash(char *data, char key[64], unsigned long long int i)
    {
    }
public:
    enum{RiverHash_CPU, RiverHash_13v1, RiverHash_13_v2, RiverHash_13_v3, RiverHash_13_v4, RiverHash_GPU_v1, RiverHash_ProgPoW, RiverHash_PouW};

    static void mine(int algorithm, const char* input, unsigned long long& nonce, unsigned int length, char* output, uint256 difficulty)
    {
        void (*fun) (const char*, unsigned long long, unsigned int, char*);

        switch (algorithm)
        {
            case RiverHash_13_v4:
                    logger::alert("mining with RiverHash x13 v1.4");
                    logger::alert("difficulty set to: ");
                    std::cout << difficulty << " and nonce starting at: " << nonce << std::endl;
                    fun = riverhash_13_v4;
                break;
            default:
                logger::alert("mining with RiverHash x13 v1.4");
                logger::alert("difficulty set to: ");
                std::cout << difficulty << " and nonce starting at: " << nonce << std::endl;
                fun = riverhash_13_v4;
                break;
        }

        fun(input, nonce, length, output);
        uint256 result = ByteUtil::fromBytes256(output);

        if (result < difficulty) return;

        while (difficulty >= result)
        {
            nonce ++;

            fun(input, nonce, length, output);
            result = ByteUtil::fromBytes256(output);
        }
    }

    static void riverhash_13_v4(const char* input, const unsigned long long nonce, unsigned int length, char* output)
    {
        const unsigned long long REQUIRED_SIZE = 2000000;
        const char *nonce_ = (const char *) (&nonce);
        char *buffer = new char[length + 8];
        memcpy(buffer, input, length);
        buffer[length - 8] = nonce_[0];
        buffer[length - 7] = nonce_[1];
        buffer[length - 6] = nonce_[2];
        buffer[length - 5] = nonce_[3];
        buffer[length - 4] = nonce_[4];
        buffer[length - 3] = nonce_[5];
        buffer[length - 2] = nonce_[6];
        buffer[length - 1] = nonce_[7];

        memory::buffer<char> buf(REQUIRED_SIZE);
        char tempOut[64] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        /** get the digest of input + nonce **/
        digest(buffer, tempOut, length + 8);

        buf.put(tempOut, 64);

        while (buf.remaining())
        {
            unsigned long index = buf.index;
            unsigned long halfindex = index / 2;

            char temp[halfindex];

            xor_(buf.data, buf.data + halfindex, temp, halfindex);

            buf.put(temp, halfindex);

            digest(buf.data, tempOut, buf.index);

            buf.put(tempOut, 64);
        }

        int indexer = buf.index / 2;

        buf.rewind();

        while (indexer >= 32)
        {
            char tempA[indexer];
            char tempB[indexer];
            char tempC[indexer];

            buf.next(tempA, indexer);
            buf.next(tempB, indexer);

            xor_(tempA, tempB, tempC, indexer);

            buf.rewind();
            buf.put(tempC, indexer);

            indexer = buf.index / 2;
        }

        digest(buf.data, tempOut, 32);

        xor_(buf.data, buf.data + 32, output, 32);
    }

    static void mine_cpu_variant(const char* input, unsigned long long& nonce, unsigned int length,
                                 char* output)
    {
//        boost::multiprecision::uint256_t result;
    }

    static void apply_cpu_variant(const char* input, unsigned long long nonce, unsigned int length,
                                  char* output)
    {
        const unsigned long long REQUIRED_SIZE = 2000000;
        const char *nonce_ = (const char *) (&nonce);
        char *buffer = new char[length + 8];
        memcpy(buffer, input, length);
        buffer[length - 8] = nonce_[0];
        buffer[length - 7] = nonce_[1];
        buffer[length - 6] = nonce_[2];
        buffer[length - 5] = nonce_[3];
        buffer[length - 4] = nonce_[4];
        buffer[length - 3] = nonce_[5];
        buffer[length - 2] = nonce_[6];
        buffer[length - 1] = nonce_[7];

        memory::buffer<char> buf(REQUIRED_SIZE);

        char temp_buffer_a[32];
        char temp_buffer_b[32];
        char temp_buffer_c[32];
        char temp_buffer_d[64];

        /** get the digest of input + nonce **/
        digest(buffer, output, length + 8);

//        memcpy(temp_buffer_a, temp_buffer_d, 32);
//        memcpy(temp_buffer_b, temp_buffer_d + 32, 32);
//
//        while (buf.remaining())
//        {
//            xor_(temp_buffer_a, temp_buffer_b, temp_buffer_c, 32);
//
//            digest(temp_buffer_c, temp_buffer_d, 32);
//
//            buf.put(temp_buffer_d, 64);
//
//            memcpy(temp_buffer_a, temp_buffer_d, 32);
//            memcpy(temp_buffer_b, temp_buffer_d + 32, 32);
//        }
//
//        unsigned int half = (REQUIRED_SIZE / 2) / 2;
//        unsigned int index = 0;
//
//        memory::buffer<char> fbuffer(REQUIRED_SIZE / 2);
//
//        char key[64];
//        char dat[REQUIRED_SIZE / 2];
//
//        xor_(buf.data, buf.data + (REQUIRED_SIZE / 2), fbuffer.data, (REQUIRED_SIZE / 2));
//        digest(fbuffer.data, key, REQUIRED_SIZE / 2);
//
//        while(half > 64)
//        {
//            xor_(fbuffer.data, fbuffer.data + (half / 2), fbuffer.data, half / 2);
//            digest(fbuffer.data, key, half);
//
//            fbuffer.index = half;
//            fbuffer.put(key, 64);
//
//            xor_(fbuffer.data, fbuffer.data + (half / 2) + 32, fbuffer.data, half / 2 + 32);
//
//            index += half;
//
//            half = half / 2;
//        }
//
//        xor_(fbuffer.data, fbuffer.data + 32, output, 32);
    }

    static void apply(const char* input, unsigned long long nonce, unsigned int length,
                      char* output)
    {
        const unsigned long long REQUIRED_SIZE = 512;
        const char * nonce_ = (const char *)(&nonce);
        char * buffer       = new char[length + 8];
        memcpy(buffer, input, length);
        buffer[length - 8] = nonce_[0];
        buffer[length - 7] = nonce_[1];
        buffer[length - 6] = nonce_[2];
        buffer[length - 5] = nonce_[3];
        buffer[length - 4] = nonce_[4];
        buffer[length - 3] = nonce_[5];
        buffer[length - 2] = nonce_[6];
        buffer[length - 1] = nonce_[7];

        char buffer_64b[64];

        /** get the digest of input + nonce **/
        digest(buffer, buffer_64b, length + 8);

        mat4 initial_matrix;

        /** use matrix as a key **/
        fill_mat(initial_matrix, buffer_64b);

        vec3 rotationvector(1,1,1);
        vec3 translationvector(2,2,120);

        mat4 rotation;
        rotation.rotate(rotationvector);

        mat4 translation;
        translation.translate(translationvector);

        char data_buffer[REQUIRED_SIZE];

        unsigned long long data_size = 0;

        while (data_size < REQUIRED_SIZE)
        {
            rotation.rotate(rotationvector);
            translation.translate(translationvector);

            mat4 worldMatrix = rotation * translation;

            initial_matrix = initial_matrix * worldMatrix;
            data_size += (64);

            const char * temp = (char*)initial_matrix.mat;

            for(int i = 0; i < 16; i ++)
                data_buffer[data_size + i] = temp[i];

            /** get the digest of input + nonce **/
            digest(data_buffer, buffer_64b, data_size);

            fill_mat(initial_matrix, buffer_64b);

            char dta[24];
            xor_(data_buffer + (data_size - 64), temp, dta, 24);

            rotationvector = vec3(((unsigned int*)dta)[0], ((unsigned int*)dta)[1], ((unsigned int*)dta)[2]);
            translationvector = vec3(((unsigned int*)dta)[3], ((unsigned int*)dta)[4], ((unsigned int*)dta)[5]);

            rotation.rotate(rotationvector);
            translation.translate(translationvector);
        }

        mat4* matrices = (mat4*) data_buffer;

        int total_matrices = REQUIRED_SIZE / 64;

        mat4 final_matrix = ((matrices[0] * matrices[1] * matrices[2] * matrices[3]) * (matrices[4] * matrices[5] * matrices[6] * matrices[7]));

        char * matrix_data_temp = (char *) final_matrix.mat;

        xor_(matrix_data_temp, matrix_data_temp + 32, output, 32);
    }
};

#endif //RIVERCOIN_CPP_RIVERHASH_H
