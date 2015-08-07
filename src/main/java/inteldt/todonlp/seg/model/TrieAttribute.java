package inteldt.todonlp.seg.model;

import inteldt.todonlp.model.Nature;

import java.io.Serializable;

/**
 * 属性类，存放词性和词性频率。
 * <p>
 * 采用静态内部类，是因为该类不调用外部对象，而只是作为此词典中词的属性。
 * </p>
 * @author lenovo
 *
 */
public class TrieAttribute implements Serializable {
		private static final long serialVersionUID = 1L;
		 /**
         * 总词频      
         */
        public int totalFreq;
        
		/**
         * 词性列表
         */
        public Nature[] natures;
        /**
         * 词性对应的词频
         */
        public int[] freqs;
        
        public TrieAttribute(int size){
        	natures = new Nature[size];
        	freqs = new int[size];
        }

        public TrieAttribute(Nature[] nature, int[] frequency){
            this.natures = nature;
            this.freqs = frequency;
        }

        public TrieAttribute(Nature nature, int frequency){
            this(1);
            totalFreq = frequency;
            this.natures[0] = nature;
            this.freqs[0] = frequency;
        }
        
        /**
         * 获取词性的词频
         *
         * @param nature 词性
         * @return 词频
         */
        public int getNatureFreq(final Nature nature)
        {
            int result = 0;
            int i = 0;
            for (Nature pos : this.natures)
            {
                if (nature == pos)
                {
                    return freqs[i];
                }
                ++i;
            }
            return result;
        }
    }