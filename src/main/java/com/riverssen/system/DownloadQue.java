package com.riverssen.system;

import com.riverssen.core.block.FullBlock;

import java.util.*;

public class DownloadQue<T>
{
    public class QueElement<T>{
        String address;
        Queue<T> elements = new PriorityQueue<>();

        private QueElement(String address)
        {
            this.address = address;
        }

        private void add(T t)
        {
            if (!this.elements.contains(t))
                this.elements.add(t);
        }

        public Queue<T> getQueue()
        {
            return elements;
        }
    }

    private Map<String, QueElement> map = Collections.synchronizedMap(new HashMap<>());

    public void add(T tElement, String address)
    {
        if (map.containsKey(address))
            map.get(address).add(tElement);
        else {
            QueElement element = new QueElement(address);
            element.add(tElement);

            map.put(address, element);
        }
    }

    public QueElement getElements(String address)
    {
        if (!map.containsKey(address))
            return new QueElement(address);

        return map.get(address);
    }

    public int size()
    {
        return map.size();
    }

    public Set<String> addresses()
    {
        return map.keySet();
    }

    public QueElement getLongest()
    {
        String longest = addresses().iterator().next();

        for (String string : addresses())
            if (map.get(string).elements.size() > map.get(longest).elements.size())
                longest = string;

        return map.get(longest);
    }
}