package inteldt.todonlp.seg.model;

import inteldt.todonlp.manager.Predefine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * 转移矩阵词典
 *
 * @param <E> 标签的枚举类型
 * @author pei
 */
public class TransferMatrix<E extends Enum<E>>
{
    Class<E> enumType;
    /**
     * 内部标签下标最大值不超过这个值，用于矩阵创建
     */
    public int ordinaryMax;

    public TransferMatrix(Class<E> enumType)
    {
        this.enumType = enumType;
    }

    /**
     * 储存转移矩阵
     */
    int matrix[][];

    /**
     * 储存每个标签出现的次数
     */
    int total[];

    /**
     * 所有标签出现的总次数
     */
    int totalFrequency;

    // HMM的五元组
    /**
     * 隐状态
     */
    public int[] states;
    //int[] observations;
    /**
     * 初始概率
     */
    public double[] start_probability;
    /**
     * 转移概率
     */
    public double[][] transititon_probability;

    /**
     * 加载转移矩阵
     * @param path
     * @return
     */
    public boolean load(String path){
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-8"));
           
            // 第一行是矩阵的各个类型
            String line = br.readLine();
            String[] _param = line.split(",");
            
            // 为了制表方便，第一个label是无用，要去除
            String[] labels = new String[_param.length - 1];// 存放标签
            System.arraycopy(_param, 1, labels, 0, labels.length);
            int[] ordinaryArray = new int[labels.length];
            ordinaryMax = 0;
            for (int i = 0; i < ordinaryArray.length; ++i){
                ordinaryArray[i] = convert(labels[i]).ordinal();//枚举常量的序数，从0开始
                ordinaryMax = Math.max(ordinaryMax, ordinaryArray[i]);
            }
            ++ordinaryMax;
            matrix = new int[ordinaryMax][ordinaryMax];// 词性转移频次矩阵，词性在枚举中的序列，对应matrix的位置索引
            for (int i = 0; i < ordinaryMax; ++i){
                for (int j = 0; j < ordinaryMax; ++j){
                    matrix[i][j] = 0;
                }
            }
            
            // 之后就描述了矩阵，将具体的值读近矩阵
            while ((line = br.readLine()) != null){
                String[] paramArray = line.split(",");
                int currentOrdinary = convert(paramArray[0]).ordinal();
                for (int i = 0; i < ordinaryArray.length; ++i){// 矩阵按顺序存储，通过词性枚举对应的次序映射
                    matrix[currentOrdinary][ordinaryArray[i]] = Integer.valueOf(paramArray[1 + i]);
                }
            }
            br.close();
            
            // 需要统计一下每个标签出现的次数
            total = new int[ordinaryMax];
            int[] totalRow = new int[ordinaryMax];
            for (int j = 0; j < ordinaryMax; ++j){
                total[j] = 0;
                totalRow[j] = 0;
                for (int i = 0; i < ordinaryMax; ++i){
                    total[j] += matrix[i][j];
                    totalRow[j] += matrix[j][i];
                }
            }
//            
//            for (int j = 0; j < ordinaryMax; ++j)
//            {// 这是什么意思，难道不是多计算了吗？
//                total[j] += matrix[j][j];
//            }
            for (int j = 0; j < ordinaryMax; ++j){
                totalFrequency += total[j];
            }
           
            // 下面计算HMM四元组
            states = ordinaryArray;
            start_probability = new double[ordinaryMax];
            for (int s : states){
                double frequency = total[s] + 1e-8;
                start_probability[s] = -Math.log(frequency / totalFrequency); // 使用概率可能会存在浮点溢出的问题，所有取其log值，加负号边正，取最短路径
            }
            transititon_probability = new double[ordinaryMax][ordinaryMax];
            for (int from : states){
                for (int to : states)
                { // 计算方法是否有问题？？？这个计算出来的应该是词性的联合概率，难道这里不是应该计算转移概率，也就是条件概率吗？
                    double frequency = matrix[from][to] + 1e-8;
                    transititon_probability[from][to] = -Math.log(frequency / totalRow[from]);// 这是我改的，原本是除-Math.log(frequency /totalFrequency)
//                  System.out.println("from" + NR.values()[from] + " to" + NR.values()[to] + " = " + transititon_probability[from][to]);
                }
            }
        }catch (Exception e){
            Predefine.logger.warning("读取" + path + "失败" + e);
            return false;
        }
        return true;
    }

    /**
     * 获取转移频次
     *
     * @param from
     * @param to
     * @return
     */
    public int getFrequency(String from, String to)
    {
        return getFrequency(convert(from), convert(to));
    }

    /**
     * 获取转移频次
     *
     * @param from
     * @param to
     * @return
     */
    public int getFrequency(E from, E to)
    {
        return matrix[from.ordinal()][to.ordinal()];
    }

    /**
     * 获取e的总频次
     *
     * @param e
     * @return
     */
    public int getTotalFrequency(E e)
    {
        return total[e.ordinal()];
    }

    /**
     * 获取所有标签的总频次
     *
     * @return
     */
    public int getTotalFrequency()
    {
        return totalFrequency;
    }

    protected E convert(String label)
    {
        return Enum.valueOf(enumType, label);
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("TransformMatrixDictionary{");
        sb.append("enumType=").append(enumType);
        sb.append(", ordinaryMax=").append(ordinaryMax);
        sb.append(", matrix=").append(Arrays.toString(matrix));
        sb.append(", total=").append(Arrays.toString(total));
        sb.append(", totalFrequency=").append(totalFrequency);
        sb.append('}');
        return sb.toString();
    }
}
