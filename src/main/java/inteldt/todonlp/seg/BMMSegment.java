package inteldt.todonlp.seg;

import inteldt.todonlp.dict.CoreDictionary;
import inteldt.todonlp.model.Nature;
import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 逆向最大长度匹配分词法
 * <p>
 * 这个和上面相反，就是倒着推理。比如“沿海南方向”，我们按正向最大匹配
 * 来做就会切分成 “沿海/南方/向”，这样就明显不对。采用逆向最大匹配法则来
 * 解决这个问题，从句子的最后取得“方向”这两个字查找词典找到“方向”这个词。
 * 再加上“南方向”组成三字组合查找词典没有这个词，查找结束，找到“方向”这个
 * 词。以此类推，最终分出“沿/海南/方向”。
 * </p>
 * @author pei
 *
 */
public class BMMSegment extends MechanicalSegment {
	
	public static void main(String[] args) {
		List<Term> nodes = new BMMSegment().segSentence("人民日报历史上第一块新闻评论版");
		System.out.println("逆向最大长度匹配分词：" + nodes);
	}

	@Override
	protected List<Term> segSentence(String sentence) {
		List<Term>  list = new ArrayList<Term>();
		if(sentence == null || sentence.length() == 0){
			return list;
		}
		
		while(sentence.length() > 0){
			String candidate = sentence;
			int index = 0;
			for(; index < candidate.length(); index++){
				CoreDictionary.Attribute attri = CoreDictionary.trie.getAttribute(candidate.substring(index));
				if(attri != null){
					list.add(new Term(candidate.substring(index),attri.natures[0]));
					break;
				}
				
				if(index == candidate.length() - 1){
					list.add(new Term(candidate.substring(index),Nature.un));
					break;
				}
			}
			sentence = sentence.substring(0,index);
		}
		Collections.reverse(list);		
		return list;
	}
}
