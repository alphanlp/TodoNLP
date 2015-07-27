package inteldt.todonlp.trie;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 三叉Trie树(该项目用来构造词典的基础数据结构)
 * 
 * @author apei
 *
 */
public class TernaryTrie<V> implements Serializable {
	/***/
	private static final long serialVersionUID = 1L;
	
	private TernaryNode root;// 三叉树的根
	public TernaryTrie<V> trie;// 词典三叉trie树
	
	private int size;// 词典大小
	
	private class TernaryNode{
		/**
		 * 词典的基础：词。所有词典都必须有词。不过需要注意，trie的每个节点上只存储一个字符。
		 */
		char charValue;
		/**
		 * 依附与词的其他属性值
		 */
		V attriValue;
		
		/**
		 * 数据结构节点
		 */
		TernaryNode lNode;
		TernaryNode eNode;
		TernaryNode rNode;
		
		/**
		 * 是否是结束节点。结束节点为空节点。
		 */
		boolean isEnd;
		
		
		TernaryNode(){
			this.isEnd = false;
		}
	}
	
	
	/**
	 * 构造函数，建立一个空Trie数，跟为root
	 */
	public TernaryTrie(){
		this.root = new TernaryNode();
	}
	
	/**
	 * 生成一颗词典Trie树
	 */
	public void generateTrie(List<Map.Entry<String, V>> words){		
		if(words == null){
			return;
		}
		
		long start = System.currentTimeMillis();
		trie = new TernaryTrie<V>();
		for(Map.Entry<String, V> word : words){
			trie.insert(word.getKey(),word.getValue());
		}
		System.out.println("词典加载完成，共耗时：" + (System.currentTimeMillis() - start) + "毫秒");
	}
	
	/**
	 * 插入词及其属性到三叉数词典中
	 * 
	 * @param wordStr
	 */
	public void insert(String wordStr, V attriValue){
		if(wordStr == null || "".equals(wordStr.trim())){
			return;
		}
		
		TernaryNode currentNode = root;
		int charIndex = 0;
		while(true){
			int charComp = wordStr.charAt(charIndex) - currentNode.charValue;
			if(charComp == 0){// 相等
				charIndex ++;
				if(charIndex == wordStr.length()){
					currentNode.isEnd = true;
					TernaryNode node = currentNode;
					node.attriValue = attriValue;
					size++;// 词的大小+1
					return;
				}
				if(currentNode.eNode == null){
					currentNode.eNode = new TernaryNode();
					currentNode.eNode.charValue = wordStr.charAt(charIndex);
				}
				currentNode = currentNode.eNode;
			}else if(charComp < 0){// 小于
				if(currentNode.lNode == null){
					currentNode.lNode = new TernaryNode();
					currentNode.lNode.charValue = wordStr.charAt(charIndex);
				}
				currentNode = currentNode.lNode;
			}else{// 大于
				if(currentNode.rNode == null){
					currentNode.rNode = new TernaryNode();
					currentNode.rNode.charValue = wordStr.charAt(charIndex);
				}
				currentNode = currentNode.rNode;
			}
		}// while
	}
	
	/**
	 * 查找词是否存在
	 * 
	 * @param wordStr
	 * @return
	 */
	public boolean contains(String wordStr){
		if(wordStr == null || "".equals(wordStr.trim())){
			return false;
		}
		
		TernaryNode currentNode = root;
		int charIndex = 0;
		char cmpChar = wordStr.charAt(charIndex);
		int charComp;
		while(true){
			if(currentNode == null){
				return false;
			}
			
			charComp = cmpChar - currentNode.charValue;
			if(charComp == 0){// 相等
				charIndex ++;
				if(charIndex == wordStr.length()){
					return currentNode.isEnd;
				}else{
					cmpChar = wordStr.charAt(charIndex);
				}
				currentNode = currentNode.eNode;
			}else if(charComp < 0){// 小于
				currentNode = currentNode.lNode;
			}else{// 大于
				currentNode = currentNode.rNode;
			}
		}
	}
	
