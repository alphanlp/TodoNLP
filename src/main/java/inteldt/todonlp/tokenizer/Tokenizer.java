package inteldt.todonlp.tokenizer;

import inteldt.todonlp.seg.model.Term;

import java.util.List;

/**
 * 分词器接口
 * 
 * @author lenovo
 *
 */
public interface Tokenizer {
	/**
     * 分词
     * @param text 文本
     * @return 分词结果
     */
    List<Term> segment(String text);
    /**
     * 分词
     * @param text 文本
     * @return 分词结果
     */
    List<Term> segment(char[] text);

    /**
     * 切分为句子形式
     * 
     * @param text 文本
     * @return 句子列表
     */
    List<List<Term>> seg2sentence(String text);
}
