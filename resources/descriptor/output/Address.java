package com.riverssen.protocols;


import java.io.*;

public class Address
{
	private byte[] address = new byte[25];

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
		this.address = stream.read(null);
	}
}