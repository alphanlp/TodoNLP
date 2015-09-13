package inteldt.todonlp.seg.model.crf;

import inteldt.todonlp.manager.Config;
import inteldt.todonlp.trie.TernaryTrie;
import inteldt.todonlp.util.IOUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * CRF模型
 * 
 * @author pei
 * 
 */
public class CRFSegmentModel {
	private Logger logger = Logger.getLogger(CRFSegmentModel.class.getName());

	private static CRFSegmentModel model;
	
	private int idM;
	private int idE;
	private int idS;

	/** 标签和id的相互转换 */
	private Map<String, Integer> tag2id;
	private String[] id2tag;

	/** 特征函数 */
	private TernaryTrie<FeatureFunction> featureFunctionTrie;
	/** 特征模板 */
	private List<FeatureTemplate> featureTemplateList;

	/** tag的转移矩阵 */
	private double[][] matrix;
	
	public CRFSegmentModel() {
		featureFunctionTrie = new TernaryTrie<FeatureFunction>();
	}
	
	/**
	 * 获取model对象
	 * @return
	 */
	public static CRFSegmentModel getModel(){
		if(model == null){
			synchronized(CRFSegmentModel.class){
				if(model == null){
					model = new CRFSegmentModel();
					model.init();
				}
			}
		}
		
		return model;
	}

	public void init(){
		model.loadTxt(Config.CRFSegmentModelPath);
		
		idM = getTagId("M");
		idE = getTagId("E");
		idS = getTagId("S");
	}
	
