package com.riverssen.protocols;


import java.io.*;

public class CompressedKey
{
	private byte[] key = new byte[37];

	//GETTERS

	public byte[] getKey() { return key; }

	//SETTERS

	private void  setKey(byte[] key) { this.key = key; }



	public void write(final DataOutputStream stream) throws IOException
	{
		stream.write(null);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.key = stream.read(null);
	}
}