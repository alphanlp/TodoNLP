package inteldt.todonlp.opennlp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 * OpenNLP的使用用例
 * 
 * @author User
 *
 */
public class Usecase {

	public static void main(String[] args) throws FileNotFoundException {
		InputStream modelIn = new FileInputStream("D:/pei/data/opennlp/da-sent.zip");

		try {
		  SentenceModel model = new SentenceModel(modelIn);
		  SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		  String[] sentences = sentenceDetector.sentDetect("你好世界。我的世界你不懂。中文分句不行。");
		  for(String sentence : sentences){
			  System.out.println(sentence);
		  }
		  
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelIn != null) {
		    try {
		      modelIn.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
		
	}
	
}
