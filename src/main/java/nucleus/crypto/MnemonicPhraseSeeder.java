package nucleus.crypto;

import nucleus.util.BinaryTree;
import nucleus.util.FileUtils;

import java.security.SecureRandom;
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
        this.tree = new BinaryTree();
        this.wordList = new ArrayList<>();
        this.string = new MnemonicString();

        String data = FileUtils.readUTF(this.getClass().getResourceAsStream("/generators/wordlist.txt"));

        String splitData[] = data.split("\n");

        for (String string : splitData)
            if (string.length() > 2)
                wordList.add(new MnemonicWord(string));

        SecureRandom random = new SecureRandom();

        Set<Integer> used = new LinkedHashSet<>();

        for (int i = 0; i < 24; i ++)
        {
            int newInt = random.nextInt(wordList.size());

            while (used.contains(newInt))
                newInt = random.nextInt(wordList.size());

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
}