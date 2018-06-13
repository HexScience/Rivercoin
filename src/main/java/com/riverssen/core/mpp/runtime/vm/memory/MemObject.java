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

import java.nio.ByteBuffer;

public interface MemObject
{
    void set(int address, int size, MemObject object);

    void set(byte arr[]);

    public void setNull();

    public int  size();

    public byte[] get();

    public long getLong(int address);

    public void getObject(int address, int size, MemObject object);

    public static MemObject allocate(int size)
    {
        return new ByteArrayMemObject(new byte[size]);
    }

    public static MemObject allocate(byte array[])
    {
        return new ByteArrayMemObject(array);
    }

    public static MemObject allocate(int address, int size, MemObject object)
    {
        return new ByteArrayMemObject(new byte[size]);
    }

    public default boolean isNull()
    {
        return get() == null;
    }

    public default void call(VirtualMachine context)
    {
    }

    class ByteArrayMemObject
    implements MemObject{
        private byte array[];

        public ByteArrayMemObject(byte arr[])
        {
            this.array = arr;
        }

        public void set(int address, int size, MemObject object)
        {
        }

        public void set(byte arr[])
        {
            this.array = arr;
        }

        public void setNull()
        {
            this.array = null;
        }

        public int  size()
        {
            return array.length;
        }

        public byte[] get()
        {
            return array;
        }

        public long getLong(int address)
        {
            return ByteBuffer.wrap(new byte[] {array[address], array[address + 1], array[address + 2], array[address + 3], array[address + 4], array[address + 5], array[address + 6], array[address + 7]}).getLong();
        }

        public void getObject(int address, int size, MemObject object)
        {
            object.set(address, size, this);
        }
    }

    class PointerToArrayMemObject
            implements MemObject{
        private MemObject array;
        private int       address;
        private int       size;

        public PointerToArrayMemObject(int address, int size, MemObject object)
        {
            this.address = address;
            this.array   = object;
            this.size    = size;
        }

        public void set(int address, int size, MemObject object)
        {
            this.address = address;
            this.array   = object;
            this.size    = size;
        }

        public void set(byte arr[])
        {
        }

        public void setNull()
        {
            this.array = null;
        }

        public int  size()
        {
            return size;
        }

        public byte[] get()
        {
            return null;
        }

        public long getLong(int address)
        {
            address = this.address + address;
            byte[] array = this.array.get();
            return ByteBuffer.wrap(new byte[] {array[address], array[address + 1], array[address + 2], array[address + 3], array[address + 4], array[address + 5], array[address + 6], array[address + 7]}).getLong();
        }

        public void getObject(int address, int size, MemObject object)
        {
            object.set(this.address + address, size, this.array);
        }
    }
}
