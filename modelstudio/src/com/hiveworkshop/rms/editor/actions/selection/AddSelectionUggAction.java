package com.hiveworkshop.rms.editor.actions.selection;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.model.CameraNode;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionBundle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class AddSelectionUggAction implements UndoAction {

	private final Set<GeosetVertex> affectedVerts;
	private final Set<IdObject> affectedIdObjects;
	private final Set<CameraNode> affectedCameraNodes;
	private final Set<GeosetVertex> previousVerts;
	private final Set<IdObject> previousIdObjects;
	private final Set<CameraNode> previousCameraNodes;
	private final ModelView modelView;
	private final ModelStructureChangeListener changeListener;

	public AddSelectionUggAction(SelectionBundle newSelection,
	                             ModelView modelView, ModelStructureChangeListener changeListener) {
		this.modelView = modelView;

		this.previousVerts = new HashSet<>(modelView.getSelectedVertices());
		this.previousIdObjects = new HashSet<>(modelView.getSelectedIdObjects());
		this.previousCameraNodes = new HashSet<>(modelView.getSelectedCameraNodes());

		this.affectedVerts = new HashSet<>(newSelection.getSelectedVertices());
		this.affectedIdObjects = new HashSet<>(newSelection.getSelectedIdObjects());
		this.affectedCameraNodes = new HashSet<>(newSelection.getSelectedCameraNodes());
		this.changeListener = changeListener;
	}

	public AddSelectionUggAction(Collection<GeosetVertex> newVerts, ModelView modelView, ModelStructureChangeListener changeListener) {
		this.modelView = modelView;

		this.previousVerts = new HashSet<>(modelView.getSelectedVertices());
		this.previousIdObjects = new HashSet<>(modelView.getSelectedIdObjects());
		this.previousCameraNodes = new HashSet<>(modelView.getSelectedCameraNodes());

		this.affectedVerts = new HashSet<>(newVerts);
		this.affectedIdObjects = new HashSet<>();
		this.affectedCameraNodes = new HashSet<>();
		this.changeListener = changeListener;
	}

	@Override
	public UndoAction undo() {
		modelView.setSelectedVertices(previousVerts);
		modelView.setSelectedIdObjects(previousIdObjects);
		modelView.setSelectedCameraNodes(previousCameraNodes);
		if (changeListener != null) {
			changeListener.selectionChanged();
		}
		return this;
	}

	@Override
	public UndoAction redo() {
		modelView.addSelectedVertices(affectedVerts);
		modelView.addSelectedIdObjects(affectedIdObjects);
		modelView.removeSelectedCameraNodes(affectedCameraNodes);
		if (changeListener != null) {
			changeListener.selectionChanged();
		}
		return this;
	}

	@Override
	public String actionName() {
		return "add selection";
	}
}
