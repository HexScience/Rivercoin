package nucleus.protocols.protobufs;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BlockHeader
{
	public static final int SIZE = 8 + 8 + 32 + 32 + 32 + 8 + 8 + 8 + 33 + 8;

	private long 			version;
	private long 			blockID;
	private byte[] 			parentHash = new byte[32];
	private byte[] 			merkleRoot = new byte[32];
	private byte[] 			forkRoot = new byte[32];
	/**
	 * timeStamp = ((block.earliesttransaction + block.latesttransaction) / 2);
	 */
	private long 			timeStamp;
	private double 			difficulty;
	private long 			nonce;
	private CompressedKey 	minerAddress = new CompressedKey();
	private long 			reward;

	//GETTERS

	public long getVersion() { return version; }

	//GETTERS

	public long getBlockID() { return blockID; }

	//GETTERS

	public byte[] getParentHash() { return parentHash; }

	//GETTERS

	public byte[] getMerkleRoot() { return merkleRoot; }

	//GETTERS

	public byte[] getForkRoot() { return forkRoot; }

	//GETTERS

	public long getTimeStamp() { return timeStamp; }

	//GETTERS

	public double getDifficulty() { return difficulty; }

	//GETTERS

	public long getNonce() { return nonce; }

	//GETTERS

	public CompressedKey getMinerAddress() { return minerAddress; }

	//GETTERS

	public long getReward() { return reward; }

	//SETTERS

	public void  setVersion(long version) { this.version = version; }

	//SETTERS

	public void  setBlockID(long blockID) { this.blockID = blockID; }

	//SETTERS

	public void  setParentHash(byte[] parentHash) { this.parentHash = parentHash; }

	//SETTERS

	public void  setMerkleRoot(byte[] merkleRoot) { this.merkleRoot = merkleRoot; }

	//SETTERS

	public void  setForkRoot(byte[] forkRoot) { this.forkRoot = forkRoot; }

	//SETTERS

	public void  setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

	//SETTERS

	public void  setDifficulty(double difficulty) { this.difficulty = difficulty; }

	//SETTERS

	public void  setNonce(long nonce) { this.nonce = nonce; }

	//SETTERS

	public void  setMinerAddress(CompressedKey minerAddress) { this.minerAddress = minerAddress; }

	//SETTERS

	public void  setReward(long reward) { this.reward = reward; }

	public void write(final DataOutputStream stream) throws IOException
	{
		stream.writeLong(version);
		stream.writeLong(blockID);
		stream.write(parentHash);
		stream.write(merkleRoot);
		stream.write(forkRoot);
		stream.writeLong(timeStamp);
		stream.writeDouble(difficulty);
		stream.writeLong(nonce);
		minerAddress.write(stream);
		stream.writeLong(reward);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.version = stream.readLong();
		this.blockID = stream.readLong();
		stream.read(this.parentHash);
		stream.read(this.merkleRoot);
		stream.read(this.forkRoot);
		this.timeStamp = stream.readLong();
		this.difficulty = stream.readDouble();
		this.nonce = stream.readLong();
		this.minerAddress.read(stream);
		this.reward = stream.readLong();
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
}