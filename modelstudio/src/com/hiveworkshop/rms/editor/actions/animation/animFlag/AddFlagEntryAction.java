package com.hiveworkshop.rms.editor.actions.animation.animFlag;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.model.TimelineContainer;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.editor.model.animflag.Entry;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;

public class AddFlagEntryAction implements UndoAction {
	private final ModelStructureChangeListener structureChangeListener;
	AnimFlag<?> animFlag;
	Entry<?> entry;

	public AddFlagEntryAction(AnimFlag<?> animFlag, Entry<?> entry, TimelineContainer timelineContainer, ModelStructureChangeListener structureChangeListener) {
		this.structureChangeListener = structureChangeListener;
		this.animFlag = animFlag;
		this.entry = entry;
	}

	@Override
	public UndoAction undo() {
		animFlag.removeKeyframe(entry.time);
		if (structureChangeListener != null) {
			structureChangeListener.materialsListChanged();
		}
		return this;
	}

	@Override
	public UndoAction redo() {
		animFlag.setOrAddEntryT(entry.time, entry);
		if (structureChangeListener != null) {
			structureChangeListener.materialsListChanged();
		}
		return this;
	}

	@Override
	public String actionName() {
		return "add animation";
	}
}
