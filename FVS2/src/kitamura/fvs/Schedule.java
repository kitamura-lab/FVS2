package kitamura.fvs;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.*;
import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 * 練習メニューの読み込み
 * 
 * @author Kitamura
 * 
 */
public class Schedule {

	static final int ROW0 = 26;
	static final int COL0 = 2;
	//static final int COLMAX = 20;
	//static final int ROWMAX = 100;

	Menu[][] menu = new Menu[Common.POSMAX][Common.ITEMMAX];
	
	//String[][] menu = new String[Common.POSMAX][Common.ITEMMAX];
	//int[][] with = new int[Common.POSMAX][Common.ITEMMAX];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String file = "C:\\Users\\kitamura\\Documents\\FIGHTERS\\Menu.xlsx";
		new Schedule(new File(file));
	}

	// メニュの読み出し
	Menu[][] getMenu() {
		return menu;
	}

	/*
	int[][] getWith() {
		return with;
	}
	*/

	Schedule(File filename) {
		FileInputStream in = null;
		Workbook wb = null;

		// メニューファイルのオープン
		try {
			in = new FileInputStream(filename);
			wb = WorkbookFactory.create(in);
		} catch (IOException e) {
			System.out.println(e.toString());
			Common.logger.log(Level.SEVERE, "ERROR:", e);
		} catch (InvalidFormatException e) {
			System.out.println(e.toString());
			Common.logger.log(Level.SEVERE, "ERROR:", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				System.out.println(e.toString());
				Common.logger.log(Level.SEVERE, "ERROR:", e);
			}
		}

		// メニューアイテムの初期化
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 100; j++) {
				menu[i][j] = new Menu();
				//menu[i][j].name = "";
				//menu[i][j].with = false;
			}
		}

		// メニューの解析
		Sheet sheet = wb.getSheetAt(0);
		Row row1 = sheet.getRow(ROW0);

		int poscnt = 0;
		for (int i = 0; i < Common.POSMAX; i++) {
			Cell cell = row1.getCell(i + COL0);
			if (cell.toString().equals(""))
				break;
			else {
				menu[poscnt][0].name = cell.toString();
				poscnt++;
			}
		}

		for (int i = 1; i < Common.ITEMMAX; i++) {
			try {
				Row row = sheet.getRow(i + ROW0);
				for (int j = 0; j < poscnt; j++) {

					Cell cell = row.getCell(j + COL0);
					menu[j][i].name = cell.toString();
					CellStyle style = cell.getCellStyle();
					// System.out.println(cell.toString() + ":" +
					// style.getFillForegroundColor());
					if (style.getFillForegroundColor() == 0) {
						menu[j][i].with = true;
						System.out.println(cell.toString() + ":" + style.getFillForegroundColor());
					}
					// メニューの左に境がなければ，コピー
					if (menu[j][i].name.equals("") && j > 0 && style.getBorderLeft() == 0) {
						menu[j][i] = menu[j - 1][i];
						//with[j][i] = with[j - 1][i];
					}
					// if(style.getFillForegroundColor()==0) menu[j][i] =
					// "*"+menu[j][i];
				}
				// POSTまたはENDで修了
				if (menu[0][i].name.equals("POST"))
					break;
				if (menu[0][i].name.equals("END"))
					break;
			} catch (Exception e) {
				// セルがなくなれば修了
				// e.printStackTrace();
				// logger.log(Level.SEVERE, "ERROR:", e);
				break;
			}
		}
	}
}
