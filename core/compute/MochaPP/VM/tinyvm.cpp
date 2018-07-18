//
// Created by Ragnarr Ivarssen on 14.07.18.
//

#include "tinyvm.h"
#include <boost/multiprecision/cpp_int.hpp>

void MochaVM::execute(unsigned char *prgrm, unsigned int size, unsigned int& index)
{
    while (index < size && execute_)
    {
        instruction current = static_cast<instruction>(prgrm[index ++]);

        switch (current)
        {
            case push_i_8:      vm_stack.push((char) prgrm[index ++], this); break;
            case push_i_8u:     vm_stack.push((unsigned char) prgrm[index ++], this); break;
            case push_i_16:     vm_stack.push(((short*) ((unsigned char[2]){prgrm[index], prgrm[index + 1]}))[0], this); index += 2; break;
            case push_i_16u:    vm_stack.push(((unsigned short*) ((unsigned char[2]){prgrm[index], prgrm[index + 1]}))[0], this); index += 2; break;
            case push_i_32:     vm_stack.push(((int*) ((unsigned char[4]){prgrm[index], prgrm[index + 1], prgrm[index + 2], prgrm[index + 3]}))[0], this); index += 4; break;
            case push_i_32u:    vm_stack.push(((unsigned int*) ((unsigned char[4]){prgrm[index], prgrm[index + 1], prgrm[index + 2], prgrm[index + 3]}))[0], this); index += 4; break;
            case push_i_64:     vm_stack.push(((int*) ((unsigned char[8]){prgrm[index], prgrm[index + 1], prgrm[index + 2], prgrm[index + 3], prgrm[index + 4], prgrm[index + 5], prgrm[index + 6], prgrm[index + 7]}))[0], this); index += 8; break;
            case push_i_64u:    vm_stack.push(((unsigned int*) ((unsigned char[8]){prgrm[index], prgrm[index + 1], prgrm[index + 2], prgrm[index + 3], prgrm[index + 4], prgrm[index + 5], prgrm[index + 6], prgrm[index + 7]}))[0], this); index += 8; break;
            case push_i_128:    vm_stack.push(((boost::multiprecision::int128_t*) ((unsigned char[16]){prgrm[index], prgrm[index + 1], prgrm[index + 2], prgrm[index + 3], prgrm[index + 4], prgrm[index + 5], prgrm[index + 6], prgrm[index + 7], prgrm[index + 8], prgrm[index + 9], prgrm[index + 10], prgrm[index + 11], prgrm[index + 12], prgrm[index + 13], prgrm[index + 14], prgrm[index + 15]}))[0], this); index += 8; break;
            case push_i_128u:   vm_stack.push(((boost::multiprecision::uint128_t*) ((unsigned char[16]){prgrm[index], prgrm[index + 1], prgrm[index + 2], prgrm[index + 3], prgrm[index + 4], prgrm[index + 5], prgrm[index + 6], prgrm[index + 7], prgrm[index + 8], prgrm[index + 9], prgrm[index + 10], prgrm[index + 11], prgrm[index + 12], prgrm[index + 13], prgrm[index + 14], prgrm[index + 15]}))[0], this); index += 8; break;
            case push_i_256:    vm_stack.push(((boost::multiprecision::int256_t*) ((unsigned char[32]){prgrm[index], prgrm[index + 1], prgrm[index + 2], prgrm[index + 3], prgrm[index + 4], prgrm[index + 5], prgrm[index + 6], prgrm[index + 7], prgrm[index + 8], prgrm[index + 9], prgrm[index + 10], prgrm[index + 11], prgrm[index + 12], prgrm[index + 13], prgrm[index + 14], prgrm[index + 15], prgrm[index + 16], prgrm[index + 17], prgrm[index + 18], prgrm[index + 19], prgrm[index + 20], prgrm[index + 21], prgrm[index + 22], prgrm[index + 23], prgrm[index + 24], prgrm[index + 25], prgrm[index + 26], prgrm[index + 27], prgrm[index + 28], prgrm[index + 29], prgrm[index + 30], prgrm[index + 31]}))[0], this); index += 8; break;
            case push_i_256u:   vm_stack.push(((boost::multiprecision::uint256_t*) ((unsigned char[32]){prgrm[index], prgrm[index + 1], prgrm[index + 2], prgrm[index + 3], prgrm[index + 4], prgrm[index + 5], prgrm[index + 6], prgrm[index + 7], prgrm[index + 8], prgrm[index + 9], prgrm[index + 10], prgrm[index + 11], prgrm[index + 12], prgrm[index + 13], prgrm[index + 14], prgrm[index + 15], prgrm[index + 16], prgrm[index + 17], prgrm[index + 18], prgrm[index + 19], prgrm[index + 20], prgrm[index + 21], prgrm[index + 22], prgrm[index + 23], prgrm[index + 24], prgrm[index + 25], prgrm[index + 26], prgrm[index + 27], prgrm[index + 28], prgrm[index + 29], prgrm[index + 30], prgrm[index + 31]}))[0], this); index += 8; break;

            case pop_i_8:
            case pop_i_8u:
                    vm_stack.pop<char>(this);
                break;
            case pop_i_16:
            case pop_i_16u:
                vm_stack.pop<short>(this);
                break;
            case pop_i_32:
            case pop_i_32u:
                vm_stack.pop<int>(this);
                break;
            case pop_i_64:
            case pop_i_64u:
                vm_stack.pop<long>(this);
                break;

            case pop_i_128:
            case pop_i_128u:
                vm_stack.pop<boost::multiprecision::int128_t>(this);
                break;
            case pop_i_256:
            case pop_i_256u:
                vm_stack.pop<boost::multiprecision::int128_t>(this);
                break;

            case push_s:
                break;
            case pop_s:
                break;

            case op_add:
                vm_stack.iadd(this, static_cast<type>(prgrm[index]), static_cast<type>(prgrm[index + 1]), prgrm[index + 2]);
                index += 3;
                break;

            case isub:
                vm_stack.isub(this, static_cast<type>(prgrm[index]), static_cast<type>(prgrm[index + 1]), prgrm[index + 2]);
                index += 3;
                break;

            case imul:
                vm_stack.imul(this, static_cast<type>(prgrm[index]), static_cast<type>(prgrm[index + 1]), prgrm[index + 2]);
                index += 3;
                break;

            case idiv:
                vm_stack.idiv(this, static_cast<type>(prgrm[index]), static_cast<type>(prgrm[index + 1]), prgrm[index + 2]);
                index += 3;
                break;

            case print:
                vm_stack.print(this, static_cast<type>(prgrm[index++]));
                break;

            case stack_load:
                    vm_stack.load(this, static_cast<type>(prgrm[index ++]));
                break;

            case goto_:
                {
                    index = prgrm[index ++];
                }
                break;

            case inc:
                    vm_stack.inc(this, static_cast<type>(prgrm[index ++]));
                break;

            default:
                std::string instruction_name = "";

                switch (static_cast<instruction>(current))
                {
                    case push_i_8: instruction_name = "push_i_8"; break;
                    case push_i_16: instruction_name = "push_i_16"; break;
                    case push_i_32: instruction_name = "push_i_32"; break;
                    case push_i_64: instruction_name = "push_i_64"; break;
                    case push_i_128: instruction_name = "push_i_128"; break;
                    case push_i_256: instruction_name = "push_i_256"; break;
                    case push_i_8u: instruction_name = "push_i_8u"; break;
                    case push_i_16u: instruction_name = "push_i_16u"; break;
                    case push_i_32u: instruction_name = "push_i_32u"; break;
                    case push_i_64u: instruction_name = "push_i_64u"; break;
                    case push_i_128u: instruction_name = "push_i_128u"; break;
                    case push_i_256u: instruction_name = "push_i_256u"; break;
                    case push_f_8: instruction_name = "push_f_8"; break;
                    case push_f_16: instruction_name = "push_f_16"; break;
                    case push_f_32: instruction_name = "push_f_32"; break;
                    case push_f_64: instruction_name = "push_f_64"; break;
                    case push_f_128: instruction_name = "push_f_128"; break;
                    case push_f_256: instruction_name = "push_f_256"; break;
                    case push_a: instruction_name = "push_a"; break;
                    case push_s: instruction_name = "push_s"; break;
                    case pop_i_8: instruction_name = "pop_i_8"; break;
                    case pop_i_16: instruction_name = "pop_i_16"; break;
                    case pop_i_32: instruction_name = "pop_i_32"; break;
                    case pop_i_64: instruction_name = "pop_i_64"; break;
                    case pop_i_128: instruction_name = "pop_i_128"; break;
                    case pop_i_256: instruction_name = "pop_i_256"; break;
                    case pop_i_8u: instruction_name = "pop_i_8u"; break;
                    case pop_i_16u: instruction_name = "pop_i_16u"; break;
                    case pop_i_32u: instruction_name = "pop_i_32u"; break;
                    case pop_i_64u: instruction_name = "pop_i_64u"; break;
                    case pop_i_128u: instruction_name = "pop_i_128u"; break;
                    case pop_i_256u: instruction_name = "pop_i_256u"; break;
                    case pop_f_8: instruction_name = "pop_f_8"; break;
                    case pop_f_16: instruction_name = "pop_f_16"; break;
                    case pop_f_32: instruction_name = "pop_f_32"; break;
                    case pop_f_64: instruction_name = "pop_f_64"; break;
                    case pop_f_128: instruction_name = "pop_f_128"; break;
                    case pop_f_256: instruction_name = "pop_f_256"; break;
                    case pop_a: instruction_name = "pop_a"; break;
                    case pop_s: instruction_name = "pop_s"; break;
                    case op_add: instruction_name = "op_add"; break;
                    case op_mul: instruction_name = "op_mul"; break;
                    case op_div: instruction_name = "op_div"; break;
                    case op_sub: instruction_name = "op_sub"; break;
                    case op_mod: instruction_name = "op_mod"; break;
                    case op_pow: instruction_name = "op_pow"; break;
                    case iadd: instruction_name = "iadd"; break;
                    case imul: instruction_name = "imul"; break;
                    case idiv: instruction_name = "idiv"; break;
                    case isub: instruction_name = "isub"; break;
                    case fadd: instruction_name = "fadd"; break;
                    case fmul: instruction_name = "fmul"; break;
                    case fdiv: instruction_name = "fdiv"; break;
                    case fsub: instruction_name = "fsub"; break;
                    case imod: instruction_name = "imod"; break;
                    case ipow: instruction_name = "ipow"; break;
                    case fmod_: instruction_name = "fmod_"; break;
                    case fpow: instruction_name = "fpow"; break;
                    case boolean_and: instruction_name = "boolean_and"; break;
                    case boolean_or: instruction_name = "boolean_or"; break;
                    case boolean_not: instruction_name = "boolean_not"; break;
                    case logical_and: instruction_name = "logical_and"; break;
                    case logical_or: instruction_name = "logical_or"; break;
                    case logical_xor: instruction_name = "logical_xor"; break;
                    case lshift: instruction_name = "lshift"; break;
                    case rshift: instruction_name = "rshift"; break;
                    case inc: instruction_name = "inc"; break;
                    case url_read: instruction_name = "url_read"; break;
                    case url_write: instruction_name = "url_write"; break;
                    case cte_read: instruction_name = "cte_read"; break;
                    case cte_write: instruction_name = "cte_write"; break;
                    case fle_read: instruction_name = "fle_read"; break;
                    case fle_write: instruction_name = "fle_write"; break;
                    case stack_read: instruction_name = "stack_read"; break;
                    case stack_load: instruction_name = "stack_load"; break;
                    case memory_read: instruction_name = "memory_read"; break;
                    case memory_load: instruction_name = "memory_load"; break;
                    case memory_write: instruction_name = "memory_write"; break;
                    case memory_write_stack: instruction_name = "memory_write_stack"; break;
                    case stack_duplicate: instruction_name = "stack_duplicate"; break;
                    case stack_swap: instruction_name = "stack_swap"; break;
                    case iprint: instruction_name = "iprint"; break;
                    case fprint: instruction_name = "fprint"; break;
                    case print: instruction_name = "print"; break;
                    case goto_: instruction_name = "goto_"; break;

                    default: instruction_name = "unknown";
                        break;
                }



                terminate((std::string("UnsupportedInstruction: ") + instruction_name).c_str());
                break;
        }
    }
    std::cout << std::endl;
}

