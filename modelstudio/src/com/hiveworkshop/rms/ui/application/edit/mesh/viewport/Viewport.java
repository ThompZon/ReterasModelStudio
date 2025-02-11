package com.hiveworkshop.rms.ui.application.edit.mesh.viewport;

import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditorManager;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.ViewportActivityManager;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordDisplayListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers.ViewportModelRenderer;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.cutpaste.ViewportTransferHandler;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.util.Vec3;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.*;

public class Viewport extends ViewportView {
	Timer paintTimer;

	private final ViewportModelRenderer viewportModelRenderer;
	private final LinkRenderer linkRenderer;
	private final ModelEditorManager modelEditorManager;
	private final Vec3 facingVector;
	private View view;

	long totTempRenderTime;
	long renderCount;


	public Viewport(byte d1, byte d2, ModelHandler modelHandler,
	                ViewportActivityManager activityListener,
	                CoordDisplayListener coordDisplayListener,
	                ModelEditorManager modelEditorManager,
	                ViewportTransferHandler viewportTransferHandler) {
		super(d1, d2, new Dimension(200, 200), coordDisplayListener);
		setModel(modelHandler, activityListener);
		// Dimension 1 and Dimension 2, these specify which dimensions to display.
		// the d bytes can thus be from 0 to 2, specifying either the X, Y, or Z dimensions

		this.modelEditorManager = modelEditorManager;
		setupCopyPaste(viewportTransferHandler);

//		contextMenu = new ViewportPopupMenu(this, this.getRootPane(), this.modelHandler, this.modelEditorManager);
//		add(contextMenu);

		viewportModelRenderer = new ViewportModelRenderer(ProgramGlobals.getPrefs().getVertexSize());
		linkRenderer = new LinkRenderer();

		facingVector = new Vec3(0, 0, 0);
		final byte unusedXYZ = coordinateSystem.getUnusedXYZ();
		facingVector.setCoord(unusedXYZ, unusedXYZ == 0 ? 1 : -1);

		paintTimer = new Timer(16, e -> {
			repaint();
			if (!isShowing()) {
				paintTimer.stop();
			}
		});
		paintTimer.start();
	}

	public void setView(View view) {
		this.view = view;
	}

	public void setupViewportBackground(ProgramPreferences programPreferences) {
		// if (programPreferences.isInvertedDisplay()) {
		// setBackground(Color.DARK_GRAY.darker());
		// } else {setBackground(new Color(255, 255, 255));}
		setBackground(programPreferences.getBackgroundColor());
	}

	private void setupCopyPaste(ViewportTransferHandler viewportTransferHandler) {
		setTransferHandler(viewportTransferHandler);
		ActionMap map = getActionMap();
		map.put(TransferHandler.getCutAction().getValue(Action.NAME), TransferHandler.getCutAction());
		map.put(TransferHandler.getCopyAction().getValue(Action.NAME), TransferHandler.getCopyAction());
		map.put(TransferHandler.getPasteAction().getValue(Action.NAME), TransferHandler.getPasteAction());
		setFocusable(true);
	}

	public void paintComponent(Graphics g, int vertexSize) {
		long renderStart = System.nanoTime();
		if (ProgramGlobals.getPrefs().show2dGrid()) {
			drawGrid(g);
		}

		Graphics2D graphics2d = (Graphics2D) g;

		if (modelEditorManager.getModelEditor().editorWantsAnimation()) {
			Stroke stroke = graphics2d.getStroke();
			graphics2d.setStroke(new BasicStroke(3));
			modelHandler.getRenderModel().updateNodes(false);

			linkRenderer.renderLinks(graphics2d, coordinateSystem, modelHandler);

			graphics2d.setStroke(stroke);

			viewportModelRenderer.renderModel(graphics2d, coordinateSystem, modelHandler, true);

			viewportActivity.render(graphics2d, coordinateSystem, modelHandler.getRenderModel(), true);
		} else {
			viewportModelRenderer.renderModel(graphics2d, coordinateSystem, modelHandler, false);

			viewportActivity.render(graphics2d, coordinateSystem, modelHandler.getRenderModel(), false);
		}

		getColor(g, coordinateSystem.getPortFirstXYZ());
		g.drawLine((int) Math.round(coordinateSystem.viewX(0)), (int) Math.round(coordinateSystem.viewY(0)), (int) Math.round(coordinateSystem.viewX(5)), (int) Math.round(coordinateSystem.viewY(0)));

		getColor(g, coordinateSystem.getPortSecondXYZ());
		g.drawLine((int) Math.round(coordinateSystem.viewX(0)), (int) Math.round(coordinateSystem.viewY(0)), (int) Math.round(coordinateSystem.viewX(0)), (int) Math.round(coordinateSystem.viewY(5)));


		adjustAndRunPaintTimer(renderStart);
	}

	public void adjustAndRunPaintTimer(long renderStart) {
		long renderEnd = System.nanoTime();
		long currFrameRenderTime = renderEnd - renderStart;

		totTempRenderTime += currFrameRenderTime;
		renderCount += 1;
		if (renderCount >= 100) {
			long millis = ((totTempRenderTime / 1000000L) / renderCount) + 1;
			paintTimer.setDelay(Math.max(16, (int) (millis * 5)));
//			System.out.println("delay: " + paintTimer.getDelay());

			totTempRenderTime = 0;
			renderCount = 0;
		}
		boolean showing = isShowing();
		boolean running = paintTimer.isRunning();
		if (showing && !running) {
			paintTimer.start();
		} else if (!showing && running) {
			paintTimer.stop();
		}
	}

	private void getColor(Graphics g, byte dir) {
		switch (dir) {
			case 0 -> g.setColor(new Color(0, 255, 0));
			case 1 -> g.setColor(new Color(255, 0, 0));
			case 2 -> g.setColor(new Color(0, 0, 255));
		}
	}

	public void setViewportAxises(String name, byte dim1, byte dim2) {
		view.getViewProperties().setTitle(name);
		coordinateSystem.setDimensions(dim1, dim2);
	}

	public ModelEditorManager getModelEditorManager() {
		return modelEditorManager;
	}

	public ModelView getModelView() {
		return modelHandler.getModelView();
	}

	public Point getLastMouseMotion() {
		return lastMouseMotion;
	}

	public Vec3 getFacingVector() {
		return facingVector;
	}

}