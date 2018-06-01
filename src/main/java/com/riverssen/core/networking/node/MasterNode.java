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

package com.riverssen.core.networking.node;

import com.riverssen.core.FullBlock;
import com.riverssen.core.security.PublicAddress;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MasterNode
{
    private List<Node>              peerNodes;
    private List<Node>              peerClients;
    private Map<Long, FullBlock>    newBlocks;
    private boolean                 running;
    private PublicAddress           nodeAddress;

    public MasterNode()
    {
        this.peerNodes      = new ArrayList<>();
        this.peerClients    = new ArrayList<>();
        this.newBlocks      = new LinkedHashMap<>();
        this.running        = true;
    }

    private void fetchAll()
    {
        for(Node node : peerNodes)
            node.fetch(this);
    }

    private void sendAll()
    {
    }

    public void run()
    {
        while(running)
        {
            fetchAll();
            sendAll();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}