template <typename T> void stack::push(T v, MochaVM* vm)
{
    unsigned char size = sizeof(v);
    unsigned char* object = (unsigned char*)&v;

    if(_index < (_size + size))
        for (int i = 0; i < size; i ++)
            _stack[_index ++] = object[i];
    else vm->terminate("StackOverFlow exception.");
}


/**
 * This implementation is actually slow, if we want to optimize the vm, stacks should have pop_<type> functions.
 */
template <typename T> T stack::pop(MochaVM* vm)
{
    T t;
    long size = sizeof(T);

    if(_index - size >= 0)
    {
        unsigned char my_object[size];
        for(int i = 0; i < size; i ++)
            my_object[i] = _stack[i + (_index - size)];

        t = ((T*) my_object)[0];

        _index -= size;
    }
    else vm->terminate("StackUnderFlow exception (stack.pop).");

    return t;
}

/**
 * This implementation is actually slow, if we want to optimize the vm, stacks should have peek_<type> functions.
 */
template <typename T> T stack::peek(MochaVM* vm)
{
    T t;
    long size = sizeof(T);

    if(_index - size >= 0)
    {
        unsigned char my_object[size];
        for(int i = 0; i < size; i ++)
            my_object[i] = _stack[i + (_index - size)];

        t = ((T*) my_object)[0];
    }
    else vm->terminate("StackUnderFlow exception (stack.peek).");

    return t;
}

