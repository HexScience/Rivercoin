package nucleus.protocols.transaction;


import nucleus.crypto.ec.ECDerivedPublicKey;
import nucleus.exceptions.PayLoadException;
import nucleus.ledger.AddressBalanceTable;
import nucleus.protocols.protobufs.Address;
import nucleus.protocols.transactionapi.TransactionPayload;
import nucleus.system.Context;
import nucleus.util.ByteUtil;
import nucleus.util.HashUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static nucleus.util.HashUtil.applySha256;

public class Transaction
{
	/**
	 * Flags
	 */
	public static final class Flag{
		public static final int UNDEFINED = 0x00;
		public static final int P2PKH = 0x01;
		public static final int MULTISIG = 0x02;
		public static final int P2PKH_CARRY_PAYLOAD = 0x03;
		public static final int MULTISIG_CARRY_PAYLOAD = 0x04;
		public static final int PAYLOAD_ONLY = 0x05;
	}

	private int						version		= 0;
	private long					flag		= 0;

	private long					magicheader = 0;
	private long					locktime	= 0;

	/** an array of inputs **/
	private TransactionInput     	inputs[] 	= new TransactionInput[0];
//	/** an array of outputs that if the transaction succeeds, will be added to the ledger. **/
//	private List<byte[]>			outputs		= new ArrayList<>();
	private TransactionOutput		outputs[]	= new TransactionOutput[0];
	/** a comment written by the sender **/
	private byte 					comment[] 	= new byte[256];
	/**
	 * a payload to be injected into the ledger
	 * fee varies depending on the
	 * size of the payload.
	 */
	private byte					payload[] 	= new byte[0];
	private long					timeStamp	= 0L;

	public Transaction()
	{
	}

	public Transaction(long flags, long magh, long locktime, TransactionInput inputs[], TransactionOutput outputs[], byte comment[], byte payload[])
	{
		this.flag = flags;
		this.magicheader = magh;
		this.locktime = locktime;
		this.inputs = inputs;
		this.outputs = outputs;
		this.comment = comment;
		this.payload = payload;
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
	}

	public boolean execute(Context context, Object ledgerdb)
	{
		boolean noErrors = true;
		byte[] transaction = getBytes();
		for (TransactionInput input : inputs)
		{
//			TransactionOutput unspentOutput = ledgerdb.getUTXO(input.getUniqueIdentifier());
//			if (unspentOutput != null)
//			{
//				try{
//					noErrors = noErrors && TransactionPayload.execute(ByteUtil.concatenate(input.getUnlockingScript(), unspentOutput.getScriptPubKey()), transaction);
//				} catch (PayLoadException e)
//				{
//					noErrors = noErrors && false;
//				}
//			} else noErrors = noErrors && false;
		}
//			if (ledgerdb.UTXOExists(utxoid))
//			{
//				TransactionOutput output = ledgerdb.getUTXO(utxoid);
//
//				try
//				{
//					noErrors = noErrors && TransactionPayload.execute(output.getSpendScript(), this);
//					ledgerdb.removeUTXO(utxoid);
//				} catch (PayLoadException e)
//				{
//					e.printStackTrace();
//					noErrors = noErrors && false;
//				}
//			} else noErrors = noErrors && false;
//		}

		return noErrors;
	}

//	public static byte[] sign(Transaction transaction, BigInteger privateKey, String pph) throws IOException {
//		return privateKey.sign(transaction.getSignatureData(), pph.getBytes());
//	}

	private byte[] getSignatureData() throws IOException {
		ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
		DataOutputStream stream = new DataOutputStream(stream1);

		stream.flush();
		stream.close();
		stream1.flush();
		stream1.close();

		return stream1.toByteArray();
	}

	//GETTERS

	public byte[] getComment() { return comment; }

	//SETTERS
	private void 	setComment(byte comment[])
	{
		this.comment = comment;
	}

	//IO Functions And Commands

	public void write(final DataOutputStream stream) throws IOException
	{
	}


	public void read(final DataInputStream stream) throws IOException
	{
	}

	public boolean isTransactionValid() throws Exception {
		/** check against the signature **/
		return true;
	}

	public byte[] getBytes()
	{
		try{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(stream);

			write(dos);

			dos.flush();
			dos.close();

			return stream.toByteArray();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public byte[] getTransactionID()
	{
		return applySha256(applySha256(getBytes()));
	}

	public long collectiveValue(AddressBalanceTable abt)
	{
		long b = 0;

//		for (TransactionInput utxo_input : utxos)
//			b += abt.getOutput(output).getValue();

		return b;
	}

	public List<TransactionOutput> calculateOutputs(Context context, Object db, Address miner)
	{
		List<TransactionOutput> outputs = new ArrayList<>();

//		long collective = collectiveValue(db.getAddressBalanceTable(sender.toAddress()));

//		long change = (collective - fee) - amount;

		return outputs;
	}

	public TransactionOutput getOutput(int output)
	{
//		if (output >= 0 && output < outputs.length)
//			return new TransactionOutput(this, );
//		else return null;
		return null;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public byte[] getHash()
	{
		return applySha256(applySha256(getBytes()));
	}
}