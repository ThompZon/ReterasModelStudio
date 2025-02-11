package com.hiveworkshop.rms.ui.gui.modeledit.manipulator;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.actions.mesh.ExtendAction;
import com.hiveworkshop.rms.editor.actions.util.CompoundAction;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditor;
import com.hiveworkshop.rms.ui.application.viewer.CameraHandler;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.event.MouseEvent;
import java.util.Arrays;

public final class ExtendManipulator extends AbstractMoveManipulator {

	private UndoAction extendAction;

	public ExtendManipulator(ModelEditor modelEditor, MoveDimension dir) {
		super(modelEditor, dir);
	}

	@Override
	protected void onStart(MouseEvent e, Vec2 mouseStart, byte dim1, byte dim2) {
		super.onStart(e, mouseStart, dim1, dim2);
//		extendAction = modelEditor.beginExtendingSelection();
		ModelView modelView = ProgramGlobals.getCurrentModelPanel().getModelView();
		extendAction = new ExtendAction(modelView.getSelectedVertices(), new Vec3(0, 0, 0));
		extendAction.redo();
	}

	@Override
	public UndoAction finish(MouseEvent e, Vec2 mouseStart, Vec2 mouseEnd, byte dim1, byte dim2) {
		return new CompoundAction("extend", Arrays.asList(extendAction, super.finish(e, mouseStart, mouseEnd, dim1, dim2)));
	}

	@Override
	protected void onStart(MouseEvent e, Vec2 mouseStart, CameraHandler cameraHandler) {
		super.onStart(e, mouseStart, cameraHandler);
//		extendAction = modelEditor.beginExtendingSelection();
		ModelView modelView = ProgramGlobals.getCurrentModelPanel().getModelView();
		extendAction = new ExtendAction(modelView.getSelectedVertices(), new Vec3(0, 0, 0));
		extendAction.redo();
	}

	@Override
	public UndoAction finish(MouseEvent e, Vec2 mouseStart, Vec2 mouseEnd, CameraHandler cameraHandler) {
		return new CompoundAction("extend", Arrays.asList(extendAction, super.finish(e, mouseStart, mouseEnd, cameraHandler)));
	}

//	@Override
//	protected void buildMoveVector(Double mouseStart, Double mouseEnd, byte dim1, byte dim2) {
//		moveVector.setCoord(dim1, mouseEnd.x - mouseStart.x);
//		moveVector.setCoord(dim2, mouseEnd.y - mouseStart.y);
//	}
}
