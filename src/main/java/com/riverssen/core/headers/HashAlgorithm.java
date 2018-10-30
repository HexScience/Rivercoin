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

package com.riverssen.core.headers;

import com.riverssen.core.utils.ByteUtil;

public interface HashAlgorithm
{
    byte[] encode(byte data[]);
    String encode16(byte data[]);
    String encode32(byte data[]);
    String encode58(byte data[]);
    String encode64(byte data[]);

    static byte[] xorif(int max, byte[] a)
    {
        if (a.length > max)
        {
            byte[] a1 = ByteUtil.trim(a, max, a.length);
            byte[] a2 = ByteUtil.trim(a, 0, max);

            byte[] a3 = new byte[a2.length];

            for (int i = 0; i < a3.length; i ++)
                a3[i] = (byte) (a1[i] | a2[i]);

            return a3;
        }

        return a;
    }

    default int numBits() { return 256; }
}