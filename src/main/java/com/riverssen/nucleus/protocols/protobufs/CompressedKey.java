package com.riverssen.nucleus.protocols.protobufs;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CompressedKey
{
	private byte[] key = new byte[33];

	public CompressedKey() {}

	public CompressedKey(byte key[])
	{
		setKey(key);
	}

	//GETTERS

	public byte[] getKey() { return key; }

	//SETTERS

	public void  setKey(byte[] key) { this.key = key; }

	public void write(final DataOutputStream stream) throws IOException
	{
		stream.write(key.length);
		stream.write(key);
	}

	public void read(final DataInputStream stream) throws IOException
	{
		int length = stream.read();
		this.key = new byte[length];
		stream.read(this.key);
	}

	public void verify(byte[] signature, byte[] signatureData) {
	}
}