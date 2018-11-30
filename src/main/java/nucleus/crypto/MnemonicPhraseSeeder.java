package nucleus.crypto;

import nucleus.util.BinaryTree;
import nucleus.util.FileUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MnemonicPhraseSeeder
{
    private BinaryTree tree;
    private List<MnemonicWord> wordList;
    private MnemonicString     string;

    public MnemonicPhraseSeeder()
    {
        this(null);
    }

    public MnemonicPhraseSeeder(byte seed[])
    {
        this.tree = new BinaryTree();
        this.wordList = new ArrayList<>();
        this.string = new MnemonicString();

        String data = FileUtils.readUTF(this.getClass().getResourceAsStream("/generators/wordlist.txt"));

        String splitData[] = data.split("\n");

        for (String string : splitData)
            if (string.length() > 2)
                wordList.add(new MnemonicWord(string));

        SecureRandomI random = null;

        if (seed == null) random = new SecureRandomImp();
        else random = new SecureRandomMultiPlatformImp(seed);

        Set<Integer> used = new LinkedHashSet<>();

        for (int i = 0; i < 24; i ++)
        {
            int newInt = random.getInt(wordList.size());

            while (used.contains(newInt))
                newInt = random.getInt(wordList.size());

            used.add(newInt);

            string.concatenate(wordList.get(newInt));
        }

        for (MnemonicWord word : string.getMnemonicWords())
            tree.insert(word.data);
    }

    public MnemonicString getString()
    {
        return string;
    }

    public byte[] getSeed()
    {
        tree.clear();

        for (MnemonicWord word : string.getMnemonicWords())
            tree.insert(word.data);

        return tree.build();
    }

    public void setWords(String words)
    {
        String words_[] = words.replaceAll("\\s+", " ").replaceAll("\n", " ").split("\\s+");

        string.clear();

        for (String word : words_)
            string.concatenate(new MnemonicWord(word));
    }
}