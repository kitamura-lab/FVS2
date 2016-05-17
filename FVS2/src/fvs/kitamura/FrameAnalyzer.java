package fvs.kitamura;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.javacv.FFmpegFrameGrabber;

public class FrameAnalyzer {
	
	int white = 0;
	
	public static void main(String[] args) {
		new FrameAnalyzer("MVI_4408.mp4",null);
	}
	
	FrameAnalyzer(String file, Logger logger){
		
		try {
			FFmpegFrameGrabber gr = new FFmpegFrameGrabber(file);

			gr.start();
			BufferedImage read = gr.grab().getBufferedImage();
			gr.stop();

			int w = read.getWidth(), h = read.getHeight();

			int rgb;
			int cnt = 0;
			double sum = 0;
			for (int y = 20; y < h; y += 100) {
				for (int x = 80; x < w; x += 100) {
					rgb = read.getRGB(x, y);
					sum = sum + r(rgb) + g(rgb) + b(rgb);
					cnt += 3;
				}
			}

			white = (int)(sum/cnt);
		} catch (Exception e) {
			e.printStackTrace();
			 logger.log(Level.SEVERE, "ERROR:", e);
		}
	}

	static int r(int c) {
		return c >> 16 & 0xff;
	}

	static int g(int c) {
		return c >> 8 & 0xff;
	}

	static int b(int c) {
		return c & 0xff;
	}

	public int getW(){
		return white;
	}
}

