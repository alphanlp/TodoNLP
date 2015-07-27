package inteldt.todonlp.seg.model;

import inteldt.todonlp.dictionary.CoreDictionary;
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
    
    /**
     * 词的属性，谨慎修改属性内部的数据，因为会影响到字典<br>
     * 如果要修改，应当new一个Attribute
     */
    public CoreDictionary.Attribute attribute;
    
    public Vertex(String word, String realWord, CoreDictionary.Attribute attribute){
    	this.word = word;
    	this.realWord = realWord;
    	this.attribute = attribute;
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
     * @return
     */
    public static Vertex newBegin(){
        return new Vertex(Predefine.TAG_BIGIN, " ", new CoreDictionary.Attribute(Nature.begin, Predefine.MAX_FREQUENCY / 10));
    }
    
    /**
     * 终止节点
     * @return
     */
    public static Vertex newEnd(){
        return new Vertex(Predefine.TAG_END, " ", new CoreDictionary.Attribute(Nature.end, Predefine.MAX_FREQUENCY / 10));
    }
    
    /**
     * 获取词的属性
     *
     * @return
     */
    public CoreDictionary.Attribute getAttribute(){
        return attribute;
    }
    
    /**
     * 计算路径最短的前驱节点
     * @param from
     */
    public void updateFrom(Vertex from){
        double weight = from.weight + WordNet.calculateUnigramWeight(this);
        if (this.from == null || this.weight > weight)
        {
            this.from = from;
            this.weight = weight;
        }
    }
    
    @Override
    public String toString(){
        return realWord;
//        return "WordNode{" +
//                "word='" + word + '\'' +
//                (word.equals(realWord) ? "" : (", realWord='" + realWord + '\'')) +
//                ", attribute=" + attribute +
//                '}';
    }
}
