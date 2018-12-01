package nucleus.protocols.protobufs;


import nucleus.protocols.transaction.Transaction;
import nucleus.protocols.transaction.TransactionOutput;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Block implements StrippedObject
{
	private BlockHeader header = new BlockHeader();
	private List<Transaction> transactions = new ArrayList<>();
	private List<TransactionOutput> coinbase;
	private byte codebase[];

	public Block()
	{
	}

	public Block(DataInputStream inputStream)
	{
	}

	public Block(BlockHeader header, List<Transaction> buffer, byte codebase[])
	{
		this.header = header;
		this.transactions = buffer;
		this.codebase = codebase;
		this.coinbase = new ArrayList<>();
	}

	public void build()
	{
		this.codebase = null;
	}

	//GETTERS

	public BlockHeader getHeader() { return header; }

	//GETTERS

	public List<Transaction> getTransactions() { return transactions; }

	//SETTERS

	private void  setHeader(BlockHeader header) { this.header = header; }

	//SETTERS

	private void  setTransactions(List<Transaction> transactions) { this.transactions = transactions; }


	public void write(final DataOutputStream stream) throws IOException
	{
		header.write(stream);
		stream.writeShort(transactions.size());

		for (Transaction transaction : transactions)
			transaction.write(stream);

		stream.writeShort(codebase.length);
		stream.write(codebase);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.header.read(stream);
		int numTransactions = stream.readShort();

		for (int i = 0; i < numTransactions; i ++)
		{
			Transaction transaction = new Transaction();

			transaction.read(stream);

			transactions.add(transaction);
		}

		short cbl = stream.readShort();
		codebase = new byte[cbl];

		stream.read(codebase);
	}

	public List<TransactionOutput> getCoinbase()
	{
		return coinbase;
	}

	@Override
	public byte[] strip()
	{
		return new byte[0];
	}
}