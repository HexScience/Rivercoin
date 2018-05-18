package com.riverssen.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LinkedList<T>
{
    private Set<T>      set;
    private Element<T>  root;

    public int size()
    {
        return set.size();
    }

    private class Element<T>
    {
        private T           t;
        private Element<T>  next;

        private Element(T t)
        {
            this.t = t;
        }

        private void add(Element<T> element)
        {
            if(next == null) next = element;
            else next.add(element);
        }
    }

    public LinkedList()
    {
        set = Collections.synchronizedSet(new HashSet<>());
    }

    public void add(T element)
    {
        if(set.contains(element)) return;

        if(root == null)
            root = new Element<>(element);
        else root.add(new Element<>(element));

        set.add(element);
    }

    public boolean contains(T element)
    {
        return set.contains(element);
    }

    public void removeEldestEntry()
    {
        set.remove(root.t);
        root = root.next;
    }
}