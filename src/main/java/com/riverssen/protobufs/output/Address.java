package com.riverssen.protobufs.output;


import java.io.*;

public class Address
{
	private byte[] address = new byte[25];

	public Address()
	{
	}

	public Address(byte address[])
	{
		this.address = address;
	}

	//GETTERS

	public byte[] getAddress() { return address; }

	//SETTERS

	private void  setAddress(byte[] address) { this.address = address; }

	public void write(final DataOutputStream stream) throws IOException
	{
		stream.write(null);
	}

	public void read(final DataInputStream stream) throws IOException
	{
		stream.read(address);
	}
}