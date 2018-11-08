package nucleus.protocol.transaction;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TransactionOutput
{
	/** Output value **/
	private long	value 		= 0;
	/** The spend or "unlock" script **/
	private byte[]  spendScript = new byte[0];

	public TransactionOutput()
	{
	}

	public TransactionOutput(long value, byte spendScript[])
	{
		this.value			= value;
		this.spendScript	= spendScript;
	}

	public void write(final DataOutputStream stream) throws IOException
	{
		stream.writeLong(value);
		stream.writeInt(spendScript.length);
		stream.write(spendScript);
	}

	public void read(final DataInputStream stream) throws IOException
	{
		this.value 	= stream.readLong();
		int size	= stream.readInt();
		spendScript = new byte[size];

		stream.read(spendScript);
	}

	public long getValue()
	{
		return value;
	}

	public byte[] getScriptPubKey()
	{
		return spendScript;
	}
}