	/**
	 * 精确查找词，并返回词的属性值。如果没有查找到词，则返回null
	 * 
	 * @param wordStr
	 * @return
	 */
	public V search(String wordStr){
		if(wordStr == null || "".equals(wordStr.trim())){
			return null;
		}

		TernaryNode currentNode = root;
		int charIndex = 0;
		char cmpChar = wordStr.charAt(charIndex);
		int charComp;
		while(true){
			if(currentNode == null){
				return null;
			}
			
			charComp = cmpChar - currentNode.charValue;
			if(charComp == 0){// 相等
				charIndex ++;
				if(charIndex == wordStr.length()){
					if(currentNode.isEnd){
						TernaryNode node = new TernaryNode();
						node.attriValue = currentNode.attriValue;
						return node.attriValue;
					}
					return null;
				}else{
					cmpChar = wordStr.charAt(charIndex);
				}
				currentNode = currentNode.eNode;
			}else if(charComp < 0){// 小于
				currentNode = currentNode.lNode;
			}else{// 大于
				currentNode = currentNode.rNode;
			}
		}
	}
	
	/**
	 * 前缀查找。所有前缀为str的词全部查出，返回一个list，list以键值对的形式存储词和词对应的属性。
	 * @return
	 */
	public List<Map.Entry<String, V>> preSearch(String str){
		if(str == null || "".equals(str.trim())){
			return Collections.emptyList();
		}
		
		List<Map.Entry<String, V>> list = new ArrayList<Map.Entry<String, V>>();
		
		TernaryNode currentNode = root;
		int charIndex = 0;
		char cmpChar = str.charAt(charIndex);
		int charComp;
		while(true){
			if(currentNode == null){
				return list;
			}
			
			charComp = cmpChar - currentNode.charValue;
			if(charComp == 0){// 相等
				charIndex ++;
				if(charIndex == str.length()){
					if(currentNode.isEnd){
						list.add(new AbstractMap.SimpleEntry<String, V>(str, currentNode.attriValue));
						if(currentNode.eNode != null){
							getAllWord(currentNode.eNode, list, str + currentNode.eNode.charValue);// 获取该currentNode下对应所有可能的词
						}
					}
					return list;
				}else{
					cmpChar = str.charAt(charIndex);
				}
				currentNode = currentNode.eNode;
			}else if(charComp < 0){// 小于
				currentNode = currentNode.lNode;
			}else{// 大于
				currentNode = currentNode.rNode;
			}
		}
	}
	
	private void getAllWord(TernaryNode node, List<Map.Entry<String, V>> list, String str){
		if(node.isEnd){ list.add(new AbstractMap.SimpleEntry<String, V>(str, node.attriValue));}

		if(node.eNode != null){
			getAllWord(node.eNode,list,(str == null ? "" : str) + node.eNode.charValue);
		}
		
		if(node.lNode != null){
			if(str != null && str.length() > 0){
				getAllWord(node.lNode, list, str.substring(0,str.length()-1) + node.lNode.charValue);
			}else{
				getAllWord(node.lNode, list, "" + node.lNode.charValue);
			}
			
		}
		if(node.rNode != null){
			if(str != null && str.length() > 0){
				getAllWord(node.rNode, list, str.substring(0,str.length()-1) + node.rNode.charValue);
			}else{
				getAllWord(node.rNode, list, "" + node.rNode.charValue);
			}
		}
	}
	
	/**
	 * 词典大小
	 * @return
	 */
	public int size(){
		return this.size;
	}
	
	/**
	 * 测试三叉树是否运行正常。
	 * @param args
	 */
	public static void main(String[] args) {
//		String word1 = "你好";		String word2 = "美丽";
//		String word3 = "世界";
//		String word4 = "你好美";
//		
//		// 三叉trie测试
//		TernaryTrie<Nature> trie = new TernaryTrie<Nature>();
//		trie.insert(word1, nature);
//		trie.insert(word2, value2);
//		trie.insert(word3, value3);
//		trie.insert(word4, value4);
//		
//		System.out.println(trie.search(word4).nature);
//		List<Map.Entry<String, Nature>> list = trie.preSearch(word1);
//		for(Map.Entry<String, Nature> entry : list){
//			System.out.println(entry.getKey() + ":" + entry.getValue().nature);
//		}
//		
//		value4.nature = "shit";// 改变了词典中的词
//		System.out.println(trie.search(word4).nature);
		
//		hancks的双数组trie测试
//		DoubleArrayTrie<Nature> trie = new DoubleArrayTrie<Nature>();
//		TreeMap<String, Nature> keyValueMap = new TreeMap<String, Nature>();
//		keyValueMap.put(word1, value1);
//		trie.build(keyValueMap);
//		System.out.println(trie.get(word1).nature);
//		
//		value1.nature = "shit";
//		System.out.println(trie.get(word1).nature);
		
	}
}