void stack::iadd(MochaVM *vm, type a, type b, unsigned char length)
{
    switch (a)
    {
        case char_:
            i_add<char>(vm, pop<char>(vm), b, length);
            break;
        case uchar_:
            i_add<unsigned char>(vm, pop<unsigned char>(vm), b, length);
            break;
        case short_:
            i_add<short>(vm, pop<short>(vm), b, length);
            break;
        case ushort_:
            i_add<unsigned short>(vm, pop<unsigned short>(vm), b, length);
            break;
        case int_:
            i_add<int>(vm, pop<int>(vm), b, length);
            break;
        case uint_:
            i_add<unsigned int>(vm, pop<unsigned int>(vm), b, length);
            break;
        case long_:
            i_add<long>(vm, pop<long>(vm), b, length);
            break;
        case ulong_:
            i_add<unsigned long>(vm, pop<unsigned long>(vm), b, length);
            break;
        case int128_:
            i_add<boost::multiprecision::int128_t>(vm, pop<boost::multiprecision::int128_t>(vm), b, length);
            break;
        case uint128_:
            i_add<boost::multiprecision::uint128_t>(vm, pop<boost::multiprecision::uint128_t>(vm), b, length);
            break;
        case int256_:
            i_add<boost::multiprecision::int256_t>(vm, pop<boost::multiprecision::int256_t>(vm), b, length);
            break;
        case uint256_:
            i_add<boost::multiprecision::uint256_t>(vm, pop<boost::multiprecision::uint256_t>(vm), b, length);
            break;
        case float8_:
            vm->terminate("UnsupportedOperation: float8+<T>");
            break;
        case float16_:
            vm->terminate("UnsupportedOperation: float16+<T>");
            break;
        case float32_:
            i_add<float>(vm, pop<float>(vm), b, length);
            break;
        case float64_:
            i_add<double>(vm, pop<double>(vm), b, length);
            break;
        case float128_:
            vm->terminate("UnsupportedOperation: float128+<T>");
            break;
        case float256_:
            vm->terminate("UnsupportedOperation: float256+<T>");
            break;
        default:
            vm->terminate("UnsupportedOperation: <T>+<T>");
            break;
    }
}

