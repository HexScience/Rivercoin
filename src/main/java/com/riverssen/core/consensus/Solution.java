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

package com.riverssen.core.consensus;

import com.riverssen.core.FullBlock;
import com.riverssen.core.headers.Encodeable;
import com.riverssen.core.headers.Exportable;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.utils.SmartDataTransferer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Solution implements Encodeable, Exportable
{
    private boolean   isLocal;
    private FullBlock block;

    public Solution(DataInputStream in) throws IOException
    {
        this.isLocal = false;
        this.block   = new FullBlock(in);
    }

    public Solution(FullBlock block)
    {
        this.isLocal = true;
        this.block   = block;
    }

    @Override
    public byte[] getBytes()
    {
        return new byte[0];
    }

    public long blockID()
    {
        return block.getBlockID();
    }

    public boolean solutionValid(ContextI context)
    {
        return block.validate(context) == 0;
    }

    public FullBlock getBlock()
    {
        return block;
    }

    @Override
    public byte[] header()
    {
        return new byte[0];
    }

    @Override
    public byte[] content()
    {
        return new byte[0];
    }

    @Override
    public void export(SmartDataTransferer smdt)
    {
    }

    @Override
    public void export(DataOutputStream dost) throws IOException
    {
        block.export(dost);
    }
}