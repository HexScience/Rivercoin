package com.riverssen.core.mpp.runtime.vm.global;

import com.riverssen.core.mpp.runtime.vm.VirtualMachine;
import com.riverssen.core.mpp.runtime.vm.memory.MemObject;
import com.riverssen.core.mpp.runtime.vm.memory.VirtualMemory;

public class Methods
{
    public static void addGlobalMethods(VirtualMemory memory)
    {
        memory.addObject(new readFile());
        memory.addObject(new writeFile());
    }

    /**
     * File: location
     *  hello world!
     * Usage:
     *          MemBlock bytes;
     *          readFile(bytes, 'location');
     *
     *
     *
     *
     *          println(bytes);
     *
     * Output:
     *
     *          hello world!
     */
    private static class writeFile
            implements MemObject
    {
        @Override
        public void call(VirtualMachine context)
        {
            String string = context.getMemory().pop().toString();
            boolean bool  = context.getStorage().writeFile(string, context.getMemory().pop());
            context.getMemory().push(MemObject.allocate(new byte[] {bool ? (byte)1 : (byte)0}));
        }

        @Override
        public void set(int address, int size, MemObject object)
        {
        }

        @Override
        public void set(byte[] arr)
        {
        }

        @Override
        public void setNull()
        {
        }

        @Override
        public int size()
        {
            return 0;
        }

        @Override
        public byte[] get()
        {
            return new byte[0];
        }

        @Override
        public long getLong(int address)
        {
            return 0;
        }

        @Override
        public void getObject(int address, int size, MemObject object)
        {
        }
    }

    /**
     * File: location
     *  hello world!
     * Usage:
     *          MemBlock bytes;
     *          readFile(bytes, 'location');
     *
     *
     *
     *
     *          println(bytes);
     *
     * Output:
     *
     *          hello world!
     */
    private static class readFile
    implements MemObject
    {
        @Override
        public void call(VirtualMachine context)
        {
            String string = context.getMemory().pop().toString();
            context.getStorage().readFile(string, context.getMemory().peek());
        }

        @Override
        public void set(int address, int size, MemObject object)
        {
        }

        @Override
        public void set(byte[] arr)
        {
        }

        @Override
        public void setNull()
        {
        }

        @Override
        public int size()
        {
            return 0;
        }

        @Override
        public byte[] get()
        {
            return new byte[0];
        }

        @Override
        public long getLong(int address)
        {
            return 0;
        }

        @Override
        public void getObject(int address, int size, MemObject object)
        {
        }
    }
}
