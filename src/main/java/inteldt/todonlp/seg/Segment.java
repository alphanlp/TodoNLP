package inteldt.todonlp.seg;

import inteldt.todonlp.dict.CoreDictionary;
import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.util.SentenceDetect;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 分词器的基类
 * @author pei
 *
 */
public abstract class Segment {
	
	/**分词器的配置*/
	protected SegConfig segConfig = new SegConfig();
	/**
	 * 分词
	 * @param text  文本
	 * @return 
	 */
	public List<Term> seg(String text){
        return segSentence(text);
    }
	
	 /**
     * 分词
     *
     * @param text 待分词文本char数组
     * @return 单词列表
     */
    public List<Term> seg(char[] text)
    {
        assert text != null;
//        if (Config.Normalization)
//        {
//            CharTable.normalization(text);
//        }
        return segSentence(new String(text));
    }
	
	/**
     * 给一个句子分词。工厂方法，有具体的子类实现。也就是不同的分词方法实现。
     *
     * @param sentence 待分词句子
     * @return 单词列表
     */
    protected abstract List<Term> segSentence(String sentence);
    
    public List<List<Term>> seg2sentence(String text){
        List<List<Term>> resultList = new LinkedList<List<Term>>();
        {
            for (String sentence : SentenceDetect.toSentenceList(text))
            {
                resultList.add(segSentence(sentence));
            }
        }

        return resultList;
    }

    
    /**
     * 是否启用用户词典
     *
     * @param enable
     */
    public void enableCustomDictionary(boolean enable){
    	segConfig.isUseCustomDictionary = enable;
    }
}
