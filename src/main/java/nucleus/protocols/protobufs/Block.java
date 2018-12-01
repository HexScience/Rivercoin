package nucleus.protocols.protobufs;


import nucleus.exceptions.FileServiceException;
import nucleus.protocols.transaction.Transaction;
import nucleus.protocols.transaction.TransactionOutput;
import nucleus.util.FileService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class Block implements StrippedObject
{
    /**
     * A block header that contains important information
     * relating to this block.
     */
	private BlockHeader header = new BlockHeader();
    /**
     * A list of accepted transactions that went through,
     * these transactions are finalized.
     */
	private Set<Transaction>        accepted;
    /**
     * A list of rejected transactions that did not go through,
     * if a transaction is added to the rejected list, then it
     * is safe to re-submit the transaction.
     *
     * Only valid transactions should go into this list,
     * therefore the rejection reason is obvious;
     * Transactions that are valid, but are too old to be
     * considered valid anymore (older than the block meantime)
     * due to low fees or bad communication in the network (la
     * -te arrivals).
     *
     * The rejected transaction is represented by a double sha256
     * transaction hash.
     */
    private Set<byte[]>             rejected;
    /**
     * A list of transaction outputs, the first output should be
     * the reward, the rest are appended as fees.
     */
    private List<TransactionOutput> coinbase;


    /**
     * A simple block constructor for creating an empty block.
     */
    public Block()
	{
	    this.accepted = new LinkedHashSet<>();
	    this.rejected = new LinkedHashSet<>();
	}

    /**
     * @param service
     * @throws IOException
     * @throws FileServiceException
     */
    public Block(FileService service) throws IOException, FileServiceException
    {
        this(service.as(DataInputStream.class));
    }

    /**
     * @param inputStream
     */
	public Block(DataInputStream inputStream)
	{
	}

	public Block(BlockHeader header, Set<Transaction> accepted, Set<byte[]> rejected, byte codebase[])
	{
		this.header     = header;
		this.accepted   = accepted;
		this.rejected   = rejected;
		this.coinbase   = new ArrayList<>();
	}

	//GETTERS

	public BlockHeader getHeader() { return header; }

	//GETTERS

    public Set<Transaction> getAcceptedTransactions() { return accepted; }
    public Set<byte[]>      getRejectedTransactions() { return rejected; }

	//SETTERS

	private void  setHeader(BlockHeader header) { this.header = header; }

	//SETTERS

    private void  setAccepted(Set<Transaction> accepted)    { this.accepted = accepted; }
    private void  setRejected(Set<byte[]> rejected)         { this.rejected = rejected; }

    public void addAcceptedTransaction(Transaction transaction)
    {
        this.accepted.add(transaction);
    }

    public void addRejectedTransaction(Transaction transaction)
    {
        this.rejected.add(transaction.getHash());
    }


	public void write(final DataOutputStream stream) throws IOException
	{
		header.write(stream);
		stream.writeShort(accepted.size());

		for (Transaction transaction : accepted)
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

			accepted.add(transaction);
		}
	}

	public final long latestTransaction()
    {
        if (accepted.size() == 0)
            return -1;

        if (accepted.size() == 1)
            return accepted.iterator().next().getTimeStamp();

        List<Transaction> list = new ArrayList<>(accepted);

        Collections.sort(list, (a, b)->{
            return (a.getTimeStamp() < b.getTimeStamp()) ? -1 : (a.getTimeStamp() == b.getTimeStamp() ? 0 : 1);
        });

        return list.get(0).getTimeStamp();
    }

    public final long earliestTransaction()
    {
        if (accepted.size() == 0)
            return -1;

        if (accepted.size() == 1)
            return accepted.iterator().next().getTimeStamp();

        List<Transaction> list = new ArrayList<>(accepted);

        Collections.sort(list, (a, b)->{
            return (a.getTimeStamp() < b.getTimeStamp()) ? -1 : (a.getTimeStamp() == b.getTimeStamp() ? 0 : 1);
        });

        return list.get(list.size() - 1).getTimeStamp();
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