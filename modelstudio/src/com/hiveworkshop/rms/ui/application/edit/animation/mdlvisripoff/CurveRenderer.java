package com.hiveworkshop.rms.ui.application.edit.animation.mdlvisripoff;

import com.hiveworkshop.rms.util.GU;
import com.hiveworkshop.rms.util.Vec2;

import javax.swing.*;
import java.awt.*;

public class CurveRenderer extends JPanel {
	SplineTracker<?> splineTracker;
	Vec2 renderStartPoint = new Vec2();
	Vec2 renderEndPoint = new Vec2();
	float pixPerUnitX;
	float pixPerUnitY;

	Vec2[] curve = new Vec2[101];

	public CurveRenderer() {
		for (int i = 0; i<curve.length; i++){
			curve[i] = new Vec2(i, 0);
		}
		setPreferredSize(new Dimension(100, 70));
	}

	public void setSplineTracker(SplineTracker<?> splineTracker) {
		this.splineTracker = splineTracker;
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		final Rectangle rect = getBounds();
		int height = rect.height;
		pixPerUnitX = 0.005f * rect.width;
		pixPerUnitY = height / 130f;
		renderStartPoint.set(rect.x, rect.y + height);

		g.setColor(Color.BLUE);
//		g.drawRect(rect.x, rect.y, rect.width, height);
		g.drawRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.BLACK);

		if (splineTracker != null && splineTracker.hasDer()) {
			drawSplines(g);
//			makeSplines(g);
		}

		// Central line
		g.setColor(Color.RED);

//		int x1 = getEndX(100);
		int x1 = 50;
//		g.drawLine(x1, height, x1, height - Math.round(pixPerUnitY * 100));
		g.drawLine(x1, 0, x1, height);
	}

	private void drawSplines(Graphics g){
		GU.drawLines(g, curve);
	}
	public void makeSplines() {
		splineTracker.prepareTTan();
		int i = 0;
		int timeStep = 2;
		int totTime = 200;
		for (; i <= 100; i += 2) {
			splineTracker.resetEntriesIn(0);

			splineTracker.interpolate(i);
			curve[i/2].y = getEndYIn();
		}

		for (; i <= 200; i += 2) {
			splineTracker.resetEntriesOut(200);

			splineTracker.interpolate(i);
			curve[i/2].y = getEndYOut();
		}
	}
	public void makeSplines2() {
		int i = 0;
		int timeStep = 2;
		int totTime = 200;
		for (; i <= 100; i += 2) {
			splineTracker.resetEntriesIn(0);

			splineTracker.interpolate(i);
			curve[i/2].y = getEndYIn();
		}

		for (; i <= 200; i += 2) {
			splineTracker.resetEntriesOut(200);

			splineTracker.interpolate(i);
			curve[i/2].y = getEndYOut();
		}
	}
//	public void makeSplines() {
//		splineTracker.prepareTTan();
//		int i = 0;
//		int timeStep = 2;
//		int totTime = 200;
//		for (; i <= 100; i += 2) {
//			splineTracker.calcSplineStepStart(0);
//
//			splineTracker.interpolate(i);
//			curve[i/2].y = getEndY();
//		}
//
//		for (; i <= 200; i += 2) {
//			splineTracker.calcSplineStepEnd(200);
//
//			splineTracker.interpolate(i);
//			curve[i/2].y = getEndY();
//		}
//	}

	private int getEndYIn() {
//		float a = pixPerUnitY * splineTracker.getEndX() * 100000;
//		float a = splineTracker.getEndXIn() * 0.1f;
//		float a = splineTracker.getEndXIn() * 1f;
//		float a = splineTracker.getEndXOut() * 1000f;
		float a = splineTracker.getEndXIn() * .5f;
		int i = getHeight() - Math.round(a);
		System.out.println("a: "+ a +", drawHeight: " + i + ", height: " + getHeight());
		return i;
	}
	private int getEndYOut() {
//		float a = pixPerUnitY * splineTracker.getEndX() * 100000;
//		float a = splineTracker.getEndXOut() * 0.1f;
//		float a = splineTracker.getEndXOut() * 1f;
//		float a = splineTracker.getEndXOut() * 1000f;
		float a = splineTracker.getEndXOut() * .5f;
		int i = getHeight() - Math.round(a);
//		System.out.println("a: "+ a +", drawHeight: " + i + ", height: " + getHeight());
		return i;
	}

	public void clearCurve(){
		for (int i = 0; i<curve.length; i++){
			curve[i].set(i, -1);
		}
	}

//	public void makeSplines() {
//		splineTracker.prepareTTan();
//
//		drawFirstSpline();
//		drawSecondSpline();
//	}

//	private void drawFirstSpline() {
//		for (int i = 0; i <= 100; i += 2) {
//			splineTracker.calcSplineStepStart(0);
//
//			splineTracker.interpolate(i);
//			curve[i/2].y = getEndY();
////			drawSplineLine(g, i);
//		}
//	}
//
//	private void drawSecondSpline() {
////		for (int i = 100; i <= 101; i += 2) {
//		for (int i = 100; i <= 200; i += 2) {
//			splineTracker.calcSplineStepEnd(200);
//
//			splineTracker.interpolate(i);
//			curve[i/2].y = getEndY();
////			drawSplineLine(g, i);
//		}
//	}


//	private void drawSplineLine(Graphics g, int time) {
//		renderEndPoint.set(getEndX(time), getEndY());
//		GU.drawLines(g, renderStartPoint, renderEndPoint);
//		renderStartPoint.set(renderEndPoint);
//	}
//
//	private int getEndX(int time) {
//		return Math.round(pixPerUnitX * time);
//	}

//	private int getEndY() {
//		float a = pixPerUnitY * splineTracker.getEndX() * 100000;
//		int i = getHeight() - Math.round(a);
//		System.out.println("a: " + a + ", drawHeight: " + i + ", height: " + getHeight() + ", pixY: " + pixPerUnitY);
//		return i;
//	}
//	public void makeSplines(Graphics g) {
//		splineTracker.prepareTTan();
//
//		drawFirstSpline(g);
//		drawSecondSpline(g);
//	}
//
//	private void drawFirstSpline(Graphics g) {
//		for (int i = 0; i <= 100; i += 2) {
//			splineTracker.calcSplineStepStart(0);
//
//			splineTracker.interpolate(i);
//			drawSplineLine(g, i);
//		}
//	}
//
//	private void drawSecondSpline(Graphics g) {
////		for (int i = 100; i <= 101; i += 2) {
//		for (int i = 100; i <= 200; i += 2) {
//			splineTracker.calcSplineStepEnd(200);
//
//			splineTracker.interpolate(i);
//			drawSplineLine(g, i);
//		}
//	}
//
//
//	private void drawSplineLine(Graphics g, int time) {
//		renderEndPoint.set(getEndX(time), getEndY());
//		GU.drawLines(g, renderStartPoint, renderEndPoint);
//		renderStartPoint.set(renderEndPoint);
//	}
//
//	private int getEndX(int time) {
//		return Math.round(pixPerUnitX * time);
//	}
//
//	private int getEndY() {
//		float a = pixPerUnitY * splineTracker.getEndX() * 100000;
//		int i = getHeight() - Math.round(a);
//		System.out.println("a: " + a + ", drawHeight: " + i + ", height: " + getHeight() + ", pixY: " + pixPerUnitY);
//		return i;
//	}
}
