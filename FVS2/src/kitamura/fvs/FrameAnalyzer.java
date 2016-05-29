package kitamura.fvs;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import com.googlecode.javacv.FFmpegFrameGrabber;

/**
 *　フレームの解析
 * @author Kitamura
 *
 */
public class FrameAnalyzer {
	
	int white = 0;
	
	/**
	 * @param args 未使用
	 */
	public static void main(String[] args) {
		new FrameAnalyzer("MVI_4408.mp4");
	}
	
	FrameAnalyzer(String file){
		
		try {
			//動画ファイルから最初のフレームの抽出
			//Common.logger.log(Level.CONFIG, "FFmpegFrameGrabber:"+file);
			FFmpegFrameGrabber gr = new FFmpegFrameGrabber(file);

			gr.start();
			//Common.logger.log(Level.CONFIG, "Before getBufferedImage:"+file);
			BufferedImage read = gr.grab().getBufferedImage();
			gr.stop();
			//Common.logger.log(Level.CONFIG, "After getBufferedImage:"+file);
			
			//フレームの幅と高さを取得
			int w = read.getWidth(), h = read.getHeight();

			//フレームの明るさを計算
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
			 Common.logger.log(Level.SEVERE, "ERROR:", e);
		}
	}

	//RGBの赤成分
	static int r(int c) {
		return c >> 16 & 0xff;
	}

	//RGBの緑成分
	static int g(int c) {
		return c >> 8 & 0xff;
	}

	//RGBの青成分
	static int b(int c) {
		return c & 0xff;
	}
	
	/**
	 * 明るさの取得
	 * @return　white 明るさ
	 */
	public int getWhite(){
		return white;
	}
}

