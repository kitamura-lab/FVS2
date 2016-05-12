package fvs.kitamura;

import java.awt.image.BufferedImage;

import com.googlecode.javacv.FFmpegFrameGrabber;

public class FrameAnalyzer {
	
	int wValue = 0;
	
	public static void main(String[] args) {
		new FrameAnalyzer("MVI_4408.mp4");
		//System.out.println("PASS2");
	}
	
	FrameAnalyzer(String file){
		BufferedImage read=null;
		
		try {
			//System.out.println("PASS1");
			FFmpegFrameGrabber gr = new FFmpegFrameGrabber(file);

			gr.start();
			read = gr.grab().getBufferedImage();
			gr.stop();


			int w = read.getWidth(), h = read.getHeight();
			//System.out.println("" + w + ":" + h);

			int p;
			int cnt = 0;
			double s = 0;
			for (int y = 20; y < h; y += 100) {
				for (int x = 80; x < w; x += 100) {
					p = read.getRGB(x, y);
					s = s + r(p) + g(p) + b(p);
					cnt += 3;
				}
			}

			wValue = (int)(s/cnt);
			//System.out.println("W-Value:"+(int)(s/cnt));
		} catch (Exception e) {
			e.printStackTrace();
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
		return wValue;
	}
}

