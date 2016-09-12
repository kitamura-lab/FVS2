package kitamura.fvs;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;
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
	
	String monthday() {
		String sMonth;
		String sDay;

		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DATE);

		if (month < 10)
			sMonth = "0" + month;
		else
			sMonth = "" + month;
		if (day < 10)
			sDay = "0" + day;
		else
			sDay = "" + day;

		return sMonth + sDay;
	}

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

		int category = 1; // カテゴリのカウンタ
		int videoNo = 1; // ビデオ番号
		int itemIndex = 1; // アイテム番号

		for (String sfile : sfiles) {
			File srcFile = new File(src, sfile);

			// 拡張子がMP4でなければ飛ばす
			if (!srcFile.getPath().endsWith("MP4") && !srcFile.getPath().endsWith("mp4"))
					//&& !srcFile.getPath().endsWith("MOV") && !srcFile.getPath().endsWith("mov")
					//&& !srcFile.getPath().endsWith("JPG") && !srcFile.getPath().endsWith("jpg")
					//&& !srcFile.getPath().endsWith("MOD") && !srcFile.getPath().endsWith("mod"))
				continue;

			// フレームの明るさを取得
			int white = new FrameAnalyzer(srcFile.getAbsolutePath()).getWhite();
			Common.logger.log(Level.CONFIG, srcFile.getAbsolutePath() + ":" + white);

			// フレームが暗ければカテゴリを変える
			if (white < whiteBoundary) {
				if (videoNo != 1) {
					category++;
					itemIndex++;
				}
				videoNo = 1; // ビデオ番号をリセット

			}
			// ビデオの整理
			else {
				// メニューボタンがOFFなら飛ばす
				while (item[pos][itemIndex].getBackground() == Color.BLACK) {
					itemIndex++;
				}
				// カテゴリフォルダを作る
				if (videoNo == 1) {

					// 上位フォルダを作る
					if (Common.FVSTT) {
						String folderPath = item[pos][itemIndex].getText();
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

					// カテゴリフォルダを作る
					File catFolder;
					if (!Common.FVSTT) {// 通常
						if (item[pos][itemIndex].getBackground() == Color.WHITE) {// 通常アイテム
							catFolder = new File(dest, "" + category + "." + item[pos][itemIndex].getText());
						} else {// withアイテム
							catFolder = new File(dest, "" + item[pos][itemIndex].getText());
							category--;
						}
					} else {// TeamTime
						catFolder = new File(dest, "\\" + item[pos][itemIndex].getText() + "\\");
					}

					// System.out.println("Folder" + catFolder);
					if (!catFolder.exists())
						catFolder.mkdir();
				}
				try {
					// 動画ファイルのコピー
					FileInputStream fis = new FileInputStream(srcFile);
					FileChannel srcChannel = fis.getChannel();
					File destFile;
					if (!Common.FVSTT) {// FVS
						if (item[pos][itemIndex].getBackground() == Color.WHITE)// 通常
							destFile = new File(dest,
									"\\" + category + "." + item[pos][itemIndex].getText() + "\\" + sfile);
						else // with
							destFile = new File(dest, item[pos][itemIndex].getText() + "\\" + sfile);
					} else {// FVSTT
						String ext = "MP4"; // 拡張子
						int lastDotPosition = sfile.lastIndexOf(".");
						if (lastDotPosition != -1) {
							ext = sfile.substring(lastDotPosition + 1);
						}
						String filePath = item[pos][itemIndex].getText();
						String postfix = ""; // 接尾句
						String prefix = ""; // 接頭句
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

						prefix = monthday()+prefix;
						
						sfile = prefix + String.format("%03d", videoNo) + postfix + "." + ext;
						destFile = new File(dest, "\\" + item[pos][itemIndex].getText() + "\\" + sfile);
						// System.out.println("File:" + sfile);
					}
					videoNo++;
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
		}
	}

}
