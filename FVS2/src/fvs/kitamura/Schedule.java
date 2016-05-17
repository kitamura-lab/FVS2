package fvs.kitamura;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kitamura
 * ���K���j���[�̓ǂݍ���
 */
public class Schedule {

	static final int ROW0 = 26;
	static final int COL0 = 2;
	static final int COLMAX = 20;
	static final int ROWMAX = 100;

	String[][] menu = new String[COLMAX][ROWMAX];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String file = "C:\\Users\\kitamura\\Documents\\FIGHTERS\\Menu.xlsx";
		new Schedule(new File(file), null);
	}

	// ���j���[�̊l��
	String[][] getMenu() {
		return menu;
	}

	Schedule(File filename, Logger logger) {
		FileInputStream in = null;
		Workbook wb = null;

		//���K���j���[�G�N�Z���t�@�C���̃I�[�v��
		try {
			in = new FileInputStream(filename);
			wb = WorkbookFactory.create(in);
		} catch (IOException e) {
			System.out.println(e.toString());
			logger.log(Level.SEVERE, "ERROR:", e);
		} catch (InvalidFormatException e) {
			System.out.println(e.toString());
			logger.log(Level.SEVERE, "ERROR:", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				System.out.println(e.toString());
				logger.log(Level.SEVERE, "ERROR:", e);
			}
		}

		//���j���[�̏�����
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 100; j++)
				menu[i][j] = "";
		}

		Sheet sheet = wb.getSheetAt(0);
		Row row1 = sheet.getRow(ROW0);

		//�|�W�V�������̊l��
		int poscnt = 0;
		for (int i = 0; i < COLMAX; i++) {
			Cell cell = row1.getCell(i + COL0);
			if (cell.toString().equals(""))
				break;
			else {
				menu[poscnt][0] = cell.toString();
				poscnt++;
			}
		}

		//���K���j���[�̊l��
		for (int i = 1; i < ROWMAX; i++) {
			try {
				Row row = sheet.getRow(i + ROW0);
				for (int j = 0; j < poscnt; j++) {

					Cell cell = row.getCell(j + COL0);
					menu[j][i] = cell.toString();
					CellStyle style = cell.getCellStyle();
					// ���j���[���󔒂ŁC�����Ƀ��C�����Ȃ��Ȃ�C���̃��j���[���R�s�[
					if (menu[j][i].equals("") && j > 0 && style.getBorderLeft() == 0) {
						menu[j][i] = menu[j - 1][i];
					}
				}
				// ���j���[��POST��END�����ꂽ��I��
				if (menu[0][i].equals("POST"))
					break;
				if (menu[0][i].equals("END"))
					break;
			} catch (Exception e) {
				//�G�N�Z���t�@�C���̏I�[�܂œ��B�I��
				// e.printStackTrace();
				// logger.log(Level.SEVERE, "ERROR:", e);
				break;
			}
		}
	}
}
