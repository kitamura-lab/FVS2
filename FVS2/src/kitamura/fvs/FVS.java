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
 * @author Yasuhiko Kitamura
 *
 */
public class FVS {

	// static final int FVSLIGHT = 1;
	static final int POSMAX = 20; // ポジションの最大数
	static final int ITEMMAX = 100; // メニューアイテムの最大数

	private JFrame frame;
	private JButton[][] item = new JButton[POSMAX][ITEMMAX];
	private JPanel panel;
	private JLabel status = new JLabel(""); // 途中経過表示用ラベル

	private String sysName = "FVS";
	final String version = "2.4";
	final String logfile = "FVS.log";
	private Logger logger = null;

	String[][] menu;

	/**
	 * @param args
	 *            未使用
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

		/* ログファイルの初期化 */
		logger = Logger.getLogger(this.getClass().getName());
		try {
			FileHandler fh = new FileHandler(logfile);
			fh.setFormatter(new java.util.logging.SimpleFormatter());
			logger.addHandler(fh);
		} catch (IOException e) {
			e.printStackTrace();
			// logger.log(Level.SEVERE, "ERROR:", e);
		}
		logger.setLevel(Level.CONFIG);

		LineBorder border = new LineBorder(Color.BLACK, 2, true);

		/* 練習メニューの初期化 */
		for (int i = 0; i < POSMAX; i++) {
			for (int j = 0; j < ITEMMAX; j++) {
				item[i][j] = new JButton();
				item[i][j].setBorder(border);
				item[i][j].setForeground(Color.BLACK);
				item[i][j].setText("");
			}
		}

		if (!Common.FVSTT)
			sysName = "FVS";
		else
			sysName = "FVSTT";

		frame = new JFrame();
		frame.setBounds(100, 100, 500, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(sysName + version);
		frame.getContentPane().setBackground(Color.red);

		// 練習メニューボタン用パネル
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.setBackground(Color.red);

		// 初期メッセージ
		panel.add(item[0][0]);
		if (!Common.FVSTT) {
			item[0][0].setText("ここに練習メニュー，ビデオフォルダの順にドロップしてね！");
			item[0][0].setBackground(new Color(153,255,255));
		} else {
			item[0][0].setText("ここにTeamTimeメニュー，ビデオフォルダの順にドロップしてね！");
			item[0][0].setBackground(new Color(255,255,153));
		}

		// ドラッグアンドドロップ
		panel.setTransferHandler(new DropFileHandler());
	}

	/**
	 * @author Kitamura 練習メニューボタン用リスナー
	 */
	public class myListener implements ActionListener {
		int x, y;

		// 練習メニューボタンの位置設定
		myListener(int x0, int y0) {
			x = x0;
			y = y0;
		}

		// 練習メニューボタンのON/OFF
		public void actionPerformed(ActionEvent e) {
			if (item[x][y].getBackground() == Color.WHITE) {
				item[x][y].setBackground(Color.BLACK);
				item[x][y].setForeground(Color.WHITE);
			} else {
				item[x][y].setBackground(Color.WHITE);
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
						new SortVideo(file, item, logger);
						frame.setTitle(sysName + version + ": " + "Completed");
						// setStatus("Completed");
					}
					// 練習メニューならアイテムを表示
					else {
						menu = new Schedule(file, logger).getMenu(); // メニューを得る
						int xmax = 0;
						int ymax = 0;
						for (xmax = 0; xmax < POSMAX; xmax++) {
							if (menu[xmax][0].equals("")) // ポジションが空白なら終了
								break;
							int y1 = 0;
							for (int y = 0; y < ITEMMAX; y++) {
								if (menu[xmax][y].equals("")) // メニューが空白なら飛ばす
									continue;
								if (menu[xmax][y].equals("POST")) // メニューがPOSTなら終了
									break;
								if (menu[xmax][y].equals("END")) // メニューがENDなら終了
									break;
								if (Pattern.compile("^Break").matcher(menu[xmax][y]).find()) // メニューがBreakなら飛ばす
									continue;
								if (Pattern.compile("^Fundamental").matcher(menu[xmax][y]).find())// メニューがFundamentalなら飛ばす
									continue;

								// 不適切な文字コードを置換
								item[xmax][y1].setText(
										menu[xmax][y].replaceAll("/", "／").replaceAll("&", "＆").replaceAll("\n", " "));

								// メニューをクリック可能に
								if (y1 != 0) {
									item[xmax][y1].addActionListener(new myListener(xmax, y1));
									item[xmax][y1].setBackground(Color.WHITE);
									item[xmax][y1].setForeground(Color.BLACK);
								}
								y1++;
							}
							// メニューアイテム最大数を設定
							if (ymax < y1)
								ymax = y1;
						}

						// メニューボタンの配置
						panel.setLayout(new GridLayout(ymax + 1, xmax));
						for (int i = 0; i < ymax; i++) {
							for (int j = 0; j < xmax; j++)
								panel.add(item[j][i]);
						}
						// 途中経過表示ラベルの付加
						panel.add(status);
						// status.setText("AAAAAAAAAAAAAAAAAAAAAAAAAAAA");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.log(Level.SEVERE, "ERROR:", e);
			}
			return true;
		}
	}

}