/** this is in reverse where a = b and b = a **/
template <typename T> void stack::i_add(MochaVM *vm, T a, type b, unsigned char length)
{
#define OP +
    boost::multiprecision::int1024_t i;

    switch (b)
    {
        case char_:
            i = (char)(pop<char>(vm) OP a);
            break;
        case uchar_:
            i = (unsigned char)(pop<unsigned char>(vm) OP a);
            break;
        case short_:
            i = (short)(pop<short>(vm) OP a);
            break;
        case ushort_:
            i = (unsigned short) (pop<unsigned short>(vm) OP a);
            break;
        case int_:
            i = (int)(pop<int>(vm) OP a);
            break;
        case uint_:
            i = (unsigned int)(pop<unsigned int>(vm) OP a);
            break;
        case long_:
            i = (long)(pop<long>(vm) OP a);
            break;
        case ulong_:
            i = (unsigned long)(pop<unsigned long>(vm) OP a);
            break;
        case int128_:
            i = (boost::multiprecision::int128_t)(pop<(boost::multiprecision::int128_t)>(vm) OP a);
            break;
        case uint128_:
            i = (boost::multiprecision::uint128_t)(pop<(boost::multiprecision::uint128_t)>(vm) OP a);
            break;
        case int256_:
            i = (boost::multiprecision::int256_t)(pop<(boost::multiprecision::int256_t)>(vm) OP a);
            break;
        case uint256_:
            i = (boost::multiprecision::uint256_t)(pop<(boost::multiprecision::uint256_t)>(vm) OP a);
            break;
        case float8_:
            vm->terminate("UnsupportedOperation: <T>+float8");
            break;
        case float16_:
            vm->terminate("UnsupportedOperation: <T>+float16");
            break;
        case float32_:
            i = (long long)(pop<float>(vm) OP a);
            break;
        case float64_:
            i = (long long)(pop<double>(vm) OP a);
            break;
        case float128_:
            vm->terminate("UnsupportedOperation: <T>+float128");
            break;
        case float256_:
            vm->terminate("UnsupportedOperation: <T>+float256");
            break;
        default:
            vm->terminate("UnsupportedOperation: <T>+<T>");
            break;
    }

#undef OP

    switch (length)
    {
        case char_:
            push((char)(i.convert_to<char>()), vm);
            break;
        case uchar_:
            push((unsigned char)(i.convert_to<unsigned char>()), vm);
            break;
        case short_:
            push((short)(i.convert_to<short>()), vm);
            break;
        case ushort_:
            push((unsigned short)(i.convert_to<unsigned short>()), vm);
            break;
        case int_:
            push((int)(i.convert_to<int>()), vm);
            break;
        case uint_:
            push((unsigned int)(i.convert_to<unsigned int>()), vm);
            break;
        case long_:
            push((long)(i.convert_to<long>()), vm);
            break;
        case ulong_:
            push((unsigned long)(i.convert_to<unsigned long>()), vm);
            break;
        case int128_:
            vm->terminate("UnsupportedOperation: cast_int128");
            break;
        case uint128_:
            vm->terminate("UnsupportedOperation: cast_uint128");
            break;
        case int256_:
            vm->terminate("UnsupportedOperation: cast_int256");
            break;
        case uint256_:
            vm->terminate("UnsupportedOperation: cast_uint256");
            break;
        case float8_:
            vm->terminate("UnsupportedOperation: cast_float8");
            break;
        case float16_:
            vm->terminate("UnsupportedOperation: cast_float16");
            break;
        case float32_:
            push((float)(i.convert_to<float>()), vm);
            break;
        case float64_:
            push((double)(i.convert_to<double>()), vm);
            break;
        case float128_:
            vm->terminate("UnsupportedOperation: cast_float128");
            break;
        case float256_:
            vm->terminate("UnsupportedOperation: cast_float256");
            break;
    }
}

