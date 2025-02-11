package com.hiveworkshop.rms.ui.gui.modeledit.manipulator;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.actions.util.GenericScaleAction;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;
import com.hiveworkshop.rms.ui.application.viewer.CameraHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.AbstractSelectionManager;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.event.MouseEvent;

public abstract class AbstractScaleManipulator extends Manipulator {
	protected final ModelEditor modelEditor;
	protected final AbstractSelectionManager selectionManager;
	protected final Vec3 scaleVector;
	protected GenericScaleAction scaleAction;
	protected MoveDimension dir;
	protected boolean isNeg = false;

	public AbstractScaleManipulator(ModelEditor modelEditor, AbstractSelectionManager selectionManager, MoveDimension dir) {
		this.modelEditor = modelEditor;
		this.selectionManager = selectionManager;
		this.scaleVector = new Vec3(1, 1, 1);
		this.dir = dir;
	}

//	@Override
//	protected void onStart(MouseEvent e, Vec2 mouseStart, byte dim1, byte dim2) {
//		Vec3 center = selectionView.getCenter();
//		resetScaleVector();
//		scaleAction = modelEditor.beginScaling(center);
//	}

	@Override
	public void update(MouseEvent e, Vec2 mouseStart, Vec2 mouseEnd, byte dim1, byte dim2) {
		resetScaleVector();
		buildScaleVector(mouseStart, mouseEnd, dim1, dim2);
		scaleAction.updateScale(scaleVector);
	}

	@Override
	public UndoAction finish(MouseEvent e, Vec2 mouseStart, Vec2 mouseEnd, byte dim1, byte dim2) {
		update(e, mouseStart, mouseEnd, dim1, dim2);
		resetScaleVector();
		isNeg = false;
		return scaleAction;
	}

	protected final void buildScaleVector(Vec2 mouseStart, Vec2 mouseEnd, byte dim1, byte dim2) {
		double scaleFactor = computeScaleFactor(mouseStart, mouseEnd, dim1, dim2);
		if (dir == MoveDimension.XYZ) {
			scaleVector.set(scaleFactor, scaleFactor, scaleFactor);
		} else {
			if (dir.containDirection(dim1)) {
				scaleVector.setCoord(dim1, scaleFactor);
			}
			if (dir.containDirection(dim2)) {
				scaleVector.setCoord(dim2, scaleFactor);
			}
		}
	}

	@Override
	public void update(MouseEvent e, Vec2 mouseStart, Vec2 mouseEnd, CameraHandler cameraHandler) {
		resetScaleVector();
		buildScaleVector(mouseStart, mouseEnd, cameraHandler);
		scaleAction.updateScale(scaleVector);
	}

	@Override
	public UndoAction finish(MouseEvent e, Vec2 mouseStart, Vec2 mouseEnd, CameraHandler cameraHandler) {
		update(e, mouseStart, mouseEnd, cameraHandler);
		resetScaleVector();
		isNeg = false;
		return scaleAction;
	}

	protected final void buildScaleVector(Vec2 mouseStart, Vec2 mouseEnd, CameraHandler cameraHandler) {
		double scaleFactor = computeScaleFactor(mouseStart, mouseEnd, cameraHandler);
		if (dir == MoveDimension.XYZ) {
			scaleVector.set(scaleFactor, scaleFactor, scaleFactor);
		} else {
//			if (dir.containDirection(dim1)) {
//				scaleVector.setCoord(dim1, scaleFactor);
//			}
//			if (dir.containDirection(dim2)) {
//				scaleVector.setCoord(dim2, scaleFactor);
//			}
		}
	}

	protected abstract double computeScaleFactor(Vec2 mouseStart, Vec2 mouseEnd, byte dim1, byte dim2);

	protected abstract double computeScaleFactor(Vec2 mouseStart, Vec2 mouseEnd, CameraHandler cameraHandler);

	protected int getFlipNeg(double dEnd) {
		int flipNeg;
		flipNeg = (!isNeg && dEnd < 0) || (isNeg && dEnd > 0) ? -1 : 1;
		isNeg = (flipNeg < 0) != isNeg;
		return flipNeg;
	}

	protected void resetScaleVector() {
		scaleVector.set(1, 1, 1);
	}

}
