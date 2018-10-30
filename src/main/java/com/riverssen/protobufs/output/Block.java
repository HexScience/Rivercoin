package com.riverssen.protobufs.output;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Block
{
	private Header header = new Header();
	private List<Transaction> transactions = new ArrayList<>();

	//GETTERS

	public Header getHeader() { return header; }

	//GETTERS

	public List<Transaction> getTransactions() { return transactions; }

	//SETTERS

	private void  setHeader(Header header) { this.header = header; }

	//SETTERS

	private void  setTransactions(List<Transaction> transactions) { this.transactions = transactions; }



	public void write(final DataOutputStream stream) throws IOException
	{
		stream.write(null);
		stream.write(null);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.header.read(stream);
//		this.transactions = stream.read(null);
	}
}