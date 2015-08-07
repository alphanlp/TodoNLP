package inteldt.todonlp.seg;

import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.model.Vertex;
import inteldt.todonlp.seg.model.WordNet;

import java.util.LinkedList;
import java.util.List;

/**
 * N元分词，viterbi算法求解
 * @author pei
 *
 */
public class ViterbiSegment extends LinguisticSegment{

	/**
	 * 找时间把权重全部打印出来看看，计算过程中所有的权重
	 * @param args
	 */
	public static void main(String[] args) {
		//test
		ViterbiSegment seg = new ViterbiSegment();		
//		List<Term> terms = seg.segSentence("月花费1806元，汉兰达2.0T车型用车成本");
		List<Term> terms = seg.segSentence("把你的爱给他，他会爱你");
		System.out.println("隐马标注：" + terms);
	}
	
	@Override
	protected List<Term> segSentence(String sentence) {
		// 生成词网
		WordNet wordnet = generateWordNet(sentence);
		
		System.out.println("原子切分词网：\n" + wordnet.toString());
		
		//求解
		List<Vertex> vertexList = null;
		if("bigram".equalsIgnoreCase(segConfig.getSELECTED_GRAM())){
			vertexList = bigramByviterbi(wordnet);
		}else if("unigram".equalsIgnoreCase(segConfig.getSELECTED_GRAM())){
			vertexList = unigramByviterbi(wordnet);
		}
		
//		for(Vertex vertex : vertexList){
//			for(Nature nature : vertex.attribute.natures){
//				System.out.print(vertex.realWord + "  " + nature + "  ");
//			}
//			System.out.println();
//		}
		
		System.out.println("粗切分：" + convert(vertexList));
		
		if(segConfig.speechTag){
			speechTag(vertexList);// 隐马词性标注
		}
		
//		for(Vertex vertex : vertexList){
//			for(Nature nature : vertex.attribute.natures){
//				System.out.print(vertex.realWord + "  " + nature + "  ");
//			}
//			System.out.println();
//		}

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
