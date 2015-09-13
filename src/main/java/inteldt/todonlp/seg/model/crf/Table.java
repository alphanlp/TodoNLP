/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/9 21:34</create-date>
 *
 * <copyright file="Table.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package inteldt.todonlp.seg.model.crf;

/**
 * CRF模型的字符串标记数据结构
 * 
 * @author pei
 */
public class Table
{
    /**
     * 真实值，请不要直接读取
     */
    public String[][] value;
    static final String HEAD = "_B";

    @Override
    public String toString()
    {
        if (value == null) return "null";
        final StringBuilder sb = new StringBuilder(value.length * value[0].length * 2);
        for (String[] line : value)
        {
            for (String element : line)
            {
                sb.append(element).append('\t');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * 获取表中某一个元素
     * @param x
     * @param y
     * @return
     */
    public String get(int x, int y)
    {
        if (x < 0) return HEAD + x;
        if (x >= value.length) return HEAD + "+" + (x - value.length + 1);

        return value[x][y];
    }

    public void setLast(int x, String t)
    {
        value[x][value[x].length - 1] = t;
    }

    public int size()
    {
        return value.length;
    }
}
