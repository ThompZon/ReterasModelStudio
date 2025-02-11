package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import net.miginfocom.swing.MigLayout;
import org.lwjgl.LWJGLException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


public class PerspDisplayPanel extends JPanel {
	private PerspectiveViewport vp;
	private String title;

	public PerspDisplayPanel(String title) {
		super(new BorderLayout());
		setOpaque(true);

		try {
			vp = new PerspectiveViewport();
//			vp.setIgnoreRepaint(false);
			vp.setMinimumSize(new Dimension(200, 200));
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		this.title = title;

//		getButtonPanel();

		add(vp);
	}

	public void reloadTextures() {
		vp.reloadTextures();
	}

	public void reloadAllTextures() {
		vp.reloadAllTextures();
	}

	public PerspDisplayPanel setModel(ModelHandler modelHandler) {
		System.out.println("PerspDisplayPanel#setModel");
		if (modelHandler == null) {
			vp.setModel(null, null, false);
		} else {
			vp.setModel(modelHandler.getModelView(), modelHandler.getRenderModel(), true);
		}
		return this;
	}

	public void setTitle(String what) {
		title = what;
		setBorder(BorderFactory.createTitledBorder(title));
	}

	public PerspectiveViewport getViewport() {
		return vp;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
//		vp.paint(vp.getGraphics());
		// g.drawString(title,3,3);
		// vp.repaint();
	}


	private void getButtonPanel() {
		JPanel buttonPanel = new JPanel(new MigLayout(""));
		JButton plusZoom = getButton(e -> zoom(.15), 20, 20);
		buttonPanel.add(plusZoom, "wrap");
		JButton minusZoom = getButton(e -> zoom(-.15), 20, 20);
		buttonPanel.add(minusZoom, "wrap");
		JButton up = getButton(e -> translateViewUpDown(20), 32, 16);
		buttonPanel.add(up, "wrap");
		JButton down = getButton(e -> translateViewUpDown(-20), 32, 16);
		buttonPanel.add(down, "wrap");
		JButton left = getButton(e -> translateViewLeftRight(20), 16, 32);
		buttonPanel.add(left, "wrap");
		JButton right = getButton(e -> translateViewLeftRight(-20), 16, 32);
		buttonPanel.add(right, "wrap");
	}

	private static JButton getButton(ActionListener actionListener, int width, int height) {
		Dimension dim = new Dimension(width, height);
		JButton button = new JButton("");
		button.setMaximumSize(dim);
		button.setMinimumSize(dim);
		button.setPreferredSize(dim);
		button.addActionListener(actionListener);
		return button;
	}
	public void zoom(double v) {
		vp.zoom(v);
		vp.repaint();
	}

	public void translateViewLeftRight(int i) {
		vp.translate((i * (1 / vp.getZoomAmount())), 0);
		vp.repaint();
	}

	public void translateViewUpDown(int i) {
		vp.translate(0, (i * (1 / vp.getZoomAmount())));
		vp.repaint();
	}

	public ImageIcon getImageIcon() {
		return new ImageIcon(ViewportRenderExporter.getBufferedImage(vp));
	}

	public BufferedImage getBufferedImage() {
		return ViewportRenderExporter.getBufferedImage(vp);
	}
}
