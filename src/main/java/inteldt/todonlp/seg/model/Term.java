package inteldt.todonlp.seg.model;

import inteldt.todonlp.model.Nature;
import inteldt.todonlp.seg.SegConfig;

/**
 * 切分后的词
 * @author User
 *
 */
public class Term {
	/**
     * 词语
     */
    public String word;

    /**
     * 词性
     */
    public Nature nature;

    /**
     * 在文本中的起始位置（需开启分词器的offset选项）
     */
    public int offset;

    /**
     * 构造一个单词
     * @param word 词语
     * @param nature 词性
     */
    public Term(String word, Nature nature)
    {
        this.word = word;
        this.nature = nature;
    }

    @Override
    public String toString()// TODO
    {
//        if (true)
            return word + "/" + nature;
//        return word;
    }

    /**
     * 长度
     * @return
     */
    public int length()
    {
        return word.length();
    }
}
