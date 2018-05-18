package com.riverssen.core.chain;

import com.riverssen.core.Config;
import com.riverssen.core.RiverCoin;
import com.riverssen.core.tokens.Token;
import com.riverssen.core.security.CompressedAddress;
import com.riverssen.core.security.PublicAddress;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Transaction In/Out
 */
public class TXIO
{
    private static final WalletOutputInput empty    = new WalletOutputInput();
    private static final byte              INPUT    = 0;
    private static final byte              OUTPUT   = 1;
    private Map<String, WalletOutputInput> addresses;

    public TXIO()
    {
        addresses = Collections.synchronizedMap(new HashMap<>());
    }

    public TXIO(DataInputStream stream) throws IOException
    {
        addresses = Collections.synchronizedMap(new HashMap<>());

//        String tx[] = txio.split("\n");

        String address = "";

        int size = stream.readInt();

        for(int i = 0; i < size; i ++)
        {
//            if(line.startsWith("a:"))
//            {
                address = stream.readUTF();//line.substring(2);
                if(!addresses.containsKey(address))
                    addresses.put(address, new WalletOutputInput());

                int txio = stream.readInt();

                for(int t = 0; t < txio; t ++)
                {
                    byte type = stream.readByte();

                    byte bytes[] = new byte[RiverCoin.MAX_BYTES];
                    stream.read(bytes);

                    switch (type)
                    {
                        case INPUT: addresses.get(address).addInput(new RiverCoin(new BigInteger(bytes)), stream.readUTF());
                        case OUTPUT: addresses.get(address).addOutput(new RiverCoin(new BigInteger(bytes)), stream.readUTF());
                    }
                }
//            } else if(line.startsWith("o:")) addresses.get(address).addOutput(line);
//            else if(line.startsWith("i:")) addresses.get(address).addInput(line);
        }
    }

    //Check in a local hashmap that this file doesn't exist.
    public static synchronized boolean transactionSafe(Token token)
    {
        File file_trx = new File(Config.getConfig().BLOCKCHAIN_TRX_DB + File.separator + token.getHashAsString());

        return !file_trx.exists();
    }

    public synchronized void add(Token token)
    {
        CompressedAddress sender    = new CompressedAddress(token.getSenderAddress());
        PublicAddress receiver      = new PublicAddress(token.getReceiverAddress());
        RiverCoin amount = token.getAmount();

        PublicAddress senderAddress = new PublicAddress("");

        if(sender.toPublicKey() != null) senderAddress = sender.toPublicKey().getPublicWalletAddress();
        String senderSender = (senderAddress.toString() == null ? "null" : senderAddress.toString());
//        if(receiver == null)                receiver          = "null";

        if(addresses.containsKey(senderSender))
            addresses.get(senderSender).addOutput(amount, token.getHashAsString());
        else
        {
            addresses.put(senderSender, new WalletOutputInput());
            addresses.get(senderSender).addOutput(amount, token.getHashAsString());
        }

        if(addresses.containsKey(receiver.toString()))
            addresses.get(receiver.toString()).addInput(amount, token.getHashAsString());
        else
        {
            addresses.put(receiver.toString(), new WalletOutputInput());
            addresses.get(receiver.toString()).addInput(amount, token.getHashAsString());
        }
    }

    public synchronized WalletOutputInput get(String publicAddress)
    {
        if(addresses.containsKey(publicAddress)) return addresses.get(publicAddress);
        return empty;
    }

    public byte[] getBytes()
    {
        try{
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(stream);

            dos.writeInt(addresses.size());

            for(String address : addresses.keySet())
                addresses.get(address).exportToDataOutputStream(dos);

            dos.flush();
            dos.close();
            stream.flush();
            stream.close();

            return stream.toByteArray();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return new byte[1];
    }

    @Override
    public String toString()
    {
        synchronized (addresses)
        {
            String string = "";//"\"TXIO\":{\n";

            for(String address : addresses.keySet())
            {
//                string += "a:" + address + "\n";
//                for(String txn : addresses.get(address))
//                    string +=
////                        "tx:" +
//                            txn + "\n";
                string += addresses.get(address).toString();
            }
            return string;
        }
    }

    public synchronized void addAll(TXIO txio)
    {
        synchronized (txio)
        {
            for(String address : txio.addresses.keySet())
            {
                this.addresses.put(address, new WalletOutputInput());
                this.addresses.get(address).addAll(txio.get(address));
            }
        }
    }

    public Map<String, WalletOutputInput> getAddresses()
    {
        return addresses;
    }
}
