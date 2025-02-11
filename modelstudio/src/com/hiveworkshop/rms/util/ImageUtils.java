package com.hiveworkshop.rms.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageUtils {
	public static BufferedImage removeAlphaChannel(BufferedImage source) {
		BufferedImage combined = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);

		Graphics g = combined.getGraphics();
		g.drawImage(source, 0, 0, source.getWidth(), source.getHeight(), null);

		return combined;
	}
}
