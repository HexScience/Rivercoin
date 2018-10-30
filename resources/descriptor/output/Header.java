package com.riverssen.protocols;


import java.io.*;

public class Header
{
	private long version = new long();
	private long blockID = new long();
	private byte[] parentHash = new byte[32];
	private byte[] merkleRoot = new byte[32];
	private byte[] forkRoot = new byte[32];
	private long timeStamp = new long();
	private double difficulty = new double();
	private long nonce = new long();
	private Address minerAddress = new Address();
	private long reward;

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
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
		stream.write(null);
	}


	public void read(final DataInputStream stream) throws IOException
	{
		this.version = stream.read(null);
		this.blockID = stream.read(null);
		this.parentHash = stream.read(null);
		this.merkleRoot = stream.read(null);
		this.forkRoot = stream.read(null);
		this.timeStamp = stream.read(null);
		this.difficulty = stream.read(null);
		this.nonce = stream.read(null);
		this.minerAddress = stream.read(null);
		this.reward = stream.read(null);
	}
}