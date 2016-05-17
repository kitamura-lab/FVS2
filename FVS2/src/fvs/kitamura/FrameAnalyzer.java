package fvs.kitamura;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.javacv.FFmpegFrameGrabber;

/**
 * @author Kitamura
 *　動画の白さを判定
 */
public class FrameAnalyzer {
	
	int white = 0;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new FrameAnalyzer("MVI_4408.mp4",null);
	}
	
	FrameAnalyzer(String file, Logger logger){
		
		try {
			//動画の最初のフレームを抜き出す
			logger.log(Level.CONFIG, "FFmpegFrameGrabber:"+file);
			FFmpegFrameGrabber gr = new FFmpegFrameGrabber(file);

			gr.start();
			logger.log(Level.CONFIG, "Before getBufferedImage:"+file);
			BufferedImage read = gr.grab().getBufferedImage();
			gr.stop();
			logger.log(Level.CONFIG, "After getBufferedImage:"+file);
			
			//フレームの縦横を獲得
			int w = read.getWidth(), h = read.getHeight();

			//フレームの白さを計算する
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

	//赤成分の抽出
	static int r(int c) {
		return c >> 16 & 0xff;
	}

	//緑成分の抽出
	static int g(int c) {
		return c >> 8 & 0xff;
	}

	//青成分の抽出
	static int b(int c) {
		return c & 0xff;
	}
	
	/**
	 * @return　白さ
	 */
	public int getWhite(){
		return white;
	}
}

