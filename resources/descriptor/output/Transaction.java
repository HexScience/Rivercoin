package com.riverssen.protocols;


import java.io.*;

public class Transaction
{
	private CompressedKey sender = new CompressedKey();
	private Address receiver = new Address();
	private long amount;
	private List<TransactionOutput> utxos = new List<>();
	private long fee;
	private byte[] comment = new byte[256];
	private bytes signature;

	//GETTERS

	public CompressedKey getSender() { return sender; }

	//GETTERS

	public Address getReceiver() { return receiver; }

	//GETTERS

	public long getAmount() { return amount; }

	//GETTERS

	public List<TransactionOutput> getUtxos() { return utxos; }

	//GETTERS

	public long getFee() { return fee; }

	//GETTERS

	public byte[] getComment() { return comment; }

	//GETTERS

	public bytes getSignature() { return signature; }

	//SETTERS

	private void  setSender(CompressedKey sender) { this.sender = sender; }

	//SETTERS

	private void  setReceiver(Address receiver) { this.receiver = receiver; }

	//SETTERS

	private void  setAmount(long amount) { this.amount = amount; }

	//SETTERS

	private void  setUtxos(List<TransactionOutput> utxos) { this.utxos = utxos; }

	//SETTERS

	private void  setFee(long fee) { this.fee = fee; }

	//SETTERS

	private void  setComment(byte[] comment) { this.comment = comment; }

	//SETTERS

	private void  setSignature(bytes signature) { this.signature = signature; }



	public void write(final DataOutputStream stream) throws IOException
	{
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.sender = stream.read(null);
		this.receiver = stream.read(null);
		this.amount = stream.read(null);
		this.utxos = stream.read(null);
		this.fee = stream.read(null);
		this.comment = stream.read(null);
		this.signature = stream.read(null);
	}
}