package inteldt.todonlp.seg.ngram;

import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.model.Vertex;
import inteldt.todonlp.seg.model.WordNet;

import java.util.List;

/**
 * 一元分词
 * @author pei
 *
 */
public class UnigramSegment extends NGramSegment{
	/**
	 * 找时间把权重全部打印出来看看，计算过程中所有的权重
	 * @param args
	 */
	public static void main(String[] args) {
		//test
		BigramSegment seg = new BigramSegment();		
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
		List<Vertex> vertexList =  optimSeg(wordnet);
		
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
	
	@Override
	protected void updateFrom(Vertex to, Vertex from) {
		to.updateFromByUnigram(from);
	}
}
