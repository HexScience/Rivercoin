package nucleus.crypto;

import java.util.ArrayList;
import java.util.List;

public class MnemonicString
{
    private List<MnemonicWord> mnemonicString;

    public MnemonicString()
    {
        this.mnemonicString = new ArrayList<>();
    }

    public MnemonicString(List<MnemonicWord> string)
    {
        this.mnemonicString = string;
    }

    public void concatenate(MnemonicWord word)
    {
        this.mnemonicString.add(word);
    }

    public MnemonicString concatenate(MnemonicString other)
    {
        List<MnemonicWord> string = new ArrayList<>();
        string.addAll(mnemonicString);
        string.addAll(other.mnemonicString);

        return new MnemonicString(string);
    }

    public List<MnemonicWord> getMnemonicWords()
    {
        return mnemonicString;
    }

    @Override
    public String toString()
    {
        String string = "";

        for (MnemonicWord word : mnemonicString)
            string += word + " ";

        if (string.length() > 0)
            string = string.substring(0, string.length() - 1);

        return string;
    }
}