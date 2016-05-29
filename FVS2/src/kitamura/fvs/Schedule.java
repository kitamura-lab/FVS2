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

/**
 * 練習メニューの読み込み
 * 
 * @author Kitamura
 * 
 */
public class Schedule {

	static final int ROW0 = 26; // エクセルファイルの行オフセット
	static final int COL0 = 2; // エクセルファイルの列オフセット

	Menu[][] menu = new Menu[Common.POSMAX][Common.ITEMMAX];

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

	Schedule(File filename) {
		FileInputStream in = null;
		Workbook wb = null;

		// メニューファイルのオープン
		try {
			in = new FileInputStream(filename);
			wb = WorkbookFactory.create(in);
		} catch (IOException e) {
			e.printStackTrace();
			Common.logger.log(Level.SEVERE, "ERROR:", e);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			Common.logger.log(Level.SEVERE, "ERROR:", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				Common.logger.log(Level.SEVERE, "ERROR:", e);
			}
		}

		// メニューアイテムの初期化
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 100; j++) {
				menu[i][j] = new Menu();
			}
		}

		// メニューの解析
		Sheet sheet = wb.getSheetAt(0);
		Row row1 = sheet.getRow(ROW0);

		int pos = 0;
		for (int i = 0; i < Common.POSMAX; i++) {
			Cell cell = row1.getCell(i + COL0);
			if (cell.toString().equals(""))
				break;
			else {
				menu[pos][0].name = cell.toString();
				pos++;
			}
		}

		for (int i = 1; i < Common.ITEMMAX; i++) {
			try {
				Row row = sheet.getRow(i + ROW0);
				for (int j = 0; j < pos; j++) {

					Cell cell = row.getCell(j + COL0);
					menu[j][i].name = cell.toString();
					CellStyle style = cell.getCellStyle();
					if (style.getFillForegroundColor() == 0) {
						menu[j][i].with = true;
						// System.out.println(cell.toString() + ":" +
						// style.getFillForegroundColor());
					}
					// メニューの左に境がなければ，コピー
					if (menu[j][i].name.equals("") && j > 0 && style.getBorderLeft() == 0) {
						menu[j][i] = menu[j - 1][i];
					}
				}
				// POSTまたはENDで終了
				if (menu[0][i].name.equals("POST"))
					break;
				if (menu[0][i].name.equals("END"))
					break;
			} catch (NullPointerException e) {
				// セルがなくなれば終了
				break;
			} catch (Exception e) {
				e.printStackTrace();
				Common.logger.log(Level.SEVERE, "ERROR:", e);
			}

		}
	}
}
