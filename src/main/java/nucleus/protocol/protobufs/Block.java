package nucleus.protocol.protobufs;


import nucleus.protocol.transaction.Transaction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Block
{
	private BlockHeader header = new BlockHeader();
	private List<Transaction> transactions = new ArrayList<>();

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
	}
}