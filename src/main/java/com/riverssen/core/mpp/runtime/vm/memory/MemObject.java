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

package com.riverssen.core.mpp.runtime.vm.memory;

import com.riverssen.core.mpp.runtime.vm.VirtualMachine;
import com.riverssen.core.rvm.Callable;
import com.riverssen.core.rvm.MathContext;

public interface MemObject extends MathContext, Callable
{
    static final int
            BYTE_ARRAY = 0,
            INT_ARRAY  = 1,
            FLT_ARRAY  = 2,
            BOOL_ARRAY = 3,
            PTR_ARRAY  = 4,

            POINTER    = 5,
            INTEGER    = 6,
            UINT256    = 8,
            BOOL       = 9,


            RSA_KP     = 10,
            RSA_KEY    = 11,
            ECDSA_KP   = 12,
            ECDSA_KEY  = 13,
            PUBLIC_ADDRESS = 14;

    public int getType();

    public long getPointer();

    public void fromBytes(byte data[]);

    MemObject get(long address);

    void store(VirtualMachine virtualMachine);
}
