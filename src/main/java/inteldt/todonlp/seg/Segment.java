package inteldt.todonlp.seg;

import inteldt.todonlp.seg.model.Term;

import java.util.List;

/**
 * 分词器的基类
 * @author pei
 *
 */
public abstract class Segment {
	
	/**
	 * 分词
	 * @param text  文本
	 * @return 
	 */
	public List<Term> seg(String text){
//        if (HanLP.Config.Normalization)
//        {
//            CharTable.normalization(charArray);
//        }
//        if (Config.threadNumber > 1 && charArray.length > 10000)    // 小文本多线程没意义，反而变慢了
//        {
//            List<String> sentenceList = SentencesUtil.toSentenceList(charArray);
//            String[] sentenceArray = new String[sentenceList.size()];
//            sentenceList.toArray(sentenceArray);
//            //noinspection unchecked
//            List<Term>[] termListArray = new List[sentenceArray.length];
//            final int per = sentenceArray.length / config.threadNumber;
//            WorkThread[] threadArray = new WorkThread[config.threadNumber];
//            for (int i = 0; i < config.threadNumber - 1; ++i)
//            {
//                int from = i * per;
//                threadArray[i] = new WorkThread(sentenceArray, termListArray, from, from + per);
//                threadArray[i].start();
//            }
//            threadArray[config.threadNumber - 1] = new WorkThread(sentenceArray, termListArray, (config.threadNumber - 1) * per, sentenceArray.length);
//            threadArray[config.threadNumber - 1].start();
//            try
//            {
//                for (WorkThread thread : threadArray)
//                {
//                    thread.join();
//                }
//            }
//            catch (InterruptedException e)
//            {
//                logger.severe("线程同步异常：" + TextUtility.exceptionToString(e));
//                return Collections.emptyList();
//            }
//            List<Term> termList = new LinkedList<Term>();
//            if (config.offset || config.indexMode)  // 由于分割了句子，所以需要重新校正offset
//            {
//                int sentenceOffset = 0;
//                for (int i = 0; i < sentenceArray.length; ++i)
//                {
//                    for (Term term : termListArray[i])
//                    {
//                        term.offset += sentenceOffset;
//                        termList.add(term);
//                    }
//                    sentenceOffset += sentenceArray[i].length();
//                }
//            }
//            else
//            {
//                for (List<Term> list : termListArray)
//                {
//                    termList.addAll(list);
//                }
//            }
//
//            return termList;
//        }
        return segSentence(text);
    }
	
	 /**
     * 分词
     *
     * @param text 待分词文本char数组
     * @return 单词列表
     */
    public List<Term> seg(char[] text)
    {
        assert text != null;
//        if (Config.Normalization)
//        {
//            CharTable.normalization(text);
//        }
        return segSentence(new String(text));
    }
	
	/**
     * 给一个句子分词。工厂方法，有具体的子类实现。也就是不同的分词方法实现。
     *
     * @param sentence 待分词句子
     * @return 单词列表
     */
    protected abstract List<Term> segSentence(String sentence);

}
