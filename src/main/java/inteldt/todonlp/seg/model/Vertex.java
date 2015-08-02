package inteldt.todonlp.seg.model;

import inteldt.todonlp.dict.CoreDictionary;
import inteldt.todonlp.manager.Predefine;
import inteldt.todonlp.model.Nature;

/**
 * 切分词图的点，节点为词的开始或者结束位置。
 * 
 * @author lenovo
 * 
 */
public class Vertex {
	/**
	 * 节点对应的词或等效词（如未##数），这样做有好处，比如有些不规范的字，分出来后，保持原貌，但又能切分正确。
	 */
	public String word;
	/**
	 * 节点对应的真实词，或者原始词
	 */
	public String realWord;

	/** 词在CoreDictionary词典中对应的ID,用在二元词典的编码, -1表示没有查到*/
	public int wordId;

	/**
	 * 词的属性，谨慎修改属性内部的数据，因为会影响到字典<br>
	 * 如果要修改，应当new一个Attribute
	 */
	public CoreDictionary.Attribute attribute;

	public Vertex(String word, String realWord,
			CoreDictionary.Attribute attribute, int wordId) {
		this.word = word;
		this.realWord = realWord;
		this.attribute = attribute;
		this.wordId = wordId;
	}

	/**
	 * 到该节点的最短路径的前驱节点
	 */
	public Vertex from;

	/**
	 * 最短路径对应的权重
	 */
	public double weight;

	/**
	 * 起始节点
	 * 
	 * @return
	 */
	public static Vertex newBegin() {
		return new Vertex(Predefine.TAG_BIGIN, " ",
				new CoreDictionary.Attribute(Nature.begin,
						Predefine.MAX_FREQUENCY / 10),
				CoreDictionary.trie.getID(Predefine.TAG_BIGIN));
	}

	/**
	 * 终止节点
	 * 
	 * @return
	 */
	public static Vertex newEnd() {
		return new Vertex(Predefine.TAG_END, " ", new CoreDictionary.Attribute(
				Nature.end, Predefine.MAX_FREQUENCY / 10),
				CoreDictionary.trie.getID(Predefine.TAG_END));
	}

	/**
	 * 获取词的属性
	 * 
	 * @return
	 */
	public CoreDictionary.Attribute getAttribute() {
		return attribute;
	}

	/**
	 * 计算路径最短的前驱节点
	 * 
	 * @param from
	 */
	public void updateFromByUnigram(Vertex from) {
		double weight = from.weight + WordNet.calculateUnigramWeight(this);
		if (this.from == null || this.weight > weight) {
			this.from = from;
			this.weight = weight;
		}
	}
	
	/**
	 * 计算路径最短的前驱节点
	 * 
	 * @param from
	 */
	public void updateFromByBigram(Vertex from) {
		double weight = from.weight + WordNet.calculateBigramWeight(from, this);
		if (this.from == null || this.weight > weight) {
			this.from = from;
			this.weight = weight;
		}
	}

	/**
     * 将属性的词性锁定为nature
     *
     * @param nature 词性
     * @return 如果锁定词性在词性列表中，返回真，否则返回假
     */
    public boolean confirmNature(Nature nature)
    {
        if (attribute.natures.length == 1 && attribute.natures[0] == nature){
            return true;
        }
        boolean result = true;
        int frequency = attribute.getNatureFreq(nature);
        if (frequency == 0){// 没有发现该词性，将词性的频次赋值为1000
            frequency = 1000;
            result = false;
        }
        attribute = new CoreDictionary.Attribute(nature, frequency);// 新建attribute，将词性锁定为nature
        return result;
    }
	
	@Override
	public String toString() {
		return realWord;
		// return "WordNode{" +
		// "word='" + word + '\'' +
		// (word.equals(realWord) ? "" : (", realWord='" + realWord + '\'')) +
		// ", attribute=" + attribute +
		// '}';
	}
}
