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

package com.riverssen.core.rvm.objects;

import com.riverssen.core.rvm.MathContext;
import com.riverssen.core.mpp.runtime.vm.memory.MemObject;
import com.riverssen.core.mpp.runtime.vm.VirtualMachine;

import java.security.KeyPairGenerator;

public class rsa_pair implements MemObject
{
    private rsa_key pair[];

    public rsa_pair()
    {
    }

    public rsa_pair(int length)
    {
        try{
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(length);
            byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
            StringBuffer retString = new StringBuffer();
            for (int i = 0; i < publicKey.length; ++i)
            {
                retString.append(Integer.toHexString(0x0100 + (publicKey[i] & 0x00FF)).substring(1));
            }
            System.out.println(retString);
        } catch (Exception e)
        {
        }
    }

    @Override
    public int getType() {
        return RSA_KP;
    }

    @Override
    public long getPointer()
    {
        return 0;
    }

    @Override
    public void fromBytes(byte[] data) {
    }

    @Override
    public MemObject get(long address) {
        return pair[(int) address];
    }

    @Override
    public void store(VirtualMachine virtualMachine) {

    }

    @Override
    public void call(VirtualMachine virtualMachine) {

    }

    @Override
    public <T extends MathContext> T add(T b) {
        return null;
    }

    @Override
    public <T extends MathContext> T sub(T b) {
        return null;
    }

    @Override
    public <T extends MathContext> T mul(T b) {
        return null;
    }

    @Override
    public <T extends MathContext> T div(T b) {
        return null;
    }

    @Override
    public <T extends MathContext> T mod(T b) {
        return null;
    }
}
