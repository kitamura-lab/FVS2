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

	private JFrame frame;
	private JButton[][] jl = new JButton[10][100];
	private JPanel panel;

	final String version = "2.0";
	final String LOGFILE = "FVS.log";
	private Logger logger = null;

	String[][] pos;

	/**
	 * Launch the application.
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
	 * Create the application.
	 */
	public FVS() {
		
		logger = Logger.getLogger(this.getClass().getName());
        try {
            // 出力ファイルを指定する
            FileHandler fh = new FileHandler(LOGFILE);
            // 出力フォーマットを指定する
            fh.setFormatter(new java.util.logging.SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.setLevel(Level.CONFIG);
        //logger.log(Level.INFO, "単なるおまけ");

		LineBorder border = new LineBorder(Color.BLACK, 2, true);

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 100; j++) {
				jl[i][j] = new JButton();
				jl[i][j].setBorder(border);
				jl[i][j].setText("");
				// jl[i][j].addActionListener(new myListener(i,j));
			}
		}

		initialize();
	}

	public class myListener implements ActionListener {
		int i0, j0;

		myListener(int i, int j) {
			i0 = i;
			j0 = j;
		}

		public void actionPerformed(ActionEvent e) {
			// jl[i0][j0].setText("OK");
			if (jl[i0][j0].getBackground() == Color.WHITE) {
				jl[i0][j0].setBackground(Color.BLACK);
				jl[i0][j0].setForeground(Color.WHITE);
			} else {
				jl[i0][j0].setBackground(Color.WHITE);
				jl[i0][j0].setForeground(Color.BLACK);
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("FVS" + version);

		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		panel.setLayout(new GridLayout(1, 1));

		// JLabel label = new JLabel();
		panel.add(jl[0][0]);
		// jl[0][0].setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
		jl[0][0].setText("ここに練習メニュー，ビデオフォルダの順にドロップしてね！");

		// ドロップ操作を有効にする
		panel.setTransferHandler(new DropFileHandler());
	}

	/**
	 * ドロップ操作の処理を行うクラス
	 */
	private class DropFileHandler extends TransferHandler {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * ドロップされたものを受け取るか判断 (ファイルのときだけ受け取る)
		 */
		@Override
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
		@Override
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
				//StringBuffer fileList = new StringBuffer();
				for (File file : files) {
					if (file.isDirectory())
						new SortVideo(file, jl, logger);
					else {
						Schedule sc = new Schedule(file);
						pos = sc.getPos();
						int jmax = 0;
						int imax = 0;
						for (imax = 0; imax < 10; imax++) {
							// System.out.println(""+i+pos[i][0]);
							if (pos[imax][0].equals(""))
								break;
							int j1 = 0;
							for (int j = 0; j < 100; j++) {
								if (pos[imax][j].equals(""))
									continue;
								if (pos[imax][j].equals("POST"))
									break;
								if (pos[imax][j].equals("END"))
									break;
								if (Pattern.compile("^Break").matcher(pos[imax][j]).find())
									continue;
								if (Pattern.compile("^Fundamental").matcher(pos[imax][j]).find())
									continue;
								String jlt = pos[imax][j].replaceAll("/", "／").replaceAll("&", "＆");
								jl[imax][j1].setText(jlt);
								if (j1 != 0) {
									jl[imax][j1].addActionListener(new myListener(imax, j1));
									jl[imax][j1].setBackground(Color.WHITE);
									jl[imax][j1].setForeground(Color.BLACK);
								}
								j1++;
							}
							if (jmax < j1)
								jmax = j1;
						}
						panel.setLayout(new GridLayout(jmax, imax));

						for (int k = 0; k < jmax; k++) {
							for (int l = 0; l < imax; l++)
								panel.add(jl[l][k]);
						}

						// System.out.println("PASS"+i+jmax1);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
	}
}
