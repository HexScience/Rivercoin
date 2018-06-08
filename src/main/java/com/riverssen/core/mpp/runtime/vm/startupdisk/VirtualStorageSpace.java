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

package com.riverssen.core.mpp.runtime.vm.startupdisk;

import com.riverssen.core.mpp.runtime.vm.VirtualMachine;
import com.riverssen.core.mpp.runtime.vm.memory.MemObject;
import com.riverssen.core.rvm.MathContext;

import java.io.File;
import java.util.HashMap;
import java.util.Stack;

public class VirtualStorageSpace
{
    private final long size;
    private final File root;
    private long       remaining;

    public VirtualStorageSpace(final long size, File location)
    {
        this.size = size;
        this.root = location;
    }

    public void accessFile(String hash, MemObject out)
    {
        File file = new File(root + File.separator + hash);
        if(file.exists())
        {
        }
    }

    public long getRemaining()
    {
        return remaining;
    }

    public File getRoot()
    {
        return root;
    }

    public long getSize()
    {
        return size;
    }
}
