package com.riverssen.protocols;


import java.io.*;

public class TransactionOutput
{
	private byte[] txid = new byte[32];
	private Address owner = new Address();

	//GETTERS

	public byte[] getTxid() { return txid; }

	//GETTERS

	public Address getOwner() { return owner; }

	//SETTERS

	private void  setTxid(byte[] txid) { this.txid = txid; }

	//SETTERS

	private void  setOwner(Address owner) { this.owner = owner; }



	public void write(final DataOutputStream stream) throws IOException
	{
		stream.write(null);
		stream.write(null);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.txid = stream.read(null);
		this.owner = stream.read(null);
	}
}