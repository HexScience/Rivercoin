package com.riverssen.utils;

public class BinarySearchTree<T extends Encodeable>
{
    BinaryElement<T> root;

    public void add(T t)
    {
    }

    public boolean contains(T t)
    {
        return false;
    }

    private class BinaryElement<T extends Encodeable> implements Encodeable
    {
        BinaryElement left;
        BinaryElement right;
        T             value;

        @Override
        public byte[] getBytes()
        {
            return new byte[0];
        }
    }
}