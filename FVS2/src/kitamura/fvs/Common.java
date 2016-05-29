package kitamura.fvs;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FVS共通変数用クラス
 * @author Kitamura
 *
 */
public final class Common {
	static boolean FVSTT = true;
	
	static final int POSMAX = 20; // ポジションの最大数
	static final int ITEMMAX = 100; // メニューアイテムの最大数
	
	static final String LOGFILE = "FVS.log";
	static Logger logger = null;
	
	static void initLog(String name){
		logger = Logger.getLogger(name);
		try {
			FileHandler fh = new FileHandler(Common.LOGFILE);
			fh.setFormatter(new java.util.logging.SimpleFormatter());
			logger.addHandler(fh);
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.setLevel(Level.CONFIG);
	}
}
