package inteldt.todonlp.seg;

import inteldt.todonlp.dictionary.CoreDictionary;
import inteldt.todonlp.model.Nature;
import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class FMMSegment extends MechanicalSegment {
	
	
	public static void main(String[] args) {
		List<Term> nodes = new FMMSegment().segSentence("人民日报历史上第一块新闻评论版");
		System.out.println("正向最大长度匹配分词:" + nodes);
	}

	@Override
	protected List<Term> segSentence(String sentence) {
		List<Term>  list = new ArrayList<Term>();
		if(sentence == null || "".equals(sentence)){
			return list;
		}
		
		while(sentence.length() > 0){
			String candidate = sentence;
			int index = candidate.length() -1;
			for(; index >= 0; index--){
				CoreDictionary.Attribute attri = CoreDictionary.trie.getAttribute(candidate.substring(0,index+1));
				if(attri != null){
					list.add(new Term(candidate.substring(0,index+1),attri.natures[0]));
					break;
				}
				
				if(index == 0){
					list.add(new Term(candidate.substring(0,1),Nature.un));
					break;
				}
			}
			sentence = sentence.substring(index+1);
		}
		
		return list;
	}

}
