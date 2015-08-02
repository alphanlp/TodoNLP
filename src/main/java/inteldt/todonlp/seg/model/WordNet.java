package inteldt.todonlp.seg.model;

import inteldt.todonlp.dict.CoreBiGramDictionary;
import inteldt.todonlp.manager.Predefine;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 词网，根据字符在句子的先后顺序索引。
 * @author lenovo
 *
 */
public class WordNet {
	/**
	 * 图的邻接矩阵
	 */
	private LinkedList<Vertex>[] vertexes;
	
	/**
     * 共有多少个节点
     */
    int size;
	
    /**
     * 词网对应的句子
     */
	public String sentence;
	
	/**
	 * 初始化词网，只增加开始节点和结束节点。
	 * @param sentence
	 */
	public WordNet(String sentence){
		this.sentence = sentence;
		vertexes = new LinkedList[sentence.length() + 2];
        for (int i = 0; i < vertexes.length; ++i)
        {
            vertexes[i] = new LinkedList<Vertex>();
        }
        vertexes[0].add(Vertex.newBegin());
        vertexes[vertexes.length - 1].add(Vertex.newEnd());
        size = 2;

	}
	
	public WordNet(char[] charArray){
        this(new String(charArray));
    }
	
	/**
     * 添加顶点
     *
     * @param line   行号，在具体应用中，对应着字符的位置索引
     * @param vertex 顶点
     */
    public void add(int line, Vertex vertex){
        for (Vertex oldVertex : vertexes[line])
        {
            // 保证唯一性
            if (oldVertex.realWord.length() == vertex.realWord.length()) return;
        }
        vertexes[line].add(vertex);
        ++size;// 节点+1
    }
    
    /**
     * 全自动添加顶点
     *
     * @param vertexList
     */
    public void addAll(List<Vertex> vertexList){
        int i = 0;
        for (Vertex vertex : vertexList)
        {
            add(i, vertex);
            i += vertex.realWord.length();
        }
    }
    
    /**
     * 强行添加，替换已有的顶点
     *
     * @param line
     * @param vertex
     */
    public void push(int line, Vertex vertex){
        Iterator<Vertex> iterator = vertexes[line].iterator();
        while (iterator.hasNext())
        {
            if (iterator.next().realWord.length() == vertex.realWord.length())
            {
                iterator.remove();
                --size;
                break;
            }
        }
        vertexes[line].add(vertex);
        ++size;
    }
    
//    /**
//     * 添加顶点，由原子分词顶点添加
//     *
//     * @param line
//     * @param atomSegment
//     */
//    public void add(int line, List<AtomNode> atomSegment)
//    {
//        // 将原子部分存入m_segGraph
//        int offset = 0;
//        for (AtomNode atomNode : atomSegment)//Init the cost array
//        {
//            String sWord = atomNode.sWord;//init the word
//            Nature nature = Nature.n;
//            switch (atomNode.nPOS)
//            {
//                case Predefine.CT_CHINESE:
//                    break;
//                case Predefine.CT_INDEX:
//                case Predefine.CT_NUM:
//                    nature = Nature.m;
//                    sWord = "未##数";
//                    break;
//                case Predefine.CT_DELIMITER:
//                    nature = Nature.w;
//                    break;
//                case Predefine.CT_LETTER:
//                    nature = Nature.nx;
//                    sWord = "未##串";
//                    break;
//                case Predefine.CT_SINGLE://12021-2129-3121
//                    nature = Nature.nx;
//                    sWord = "未##串";
//                    break;
//                default:
//                    break;
//            }
//            add(line + offset, new Vertex(sWord, atomNode.sWord, new CoreDictionary.Attribute(nature, 1)));
//            offset += atomNode.sWord.length();
//        }
//    }
    
    /**
     * 获取某一行的所有节点
     *
     * @param line 行号
     * @return 一个数组
     */
    public List<Vertex> get(int line){
        return vertexes[line];
    }

    /**
     * 获取某一行的第一个节点
     *
     * @param line
     * @return
     */
    public Vertex getFirst(int line){
        Iterator<Vertex> iterator = vertexes[line].iterator();
        if (iterator.hasNext()) return iterator.next();

        return null;
    }

    /**
     * 获取节点个数
     * @return
     */
    public int size(){
        return size;
    }
    
    /**
     * 采用一元模型计算从一个词到另一个词的词的花费
     *
     * @param from 前面的词
     * @param to   后面的词
     * @return 分数
     */
    public static double calculateUnigramWeight(Vertex current){
        int frequency = current.getAttribute().totalFreq;
        if (frequency == 0) frequency = 1;  // 防止发生除零错误

        double value = -Math.log(frequency / Predefine.MAX_FREQUENCY + Predefine.dTemp);// 加上一个平滑因子
        if (value < 0.0)
        {// 理论情况下，是不应该小于0的啊，为什么加这个判断呢
            value = -value;
        }
        
        return frequency;
    }
    
    /**
     * 采用二元模型计算从一个词到另一个词的词的花费
     *
     * @param from 前面的词
     * @param to   后面的词
     * @return 分数
     */
    public static double calculateBigramWeight(Vertex from, Vertex to){
    	int frequency = from.getAttribute().totalFreq;
        if (frequency == 0)  frequency = 1;  // 防止发生除零错误
        
        int nTwoWordsFreq = CoreBiGramDictionary.getBiGramFreq(from.wordId, to.wordId);
        double value = -Math.log(Predefine.dSmoothingPara * frequency / (Predefine.MAX_FREQUENCY) + 
        		(1 - Predefine.dSmoothingPara) * ((1 - Predefine.dTemp) * nTwoWordsFreq / frequency + Predefine.dTemp));
//        if (value < 0.0)
//        {// 理论情况下，是不应该小于0的啊，为什么加这个判断呢
//            value = -value;
//        }
//      logger.info(String.format("%5s frequency:%6d, %s nTwoWordsFreq:%3d, weight:%.2f", from.word, frequency, from.word + "@" + to.word, nTwoWordsFreq, value));
        return value;
    }
    
    /**
     * 获取内部顶点表格，谨慎操作！
     *
     * @return
     */
    public LinkedList<Vertex>[] getVertexes(){
        return vertexes;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        int line = 0;
        for (List<Vertex> vertexList : vertexes)
        {
            sb.append(String.valueOf(line++) + ':' + vertexList.toString()).append("\n");
        }
        return sb.toString();
    }
}
