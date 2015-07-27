package inteldt.todonlp.dictionary;

import inteldt.todonlp.manager.Config;
import inteldt.todonlp.manager.Predefine;
import inteldt.todonlp.model.Nature;
import inteldt.todonlp.trie.TernaryTrie;
import static inteldt.todonlp.manager.Predefine.logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * 核心词典：存储词、词性的词典
 * 
 * @author lenovo
 *
 */
public class CoreDictionary {
	/**
	 * Trie树，词典的组织结构
	 */
	public static TernaryTrie<Attribute> trie = new TernaryTrie<Attribute>();
	/**
	 * 词典路径，设置为final，不可更改
	 */
	public final static String path = Config.CoreDictionaryPath;
	
	// 加载词典
    static
    {
        long start = System.currentTimeMillis();
        if (!load(path))
        {
            System.err.printf("核心词典%s加载失败\n", path);
            System.exit(-1);
        }
        else
        {
        	logger.info(path + "加载成功，耗时" + (System.currentTimeMillis() - start) + "ms");
        }
    }
   
    /**
     * 实际的加载词典
     * @param path  词典路径
     * @return
     */
    public static boolean load(String path){
    	logger.info("核心词典开始加载：" + path);
    	
    	try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String line;
			long start = System.currentTimeMillis();
			while((line = br.readLine()) != null){
				String[] params = line.split("\\t");// 分割
				int natureCount = (params.length - 1) / 2;// 词性的个数
                Attribute attribute = new Attribute(natureCount);
                for (int i = 0; i < natureCount; ++i)
                {
                    attribute.natures[i] = Enum.valueOf(Nature.class, params[1 + 2 * i]);
                    attribute.freqs[i] = Integer.parseInt(params[2 + 2 * i]);
                    attribute.totalFreq += attribute.freqs[i];
                }
                trie.insert(params[0], attribute);// 插入到词典中，构建词典
			} 
			logger.info("核心词典加载成功:" + trie.size() + "个词条，耗时" + (System.currentTimeMillis() - start) + "ms");
		} catch (FileNotFoundException e) {
			logger.warning("核心词典" + path + "不存在！" + e);
			return false;
		} catch (IOException e) {
			logger.warning("核心词典" + path + "读取错误！" + e);
			return false;
		}
    	
    	return true;
    }
    
    /**
     * 属性类，存放词性和词性频率。
     * <p>
     * 采用静态内部类，是因为该类不调用外部对象，而只是作为此词典中词的属性。
     * </p>
     * @author lenovo
     *
     */
    public static class Attribute implements Serializable{
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
       
        
        public Attribute(int size){
        	natures = new Nature[size];
        	freqs = new int[size];
        }

        public Attribute(Nature[] nature, int[] frequency){
            this.natures = nature;
            this.freqs = frequency;
        }

        public Attribute(Nature nature, int frequency){
            this(1);
            totalFreq = frequency;
            this.natures[0] = nature;
            this.freqs[0] = frequency;
        }
    	
    }
    
}
