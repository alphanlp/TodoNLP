package inteldt.todonlp.tokenizer;

import inteldt.todonlp.seg.Segment;
import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.ngram.BigramSegment;

import java.util.List;

/**
 * 默认分词器，可以使用用户自定义词典
 * 
 * @author pei
 *
 */
public class DefaultTokenizer implements Tokenizer{
	private static final Segment SEGMENT = new BigramSegment();

	@Override
	public List<Term> segment(String text) {
		return SEGMENT.seg(text);
	}

	@Override
	public List<Term> segment(char[] text) {
		return SEGMENT.seg(text);
	}

	@Override
	public List<List<Term>> seg2sentence(String text) {
		return SEGMENT.seg2sentence(text);
	}	
}
