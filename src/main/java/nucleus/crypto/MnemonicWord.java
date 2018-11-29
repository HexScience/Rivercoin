package nucleus.crypto;

import nucleus.util.HashUtil;

public class MnemonicWord
{
    protected String word;
    protected byte[] data;

    public MnemonicWord(String word)
    {
        this.word = word;
        this.data = HashUtil.applyRipeMD160(HashUtil.applyKeccak(word.getBytes()));
    }

    @Override
    public String toString()
    {
        return word;
    }
}
