package com.riverssen.protobufs.output;


import com.riverssen.wallet.PrivKey;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Transaction
{
	private CompressedKey 			sender = new CompressedKey();
	private Address 				receiver = new Address();
	private long 					amount;
	private List<Integer>			utxos = new ArrayList<>();
	private long 					fee;
	private byte 					comment[] = new byte[256];
	private byte 					signature[] = new byte[0];

	public Transaction()
	{
	}

	/**
	 * @param from
	 * @param to
	 * @param amount
	 * @param utxos
	 * @param fee
	 * @param comment
	 */
	public Transaction(CompressedKey from, Address to, long amount, List<Integer> utxos, long fee, byte comment[])
	{
		setSender(from);
		setReceiver(to);
		setAmount(amount);
		setUtxos(utxos);
		setFee(fee);
		setComment(comment);
	}

	public static byte[] sign(Transaction transaction, PrivKey privateKey, String pph) throws IOException {
		return privateKey.sign(transaction.getSignatureData(), pph.getBytes());
	}

	private byte[] getSignatureData() throws IOException {
		ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(stream1);

		sender.write(stream);
		receiver.write(stream);
		stream.writeLong(amount);
		stream.writeShort(utxos.size());
		for (Integer output : utxos)
			stream.writeInt(output);
		stream.write(comment);

		stream.flush();
		stream.close();
		stream1.flush();
		stream1.close();

		return stream1.toByteArray();
	}

	//GETTERS

	public CompressedKey getSender() { return sender; }
	public Address getReceiver() { return receiver; }
	public long getAmount() { return amount; }
	public List<Integer> getUtxos() { return utxos; }
	public long getFee() { return fee; }
	public byte[] getComment() { return comment; }

	//SETTERS
	private void  setSender(CompressedKey sender) { this.sender = sender; }
	private void  setReceiver(Address receiver) { this.receiver = receiver; }
	private void  setAmount(long amount) { this.amount = amount; }
	private void  setUtxos(List<Integer> utxos) { this.utxos = utxos; }
	private void  setFee(long fee) { this.fee = fee; }
	private void setComment(byte comment[])
	{
		this.comment = comment;
	}

	//IO Functions And Commands

	public void write(final DataOutputStream stream) throws IOException
	{
		sender.write(stream);
		receiver.write(stream);
		stream.writeLong(amount);
		stream.writeShort(utxos.size());
		for (Integer output : utxos)
			stream.writeInt(output);
		stream.write(comment);
		stream.writeShort(signature.length);
		stream.write(signature);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.sender.read(stream);
		this.receiver.read(stream);
		this.amount = stream.readLong();
		long numUTXOS = stream.readShort();

		for (int i = 0; i < numUTXOS; i ++)
//			TransactionOutput output = new TransactionOutput();
//			output.read(stream);
			this.utxos.add(stream.readInt());

		this.fee = stream.readLong();
		stream.read(comment);
		short signatureLength = stream.readShort();

		this.signature = new byte[signatureLength];
		stream.read(signature);
	}

	public boolean isTransactionValid() throws Exception {
		/** check against the signature **/
		sender.verify(signature, getSignatureData());
		return true;
	}
}