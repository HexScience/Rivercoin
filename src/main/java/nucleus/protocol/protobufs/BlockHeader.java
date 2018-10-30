package nucleus.protocol.protobufs;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BlockHeader
{
	private long 			version;
	private long 			blockID;
	private byte[] 			parentHash = new byte[32];
	private byte[] 			merkleRoot = new byte[32];
	private byte[] 			forkRoot = new byte[32];
	private long 			timeStamp ;
	private double 			difficulty;
	private long 			nonce;
	private Address 		minerAddress = new Address();
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

	public Address getMinerAddress() { return minerAddress; }

	//GETTERS

	public long getReward() { return reward; }

	//SETTERS

	private void  setVersion(long version) { this.version = version; }

	//SETTERS

	private void  setBlockID(long blockID) { this.blockID = blockID; }

	//SETTERS

	private void  setParentHash(byte[] parentHash) { this.parentHash = parentHash; }

	//SETTERS

	private void  setMerkleRoot(byte[] merkleRoot) { this.merkleRoot = merkleRoot; }

	//SETTERS

	private void  setForkRoot(byte[] forkRoot) { this.forkRoot = forkRoot; }

	//SETTERS

	private void  setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

	//SETTERS

	private void  setDifficulty(double difficulty) { this.difficulty = difficulty; }

	//SETTERS

	private void  setNonce(long nonce) { this.nonce = nonce; }

	//SETTERS

	private void  setMinerAddress(Address minerAddress) { this.minerAddress = minerAddress; }

	//SETTERS

	private void  setReward(long reward) { this.reward = reward; }

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