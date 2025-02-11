package com.hiveworkshop.rms.ui.gui.modeledit.creator.activity;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.actions.addactions.AddGeosetAction;
import com.hiveworkshop.rms.editor.actions.addactions.DrawVertexAction;
import com.hiveworkshop.rms.editor.actions.model.material.AddMaterialAction;
import com.hiveworkshop.rms.editor.actions.util.CompoundAction;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.Material;
import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.animation.WrongModeException;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditorManager;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.ViewportActivity;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.application.viewer.CameraHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.toolbar.ModelEditorActionType3;
import com.hiveworkshop.rms.ui.preferences.ColorThing;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DrawVertexActivity extends ViewportActivity {

	private Point lastMousePoint;
	private ViewportActivity lastActivity;
	private ModelEditorActionType3 lastEditorType;

	public DrawVertexActivity(ModelHandler modelHandler,
	                          ModelEditorManager modelEditorManager) {
		super(modelHandler, modelEditorManager);
	}

	public DrawVertexActivity(ModelHandler modelHandler,
	                          ModelEditorManager modelEditorManager, ViewportActivity lastActivity) {
		super(modelHandler, modelEditorManager);
		this.lastActivity = lastActivity;
	}

	public DrawVertexActivity(ModelHandler modelHandler,
	                          ModelEditorManager modelEditorManager, ModelEditorActionType3 lastEditorType) {
		super(modelHandler, modelEditorManager);
		this.lastEditorType = lastEditorType;
	}

	@Override
	public void mousePressed(MouseEvent e, CoordinateSystem coordinateSystem) {
		Vec3 locationCalculator = new Vec3(0, 0, 0);
		locationCalculator.setCoord(coordinateSystem.getPortFirstXYZ(), coordinateSystem.geomX(e.getX()));
		locationCalculator.setCoord(coordinateSystem.getPortSecondXYZ(), coordinateSystem.geomY(e.getY()));
		locationCalculator.setCoord(coordinateSystem.getUnusedXYZ(), 0);
		try {
//			Viewport viewport = viewportListener.getViewport();
//			Vec3 facingVector = viewport == null ? new Vec3(0, 0, 1) : viewport.getFacingVector();
			Vec3 facingVector = new Vec3(0, 0, 1); // TODO make this work with CameraHandler

			List<UndoAction> undoActions = new ArrayList<>();
			Material solidWhiteMaterial = ModelUtils.getWhiteMaterial(modelView.getModel());
			Geoset solidWhiteGeoset = getSolidWhiteGeoset(solidWhiteMaterial);

			if (!modelView.getModel().contains(solidWhiteMaterial) || !modelView.getModel().contains(solidWhiteGeoset) || !modelView.isEditable(solidWhiteGeoset)) {
				undoActions.add(new AddGeosetAction(solidWhiteGeoset, modelView, null));
				if (!modelHandler.getModel().getMaterials().contains(solidWhiteMaterial)) {
					undoActions.add(new AddMaterialAction(solidWhiteMaterial, modelHandler.getModel(), null));
				}
			}

			GeosetVertex geosetVertex = new GeosetVertex(locationCalculator, new Vec3(facingVector));
			geosetVertex.setGeoset(solidWhiteGeoset);
			geosetVertex.addTVertex(new Vec2(0, 0));

			undoActions.add(new DrawVertexAction(geosetVertex));
			undoManager.pushAction(new CompoundAction("add vertex", undoActions, ModelStructureChangeListener.changeListener::geosetsUpdated).redo());
		} catch (WrongModeException exc) {
			JOptionPane.showMessageDialog(null, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}


	@Override
	public void mouseMoved(MouseEvent e, CoordinateSystem coordinateSystem) {
		lastMousePoint = e.getPoint();
	}

	@Override
	public void render(Graphics2D g, CoordinateSystem coordinateSystem, RenderModel renderModel, boolean isAnimated) {
		if (!isAnimated) {
			g.setColor(ProgramGlobals.getEditorColorPrefs().getColor(ColorThing.VERTEX));
			if (lastMousePoint != null) {
				g.fillRect(lastMousePoint.x, lastMousePoint.y, 3, 3);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e, CameraHandler cameraHandler) {
		Vec3 locationCalculator = cameraHandler.getGeoPoint(e.getX(), e.getY());
		try {
//			Viewport viewport = viewportListener.getViewport();
//			Vec3 facingVector = viewport == null ? new Vec3(0, 0, 1) : viewport.getFacingVector();
			Vec3 facingVector = new Vec3(1, 0, 0).transform(cameraHandler.getViewPortAntiRotMat());

			List<UndoAction> undoActions = new ArrayList<>();
			Material solidWhiteMaterial = ModelUtils.getWhiteMaterial(modelView.getModel());
			Geoset solidWhiteGeoset = getSolidWhiteGeoset(solidWhiteMaterial);

			UndoAction addAction = getAddAction(solidWhiteMaterial, solidWhiteGeoset);
			if (addAction != null) {
				undoActions.add(addAction);
			}

			GeosetVertex geosetVertex = new GeosetVertex(locationCalculator, new Vec3(facingVector));
			geosetVertex.setGeoset(solidWhiteGeoset);
			geosetVertex.addTVertex(new Vec2(0, 0));

			undoActions.add(new DrawVertexAction(geosetVertex));
			undoManager.pushAction(new CompoundAction("add vertex", undoActions, ModelStructureChangeListener.changeListener::geosetsUpdated).redo());
			if (lastEditorType != null) {
//				ProgramGlobals.getCurrentModelPanel().changeActivity(lastActivity);
				ProgramGlobals.getCurrentModelPanel().setEditorActionType(lastEditorType);
			}
		} catch (WrongModeException exc) {
			JOptionPane.showMessageDialog(null, exc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}


	@Override
	public void mouseMoved(MouseEvent e, CameraHandler cameraHandler) {
		lastMousePoint = e.getPoint();
	}


	public void render(Graphics2D g, CameraHandler cameraHandler, RenderModel renderModel, boolean isAnimated) {
		if (!isAnimated) {
			g.setColor(ProgramGlobals.getEditorColorPrefs().getColor(ColorThing.VERTEX));
			if (lastMousePoint != null) {
				g.fillRect(lastMousePoint.x, lastMousePoint.y, 3, 3);
			}
		}
	}

}
