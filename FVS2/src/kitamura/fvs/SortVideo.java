package kitamura.fvs;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;

/**
 * ビデオファイルの整理
 * 
 * @author Kitamura
 */
public class SortVideo {

	final static String destFolder = "SortedFVideo";
	final static int catBoundary = 2000;
	final static int whiteBoundary = 30;
	static final int COLMAX = 20;
	static final int ROWMAX = 100;

	/**
	 * @param args
	 *            未使用
	 */
	public static void main(String[] args) {
		final String srcPath = "C:\\Users\\Kitamura\\Documents";
		final String srcFolder = "FVideo";
		new SortVideo(new File(srcPath, srcFolder), null, null);
	}

	SortVideo(File src, JButton[][] item, Logger logger) {

		// ポジションを検索
		int pos = 0;
		for (pos = 0; pos < COLMAX - 1; pos++) {
			if (item[pos][0].getText().equals(src.getName()))
				break;
		}

		// Sortedファイルの作成
		File dest = new File(src.getParent() + "\\Sorted");
		if (!dest.exists())
			dest.mkdir();
		if (!Common.FVSTT) {
			dest = new File(src.getParent() + "\\Sorted\\" + src.getName());
			if (!dest.exists())
				dest.mkdir();
		}

		String[] sfiles = src.list();

		int catCounter = 0;
		int catOfPreviousFile = 0;

		for (String sfile : sfiles) {
			File srcFile = new File(src, sfile);

			// 拡張子がMP4でなければ飛ばす
			if (!srcFile.getPath().endsWith("MP4"))
				continue;

			// フレームの明るさを取得
			int white = new FrameAnalyzer(srcFile.getAbsolutePath(), logger).getWhite();
			logger.log(Level.CONFIG, srcFile.getAbsolutePath() + ":" + white);

			int cat = 0;

			// フレームが暗ければカテゴリを変える
			if (white < whiteBoundary)
				cat = 0;
			// ビデオの整理
			else {
				if (catOfPreviousFile == 0) {
					cat = ++catCounter;
					// メニュボタンがOFFなら飛ばす
					while (item[pos][cat].getForeground() == Color.WHITE) {
						cat = ++catCounter;
					}
					// カテゴリフォルダを作る
					if (Common.FVSTT) {
						String folderPath = item[pos][cat].getText();
						int index = 0;
						while (folderPath.indexOf("\\", index) != -1) {
							logger.log(Level.CONFIG,
									"TEST:" + folderPath.substring(0, folderPath.indexOf("\\", index)));
							File folder = new File(dest,
									"\\" + folderPath.substring(0, folderPath.indexOf("\\", index)) + "\\");
							if (!folder.exists())
								folder.mkdir();
							index = folderPath.indexOf("\\", folderPath.indexOf("\\", index) + 1);
							if (index < 0)
								break;
						}
					}

					File catFolder;
					if (!Common.FVSTT) {
						catFolder = new File(dest, "" + cat + "." + item[pos][cat].getText());
					} else {
						catFolder = new File(dest, "\\" + item[pos][cat].getText() + "\\");
					}

					if (!catFolder.exists())
						catFolder.mkdir();
				} else
					cat = catCounter;
				try {
					// 動画ファイルのコピー
					// status.setText("AAA");
					FileInputStream fis = new FileInputStream(srcFile);
					FileChannel srcChannel = fis.getChannel();
					File destFile;
					if (!Common.FVSTT) {
						destFile = new File(dest, "\\" + cat + "." + item[pos][cat].getText() + "\\" + sfile);
					} else {
						destFile = new File(dest, "\\" + item[pos][cat].getText() + "\\" + sfile);
					}
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
