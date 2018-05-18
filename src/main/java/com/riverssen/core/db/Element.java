package com.riverssen.core.db;

public class Element<T>
{
    private T t;

    public Element(T t)
    {
        this.t = t;
    }

    public T getElement() { return t; }

    public String getElementHash()
    {
//        return HashUtil.applySha256(t);
        return null;
    }

    public String toString()
    {
        return t.toString();
    }
}
