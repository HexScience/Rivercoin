package com.riverssen.nucleus.protocols.protobufs;

import com.riverssen.nucleus.protocols.transaction.Transaction;
import com.riverssen.nucleus.protocols.transaction.TransactionInput;
import com.riverssen.nucleus.protocols.transaction.TransactionOutput;
import com.riverssen.nucleus.protocols.transactionapi.TransactionPayload;
import com.riverssen.nucleus.util.ByteUtil;

public class Coinbase extends Transaction
{
    private Address miner;

    public Coinbase(long reward, String comment, byte payload[], Address miner)
    {
        this.miner = miner;
        setComment(comment);
//        inputs = new TransactionInput[] {new TransactionInput(new byte[32], 0, TransactionPayload.P2PKH_lock(miner))};
        outputs = new TransactionOutput[0];
        outputs[0] = new TransactionOutput(reward, TransactionPayload.P2PKH_lock(miner));
    }

    public void addInput(Transaction transaction, int output)
    {
        //TODO: use appropriate scriptsig payload.
        this.inputs = ByteUtil.<TransactionInput>concatenate(inputs, new TransactionInput[] {new TransactionInput(transaction.getTransactionID(), output, TransactionPayload.P2PKH_lock(miner))});
        this.outputs[0].setValue(this.outputs[0].getValue() + transaction.getOutput(output).getValue());
    }
}