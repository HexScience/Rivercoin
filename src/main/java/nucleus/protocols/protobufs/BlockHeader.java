package nucleus.protocols.protobufs;

import nucleus.mining.Nonce;
import nucleus.util.ByteUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BlockHeader
{
	public static class MinableBlockHeader
	{
		private long version;
		private byte previousHash[];
		private byte amerkleRoot[];
		private byte rmerkleRoot[];
		private long timestamp;
		private double difficulty;

		public MinableBlockHeader(long version, byte previousHash[], byte amerkleRoot[], byte rmerkleRoot[], long timestamp, double difficulty)
		{
			this.version = version;
			this.previousHash = previousHash;
			this.amerkleRoot = amerkleRoot;
			this.rmerkleRoot = rmerkleRoot;
			this.timestamp = timestamp;
			this.difficulty = difficulty;
		}

		public byte[] getBytes()
		{
			return ByteUtil.concatenate(ByteUtil.encode(version), previousHash, amerkleRoot, rmerkleRoot, ByteUtil.encode(timestamp), ByteUtil.encode_double(difficulty));
		}
	}
	public static final int SIZE = 8 + 8 + 32 + 32 + 32 + 8 + 8 + 8 + 33 + 8 + 8;

	private long 			version;
	private long 			blockID;
	private byte[]			hash;
	private byte[] 			parentHash = new byte[32];
	private byte[] 			amerkleRoot = new byte[32];
	private byte[] 			rmerkleRoot = new byte[32];
//	private byte[] 			forkRoot = new byte[32];
	/**
	 * timeStamp = ((block.earliesttransaction + block.latesttransaction) / 2);
	 */
	private long 			timeStamp;
	private double 			difficulty;
	private Nonce			nonce;
	private CompressedKey 	minerAddress = new CompressedKey();
	private long 			reward;

	public MinableBlockHeader getForMining()
	{
		return new MinableBlockHeader(version, parentHash, amerkleRoot, rmerkleRoot, timeStamp, difficulty);
	}

	//GETTERS

	public long getVersion() { return version; }

	//GETTERS

	public long getBlockID() { return blockID; }

	//GETTERS

	public byte[] getHash()
	{
		return hash;
	}

	//GETTERS

	public byte[] getParentHash() { return parentHash; }

	//GETTERS

	public byte[] getAcceptedMerkleRoot() { return amerkleRoot; }

	//GETTERS

	public byte[] getRejectedMerkleRoot() { return rmerkleRoot; }

	//GETTERS

//	public byte[] getForkRoot() { return forkRoot; }

	//GETTERS

	public long getTimeStamp() { return timeStamp; }

	//GETTERS

	public double getDifficulty() { return difficulty; }

	//GETTERS

	public Nonce getNonce() { return nonce; }

	//GETTERS

	public CompressedKey getMinerAddress() { return minerAddress; }

	//GETTERS

	public long getReward() { return reward; }

	//SETTERS

	public void  setVersion(long version) { this.version = version; }

	//SETTERS

	public void  setBlockID(long blockID) { this.blockID = blockID; }

	//SETTERS

	public void setHash(byte[] hash)
	{
		this.hash = hash;
	}

	//SETTERS

	public void  setParentHash(byte[] parentHash) { this.parentHash = parentHash; }

	//SETTERS

	public void  setAcceptedMerkleRoot(byte[] merkleRoot) { this.amerkleRoot = merkleRoot; }

	//SETTERS

	public void  setRejectedMerkleRoot(byte[] merkleRoot) { this.rmerkleRoot = merkleRoot; }

	//SETTERS

//	public void  setForkRoot(byte[] forkRoot) { this.forkRoot = forkRoot; }

	//SETTERS

	public void  setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }

	//SETTERS

	public void  setDifficulty(double difficulty) { this.difficulty = difficulty; }

	//SETTERS

	public void  setNonce(Nonce nonce) { this.nonce = nonce; }

	//SETTERS

	public void  setMinerAddress(CompressedKey minerAddress) { this.minerAddress = minerAddress; }

	//SETTERS

	public void  setReward(long reward) { this.reward = reward; }

	public void write(final DataOutputStream stream) throws IOException
	{
		stream.writeLong(version);
		stream.writeLong(blockID);
		stream.write(parentHash);
		stream.write(amerkleRoot);
		stream.write(rmerkleRoot);
//		stream.write(forkRoot);
		stream.writeLong(timeStamp);
		stream.writeDouble(difficulty);
		stream.writeLong(nonce.getA());
		stream.writeLong(nonce.getB());
		stream.writeLong(nonce.getC());
		minerAddress.write(stream);
		stream.writeLong(reward);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.version = stream.readLong();
		this.blockID = stream.readLong();
		stream.read(this.parentHash);
		stream.read(this.amerkleRoot);
		stream.read(this.rmerkleRoot);
//		stream.read(this.forkRoot);
		this.timeStamp = stream.readLong();
		this.difficulty = stream.readDouble();
		this.nonce = new Nonce(stream.readLong(), stream.readLong(), stream.readLong());
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