package inteldt.todonlp.seg;

import inteldt.todonlp.manager.Predefine;

/**
 * 分词器配置类
 * 
 * @author User
 *
 */
public class SegConfig {
	 /**
     * 分词结果是否展示词性
     */
    public static boolean ShowTermNature = true;
    
    private String SELECTED_GRAM = "bigram";// N元模型，默认选择bigram分词器

	public String getSELECTED_GRAM() {
		return SELECTED_GRAM;
	}

	public void setSELECTED_GRAM(String sELECTED_GRAM) {
		if("bigram".equalsIgnoreCase(sELECTED_GRAM)){
			this.SELECTED_GRAM = sELECTED_GRAM;
		}else if("unigram".equalsIgnoreCase(sELECTED_GRAM)){
			this.SELECTED_GRAM = sELECTED_GRAM;
		}else{
			Predefine.logger.severe("不支持" + sELECTED_GRAM + "分词！请选择BIGRAM或UNIGRAM分词");
			System.exit(-1);
		}
		
	}
	
	/**
     * 词性标注
     */
    public boolean speechTag = true;
    
    /**
     * 是否加载用户词典
     */
    public boolean userCustomDictionary = true;
}
