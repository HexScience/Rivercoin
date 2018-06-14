/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core.mpp.compiler;

public enum Opcode
{
    HLT,

    ADD,
    SUB,
    MUL,
    DIV,
    MOD,
    POW,
    SIN,
    COS,
    TAN,

    AND,
    OR,
    RSHIFT,
    LSHIFT,
    ASSERT,
    LESSTHAN,
    MORETHAN,
    LESSTHANE,
    MORETHANE,

    PUSH,
    POP,

    PUSH_INT,
    PUSH_UINT,
    PUSH_UINT256,
    PUSH_FLOAT,
    PUSH_DECIMAL,

    MOV, //move from stack to memory
    LOD, //load from memory to stack
    NEW, //create a new element in memory
    NEW_INT,
    NEW_UINT,
    NEW_UINT256,
    NEW_FLOAT,
    NEW_DECIMAL,

    IF,
    ELSEIF,
    ELSE,
    LOOP,
    FOR,
    FOREACH,
    WHILE,

    FUN,
    CALL,

    ENCRYPT,
    ;

//    public static final short
//    HLT = 0,
//
//    //MATH
//    ADD = 1,
//    SUB = 2,
//    MLT = 3,
//    DIV = 4,
//    MOD = 5,
//    POW = 6,
//    SIN = 7,
//    COS = 8,
//
//    //BOOL
//    AND = 9,
//    OR = 10,
//    RSHFT = 11,
//    LSHFT = 12,
//    ASSERT = 13,
//
//    //OPERATIONAL
//    SET = 14, //STACK.SET(INDEX, OBJECT)
//    PUT = 15, //MEMORY.PUT(INDEX, OBJECT)
//    PRT = 16,
//
//    POP = 17,
//    PUSH = 18,
//    PUSH_INT = 19,
//    PUSH_UINT = 20,
//    PUSH_UINT256 = 21,
//    PUSH_FLOAT = 22,
//    PUSH_BIGDECIMAL = 23,
//
//    NEW = 24,
//    CALL = 25,
//    IF = 26,
//    ELSEIF = 27,
//    ELSE = 28,
//    FOR = 29,
//    WHILE = 30,
//    LOOP = 31,
//
//    FUN = 32,
//
//    LOAD = 33, //stack.push(stack.get(int))
//
//    EXT = 400;
}
