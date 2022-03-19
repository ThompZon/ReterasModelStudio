package com.hiveworkshop.rms.editor.actions.uv;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.actions.util.GenericRotateAction;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import java.util.ArrayList;
import java.util.Collection;

public final class StaticMeshUVRotateAction implements GenericRotateAction {
	private final ArrayList<Vec2> selectedTVerts;
	private final Vec3 center;
	private double radians;
	private final byte dim1;
	private final byte dim2;
	int uvLayerIndex;

	public StaticMeshUVRotateAction(Collection<GeosetVertex> selectedVertices, int uvLayerIndex, Vec2 center, byte dim1, byte dim2) {
		this(selectedVertices, uvLayerIndex, new Vec3(center.x, center.y, 0), dim1, dim2);
	}

	public StaticMeshUVRotateAction(Collection<GeosetVertex> selectedVertices, int uvLayerIndex, Vec3 center, byte dim1, byte dim2) {
		selectedTVerts = new ArrayList<>();
		for (GeosetVertex vertex : selectedVertices) {
			if (uvLayerIndex < vertex.getTverts().size()) {
				selectedTVerts.add(vertex.getTVertex(uvLayerIndex));
			}
		}
		this.uvLayerIndex = uvLayerIndex;
		this.center = center;
		this.dim1 = dim1;
		this.dim2 = dim2;
		this.radians = 0;
	}

	@Override
	public UndoAction undo() {
		for (Vec2 vertex : selectedTVerts) {
			vertex.rotate(center.x, center.y, -radians, dim1, dim2);
		}
		return this;
	}

	@Override
	public UndoAction redo() {
		for (Vec2 vertex : selectedTVerts) {
			vertex.rotate(center.x, center.y, radians, dim1, dim2);
		}
		return this;
	}

	@Override
	public String actionName() {
		return "rotate";
	}

	@Override
	public GenericRotateAction updateRotation(double radians) {
		this.radians += radians;
		for (Vec2 vertex : selectedTVerts) {
			vertex.rotate(center.x, center.y, radians, dim1, dim2);
		}
		return this;
	}

}
