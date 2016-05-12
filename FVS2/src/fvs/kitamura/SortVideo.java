package fvs.kitamura;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;

public class SortVideo {
	
	final static String destFolder = "SortedFVideo";
	final static int catBoundary = 2000;
	final static int wValueBoundary = 30;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final String srcPath = "C:\\Users\\Kitamura\\Documents";
		final String srcFolder = "FVideo";
		new SortVideo(new File(srcPath,srcFolder), null, null);
	}
	
	SortVideo(File src, JButton[][] jlabel, Logger logger)
	{
		//System.out.println(src.getName());
		int pno=0;
		for(pno=0;pno<9;pno++){
			if(jlabel[pno][0].getText().equals(src.getName())) break;
		}
		
		
		//System.out.println(src.getName());
		File dest = new File(src.getParent()+"\\Sorted"+src.getName());
		if (!dest.exists())
			dest.mkdir();

		String[] sfiles = src.list();

		int catCounter = 0;
		int catOfPreviousFile = 0;

		for (String sfile : sfiles) {
			File srcFile = new File(src, sfile);
			//int fileSize = (int) srcFile.length() / 1024;
			
			int wValue = new FrameAnalyzer(srcFile.getAbsolutePath()).getW();
			//System.out.println(srcFile.getAbsolutePath()+":"+wValue);
			logger.log(Level.CONFIG, srcFile.getAbsolutePath()+":"+wValue);
			
			int cat = 0;

			if(wValue < wValueBoundary)
				cat = 0;
			else {
				if (catOfPreviousFile == 0) {
					cat = ++catCounter;
					while(jlabel[pno][cat].getForeground()==Color.WHITE){
						cat = ++catCounter;
					}
					while(jlabel[pno][cat].getForeground()==Color.WHITE){
						cat = ++catCounter;
					}
					File catFolder = new File(dest, "" + cat+"."+jlabel[pno][cat].getText());
					if (!catFolder.exists())
						catFolder.mkdir();
				} else
					cat = catCounter;
				try {
					FileInputStream fis = new FileInputStream(srcFile);
					FileChannel srcChannel = fis.getChannel();
					File destFile = new File(dest,"\\"+cat+"."+jlabel[pno][cat].getText()+"\\"+sfile);
					//System.out.println(destFile);
					//System.out.println(destFile);
					FileOutputStream fos = new FileOutputStream(destFile);
					FileChannel destChannel = fos.getChannel();
					srcChannel.transferTo(0, srcChannel.size(), destChannel);
					srcChannel.close();
					destChannel.close();
					fis.close();
					fos.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			//System.out.println(sfile + ":" + fileSize + ":" + cat);
			catOfPreviousFile = cat;
		}
	}

}
