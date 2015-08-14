package inteldt.todonlp.manager;

/**
 * 配置类，提供默认路径。
 * 可以通过.properties配置（按照 变量名=值 的形式）
 * 
 * @author lenovo
 *
 */
public class Config {
	 /**
     * 开发模式
     */
    public static boolean DEBUG = true;
	
	/**
     * 核心词典路径。词典每一行由词和词性组成，一行格式为：词  词性1 词性频率1 词性2 词性频率2
     */
    public static String CoreDictionaryPath = "resources/dictionary/CoreNatureDictionary.txt";
    
    /**
     * 2元语法词典路径
     */
    public static String BigramDictionaryPath = "resources/dictionary/CoreNatureDictionary.ngram.txt";
    
    /**
     * 核心词典词性转移矩阵路径
     */
    public static String CoreTransferMatrixDictionaryPath = "resources/dictionary/CoreNatureDictionary.tr.txt";
    
    /**
     * 字符类型对应表
     */
    public static String CharTypePath = "resources/dictionary/other/CharType.dat.yes";
    
    /**
     * 用户自定义词典路径
     */
    public static String CustomDictionaryPath = "resources/dictionary/custom/CustomDictionary.txt";
}
