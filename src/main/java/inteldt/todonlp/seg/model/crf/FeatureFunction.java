package inteldt.todonlp.seg.model.crf;

/**
 * 特征函数
 * 
 * @author pei
 */
public class FeatureFunction {
	/**
	 * 环境参数，特征
	 */
	char[] o;

	/**
	 * 权值，按照index对应于tag的id
	 */
	double[] w;// 长度为4，也就是标注集的大小

	public FeatureFunction(char[] o, int tagSize) {
		this.o = o;
		w = new double[tagSize];
	}

	public FeatureFunction() {
	}
}
