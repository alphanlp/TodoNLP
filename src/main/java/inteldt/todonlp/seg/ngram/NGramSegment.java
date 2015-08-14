package inteldt.todonlp.seg.ngram;

import inteldt.todonlp.dict.CoreDictionary;
import inteldt.todonlp.dict.CoreTransferMatrixDictionary;
import inteldt.todonlp.dict.UserCustomDictionary;
import inteldt.todonlp.seg.Segment;
import inteldt.todonlp.seg.model.AtomNode;
import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.model.TrieAttribute;
import inteldt.todonlp.seg.model.Vertex;
import inteldt.todonlp.seg.model.WordNet;
import inteldt.todonlp.util.CharType;
import inteldt.todonlp.util.Viterbi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 语言模型分词的父类，负责生成词网。
 * 
 * @author pei
 * 
 */
public abstract class NGramSegment extends Segment {

	/**
	 * 根据初始化的wordnet对象生成语言模型词网。
	 * 
	 * @param wordnet
	 * @return
	 */
	public WordNet generateWordNet(final WordNet wordnet) {
		/*该部分只将在词典中查到的词加入词网，因此词网可能不完整，或者不是原子切分*/
		int pos;// 游标，通过移动来切分出不同长度的所有词
		for (int index = 0; index < wordnet.sentence.length(); index++) {
			pos = 1;
			while (index + pos <= wordnet.sentence.length()) {
				String candidate = wordnet.sentence.substring(index, index + pos);
				// 查询是否在词典中
				Map.Entry<TrieAttribute, Integer> entry = CoreDictionary.trie.getAttributeAndID(candidate);
				// 加入词网
				if (entry != null) 
				{// index+1，因为0的位置被开始节点占领
					wordnet.add(index + 1, new Vertex(candidate, candidate, entry.getKey(), entry.getValue()));
				}else
				{
					if(segConfig.isUseCustomDictionary){// 用户自定义词典
						TrieAttribute attri = UserCustomDictionary.trie.getAttribute(candidate);
						if (attri != null) {// index+1，因为0的位置被开始节点占领
							wordnet.add(index + 1, new Vertex(candidate, candidate, attri, -1));
						}else{
							if(!CoreDictionary.trie.preContains(candidate)){
								if(!UserCustomDictionary.trie.preContains(candidate)){
									break;
								}
							}
							
						}
					}
					if(!CoreDictionary.trie.preContains(candidate))
					{
						if(segConfig.isUseCustomDictionary)
						{
							pos++;
							while(index + pos <= wordnet.sentence.length())
							{
								candidate = wordnet.sentence.substring(index, index + pos); 
								TrieAttribute attri = UserCustomDictionary.trie.getAttribute(candidate);
								if (attri != null) 
								{// index+1，因为0的位置被开始节点占领
									wordnet.add(index + 1, new Vertex(candidate, candidate, attri, -1));
								}else
								{
									if(!UserCustomDictionary.trie.preContains(candidate))
									{
										break;
									}
								}
								pos++;
							}
							
						}
						break;
					}
				}
				pos++;
			}
		}
		
		/*对词网查缺补漏，并考虑数值和字母两种类型，若是连续的数值或者连续字母，则作为整体作为一个原子加入词网*/
		LinkedList<Vertex>[] vertexes = wordnet.getVertexes();
		for(int i = 1; i < vertexes.length; ){
			if(vertexes[i].isEmpty()){// 是否为空，如果为空，说明词网不完整或者不是原子切分
				  int j = i + 1;
				  for(; j < vertexes.length - 1; ++j){// 结果是END
					  if (!vertexes[j].isEmpty()) 
						  break;
				  }
				//sentence的[i-1,j-1)为没有识别的串
				  wordnet.add(i, quickAtomSegment(wordnet.sentence.toCharArray(),i-1,j-1));
				  i = j;
			}else {
            	i += vertexes[i].getLast().realWord.length();
            }
		}
		
		return wordnet;
	}
	
	/**
     * 原子分词，主要去完成词典中没有查到的字符或串的原子切分，比如数字、英文字符
     * <p>中间同时完成了浮点数识别</p>
     * @param charArray
     * @param start [start,end)
     * @param end
     * @return
     */
    protected static List<AtomNode> quickAtomSegment(char[] charArray, int start, int end)
    {
        List<AtomNode> atomNodeList = new LinkedList<AtomNode>();
        int offsetAtom = start;
        int preType = CharType.get(charArray[offsetAtom]);// 前个字符的类型
        int curType;// 当前字符的类型
        while (++offsetAtom < end){
            curType = CharType.get(charArray[offsetAtom]);
//            System.out.println("preType:" + preType + " curType:" + curType);
            if (curType != preType){
                // 浮点数识别
                if (charArray[offsetAtom] == '.' && preType == CharType.CT_NUM){
                    while (++offsetAtom < end)
                    {
                        curType = CharType.get(charArray[offsetAtom]);
                        if (curType != CharType.CT_NUM) break;
                    }
                }
                atomNodeList.add(new AtomNode(new String(charArray, start, offsetAtom - start), preType));
//                System.out.println("NUM:" + new String(charArray, start, offsetAtom - start));
                start = offsetAtom;
            }
            preType = curType;
        }
        if (offsetAtom == end)
            atomNodeList.add(new AtomNode(new String(charArray, start, offsetAtom - start), preType));
//        System.out.println("ENG:" + new String(charArray, start, offsetAtom - start));

        return atomNodeList;
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
    protected static List<Term> convert(List<Vertex> vertexList, boolean offsetEnabled){
        assert vertexList != null;
        assert vertexList.size() >= 2 : "这条路径不应当短于2" + vertexList.toString();// 空字符串时为2，因为增加了begin和end两个节点
        int length = vertexList.size() - 2;
        List<Term> resultList = new ArrayList<Term>(length);// 初始化空间，减少内存
        Iterator<Vertex> iterator = vertexList.iterator();
        iterator.next();// 其是节点BEGIN
        if (offsetEnabled){
            int offset = 0;
            for (int i = 0; i < length; ++i)
            {
                Vertex vertex = iterator.next();
                Term term = convert(vertex);
                term.offset = offset;
                offset += term.length();
                resultList.add(term);
            }
        }else{
            for (int i = 0; i < length; ++i){
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
	
	/**
	 * 求解语言模型的最优切分
	 * @param wordnet
	 * @return
	 */
	protected List<Vertex> optimSeg(WordNet wordnet){
		// 避免生成对象，优化速度
        LinkedList<Vertex>[] nodes = wordnet.getVertexes();
        LinkedList<Vertex> vertexList = new LinkedList<Vertex>();// 存放粗分结果的
        for (Vertex node : nodes[1])
        {
        	updateFrom(node,nodes[0].getFirst());// nodes[0].getFirst() 到 node的权重，也就是路径的权重
        }
    
        for (int i = 1; i < nodes.length - 1; ++i)
        {
            LinkedList<Vertex> nodeArray = nodes[i];
            if (nodeArray == null) continue;
            for (Vertex node : nodeArray)
            {
                if (node.from == null) continue;
                for (Vertex to : nodes[i + node.realWord.length()])
                {
                	updateFrom(to,node);
                }
            }
        }
        
        Vertex from = nodes[nodes.length - 1].getFirst();
        while (from != null)
        {
            vertexList.addFirst(from);
            from = from.from;
        }
        return vertexList;
	}

	protected abstract void updateFrom(Vertex to,Vertex from);
}
