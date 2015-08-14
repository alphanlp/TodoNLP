package inteldt.todonlp.tokenizer;

import inteldt.todonlp.seg.Segment;
import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.ngram.BigramSegment;

import java.util.List;

/**
 * 基础分词器，不能使用用户自定义词典
 * 
 * @author pei
 *
 */
public class BasicTokenizer implements Tokenizer{
	
	private static final Segment SEGMENT = new BigramSegment();
	
	// 对象初始化块
	{
		SEGMENT.enableCustomDictionary(false);
	}
	
	@Override
    public List<Term> segment(String text)
    {
        return SEGMENT.seg(text.toCharArray());
    }

	@Override
    public List<Term> segment(char[] text)
    {
        return SEGMENT.seg(text);
    }

	@Override
    public List<List<Term>> seg2sentence(String text)
    {
        return SEGMENT.seg2sentence(text);
    }
	
}

