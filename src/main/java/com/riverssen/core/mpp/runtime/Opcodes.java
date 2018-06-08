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

package com.riverssen.core.mpp.runtime;

public class Opcodes
{
    public static final short

    push        = 0,
    pop         = 1,
    print       = 2,
    add         = 3,
    sub         = 4,
    mul         = 5,
    div         = 6,
    mod         = 7,

    get         = 8,
    call        = 9,
    if_         = 10,
    else_       = 11,
    elif_       = 12,

    /** RSA KeyPair **/
    rsa_kp      = 13,

    /** ECDSA KeyPair **/
    ecdsa_kp    = 14,
    cmprsd_k    = 15,
    privte_k    = 16,
    public_k    = 17,
    addressk    = 18,

    rsa_pubk    = 19,
    rsa_priv    = 20,

    file_out    = 21,
    file_in_    = 22,

    int64       = 23,
    flt64       = 24,
    uint256     = 25,
    encode      = 26,

    halt    = Short.MAX_VALUE;
}
