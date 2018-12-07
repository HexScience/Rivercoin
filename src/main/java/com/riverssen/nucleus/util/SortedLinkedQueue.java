package com.riverssen.nucleus.util;

import java.util.*;

public class SortedLinkedQueue<E extends Comparable> extends AbstractQueue<E> implements Collection<E>
{
//    private Map<IpAddress, Set<Block>> blockQueue;
//
//
//    public void add(E block)
//    {
//        if (blockQueue.containsKey(block.getSender()))
//        {
//            if (!blockQueue.get(block.getSender()).contains(block))
//                blockQueue.get(block.getSender()).add(block.getBlock());
//        }
//        else {
//            blockQueue.put(block.getSender(), new TreeSet<>());
//            blockQueue.get(block.getSender()).add(block.getBlock());
//        }
//    }
    private class Element{
        private E block;

        private Element(E block, Element parent)
        {
            this.block = block;
            this.parent = parent;
        }

        Element child;
        Element parent;

    public Element(Element queue)
    {
        this.child = queue;
    }

    private void switch_nullchild(E e)
        {
            child = new Element(block, this);
            block = e;
        }

        private void switch_notnullchild(E e)
        {
            child.insert(block);
            block = e;
        }

        public boolean insert(E e)
        {
            if (block.equals(e)) return false;

            if (block.compareTo(e) <= 0 && child == null)
                child = new Element(e, this);
            else if (block.compareTo(e) <= 0 && child != null)
                child.insert(e);
            if (block.compareTo(e) > 0 && child == null)
                switch_nullchild(e);
            else if (block.compareTo(e) > 0 && child != null)
                switch_notnullchild(e);
//            if (!this.block.getSender().equals(block.getSender()))
//                return;
//
//            if (this.block.getBlock().getHeader().getHeight() <= block.getBlock().getHeader().getHeight() && child == null)
//                child = new Element(block);
//            else if (this.block.getBlock().getHeader().getHeight() <= block.getBlock().getHeader().getHeight() && child != null)
//                child.add(block);
//            else if (this.block.getBlock().getHeader().getHeight() > block.getBlock().getHeader().getHeight() && child == null)
//            {
//                this.child = new Element(this.block);
//                this.block = block;
//            }
//            else if (this.block.getBlock().getHeader().getHeight() > block.getBlock().getHeader().getHeight() && child != null)
//            {
//
//            }

            return true;
        }

    @Override
    public String toString()
    {
        return block.toString() + (child != null ? (", " + child.toString()) : "");
    }

    public int size()
    {
        return 1 + (child != null ? child.size() : 0);
    }

    public boolean contains(Object o)
    {
        boolean bool = block.equals(o);
        return !bool ? (child == null ? bool : child.contains(o)) : bool;
    }

    public boolean remove(Object o)
    {
        if (block.equals(o))
        {
            if (child != null)
                child.moveBack(this);

            return true;
        }
        else if (child != null)
            return child.remove(o);

        return false;
    }

    private void moveBack(Element element)
    {
        element.block = block;
        if (child != null)
            child.moveBack(this);
        else element.child = null;
    }

    public E get()
    {
        E e = block;

        if (child != null)
            child.moveBack(this);

        return e;
    }
}

    private Element queue;

    public SortedLinkedQueue()
    {
    }

    @Override
    public int size()
    {
        return queue == null ? 0 : queue.size();
    }

    @Override
    public boolean isEmpty()
    {
        return queue == null;
    }

    @Override
    public boolean contains(Object o)
    {
        return queue != null ? queue.contains(o) : false;
    }

    @Override
    public Iterator<E> iterator()
    {
        return new Iterator<E>()
        {
            Element current = new Element(queue);

            @Override
            public boolean hasNext()
            {
                return current.child != null;
            }

            @Override
            public E next()
            {
                return (current = current.child).block;
            }
        };
    }

    @Override
    public Object[] toArray()
    {
        Object array[] = new Object[size()];

        int i = 0;

        for (E block : this)
            array[i ++] = block;

        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] array)
    {
        int i = 0;

        for (E block : this)
            array[i ++] = (T) block;

        return array;
    }

    @Override
    public boolean add(E e)
    {
        return insert(e);
    }

    @Override
    public boolean offer(E e)
    {
        insert(e);
        return true;
    }

    @Override
    public E poll()
    {
        return queue != null ? queue.get() : null;
    }

    @Override
    public E peek()
    {
        return queue != null ? queue.block : null;
    }

    public boolean insert(E block)
    {
        if (queue == null)
            queue = new Element(block, null);
        else return queue.insert(block);

        return true;
    }

    @Override
    public boolean remove(Object o)
    {
        return queue == null ? false : queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        for (Object e : c)
            if (!contains(e))
                return false;
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        for (E e : c)
            add(e);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        for (Object e : c)
            System.out.println(e + " -" + remove(e));

        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        Set<E> toRemove = new HashSet<>();

        for (E e : this)
            if (!c.contains(e))
                toRemove.add(e);

        return removeAll(toRemove);
    }

    @Override
    public void clear()
    {
        queue = null;
    }

    @Override
    public String toString()
    {
        if (queue == null) return "[]";
        return "[" + queue.toString() + "]";
    }
}