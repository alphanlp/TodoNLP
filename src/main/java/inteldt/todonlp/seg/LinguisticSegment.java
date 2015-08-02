package inteldt.todonlp.seg;

import inteldt.todonlp.dict.CoreDictionary;
import inteldt.todonlp.dict.CoreTransferMatrixDictionary;
import inteldt.todonlp.model.Nature;
import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.model.Vertex;
import inteldt.todonlp.seg.model.WordNet;
import inteldt.todonlp.util.Viterbi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 语言模型分词的父类，负责生成词网。
 * 
 * @author pei
 * 
 */
public abstract class LinguisticSegment extends Segment {

	/**
	 * 根据初始化的wordnet对象生成语言模型词网。
	 * 
	 * @param wordnet
	 * @return
	 */
	public WordNet generateWordNet(final WordNet wordnet) {
		int pos;// 游标，通过移动来切分出不同长度的所有词
		for (int index = 0; index < wordnet.sentence.length(); index++) {
			pos = 1;
			while (index + pos <= wordnet.sentence.length()) {
				String candidate = wordnet.sentence.substring(index, index
						+ pos);
				// 查询是否在词典中
				Map.Entry<CoreDictionary.Attribute, Integer> entry = CoreDictionary.trie
						.getAttributeAndID(candidate);
				// 加入词网
				if (entry != null) {// index+1，因为0的位置被开始节点占领
					wordnet.add(index + 1, new Vertex(candidate, candidate, entry.getKey(), entry.getValue()));
				} else {
					if (pos == 1) {
						wordnet.add(index + 1, new Vertex(candidate, candidate,
								new CoreDictionary.Attribute(Nature.un, 1), -1));
					}
				}
				pos++;
			}

		}
		return wordnet;
	}

	/**
	 * 直接根据句子生成语言模型词网。
	 * 
	 * @param sentence
	 * @return
	 */
	public WordNet generateWordNet(String sentence) {
		WordNet wordnet = new WordNet(sentence);
		generateWordNet(wordnet);
		return wordnet;
	}

	/**
     * 将一条路径转为最终结果
     *
     * @param vertexList
     * @param offsetEnabled 是否计算offset，词在句子中的起始位置
     * @return
     */
    protected static List<Term> convert(List<Vertex> vertexList, boolean offsetEnabled)
    {
        assert vertexList != null;
        assert vertexList.size() >= 2 : "这条路径不应当短于2" + vertexList.toString();// 空字符串时为2，因为增加了begin和end两个节点
        int length = vertexList.size() - 2;
        List<Term> resultList = new ArrayList<Term>(length);// 初始化空间，减少内存
        Iterator<Vertex> iterator = vertexList.iterator();
        iterator.next();// 其是节点BEGIN
        if (offsetEnabled)
        {
            int offset = 0;
            for (int i = 0; i < length; ++i)
            {
                Vertex vertex = iterator.next();
                Term term = convert(vertex);
                term.offset = offset;
                offset += term.length();
                resultList.add(term);
            }
        }
        else
        {
            for (int i = 0; i < length; ++i)
            {
                Vertex vertex = iterator.next();
                Term term = convert(vertex);
                resultList.add(term);
            }
        }
        return resultList;
    }
	
    /**
     * 将一条路径转为最终结果，不计算词在句子中的起始位置
     *
     * @param vertexList
     * @return
     */
    protected static List<Term> convert(List<Vertex> vertexList){
        return convert(vertexList, false);
    }
    
    /**
     * 将节点列表转为term列表
     *
     * @param vertex
     * @return
     */
    private static Term convert(Vertex vertex){
        return new Term(vertex.realWord, vertex.attribute.natures[0]);// 词，对应的词性，最有可能的词性
    }
    
    protected static void speechTag(List<Vertex> vertexList){
    	Viterbi.compute(vertexList, CoreTransferMatrixDictionary.transformMatrix);
    }
    
	public static void main(String[] args) {
		// LinguisticSegment seg = new LinguisticSegment();
		// WordNet wordnet= seg.generateWordNet("我们的世界");
		// System.out.println(wordnet);
	}

}
