package inteldt.todonlp.seg.model;

import inteldt.todonlp.model.Nature;
import inteldt.todonlp.util.CharType;

/**
 * 原子分词节点
 * @author pei
 */
public class AtomNode
{
    public String sWord;
    public int wordType;

    public AtomNode(String sWord, int nPOS)
    {
        this.sWord = sWord;
        this.wordType = nPOS;
    }

    public AtomNode(char c, int nPOS)
    {
        this.sWord = String.valueOf(c);
        this.wordType = nPOS;
    }

    /**
     * 原子的词性
     * @return
     */
    public Nature getNature()
    {
        Nature nature = Nature.n;
        switch (wordType)
        {
            case CharType.CT_CHINESE:
                break;
            case CharType.CT_INDEX:
            case CharType.CT_NUM:
                nature = Nature.m;
//                sWord = "未##数";
                break;
            case CharType.CT_DELIMITER:
                nature = Nature.w;
                break;
            case CharType.CT_LETTER:
                nature = Nature.nx;
//                sWord = "未##串";
                break;
            case CharType.CT_SINGLE:
                  nature = Nature.nx;
//                   sWord = "未##串";

                break;
            default:
                break;
        }
        return nature;
    }

    @Override
    public String toString()
    {
        return "AtomNode{" +
                "word='" + sWord + '\'' +
                ", nature=" + wordType +
                '}';
    }

    public static Vertex convert(String word, int type)
    {
        String name = word;
        Nature nature = Nature.n;
        int dValue = 1;
        switch (type)
        {
            case CharType.CT_CHINESE:
                break;
            case CharType.CT_INDEX:
            case CharType.CT_NUM:
                nature = Nature.m;
                word = "未##数";
                break;
            case CharType.CT_DELIMITER:
                nature = Nature.w;
                break;
            case CharType.CT_LETTER:
                nature = Nature.nx;
                word = "未##串";
                break;
            case CharType.CT_SINGLE://12021-2129-3121
//                if (Pattern.compile("^(-?\\d+)(\\.\\d+)?$").matcher(word).matches())//匹配浮点数
//                {
//                    nature = Nature.m;
//                    word = "未##数";
//                } else
//                {
                    nature = Nature.nx;
                    word = "未##串";
//                }
                break;
            default:
                break;
        }

        return new Vertex(word, name, new TrieAttribute(nature, dValue),-1);
    }
}
