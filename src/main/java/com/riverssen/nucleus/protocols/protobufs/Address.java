package com.riverssen.nucleus.protocols.protobufs;


import com.riverssen.nucleus.util.Base58;
import com.riverssen.nucleus.util.ByteUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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

	public Address(String address)
	{
		this.address = Base58.decode(address);
	}

	//GETTERS

	public byte[] getAddress() { return address; }

	//SETTERS

	private void  setAddress(byte[] address) { this.address = address; }

	public void write(final DataOutputStream stream) throws IOException
	{
		stream.write(address);
	}

	public void read(final DataInputStream stream) throws IOException
	{
		stream.read(address);
	}

	public byte[] getBytes()
	{
		return address;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Address)
		return ByteUtil.equals(address, ((Address) obj).address);
		return false;
	}

	public String toString()
	{
		return Base58.encode(address);
	}
}