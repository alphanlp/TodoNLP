package inteldt.todonlp.seg;

import inteldt.todonlp.dictionary.CoreDictionary;
import inteldt.todonlp.model.Nature;
import inteldt.todonlp.seg.model.Vertex;
import inteldt.todonlp.seg.model.WordNet;

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

	public static void main(String[] args) {
		// LinguisticSegment seg = new LinguisticSegment();
		// WordNet wordnet= seg.generateWordNet("我们的世界");
		// System.out.println(wordnet);
	}

}
