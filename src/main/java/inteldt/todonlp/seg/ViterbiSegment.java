package inteldt.todonlp.seg;

import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.model.Vertex;
import inteldt.todonlp.seg.model.WordNet;

import java.util.LinkedList;
import java.util.List;

/**
 * n元分词，viterbi算法求解
 * @author pei
 *
 */
public class ViterbiSegment extends LinguisticSegment{

	public static void main(String[] args) {
		//test
		ViterbiSegment seg = new ViterbiSegment();		
//		List<Term> terms = seg.segSentence("在自然语言处理中，我们经常需要用到语法模型");
		List<Term> terms = seg.segSentence("");
		System.out.println(terms);
	}
	
	@Override
	protected List<Term> segSentence(String sentence) {
		// 生成词网
		WordNet wordnet = generateWordNet(sentence);
		
		System.out.println(wordnet.toString());
		
		//求解
		List<Vertex> vertexList = null;
		if("bigram".equalsIgnoreCase(segConfig.getSELECTED_GRAM())){
			vertexList = bigramByviterbi(wordnet);
		}else if("unigram".equalsIgnoreCase(segConfig.getSELECTED_GRAM())){
			vertexList = unigramByviterbi(wordnet);
		}
		
		if(segConfig.speechTag){
			speechTag(vertexList);// 隐马词性标注
		}
		
		return convert(vertexList);
	}
	
	/**
	 * viterbi算法求解最优解,二元模型求解
	 * @param wordnet
	 * @return
	 */
	private List<Vertex> bigramByviterbi(WordNet wordnet){
		// 避免生成对象，优化速度
        LinkedList<Vertex>[] nodes = wordnet.getVertexes();
        LinkedList<Vertex> vertexList = new LinkedList<Vertex>();// 存放粗分结果的
        for (Vertex node : nodes[1])
        {
            node.updateFromByBigram(nodes[0].getFirst());// nodes[0].getFirst() 到 node的权重，也就是路径的权重
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
                    to.updateFromByBigram(node);
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
	
	/**
	 * viterbi算法求解最优解,一元模型求解
	 * @param wordnet
	 * @return
	 */
	private List<Vertex> unigramByviterbi(WordNet wordnet){
		// 避免生成对象，优化速度
        LinkedList<Vertex>[] nodes = wordnet.getVertexes();
        LinkedList<Vertex> vertexList = new LinkedList<Vertex>();// 存放粗分结果的
        for (Vertex node : nodes[1])
        {
            node.updateFromByUnigram(nodes[0].getFirst());// nodes[0].getFirst() 到 node的权重，也就是路径的权重
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
                    to.updateFromByUnigram(node);
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

}
