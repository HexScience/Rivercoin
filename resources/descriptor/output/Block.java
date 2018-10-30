package com.riverssen.protocols;


import java.io.*;

public class Block
{
	private Header header = new Header();
	private List<int> transactions = new List<>();

	//GETTERS

	public Header getHeader() { return header; }

	//GETTERS

	public List<int> getTransactions() { return transactions; }

	//SETTERS

	private void  setHeader(Header header) { this.header = header; }

	//SETTERS

	private void  setTransactions(List<int> transactions) { this.transactions = transactions; }



	public void write(final DataOutputStream stream) throws IOException
	{
		stream.write(null);
		stream.write(null);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.header = stream.read(null);
		this.transactions = stream.read(null);
	}
}