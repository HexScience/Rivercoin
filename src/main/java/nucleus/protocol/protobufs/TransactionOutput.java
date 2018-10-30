package nucleus.protocol.protobufs;


import com.riverssen.core.utils.ByteUtil;
import nucleus.system.Context;
import nucleus.util.HashUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TransactionOutput
{
	/**
	 * a sha512(sha512(origin)) where origin is the previous transaction.
	 */

	private byte[] 	txid 		= new byte[64];
	/**
	 * the value of the output.
	 */
	private long	value 		= 0;

	private byte[]  spendScript = new byte[0];

	public TransactionOutput()
	{
	}

	public TransactionOutput(byte txid[], long value[])
	{
	}

//	GETTERS

	public byte[] getTxid() { return txid; }

//	SETTERS

	private void  setTxid(byte[] txid) { this.txid = txid; }

	public void write(final DataOutputStream stream) throws IOException
	{
		stream.write(txid);
//		stream.write(checksum);
		stream.writeLong(value);
	}

	public void read(final DataInputStream stream) throws IOException
	{
		stream.read(txid);
//		stream.read(checksum);
		this.value = stream.readLong();
	}

//	public boolean isCorrectOwner(Address address)
//	{
//		return ByteUtil.equals(checksum, ByteUtil.trim(HashUtil.applySha512(HashUtil.applySha512(ByteUtil.concatenate(address.getBytes(), txid))), 59, 64));
//	}

	public long getValue()
	{
		return value;
	}
}