	/**
	 * 加载CRF模型
	 * 
	 * @param path
	 */
	public void loadTxt(String path) {
		IOUtil.LineIterator lineIterator = new IOUtil.LineIterator(path);
		if (!lineIterator.hasNext())
			return;

		logger.info(lineIterator.next()); // verson
		logger.info(lineIterator.next()); // cost-factor
		logger.info(lineIterator.next()); // maxid
		logger.info(lineIterator.next()); // xsize
		lineIterator.next(); // blank

		/*
		 * B,M,E,S
		 */
		String line;
		int id = 0;
		tag2id = new HashMap<String, Integer>();
		while ((line = lineIterator.next()).length() != 0) {
			tag2id.put(line, id);
			++id;
		}
		id2tag = new String[tag2id.size()];
		final int size = id2tag.length;
		for (Map.Entry<String, Integer> entry : tag2id.entrySet()) {
			id2tag[entry.getValue()] = entry.getKey();
		}

		/*
		 * U00:%x[-1,0] 
		 * U01:%x[0,0] 
		 * U02:%x[1,0] 
		 * U03:%x[-1,0]%x[0,0]
		 * U04:%x[0,0]%x[1,0] 
		 * U05:%x[-1,0]%x[1,0]
		 */
		featureTemplateList = new LinkedList<FeatureTemplate>();
		while ((line = lineIterator.next()).length() != 0) {
			if (!"B".equals(line)) {
				FeatureTemplate featureTemplate = FeatureTemplate.create(line);
				featureTemplateList.add(featureTemplate);
			} else {
				matrix = new double[size][size];
			}
		}

		if (matrix != null) {
			lineIterator.next(); // 0 B
		}

		/*
		 * 特征函数
		 */
		HashMap<String, FeatureFunction> featureFunctionMap = new HashMap<String, FeatureFunction>();
		List<FeatureFunction> featureFunctionList = new LinkedList<FeatureFunction>(); // 读取权值的时候用
		while ((line = lineIterator.next()).length() != 0) {
			String[] args = line.split(" ", 2);
			char[] charArray = args[1].toCharArray();
			FeatureFunction featureFunction = new FeatureFunction(charArray, size);
			featureFunctionMap.put(args[1],featureFunction);
			featureFunctionList.add(featureFunction);
		}

		/* 转移概率 */
		if (matrix != null) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					matrix[i][j] = Double.parseDouble(lineIterator.next());
				}
			}
		}

		/* 特征函数权值 */
		for (FeatureFunction featureFunction : featureFunctionList) {
			for (int i = 0; i < size; i++) {
				featureFunction.w[i] = Double.parseDouble(lineIterator.next());
			}
		}
		
		if (lineIterator.hasNext()) {
			logger.warning("文本读取有残留，可能会出问题！" + path);
		}
		lineIterator.close();

		for(Map.Entry<String, FeatureFunction> entry : featureFunctionMap.entrySet()){
			featureFunctionTrie.insert(entry.getKey(), entry.getValue());
		}
		
	}

	/**
	 * 维特比后向算法标注
	 * 
	 * @param table
	 */
	public void tag(Table table) {
		int size = table.size();// 待标记序列
		if (size == 1)// 如果只有一个字符，直接设置为S
		{
			table.setLast(0, "S");
			return;
		}
		double[][] net = new double[size][4];// 格架，标记类4个:B 开始、M 中间、E 结束、S 单独成

		/*
		 * 初始化，计算序列各位置，对应的标记类的特征计算值。 注意，这里没有计算标记类的转移概率，只是状态概率啊 （非规范化概率）
		 */
		for (int i = 0; i < size; ++i) {
			LinkedList<double[]> scoreList = computeScoreList(table, i);
			for (int tag = 0; tag < 4; ++tag) {
				net[i][tag] = computeScore(scoreList, tag);
//				System.out.println("net["+i+"]["+tag+"]" + net[i][tag]);
			}
		}
		net[0][idM] = -1000.0; // 第一个字不可能是M或E
		net[0][idE] = -1000.0;

		int[][] from = new int[size][4];// 记忆回溯路径
		for (int i = 1; i < size; ++i) {
			for (int now = 0; now < 4; ++now) {
				double maxScore = -1e10;
				for (int pre = 0; pre < 4; ++pre) {
					if (matrix[pre][now] <= 0)
						continue; // 转移小于0，直接下一个
					double score = net[i - 1][pre] // 之前标记类的可能性大小（非规范化概率）
							+ matrix[pre][now] // 转移的可能性大小
							+ net[i][now]; // 当前位置标记类可能性大小
					if (score > maxScore) {
						maxScore = score;
						from[i][now] = pre; // 记录路径，也即其前面的标记类
					}
				}
				net[i][now] = maxScore;
			}
		}

		// 反向回溯最佳路径
		int maxTag = net[size - 1][idS] > net[size - 1][idE] ? idS : idE;// 结尾只能从S或者E中选择
		table.setLast(size - 1, id2tag[maxTag]);
		maxTag = from[size - 1][maxTag];
		for (int i = size - 2; i > 0; --i) {
			table.setLast(i, id2tag[maxTag]);
			maxTag = from[i][maxTag];
		}
		table.setLast(0, id2tag[maxTag]);
	}

	/**
	 * 计算ScoreList
	 * 
	 * @param table
	 * @param current
	 * @return
	 */
	public LinkedList<double[]> computeScoreList(Table table, int current) {
		LinkedList<double[]> scoreList = new LinkedList<double[]>();
		for (FeatureTemplate featureTemplate : featureTemplateList) {// 模板，对应一个特征函数，一个特征函数，对应一个double[],也就是特征函数对应的参数值
			char[] o = featureTemplate.generateParameter(table, current);// U01:我
			FeatureFunction featureFunction = featureFunctionTrie.getAttribute(new String(o));
			if (featureFunction == null){				
				continue;
			}
			scoreList.add(featureFunction.w);
		}

		return scoreList;
	}

	/**
	 * 给一系列特征函数结合tag打分
	 * 
	 * @param scoreList
	 * @param tag
	 * @return
	 */
	protected double computeScore(LinkedList<double[]> scoreList, int tag) {
		double score = 0;
		for (double[] w : scoreList) {
			score += w[tag];
		}
		return score;
	}

	/**
	 * 获取某个tag的ID
	 * 
	 * @param tag
	 * @return
	 */
	public Integer getTagId(String tag) {
		return tag2id.get(tag);
	}
}
