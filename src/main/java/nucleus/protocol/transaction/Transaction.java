package nucleus.protocol.transaction;


import nucleus.crypto.ec.ECDerivedPublicKey;
import nucleus.ledger.AddressBalanceTable;
import nucleus.ledger.LedgerDatabase;
import nucleus.protocol.protobufs.Address;
import nucleus.system.Context;
import nucleus.util.HashUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Transaction
{
	private int						version		= 0;
	private short					flag		= 0;
	private long					locktime	= 0;
	/** an ECPublicKey's that can be used to send and unlock the transaction inputs. **/
	private ECDerivedPublicKey 		sender 		= new ECDerivedPublicKey();
	/** an array containing outputs that if the transaction succeeds, will be added to the ledger. **/
	private Output					outputs[]	= new Output[1];
	/** a list of UTXO indices **/
	private TransactionInput 		utxos[] 	= new TransactionInput[0];
	/** a comment written by the sender **/
	private byte 					comment[] 	= new byte[256];
	/** a signature/array of signatures **/
	private Signature 				signature[] = new Signature[1];
	/** maximum fee to pay for this transaction **/
	private long 					fee;
	/**
	 * a payload to be injected into the ledger
	 * fee might be higher depending on the
	 * size of the payload.
	 */
	private byte					payload[] 	= new byte[0];

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
	public Transaction(ECDerivedPublicKey from, Address to, long amount, List<Integer> utxos, long fee, byte comment[])
	{
		setSender(from);
//		setReceiver(to);
//		setAmount(amount);
//		setUtxos(utxos);
		setFee(fee);
		setComment(comment);
	}

//	public static byte[] sign(Transaction transaction, BigInteger privateKey, String pph) throws IOException {
//		return privateKey.sign(transaction.getSignatureData(), pph.getBytes());
//	}

	private byte[] getSignatureData() throws IOException {
		ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(stream1);

//		sender.write(stream);
//		receiver.write(stream);
//		stream.writeLong(amount);
//		stream.writeShort(utxos.size());
//		for (Integer output : utxos)
//			stream.writeInt(output);
//		stream.write(comment);

		stream.flush();
		stream.close();
		stream1.flush();
		stream1.close();

		return stream1.toByteArray();
	}

	//GETTERS

	public ECDerivedPublicKey getSender() { return sender; }
//	public Address getReceiver() { return receiver; }
//	public long getAmount()
//	{
//		return amount;
//	}
//	public List<Integer> getUtxos() { return utxos; }
	public long getFee() { return fee; }
	public byte[] getComment() { return comment; }

	//SETTERS
	private void  setSender(ECDerivedPublicKey sender) { this.sender = sender; }
//	private void  setReceiver(Address receiver) { this.receiver = receiver; }
//	private void  setAmount(long amount) { this.amount = amount; }
//	private void  setUtxos(List<Integer> utxos) { this.utxos = utxos; }
	private void  setFee(long fee) { this.fee = fee; }
	private void 	setComment(byte comment[])
	{
		this.comment = comment;
	}
	private Signature[] getSignate()
	{
		return signature;
	}

	//IO Functions And Commands

	public void write(final DataOutputStream stream) throws IOException
	{
		sender.write(stream);
//		receiver.write(stream);
//		stream.writeLong(amount);
//		stream.writeShort(utxos.size());
//		for (Integer output : utxos)
//			stream.writeInt(output);
//		stream.write(comment);
//		stream.writeShort(signature.length);
//		stream.write(signature);
	}


	public void read(final DataInputStream stream) throws IOException
	{
//		this.sender.read(stream);
//		this.receiver.read(stream);
//		this.amount = stream.readLong();
//		long numUTXOS = stream.readShort();
//
//		for (int i = 0; i < numUTXOS; i ++)
			TransactionOutput output = new TransactionOutput();
			output.read(stream);
//			this.utxos.add(stream.readInt());
//
//		this.fee = stream.readLong();
//		stream.read(comment);
//		short signatureLength = stream.readShort();
//
//		this.signature = new byte[signatureLength];
//		stream.read(signature);
	}

	public boolean isTransactionValid() throws Exception {
		/** check against the signature **/
//		sender.verify(signature, getSignatureData());
		return true;
	}

	public byte[] getBytes() throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(stream);

		write(dos);

		dos.flush();
		dos.close();

		return stream.toByteArray();
	}

	public byte[] getTXID() throws IOException
	{
		return HashUtil.applySha512(HashUtil.applySha512(getBytes()));
	}

	public long collectiveValue(AddressBalanceTable abt)
	{
		long b = 0;

//		for (TransactionInput utxo_input : utxos)
//			b += abt.getOutput(output).getValue();

		return b;
	}

	public List<TransactionOutput> calculateOutputs(Context context, LedgerDatabase db, Address miner)
	{
		List<TransactionOutput> outputs = new ArrayList<>();

		long collective = collectiveValue(db.getAddressBalanceTable(sender.toAddress()));

//		long change = (collective - fee) - amount;

		return outputs;
	}
}