void stack::idiv(MochaVM *vm, type a, type b, unsigned char length) {}
void stack::imul(MochaVM *vm, type a, type b, unsigned char length) {}
void stack::isub(MochaVM *vm, type a, type b, unsigned char length) {}

template <typename T, typename O> T stack::cast(O o)
{
    return (T) o;
}

void stack::print(MochaVM *vm, type t)
{
    switch (t)
    {
        case char_:
            std::cout << pop<char>(vm);
            break;
        case uchar_:
            std::cout << pop<unsigned char>(vm);
            break;
        case short_:
            std::cout << pop<short>(vm);
            break;
        case ushort_:
            std::cout << pop<unsigned short>(vm);
            break;
        case int_:
            std::cout << pop<int>(vm);
            break;
        case uint_:
            std::cout << pop<unsigned int>(vm);
            break;
        case long_:
            std::cout << pop<long>(vm);
            break;
        case ulong_:
            std::cout << pop<unsigned long>(vm);
            break;
        case int128_:
            vm->terminate("UnsupportedOperation: cast_int128");
            break;
        case uint128_:
            vm->terminate("UnsupportedOperation: cast_uint128");
            break;
        case int256_:
            vm->terminate("UnsupportedOperation: cast_int256");
            break;
        case uint256_:
            vm->terminate("UnsupportedOperation: cast_uint256");
            break;
        case float8_:
            vm->terminate("UnsupportedOperation: cast_float8");
            break;
        case float16_:
            vm->terminate("UnsupportedOperation: cast_float16");
            break;
        case float32_:
            std::cout << pop<float>(vm);
            break;
        case float64_:
            std::cout << pop<double>(vm);
            break;
        case float128_:
            vm->terminate("UnsupportedOperation: cast_float128");
            break;
        case float256_:
            vm->terminate("UnsupportedOperation: cast_float256");
            break;
        default:
            vm->terminate("UnsupportedOperation: print_<?>");
            break;
    }
    std::cout << std::endl;
}

