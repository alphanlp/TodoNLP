package inteldt.todonlp.util;

import inteldt.todonlp.model.Nature;
import inteldt.todonlp.seg.model.TransferMatrix;
import inteldt.todonlp.seg.model.Vertex;

import java.util.Iterator;
import java.util.List;

/**
 * 维特比算法
 *
 * @author hankcs
 */
public class Viterbi{
    /**
     * 求解HMM模型，所有概率请提前取对数
     *
     * @param obs     观测序列
     * @param states  隐状态
     * @param start_p 初始概率（隐状态）
     * @param trans_p 转移概率（隐状态）
     * @param emit_p  发射概率 （隐状态表现为显状态的概率）
     * @return 最可能的序列
     */
    public static int[] compute(int[] obs, int[] states, double[] start_p, double[][] trans_p, double[][] emit_p){
        int _max_states_value = 0;
        for (int s : states){
            _max_states_value = Math.max(_max_states_value, s);
        }
        ++_max_states_value;
        double[][] V = new double[obs.length][_max_states_value];
        int[][] path = new int[_max_states_value][obs.length];

        for (int y : states){
            V[0][y] = start_p[y] + emit_p[y][obs[0]];
            path[y][0] = y;
        }

        for (int t = 1; t < obs.length; ++t){
            int[][] newpath = new int[_max_states_value][obs.length];

            for (int y : states){
                double prob = Double.MAX_VALUE;
                int state;
                for (int y0 : states)
                {
                    double nprob = V[t - 1][y0] + trans_p[y0][y] + emit_p[y][obs[t]];
                    if (nprob < prob)
                    {
                        prob = nprob;
                        state = y0;
                        // 记录最大概率
                        V[t][y] = prob;
                        // 记录路径
                        System.arraycopy(path[state], 0, newpath[y], 0, t);
                        newpath[y][t] = y;
                    }
                }
            }

            path = newpath;
        }

        double prob = Double.MAX_VALUE;
        int state = 0;
        for (int y : states){
            if (V[obs.length - 1][y] < prob){
                prob = V[obs.length - 1][y];
                state = y;
            }
        }

        return path[state];
    }

    /**
     * 针对TodoNLP的图模型的HMM模型求解
     *
     * @param vertexList                包含Vertex.B节点的路径
     * @param transformMatrixDictionary 词典对应的转移矩阵
     */
    public static void compute(List<Vertex> vertexList, TransferMatrix<Nature> transferMatrix){
        int length = vertexList.size() - 1;
        double[][] cost = new double[2][];  // 滚动数组
        Iterator<Vertex> iterator = vertexList.iterator();
        Vertex start = iterator.next();// 开始节点，为start，其状态是确定的，也就是词性是事前规定的
        Nature pre = start.attribute.natures[0];
        // 第一个是确定的
//        start.confirmNature(pre);
        // 第二个也可以简单地算出来
        Vertex preItem;
        Nature[] preTagSet;
        {
            Vertex item = iterator.next();// 取第二个词，为实际句子的第一个词
            cost[0] = new double[item.attribute.natures.length];// 第二个词对应的词性数组
            int j = 0;
            int curIndex = 0;// 词性数组的游标
            for (Nature cur : item.attribute.natures)
            {
                cost[0][j] = transferMatrix.transititon_probability[pre.ordinal()][cur.ordinal()] - 
                		Math.log((item.attribute.freqs[curIndex] + 1e-8) / transferMatrix.getTotalFrequency(cur));// log(转移概率)+log(发射概率)
                ++j;
                ++curIndex;
            }
            preTagSet = item.attribute.natures;
            preItem = item;
        }
        
        // 第三个开始复杂一些
        for (int i = 1; i < length; ++i){
            int index_i = i & 1; // i为奇数时，index_i为1，否则为0
            int index_i_1 = 1 - index_i;
            Vertex item = iterator.next();
            cost[index_i] = new double[item.attribute.natures.length];
            double perfect_cost_line = Double.MAX_VALUE;
            int k = 0;// curTagSet的cost对应的下标
            Nature[] curTagSet = item.attribute.natures;
            for (Nature cur : curTagSet)
            {// 选择当前阶段，的一个词性
                cost[index_i][k] = Double.MAX_VALUE;
                int j = 0;// preTagSet的cost对应的下标
                for (Nature p : preTagSet)
                {
                    double now = cost[index_i_1][j] + 
                    		transferMatrix.transititon_probability[p.ordinal()][cur.ordinal()] - // 转移概率 计算公式：p(ti|ti-1) = count(ti-1-->ti)/count(ti-1)
                    		Math.log((item.attribute.freqs[k] + 1e-8) / transferMatrix.getTotalFrequency(cur));//发射概率 计算公式：count(word1.naturei) / count(naturei)
                    if (now < cost[index_i][k]) 
                    {
                        cost[index_i][k] = now;
                        if (now < perfect_cost_line)
                        {
                            perfect_cost_line = now;
                            pre = p;
                        }
                    }
                    ++j;
                }
                ++k;
            }
            preItem.confirmNature(pre);
            preTagSet = curTagSet;
            preItem = item;
        }
    }
}

