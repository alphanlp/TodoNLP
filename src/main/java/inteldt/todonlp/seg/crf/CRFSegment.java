package inteldt.todonlp.seg.crf;

import inteldt.todonlp.dict.CoreDictionary;
import inteldt.todonlp.dict.CoreTransferMatrixDictionary;
import inteldt.todonlp.manager.Config;
import inteldt.todonlp.model.Nature;
import inteldt.todonlp.seg.Segment;
import inteldt.todonlp.seg.model.Term;
import inteldt.todonlp.seg.model.TrieAttribute;
import inteldt.todonlp.seg.model.Vertex;
import inteldt.todonlp.seg.model.crf.CRFSegmentModel;
import inteldt.todonlp.seg.model.crf.Table;
import inteldt.todonlp.util.CharTable;
import inteldt.todonlp.util.CharacterHelper;
import inteldt.todonlp.util.Viterbi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 基于CRF分词。CRF训练模型由CRF++0.53训练获得。目前，训练预料不是很好，结果有点差强人意。
 * 
 * @author pei
 * 
 */
public class CRFSegment extends Segment {

	// test
	public static void main(String[] args) {
		CRFSegment seg = new CRFSegment();
		 List<Term> terms = seg.segSentence("知识产权出版社将举办中国专利信息年会，张丽霞将在分会场发言");
//		List<Term> terms = seg.segSentence("财政部副部长王保安调任国家统计局党组书记");
		System.out.println(terms);
	}

	@Override
	protected List<Term> segSentence(String sentence) {
		return segSentence(sentence.toCharArray());
	}

	private List<Term> segSentence(char[] sentence) {
		if (sentence.length == 0)  return Collections.emptyList();

		char[] sentenceConverted = CharTable.convert(sentence); // 经过正规化后的句子
		Table table = new Table(); // table为crf模型中的测试数据格式
		table.value = atomSegmentToTable(sentenceConverted);

		CRFSegmentModel.getModel().tag(table);
		List<Term> termList = new LinkedList<Term>();
		if (Config.DEBUG) {
			System.out.println("CRF标注结果");
			System.out.println(table);
		}
		int offset = 0;

		// 根据标注结果，切分成词串
		for (int i = 0; i < table.value.length; offset += table.value[i][1].length(), ++i) {
			String[] line = table.value[i];
			switch (line[2].charAt(0)) {
				case 'B': 
				{
					int begin = offset;
					while (table.value[i][2].charAt(0) != 'E') {
						offset += table.value[i][1].length();
						++i;
						if (i == table.value.length) {
							break;
						}
					}
					if (i == table.value.length) {
						termList.add(new Term(new String(sentence, begin, offset - begin), null));
					} else
						termList.add(new Term(new String(sentence, begin, offset - begin + table.value[i][1].length()), null));
				}
				break;
				
				default: 
				{
					termList.add(new Term(new String(sentence, offset, table.value[i][1].length()), null));
				}
				break;
			}
		}
		
		/*
		 * 隐马词性标注
		 */
		if (segConfig.speechTag) {
			 ArrayList<Vertex> vertexList = new ArrayList<Vertex>(termList.size() + 1);
			 
			 vertexList.add(Vertex.newBegin());
			 for (Term term : termList) {
				 TrieAttribute attribute = CoreDictionary.trie.getAttribute(term.word);
	
				 if (attribute == null)
					 attribute = new TrieAttribute(Nature.nz,1000);
				 
				 Vertex vertex = new Vertex(term.word, term.word, attribute, -1);
				 vertexList.add(vertex);
			 }

			 Viterbi.compute(vertexList,CoreTransferMatrixDictionary.transformMatrix);
			
			 int i = 0;
			 for (Term term : termList) {
				 term.nature = vertexList.get(i + 1).getAttribute().natures[0];
				 ++i;
			 }
		}
		return termList;
	}

	/**
	 * 原子切分，填充table，格式为CRF++中的测试数据格式。同时对数字和英文做了特殊处理
	 * 
	 * @param sentence
	 * @return
	 */
	public static String[][] atomSegmentToTable(char[] sentence) {
		String table[][] = new String[sentence.length][3];
		int size = 0;
		final int maxLen = sentence.length - 1;
		final StringBuilder sbAtom = new StringBuilder();
		out: 
		for (int i = 0; i < sentence.length; i++) {
//			if (sentence[i] >= '0' && sentence[i] <= '9') {
//				sbAtom.append(sentence[i]);
//				if (i == maxLen) {
//					table[size][0] = "M";
//					table[size][1] = sbAtom.toString();
//					++size;
//					sbAtom.setLength(0);
//					break;
//				}
//				char c = sentence[++i];
//				while (c == '.' || c == '%' || (c >= '0' && c <= '9')) {
//					sbAtom.append(sentence[i]);
//					if (i == maxLen) {
//						table[size][0] = "M";
//						table[size][1] = sbAtom.toString();
//						++size;
//						sbAtom.setLength(0);
//						break out;
//					}
//					c = sentence[++i];
//				}
//				table[size][0] = "M";
//				table[size][1] = sbAtom.toString();
//				++size;
//				sbAtom.setLength(0);
//				--i;
//			}
			if (CharacterHelper.isEnglishLetter(sentence[i])) {
				sbAtom.append(sentence[i]);
				if (i == maxLen) {
					table[size][0] = "W";
					table[size][1] = sbAtom.toString();
					++size;
					sbAtom.setLength(0);
					break;
				}
				char c = sentence[++i];
				while (CharacterHelper.isEnglishLetter(c)) {
					sbAtom.append(sentence[i]);
					if (i == maxLen) {
						table[size][0] = "W";
						table[size][1] = sbAtom.toString();
						++size;
						sbAtom.setLength(0);
						break out;
					}
					c = sentence[++i];
				}
				table[size][0] = "W";
				table[size][1] = sbAtom.toString();
				++size;
				sbAtom.setLength(0);
				--i;
			} else {
				table[size][0] = table[size][1] = String.valueOf(sentence[i]);
				++size;
			}
		}

		return resizeArray(table, size);
	}

	/**
	 * 数组减肥，原子分词可能会导致表格比原来的短
	 * 
	 * @param array
	 * @param size
	 * @return
	 */
	private static String[][] resizeArray(String[][] array, int size) {
		String[][] nArray = new String[size][];
		System.arraycopy(array, 0, nArray, 0, size);
		return nArray;
	}

}
