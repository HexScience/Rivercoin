package com.riverssen.core.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Message<T>
{
    long header();
    void send(DataOutputStream out, T information);
    T    receive(DataInputStream in);

    void performAction(DataInputStream in);
}
