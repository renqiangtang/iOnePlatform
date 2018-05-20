package com.base.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageUtils {
	/**
	 * 缩放图片
	 * @param srcBufImage
	 * @param width
	 * @param height
	 * @return
	 */
	public static BufferedImage resize(BufferedImage srcBufImage, int width,
			int height) {
		BufferedImage bufTarget = null;
		//
		double sx = (double) width / srcBufImage.getWidth();
		double sy = (double) height / srcBufImage.getHeight();
		//
		int type = srcBufImage.getType();
		if (type == BufferedImage.TYPE_CUSTOM) {
			ColorModel cm = srcBufImage.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(width,
					height);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			bufTarget = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else
			bufTarget = new BufferedImage(width, height, type);
		//
		Graphics2D g = bufTarget.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.drawRenderedImage(srcBufImage,
				AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return bufTarget;
	}

	public static void main(String[] args) throws Exception {
		String dir = "D:\\workplace\\iworkplace\\iOnePlatform_std\\doc\\img\\";
		BufferedImage srcImg = ImageIO.read(new File(dir + "src.jpg"));
		BufferedImage destImg = resize(srcImg, 150, 150);
		ImageIO.write(destImg, "jpg", new File(dir + "new.jpg"));
	}
}
