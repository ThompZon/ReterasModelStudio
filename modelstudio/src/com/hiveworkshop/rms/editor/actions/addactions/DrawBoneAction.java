package com.hiveworkshop.rms.editor.actions.addactions;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;

import java.util.ArrayList;
import java.util.List;

public class DrawBoneAction implements UndoAction {
	private final Bone bone;
	private final ModelStructureChangeListener changeListener;
	private final List<IdObject> boneAsList;
	private final EditableModel model;

	public DrawBoneAction(EditableModel model, ModelStructureChangeListener changeListener, Bone bone) {
		this.model = model;
		this.bone = bone;
		this.changeListener = changeListener;
		boneAsList = new ArrayList<>();
		boneAsList.add(bone);
	}

	@Override
	public UndoAction undo() {
		model.remove(bone);
		if (changeListener != null) {
			changeListener.nodesUpdated();
		}
		return this;
	}

	@Override
	public UndoAction redo() {
		model.add(bone);
		if (changeListener != null) {
			changeListener.nodesUpdated();
		}
		return this;
	}

	@Override
	public String actionName() {
		return "add " + bone.getName();
	}

}
