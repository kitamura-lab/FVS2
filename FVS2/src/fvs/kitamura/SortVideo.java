package fvs.kitamura;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;

/**
 * @author Kitamura
 *　動画の分類
 */
public class SortVideo {

	final static String destFolder = "SortedFVideo";
	final static int catBoundary = 2000;
	final static int whiteBoundary = 30;
	static final int COLMAX = 20;
	static final int ROWMAX = 100;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String srcPath = "C:\\Users\\Kitamura\\Documents";
		final String srcFolder = "FVideo";
		new SortVideo(new File(srcPath, srcFolder), null, null);
	}

	SortVideo(File src, JButton[][] item, Logger logger) {

		//ポジションの列を検索する
		int pos = 0;
		for (pos = 0; pos < COLMAX; pos++) {
			if (item[pos][0].getText().equals(src.getName()))
				break;
		}

		//Sortedフォルダを生成する
		File dest = new File(src.getParent() + "\\Sorted" + src.getName());
		if (!dest.exists())
			dest.mkdir();

		String[] sfiles = src.list();

		int catCounter = 0;
		int catOfPreviousFile = 0;

		for (String sfile : sfiles) {
			File srcFile = new File(src, sfile);
	
			//MP4ファイル以外は飛ばす
			if (!srcFile.getPath().endsWith("MP4"))
				continue;

			//動画の白さを獲得
			int white = new FrameAnalyzer(srcFile.getAbsolutePath(), logger).getWhite();
			logger.log(Level.CONFIG, srcFile.getAbsolutePath() + ":" + white);

			int cat = 0;

			//黒幕の処理
			if (white < whiteBoundary)
				cat = 0;
			//練習動画の処理
			else {
				if (catOfPreviousFile == 0) {
					cat = ++catCounter;
					//練習メニューボタンがOFFの時は飛ばす
					while (item[pos][cat].getForeground() == Color.WHITE) {
						cat = ++catCounter;
					}
					//練習カテゴリのフォルダを作る
					File catFolder = new File(dest, "" + cat + "." + item[pos][cat].getText());
					if (!catFolder.exists())
						catFolder.mkdir();
				} else
					cat = catCounter;
				try {
					//練習動画のコピー
					FileInputStream fis = new FileInputStream(srcFile);
					FileChannel srcChannel = fis.getChannel();
					File destFile = new File(dest, "\\" + cat + "." + item[pos][cat].getText() + "\\" + sfile);
					FileOutputStream fos = new FileOutputStream(destFile);
					FileChannel destChannel = fos.getChannel();
					srcChannel.transferTo(0, srcChannel.size(), destChannel);
					srcChannel.close();
					destChannel.close();
					fis.close();
					fos.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.log(Level.SEVERE, "ERROR:", ex);
				}
			}
			catOfPreviousFile = cat;
		}
	}

}
