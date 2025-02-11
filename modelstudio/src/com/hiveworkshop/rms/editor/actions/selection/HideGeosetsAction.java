package com.hiveworkshop.rms.editor.actions.selection;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;

import java.util.List;

public final class HideGeosetsAction implements UndoAction {
	private final List<Geoset> geosets;
	private final ModelView modelViewManager;
	private final Runnable refreshGUIRunnable;

	public HideGeosetsAction(final List<Geoset> geosets, final ModelView modelViewManager,
	                         final Runnable refreshGUIRunnable) {
		this.geosets = geosets;
		this.modelViewManager = modelViewManager;
		this.refreshGUIRunnable = refreshGUIRunnable;
	}

	@Override
	public UndoAction undo() {
		for (final Geoset geoset : geosets) {
			modelViewManager.makeGeosetEditable(geoset);
		}
		refreshGUIRunnable.run();
		return this;
	}

	@Override
	public UndoAction redo() {
		for (final Geoset geoset : geosets) {
			modelViewManager.makeGeosetNotEditable(geoset);
		}
		refreshGUIRunnable.run();
		return this;
	}

	@Override
	public String actionName() {
		return "hide geosets";
	}

}
