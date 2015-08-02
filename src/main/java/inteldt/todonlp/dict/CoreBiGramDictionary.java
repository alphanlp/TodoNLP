package inteldt.todonlp.dict;

import static inteldt.todonlp.manager.Predefine.logger;
import inteldt.todonlp.manager.Config;
import inteldt.todonlp.manager.Predefine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

/**
 * 二元词表
 * 
 * @author pei
 * 
 */
public class CoreBiGramDictionary {
	public final static String PATH = Config.BigramDictionaryPath;
	final static String datPath = Config.BigramDictionaryPath + ".table" + Predefine.BIN_EXT;

	private static int start[];
	private static int pair[];
	
	static {
		logger.info("开始加载二元词典" + PATH);
		long start = System.currentTimeMillis();
		load(PATH);
		logger.info(PATH + "加载成功，耗时" + (System.currentTimeMillis() - start) + "ms");

	}

	public static void load(String path) {
		if (loadDat(datPath)) return;
		TreeMap<Integer, TreeMap<Integer, Integer>> map = new TreeMap<Integer, TreeMap<Integer, Integer>>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(path)));
			String line;
			int total = 0;
            int maxWordId = CoreDictionary.trie.size();
			while ((line = br.readLine()) != null) {
				String[] params = line.split("\\s");
                String[] twoWord = params[0].split("@", 2);
               
                int id1 = CoreDictionary.trie.getID(twoWord[0]);
				if (id1 == -1) continue;
				int id2 = CoreDictionary.trie.getID(twoWord[1]);
				if (id2 == -1) continue;
				
				int freq = Integer.parseInt(params[1]);
                TreeMap<Integer, Integer> biMap = map.get(id1);
                if (biMap == null){
                    biMap = new TreeMap<Integer, Integer>();
                    map.put(id1, biMap);
                }
                biMap.put(id2, freq);
                total += 2;
			}
			br.close();
			
			start = new int[maxWordId + 2];
            pair = new int[total];  // total是接续的个数*2
            int offset = 0;

            start[0] = 0;
            for (int i = 0; i <= maxWordId; ++i){
                TreeMap<Integer, Integer> bMap = map.get(i);
                if (bMap != null)
                {
                    for (Map.Entry<Integer, Integer> entry : bMap.entrySet())
                    {
                        int index = offset << 1; // index * 2
                        pair[index] = entry.getKey();
                        pair[index + 1] = entry.getValue();
                        ++offset;
                    }
                }
                start[i + 1] = offset;
            }
		} catch (FileNotFoundException e) {
			logger.severe("二元词典加载失败，" + path + "不存在！" + e);
			System.exit(-1);
		} catch (IOException e) {
			logger.severe("二元词典" + path + "读取错误！" + e);
			System.exit(-1);
		}

		logger.info("开始缓存二元词典到" + datPath);
		if (!saveDat(datPath)) {
			logger.warning("缓存二元词典到" + datPath + "失败");
		}
	}
	
	/**
	 * 保存成2进制文件
	 * @param path
	 * @return
	 */
	public static boolean saveDat(String path) {
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(path));
			out.writeObject(start);
            out.writeObject(pair);
            out.close();
		} catch (Exception e) {
			logger.log(Level.WARNING, "在缓存" + path + "时发生异常", e);
			return false;
		}

		return true;
	}

	public static boolean loadDat(String path) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					path));
			start = (int[]) in.readObject();
            pair = (int[]) in.readObject();
			in.close();
		} catch (Exception e) {
			logger.warning("尝试载入缓存文件" + path + "发生异常[" + e
					+ "]，下面将载入源文件并自动缓存...");
			return false;
		}
		return true;
	}

	/**
	 * 获取两词的共现频次
	 * 
	 * @param wordId1
	 * @param wordId2
	 * @return
	 */
	public static int getBiGramFreq(int fromWordId, int toWordId) {
		if (fromWordId == -1){
            return 0;
        }
        if (toWordId == -1){
            return 0;
        }
        int index = binarySearch(pair, start[fromWordId], start[fromWordId + 1] - start[fromWordId], toWordId);
        if (index < 0) return 0;
        index <<= 1;
        return pair[index + 1];
	}
	
	/**
     * 二分搜索，由于二元接续前一个词固定时，后一个词比较少，所以二分也能取得很高的性能
     * @param a 目标数组
     * @param fromIndex 开始下标
     * @param length 长度
     * @param key 词的id
     * @return 共现频次
     */
    private static int binarySearch(int[] a, int fromIndex, int length, int key)
    {
        int low = fromIndex;
        int high = fromIndex + length - 1;

        while (low <= high)
        {
            int mid = (low + high) >>> 1;// 如富豪右移一位 0000000111 --> 000000011，也就是7变成3
            int midVal = a[mid << 1];

            if (midVal < key)
                low = mid + 1;
            else if (midVal > key)
                high = mid - 1;
            else
                return mid; // 找到key
        }
        return -(low + 1);  // 没有发现key
    }

	public static void main(String[] args) {
		int freq = CoreBiGramDictionary.getBiGramFreq(
				CoreDictionary.trie.getID("根本性"),
				CoreDictionary.trie.getID("冲击"));
		System.out.println(freq);
	}
}
