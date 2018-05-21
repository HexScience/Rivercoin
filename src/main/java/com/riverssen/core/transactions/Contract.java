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

package com.riverssen.core.transactions;

public class Contract
{
    static final short opcode = -1,
    /** push object/var to stack **/
            push = 0,
    /** pop from stack **/
            pop  = 1,
    /** var: long **/
            i64  = 1,
    /** var: double **/
            f64  = 1,
    /** var: int256 **/
            i256 = 1,
    /** var: double64+ **/
            bgf = 1,

            print  = 1,
            send  = 1,
            get  = 1,
            add  = 1,
            sub  = 1,
            mul  = 1,
            div  = 1,
            mod  = 1,

            thrw = 255;
            ;

    private byte bytecode[];

    public Contract(byte bytecode[])
    {
        this.bytecode = bytecode;
    }

    public static Contract generate(String code)
    {
        return new Contract(byteCode());
    }

    private static byte[] byteCode()
    {
        return new byte[0];
    }
}