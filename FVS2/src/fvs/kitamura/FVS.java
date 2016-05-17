package fvs.kitamura;

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
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

public class FVS {

	static final int COLMAX = 20;
	static final int ROWMAX = 100;
	
	private JFrame frame;
	private JButton[][] item = new JButton[COLMAX][ROWMAX];
	private JPanel panel;

	final String version = "2.2";
	final String LOGFILE = "FVS.log";
	private Logger logger = null;

	String[][] menu;

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

	public FVS() {

		logger = Logger.getLogger(this.getClass().getName());
		try {
			FileHandler fh = new FileHandler(LOGFILE);
			fh.setFormatter(new java.util.logging.SimpleFormatter());
			logger.addHandler(fh);
		} catch (IOException e) {
			e.printStackTrace();
			logger.log(Level.SEVERE, "ERROR:", e);
		}
		logger.setLevel(Level.CONFIG);

		LineBorder border = new LineBorder(Color.BLACK, 2, true);

		for (int i = 0; i < COLMAX; i++) {
			for (int j = 0; j < ROWMAX; j++) {
				item[i][j] = new JButton();
				item[i][j].setBorder(border);
				item[i][j].setText("");
			}
		}

		initialize();
	}

	public class myListener implements ActionListener {
		int x, y;

		myListener(int x0, int y0) {
			x = x0;
			y = y0;
		}

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

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("FVS" + version);

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		panel.setLayout(new GridLayout(1, 1));

		panel.add(item[0][0]);
		item[0][0].setText("ここに練習メニュー，ビデオフォルダの順にドロップしてね！");

		panel.setTransferHandler(new DropFileHandler());
	}

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

				// テキストエリアに表示するファイル名リストを作成する
				for (File file : files) {
					if (file.isDirectory())
						new SortVideo(file, item, logger);
					else {
						menu = new Schedule(file, logger).getMenu();
						int xmax = 0;
						int ymax = 0;
						for (xmax = 0; xmax < COLMAX; xmax++) {
							if (menu[xmax][0].equals(""))
								break;
							int y1 = 0;
							for (int y = 0; y < ROWMAX; y++) {
								if (menu[xmax][y].equals(""))
									continue;
								if (menu[xmax][y].equals("POST"))
									break;
								if (menu[xmax][y].equals("END"))
									break;
								if (Pattern.compile("^Break").matcher(menu[xmax][y]).find())
									continue;
								if (Pattern.compile("^Fundamental").matcher(menu[xmax][y]).find())
									continue;

								item[xmax][y1].setText(menu[xmax][y].replaceAll("/", "／").replaceAll("&", "＆"));
								if (y1 != 0) {
									item[xmax][y1].addActionListener(new myListener(xmax, y1));
									item[xmax][y1].setBackground(Color.WHITE);
									item[xmax][y1].setForeground(Color.BLACK);
								}
								y1++;
							}
							if (ymax < y1)
								ymax = y1;
						}
						panel.setLayout(new GridLayout(ymax, xmax));

						for (int i = 0; i < ymax; i++) {
							for (int j = 0; j < xmax; j++)
								panel.add(item[j][i]);
						}

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