void stack::load(MochaVM* vm, type t)
{
    switch (t)
    {
        case char_:
            push(peek<char>(vm), vm);
            break;
        case uchar_:
            push(peek<unsigned char>(vm), vm);
            break;
        case short_:
            push(peek<short>(vm), vm);
            break;
        case ushort_:
            push(peek<unsigned short>(vm), vm);
            break;
        case int_:
            push(peek<int>(vm), vm);
            break;
        case uint_:
            push(peek<unsigned int>(vm), vm);
            break;
        case long_:
            push(peek<long>(vm), vm);
            break;
        case ulong_:
            push(peek<unsigned long>(vm), vm);
            break;
        case int128_:
            vm->terminate("UnsupportedOperation: cast_int128");
            break;
        case uint128_:
            vm->terminate("UnsupportedOperation: cast_uint128");
            break;
        case int256_:
            vm->terminate("UnsupportedOperation: cast_int256");
            break;
        case uint256_:
            vm->terminate("UnsupportedOperation: cast_uint256");
            break;
        case float8_:
            vm->terminate("UnsupportedOperation: cast_float8");
            break;
        case float16_:
            vm->terminate("UnsupportedOperation: cast_float16");
            break;
        case float32_:
            push(peek<float>(vm), vm);
            break;
        case float64_:
            push(peek<double>(vm), vm);
            break;
        case float128_:
            vm->terminate("UnsupportedOperation: cast_float128");
            break;
        case float256_:
            vm->terminate("UnsupportedOperation: cast_float256");
            break;
        default:
            vm->terminate("UnsupportedOperation: print_<?>");
            break;
    }
}

void stack::inc(MochaVM* vm, type t)
{
    switch (t)
    {
        case char_:
            push(pop<char>(vm) + 1, vm);
            break;
        case uchar_:
            push(pop<unsigned char>(vm) + 1, vm);
            break;
        case short_:
            push(pop<short>(vm) + 1, vm);
            break;
        case ushort_:
            push(pop<unsigned short>(vm) + 1, vm);
            break;
        case int_:
            push(pop<int>(vm) + 1, vm);
            break;
        case uint_:
            push(pop<unsigned int>(vm) + 1, vm);
            break;
        case long_:
            push(pop<long>(vm) + 1, vm);
            break;
        case ulong_:
            push(pop<unsigned long>(vm) + 1, vm);
            break;
        case int128_:
            vm->terminate("UnsupportedOperation: cast_int128");
            break;
        case uint128_:
            vm->terminate("UnsupportedOperation: cast_uint128");
            break;
        case int256_:
            vm->terminate("UnsupportedOperation: cast_int256");
            break;
        case uint256_:
            vm->terminate("UnsupportedOperation: cast_uint256");
            break;
        case float8_:
            vm->terminate("UnsupportedOperation: cast_float8");
            break;
        case float16_:
            vm->terminate("UnsupportedOperation: cast_float16");
            break;
        case float32_:
            push(pop<float>(vm) + 1, vm);
            break;
        case float64_:
            push(pop<double>(vm) + 1, vm);
            break;
        case float128_:
            vm->terminate("UnsupportedOperation: cast_float128");
            break;
        case float256_:
            vm->terminate("UnsupportedOperation: cast_float256");
            break;
        default:
            vm->terminate("UnsupportedOperation: print_<?>");
            break;
    }
}