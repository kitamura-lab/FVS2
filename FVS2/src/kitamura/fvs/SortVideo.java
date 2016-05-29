package kitamura.fvs;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
//import java.util.logging.Logger;

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

	/**
	 * @param args
	 *            未使用
	 */
	/*
	public static void main(String[] args) {
		final String srcPath = "C:\\Users\\Kitamura\\Documents";
		final String srcFolder = "FVideo";
		new SortVideo(new File(srcPath, srcFolder), null);
	}
	*/

	SortVideo(File src, JButton[][] item) {
		
		// ポジションを検索
		int pos = 0;
		for (pos = 0; pos < Common.POSMAX - 1; pos++) {
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

		int catCounter = 0; // カテゴリのカウンタ
		int catOfPreviousFile = 0; // ひとつ前のカテゴリ
		int videoNo = 1; // ビデオ番号

		for (String sfile : sfiles) {
			File srcFile = new File(src, sfile);

			// 拡張子がMP4でなければ飛ばす
			if (!srcFile.getPath().endsWith("MP4"))
				continue;

			// フレームの明るさを取得
			int white = new FrameAnalyzer(srcFile.getAbsolutePath()).getWhite();
			Common.logger.log(Level.CONFIG, srcFile.getAbsolutePath() + ":" + white);

			int cat = 0; // カテゴリ番号

			// フレームが暗ければカテゴリを変える
			if (white < whiteBoundary) {
				cat = 0; // カテゴリ番号をリセット
				videoNo = 1; // ビデオ番号をリセット
			}
			// ビデオの整理
			else {
				if (catOfPreviousFile == 0) {
					cat = ++catCounter;
					// メニュボタンがOFFなら飛ばす
					while (item[pos][cat].getBackground() == Color.BLACK) {
						cat = ++catCounter;
					}
					// カテゴリフォルダを作る
					if (Common.FVSTT) {
						String folderPath = item[pos][cat].getText();
						int index = 0;
						while (folderPath.indexOf("\\", index) != -1) {
							Common.logger.log(Level.CONFIG,
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
						if (item[pos][cat].getBackground() == Color.WHITE)
							catFolder = new File(dest, "" + cat + "." + item[pos][cat].getText());
						else
							catFolder = new File(dest, "" + item[pos][cat].getText());
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
						if (item[pos][cat].getBackground() == Color.WHITE)
							destFile = new File(dest, "\\" + cat + "." + item[pos][cat].getText() + "\\" + sfile);
						else
							destFile = new File(dest, item[pos][cat].getText() + "\\" + sfile);
					} else {
						String extension = "MP4";
						int lastDotPosition = sfile.lastIndexOf(".");
						if (lastDotPosition != -1) {
							extension = sfile.substring(lastDotPosition + 1);
						}
						String filePath = item[pos][cat].getText();
						String postfix = "";
						String prefix = "";
						if (filePath.indexOf("1-W") != -1)
							postfix = "a";
						if (filePath.indexOf("2-Lu") != -1)
							postfix = "b";
						if (filePath.indexOf("3-V") != -1)
							postfix = "c";
						if (filePath.indexOf("4-WV") != -1)
							postfix = "d";
						if (filePath.indexOf("Run Drill OFF") != -1)
							prefix = "RDO";
						if (filePath.indexOf("Run Drill DEF") != -1)
							prefix = "RDD";
						if (filePath.indexOf("Skelly") != -1)
							prefix = "SKL";
						if (filePath.indexOf("Pass DEF Drill") != -1)
							prefix = "PDD";
						if (filePath.indexOf("Team Time\\OFF") != -1)
							prefix = "TTO";
						if (filePath.indexOf("Team Time\\DEF") != -1)
							prefix = "TTD";
						if (filePath.indexOf("KC") != -1)
							prefix = "1KC";
						if (filePath.indexOf("KR") != -1)
							prefix = "2KR";
						if (filePath.indexOf("PC") != -1)
							prefix = "3PC";
						if (filePath.indexOf("PR") != -1)
							prefix = "4PR";
						if (filePath.indexOf("PAT") != -1)
							prefix = "5PT";

						sfile = prefix + String.format("%03d", videoNo) + postfix + "." + extension;
						videoNo++;
						destFile = new File(dest, "\\" + item[pos][cat].getText() + "\\" + sfile);
						System.out.println("File:" + sfile);
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
					Common.logger.log(Level.SEVERE, "ERROR:", ex);
				}
			}
			catOfPreviousFile = cat;
		}
	}

}
