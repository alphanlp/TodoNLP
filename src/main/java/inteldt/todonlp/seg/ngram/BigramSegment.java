package inteldt.todonlp.seg.ngram;

import inteldt.todonlp.manager.Config;
import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.model.Vertex;
import inteldt.todonlp.seg.model.WordNet;

import java.util.List;

/**
 * 2元分词
 * @author pei
 *
 */
public class BigramSegment extends NGramSegment{

	public static void main(String[] args) {
		//test
		BigramSegment seg = new BigramSegment();		
		List<Term> terms = seg.segSentence("月花费1806元，汉兰达2.0T车型用车成本");
//		List<Term> terms = seg.segSentence("把你的爱给他，他会爱你");
		System.out.println("隐马标注：" + terms);
	}
	
	@Override
	protected List<Term> segSentence(String sentence) {
		// 生成词网
		WordNet wordnet = generateWordNet(sentence);
		
		if(Config.DEBUG){
			System.out.println("切分词网：\n" + wordnet.toString());
		}

		//求解
		List<Vertex> vertexList =  optimSeg(wordnet);
		
		if(Config.DEBUG){
			System.out.println("切分结果：" + convert(vertexList));
		}

		if(segConfig.speechTag){
			speechTag(vertexList);// 隐马词性标注
		}

		return convert(vertexList);
	}
	
	@Override
	protected void updateFrom(Vertex to, Vertex from) {
		to.updateFromByBigram(from);
	}
	
	
}
