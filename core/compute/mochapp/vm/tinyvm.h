//
// Created by Ragnarr Ivarssen on 14.07.18.
//

#ifndef RIVERCOIN_CPP_TINYVM_H
#define RIVERCOIN_CPP_TINYVM_H

#include "instructions.h"
#include <stack>
#include <iostream>
#include <vector>

typedef instructions instruction;

enum type{
    char_,
    uchar_,
    short_,
    ushort_,
    int_,
    uint_,
    long_,
    ulong_,
    int128_,
    uint128_,
    int256_,
    uint256_,
    float8_,
    float16_,
    float32_,
    float64_,
    float128_,
    float256_,
    c_string,
    pointer_,
};

struct executable{
    std::vector<instruction>                    instructions__;
    std::vector<std::vector<instruction>>       functions__;

    executable(std::vector<instruction> prgrm)
    {
        for (unsigned long i = 0; i < prgrm.size(); i ++)
        {
            instruction INST = prgrm[i];

            if (INST == start_func)
                functions__.push_back(pack_in_vector(prgrm, ++i));

            else if (INST == end_func)
            {
            }

            else instructions__.push_back(INST);
        }
    }

    std::vector<instruction> pack_in_vector(std::vector<instruction> prgrm, unsigned long& i)
    {
        std::vector<instruction> instr__;

        while (i < prgrm.size())
        {
            instruction INST = prgrm[i];

            if (INST == start_func)
                functions__.push_back(pack_in_vector(prgrm, ++i));

            else if (INST == end_func)
                return instr__;

            else instr__.push_back(INST);

            i ++;
        }

        return instr__;
    }
};

class MochaVM;

struct _register_
{
    char __a[32];
    char __b[32];
    char __c[32];

    type __a___;
    type __b___;
    type __c___;
};

class stack
{
private:
//    std::stack<type> type_stack;
    unsigned char *  _stack;
    long             _index;
    unsigned long    _size;
    _register_       _register;
public:
    stack(unsigned int size) : _index(0), _stack(new unsigned char(size)), _size(size) {}
    stack(const stack& o)
            : _index(o._index), _stack(new unsigned char(o._size)), _size(o._size)
    {
        memcpy(_stack, o._stack, _size);
    }
    ~stack()
    {
        delete _stack;
    }
    template <typename T> void  push(T v, MochaVM* vm);
    template <typename T> T     pop(MochaVM* vm);
    template <typename T> T     peek(MochaVM* vm);
    template <typename T> T     peek(unsigned long p, MochaVM* vm);

    void cast_by_type(type t)
    {
    }

    void pop_register_a(MochaVM* vm, type t);
    void pop_register_b(MochaVM* vm, type t);

    void load_register_a(MochaVM* vm, type t, unsigned long __i);
    void load_register_b(MochaVM* vm, type t, unsigned long __i);

    void push_register_a(MochaVM* vm, type t, char data[32]);
    void push_register_b(MochaVM* vm, type t, char data[32]);

    void _____register_add(type final_cast);
    void _____register_sub(type final_cast);
    void _____register_mul(type final_cast);
    void _____register_div(type final_cast);

    void iadd(MochaVM* vm, type a, type b, unsigned char length);
    void isub(MochaVM* vm, type a, type b, unsigned char length);
    void imul(MochaVM* vm, type a, type b, unsigned char length);
    void idiv(MochaVM* vm, type a, type b, unsigned char length);
    template <typename T>void i_add(MochaVM* vm, T a, type b, unsigned char length);
    template <typename T>void i_mul(MochaVM* vm, T a, type b, unsigned char length);
    template <typename T>void i_sub(MochaVM* vm, T a, type b, unsigned char length);
    template <typename T>void i_div(MochaVM* vm, T a, type b, unsigned char length);

    void print(MochaVM *vm, type t);
    void load(MochaVM *vm, type t);
    template <typename T, typename O> T cast(O o);

    void inc(MochaVM* vm, type t);
};

class heap{
};

#define STACK_1GB 1000000000
#define STACK_500MB 500000000

class MochaVM{
private:
    stack   vm_stack;
    heap  vm_memory;
    bool    execute_;
public:
    MochaVM() : vm_stack(STACK_500MB), execute_(true)
    {
    }

    void execute(const unsigned char* prgrm, const executable& program, unsigned int size, unsigned int& index);
    void terminate(const char* t)
    {
        execute_ = false;
        std::cout << t << std::endl;
    }
};

#endif //RIVERCOIN_CPP_TINYVM_H
