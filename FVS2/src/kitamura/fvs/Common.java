package kitamura.fvs;

import java.util.logging.Logger;

/**
 * FVS共通変数用クラス
 * @author Kitamura
 *
 */
public final class Common {
	static boolean FVSTT = false;
	
	static final int POSMAX = 20; // ポジションの最大数
	static final int ITEMMAX = 100; // メニューアイテムの最大数
	
	static final String LOGFILE = "FVS.log";
	static Logger logger = null;
}
