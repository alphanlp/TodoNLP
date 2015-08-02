package inteldt.todonlp.dict;

import inteldt.todonlp.manager.Config;
import static inteldt.todonlp.manager.Predefine.logger;
import inteldt.todonlp.model.Nature;
import inteldt.todonlp.seg.model.TransferMatrix;

/**
 * 核心词典词性转移矩阵
 * @author User
 *
 */
public class CoreTransferMatrixDictionary {
	
	public static TransferMatrix<Nature> transformMatrix;
    static
    {
    	transformMatrix = new TransferMatrix<Nature>(Nature.class);
        long start = System.currentTimeMillis();
        if(transformMatrix.load(Config.CoreTransferMatrixDictionaryPath)){
        	logger.info("加载核心词典词性转移矩阵" + Config.CoreTransferMatrixDictionaryPath + 
            		"成功，耗时：" + (System.currentTimeMillis() - start) + " ms");
        }
    }

}
