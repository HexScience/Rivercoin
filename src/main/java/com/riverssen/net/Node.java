package com.riverssen.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Node implements Runnable
{
    /** a list of peers to attempt to connect to if all else fails **/
    private String[] fallbackPeers = {};
    /** Server **/
    private ServerSocket socket;
    private boolean      keepAlive;
    private Set<Peer>    peerGroup;

    public Node() throws IOException {
        socket = new ServerSocket(5110);
        keepAlive = true;
        peerGroup = new LinkedHashSet<>();

        ExecutorService service = Executors.newFixedThreadPool(1);
        service.submit(()->{
            while (keepAlive)
            {
                try {
                    Socket peer = socket.accept();
                    Peer cpeer = new Peer(this, peer);

                    cpeer.ping();
                    addPeer(cpeer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private synchronized void addPeer(Peer peer)
    {
        peerGroup.add(peer);
    }

    public void stop()
    {
        keepAlive = false;
    }

    @Override
    public void run()
    {
    }

    public boolean keepAlive() {
        return keepAlive;
    }
}