package kitamura.fvs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

/**
 * FVS: Fighters Video Sorter<br>
 * FVSTT: Fighters Video Sorter for Team Time<br>
 * 練習ビデオを練習メニューに応じて分類するプログラム<br>
 * 練習ビデオは黒幕が切れ目になっている．<br>
 * 練習メニューはExcelファイルとして与えられる．<br>
 * 
 * @author Yasuhiko Kitamura
 *
 */
public class FVS {

	private String sysName = "FVS";
	private final String version = "2.7";

	private JFrame frame;
	private JButton[][] item = new JButton[Common.POSMAX][Common.ITEMMAX]; // メニュー用ボタン
	private JPanel panel;
	private JLabel status = new JLabel(""); // 途中経過表示用ラベル

	private Menu[][] menu;

	/**
	 * @param args
	 *            未使用
	 * 
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FVS window = new FVS();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * コンストラクタ
	 */
	public FVS() {

		/* ログの初期化 */
		Common.initLog(this.getClass().getName());

		//LineBorder border = new LineBorder(Color.BLACK, 2, true);

		/* 練習メニューの初期化 */
		for (int i = 0; i < Common.POSMAX; i++) {
			for (int j = 0; j < Common.ITEMMAX; j++) {
				item[i][j] = new JButton();
				item[i][j].setBorder(new LineBorder(Color.BLACK, 2, true));
				item[i][j].setForeground(Color.BLACK);
				item[i][j].setBackground(Color.WHITE);
				item[i][j].setText("");
			}
			if (!Common.FVSTT)
				item[i][0].setBackground(new Color(153, 255, 255));
			else
				item[i][0].setBackground(new Color(255, 255, 153));
		}

		/* システム名の設定 */
		if (!Common.FVSTT)
			sysName = "FVS";
		else
			sysName = "FVSTT";

		frame = new JFrame();
		frame.setBounds(100, 100, 500, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(sysName + version);

		// 練習メニューボタン用パネル
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(1, 1));

		// 初期メッセージ
		panel.add(item[0][0]);
		if (!Common.FVSTT) {
			item[0][0].setText("ここに練習メニュー，ビデオフォルダの順にドロップしてね！");
		} else {
			item[0][0].setText("ここにTeamTimeメニュー，ビデオフォルダの順にドロップしてね！");
		}

		// ドラッグアンドドロップ
		panel.setTransferHandler(new DropFileHandler());
	}

	/**
	 * 練習メニューボタン用リスナー
	 * @author Kitamura 
	 */
	private class myListener implements ActionListener {
		int x, y;

		// 練習メニューボタンの位置設定
		myListener(int x0, int y0) {
			x = x0;
			y = y0;
		}

		// 練習メニューボタンのON/OFF
		public void actionPerformed(ActionEvent e) {

			if (item[x][y].getBackground() == Color.WHITE && item[x][y].getForeground() == Color.BLACK) {
				item[x][y].setBackground(Color.BLACK);
				item[x][y].setForeground(Color.WHITE);
			} else if (item[x][y].getBackground() == Color.BLACK && item[x][y].getForeground() == Color.WHITE) {
				item[x][y].setBackground(Color.WHITE);
				item[x][y].setForeground(Color.BLACK);
			} else if (item[x][y].getBackground() == Color.YELLOW && item[x][y].getForeground() == Color.BLACK) {
				item[x][y].setBackground(Color.BLACK);
				item[x][y].setForeground(Color.YELLOW);
			} else if (item[x][y].getBackground() == Color.BLACK && item[x][y].getForeground() == Color.YELLOW) {
				item[x][y].setBackground(Color.YELLOW);
				item[x][y].setForeground(Color.BLACK);
			}
		}
	}

	void setStatus(String mess) {
		status.setText(mess);
		frame.setVisible(true);
	}

	// ドラッグアンドドロップハンドラ
	private class DropFileHandler extends TransferHandler {

		private static final long serialVersionUID = 1L;

		/**
		 * ドロップされたものを受け取るか判断 (ファイルのときだけ受け取る)
		 */
		public boolean canImport(TransferSupport support) {
			if (!support.isDrop()) {
				// ドロップ操作でない場合は受け取らない
				return false;
			}

			if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// ドロップされたのがファイルでない場合は受け取らない
				return false;
			}

			return true;
		}

		/**
		 * ドロップされたファイルを受け取る
		 */
		public boolean importData(TransferSupport support) {
			// 受け取っていいものか確認する
			if (!canImport(support)) {
				return false;
			}

			// ドロップ処理
			Transferable t = support.getTransferable();
			try {
				// ファイルを受け取る
				List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

				// ドロップされたファイル処理
				for (File file : files) {
					// ディレクトリならビデオソート
					if (file.isDirectory()) {
						frame.setTitle(sysName + version + ": " + "Sorting...");
						// setStatus("Sorting");
						new SortVideo(file, item);
						frame.setTitle(sysName + version + ": " + "Completed");
						// setStatus("Completed");
					}
					// 練習メニューからアイテムを表示
					else {
						Schedule sc = new Schedule(file); // メニューを得る
						menu = sc.getMenu();

						int xMax = 0;
						int yMax = 0;
						for (xMax = 0; xMax < Common.POSMAX; xMax++) {
							if (menu[xMax][0].name.equals("")) // ポジションが空白なら終了
								break;
							int yItem = 0;
							for (int yMenu = 0; yMenu < Common.ITEMMAX; yMenu++) {
								if (menu[xMax][yMenu].name.equals("")) // メニューが空白なら飛ばす
									continue;
								if (menu[xMax][yMenu].name.equals("POST")) // メニューがPOSTなら終了
									break;
								if (menu[xMax][yMenu].name.equals("END")) // メニューがENDなら終了
									break;
								if (Pattern.compile("^Break").matcher(menu[xMax][yMenu].name).find()) // メニューがBreakなら飛ばす
									continue;
								if (Pattern.compile("^Fundamental").matcher(menu[xMax][yMenu].name).find())// メニューがFundamentalなら飛ばす
									continue;

								// 不適切な文字コードを置換
								item[xMax][yItem].setText(menu[xMax][yMenu].name.replaceAll("/", "／").replaceAll("&", "＆")
										.replaceAll("\n", " "));

								// メニューをクリック可能に
								if (yItem != 0) {
									item[xMax][yItem].addActionListener(new myListener(xMax, yItem));
									item[xMax][yItem].setForeground(Color.BLACK);
									if (menu[xMax][yMenu].with) {
										item[xMax][yItem].setBackground(Color.YELLOW);
										//System.out.println("Button:" + item[xMax][yItem].getText());
									} else
										item[xMax][yItem].setBackground(Color.WHITE);
								}
								yItem++;
							}
							// メニューアイテム最大数を設定
							if (yMax < yItem)
								yMax = yItem;
						}

						// メニューボタンの配置
						panel.setLayout(new GridLayout(yMax + 1, xMax));
						for (int i = 0; i < yMax; i++) {
							for (int j = 0; j < xMax; j++)
								panel.add(item[j][i]);
						}
						// 途中経過表示ラベルの付加
						panel.add(status);					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				Common.logger.log(Level.SEVERE, "ERROR:", e);
			}
			return true;
		}
	}

}
