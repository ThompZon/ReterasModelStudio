package com.hiveworkshop.rms.ui.application.edit.mesh;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.actions.editor.*;
import com.hiveworkshop.rms.editor.actions.util.CompoundAction;
import com.hiveworkshop.rms.editor.actions.util.GenericMoveAction;
import com.hiveworkshop.rms.editor.actions.util.GenericRotateAction;
import com.hiveworkshop.rms.editor.actions.util.GenericScaleAction;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.animation.WrongModeException;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionItemTypes;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionManager;
import com.hiveworkshop.rms.util.Vec3;

public class GeometryModelEditor extends ModelEditor {
	protected final ModelStructureChangeListener structureChangeListener;
	protected ModelHandler modelHandler;
	protected SelectionItemTypes selectionMode;

	public GeometryModelEditor(SelectionManager selectionManager,
	                           ModelHandler modelHandler,
	                           SelectionItemTypes selectionMode) {
		super(selectionManager, modelHandler.getModelView());
		this.modelHandler = modelHandler;
		this.structureChangeListener = ModelStructureChangeListener.changeListener;
		this.selectionMode = selectionMode;
	}

	public GeometryModelEditor setSelectionMode(SelectionItemTypes selectionMode) {
		this.selectionMode = selectionMode;
		return this;
	}

	@Override
	public UndoAction translate(Vec3 v) {
		Vec3 delta = new Vec3(v);
		return new StaticMeshMoveAction(modelView, delta);
	}

	@Override
	public UndoAction scale(Vec3 center, Vec3 scale) {
		return new StaticMeshScaleAction(modelView, center, scale);
	}

    @Override
    public UndoAction setPosition(Vec3 center, Vec3 v) {
        Vec3 delta = Vec3.getDiff(v, center);
	    return new StaticMeshMoveAction(modelView, delta);
    }

    @Override
    public UndoAction rotate(Vec3 center, Vec3 rotate) {
	    return new CompoundAction("rotate", null,
			    new SimpleRotateAction(modelView, center, Math.toRadians(rotate.x), Vec3.X_AXIS),
			    new SimpleRotateAction(modelView, center, Math.toRadians(rotate.y), Vec3.NEGATIVE_Y_AXIS),
			    new SimpleRotateAction(modelView, center, Math.toRadians(rotate.z), Vec3.NEGATIVE_Z_AXIS));
    }

    @Override
    public boolean editorWantsAnimation() {
        return false;
    }

    @Override
    public GenericMoveAction beginTranslation() {
        return new StaticMeshMoveAction(modelView, Vec3.ZERO);
    }

	@Override
	public GenericRotateAction beginRotation(Vec3 center, byte dim1, byte dim2) {
		return new StaticMeshRotateAction(modelView, new Vec3(center), dim1, dim2);
	}

	@Override
	public GenericRotateAction beginSquatTool(Vec3 center, byte firstXYZ, byte secondXYZ) {
		throw new WrongModeException("Unable to use squat tool outside animation editor mode");
	}

	@Override
	public GenericRotateAction beginRotation(Vec3 center, Vec3 axis) {
		return new StaticMeshRotateAction2(modelView, new Vec3(center), axis);
	}

	@Override
	public GenericRotateAction beginSquatTool(Vec3 center, Vec3 axis) {
		throw new WrongModeException("Unable to use squat tool outside animation editor mode");
	}

	@Override
	public GenericScaleAction beginScaling(Vec3 center) {
		return new StaticMeshScaleAction(modelView, center);
	}
}
