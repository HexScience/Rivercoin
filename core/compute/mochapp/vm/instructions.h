//
// Created by Ragnarr Ivarssen on 13.07.18.
//

#ifndef RIVERCOIN_CPP_INSTRUCTIONS_H
#define RIVERCOIN_CPP_INSTRUCTIONS_H

enum instructions{
    /**
     * Push
     */

    /** integers **/
    push_i_8,
    push_i_16,
    push_i_32,
    push_i_64,
    push_i_128,
    push_i_256,
    push_i_8u,
    push_i_16u,
    push_i_32u,
    push_i_64u,
    push_i_128u,
    push_i_256u,

    /** floats **/
    push_f_8,
    push_f_16,
    push_f_32,
    push_f_64,
    push_f_128,
    push_f_256,

    push_a,
    push_s,


    /**
     * Pop
     */

    /** integers **/
    pop_i_8,
    pop_i_16,
    pop_i_32,
    pop_i_64,
    pop_i_128,
    pop_i_256,
    pop_i_8u,
    pop_i_16u,
    pop_i_32u,
    pop_i_64u,
    pop_i_128u,
    pop_i_256u,

    /** floats **/
    pop_f_8,
    pop_f_16,
    pop_f_32,
    pop_f_64,
    pop_f_128,
    pop_f_256,

    pop_a,
    pop_s,

    /**
     * Math Operations
     */

    op_add,
    op_mul,
    op_div,
    op_sub,
    op_mod,
    op_pow,

    iadd,
    imul,
    idiv,
    isub,

    fadd,
    fmul,
    fdiv,
    fsub,

    imod,
    ipow,
    fmod_,
    fpow,

    boolean_and, boolean_or, boolean_not,
    logical_and, logical_or, logical_xor,

    lshift, rshift,

    inc,

    /**
     * IO
     */

    url_read, url_write,
    cte_read, cte_write,
    fle_read, fle_write,

    /**
     * Other, Misc
     */

    stack_read, // undefined operator
    stack_load, // push stack_get(i)
    memory_read, // undefined operator
    memory_load, // push memory_get(i)
    memory_write, // memory.set(i, data[])
    memory_write_stack, //memory.set(i, stack.pop)

    stack_duplicate, //push stack.peek
    stack_swap, //stack.set(x, stack.get(y)) and stack.set(y, stack.get(x))

    iprint,
    fprint,
    print,

    goto_,

    call_,
    call_native,

    if_,
    elif_,
    else_,

    /** none executable instructions **/
    start_func,
    end_func,
    _new_,
    push,
    pop,
    malloc_,
    calloc_,
};

#endif //RIVERCOIN_CPP_INSTRUCTIONS_H
