package inteldt.todonlp.util;

import inteldt.todonlp.manager.Config;
import inteldt.todonlp.manager.Predefine;

/**
 * 字符类型
 * 
 * @author pei
 */
public class CharType{
    /**
     * 单字节
     */
    public static final byte CT_SINGLE = 5; // SINGLE byte

    /**
     * 分隔符"!,.?()[]{}+=
     */
    public static final byte CT_DELIMITER = CT_SINGLE + 1;// delimiter

    /**
     * 中文字符
     */
    public static final byte CT_CHINESE = CT_SINGLE + 2;// Chinese Char

    /**
     * 字母
     */
    public static final byte CT_LETTER = CT_SINGLE + 3;// Pinyin

    /**
     * 数字
     */
    public static final byte CT_NUM = CT_SINGLE + 4;// number

    /**
     * 序号
     */
    public static final byte CT_INDEX = CT_SINGLE + 5;// xu hao

    /**
     * 其他
     */
    public static final byte CT_OTHER = CT_SINGLE + 12;// Other
    
    static byte[] type;

    static{
        type = new byte[65536];
        Predefine.logger.info("字符类型对应表开始加载 " + Config.CharTypePath);
        long start = System.currentTimeMillis();
        ByteArray byteArray = ByteArray.createByteArray(Config.CharTypePath);
        if (byteArray == null){
            System.err.println("字符类型对应表加载失败：" + Config.CharTypePath);
            System.exit(-1);
        }
        else{
            while (byteArray.hasMore()){
                int b = byteArray.nextChar();
                int e = byteArray.nextChar();
                byte t = byteArray.nextByte();
                for (int i = b; i <= e; ++i)
                {
                    type[i] = t;
                }
            }
            Predefine.logger.info("字符类型对应表加载成功，耗时" + (System.currentTimeMillis() - start) + " ms");
        }
    }

    /**
     * 获取字符的类型
     * @param c
     * @return
     */
    public static byte get(char c){
        return type[(int)c];
    }
}