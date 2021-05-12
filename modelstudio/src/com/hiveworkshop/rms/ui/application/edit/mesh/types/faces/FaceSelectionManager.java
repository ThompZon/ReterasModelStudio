package com.hiveworkshop.rms.ui.application.edit.mesh.types.faces;

import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.Triangle;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelElementRenderer;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.application.edit.uv.types.TVertexModelElementRenderer;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionManager;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class FaceSelectionManager extends SelectionManager<Triangle> {
	private static final Color FACE_SELECTED_COLOR = new Color(1f, 0.45f, 0.45f, 0.3f);
	private static final Color FACE_HIGHLIGHT_COLOR = new Color(0.45f, 1f, 0.45f, 0.3f);
	private static final Color FACE_NOT_SELECTED_COLOR = new Color(0.45f, 0.45f, 1f, 0.3f);

	public FaceSelectionManager(ModelView modelView) {
		super(modelView);
	}

	@Override
	public Vec3 getCenter() {
		Set<Vec3> selectedVertices = new HashSet<>();
		for (Triangle triangle : modelView.getSelectedTriangles()) {
			selectedVertices.addAll(Arrays.asList(triangle.getVerts()));
		}
		return Vec3.centerOfGroup(selectedVertices);
	}

	@Override
	public Set<Triangle> getSelection() {
		return modelView.getSelectedTriangles();
	}

	@Override
	public void setSelection(final Collection<? extends Triangle> selectionItem) {
//		selection.clear();
//		selection.addAll(selectionItem);
		modelView.setSelectedTris((Collection<Triangle>) selectionItem);
		fireChangeListeners();
	}

	@Override
	public void addSelection(final Collection<? extends Triangle> selectionItem) {
//		selection.addAll(selectionItem);
		modelView.addSelectedTris((Collection<Triangle>) selectionItem);
		fireChangeListeners();
	}

	@Override
	public void removeSelection(final Collection<? extends Triangle> selectionItem) {
//		for (final Triangle item : selectionItem) {
////			selection.remove(item);
//		}
		modelView.removeSelectedTris((Collection<Triangle>) selectionItem);
		fireChangeListeners();
	}


	@Override
	public Set<Vec3> getSelectedVertices() {
		Set<Vec3> vertices = new HashSet<>();
		for (Triangle triangle : getSelection()) {
			vertices.addAll(Arrays.asList(triangle.getVerts()));
		}
		return vertices;
	}

	@Override
	public Collection<Triangle> getSelectedFaces() {
		return getSelection();
	}

	@Override
	public void renderSelection(ModelElementRenderer renderer,
	                            CoordinateSystem coordinateSystem,
	                            ModelView modelView) {
//		for (Geoset geoset : modelView.getEditableGeosets()) {
//			for (Triangle triangle : geoset.getTriangles()) {
//				Color outlineColor;
//				Color fillColor;
//				if (geoset == modelView.getHighlightedGeoset()) {
//					outlineColor = ProgramGlobals.getPrefs().getHighlighTriangleColor();
//					fillColor = FACE_HIGHLIGHT_COLOR;
//				} else if (selection.contains(triangle)) {
//					outlineColor = ProgramGlobals.getPrefs().getSelectColor();
//					fillColor = FACE_SELECTED_COLOR;
//				} else {
//					outlineColor = Color.BLUE;
//					fillColor = FACE_NOT_SELECTED_COLOR;
//					continue;
//				}
////				renderer.renderFace(outlineColor, fillColor, triangle);
//				renderer.renderFace(outlineColor, fillColor, triangle.get(0), triangle.get(1), triangle.get(2));
//			}
//		}
	}

	@Override
	public double getCircumscribedSphereRadius(Vec3 sphereCenter) {
		double radius = 0;
		for (Triangle item : modelView.getSelectedTriangles()) {
			for (GeosetVertex geosetVertex : item.getVerts()) {
				double distance = sphereCenter.distance(geosetVertex);
				if (distance >= radius) {
					radius = distance;
				}
			}
		}
		return radius;
	}

	@Override
	public double getCircumscribedSphereRadius(Vec2 center, int tvertexLayerId) {
		double radius = 0;
		for (Triangle item : modelView.getSelectedTriangles()) {
			for (GeosetVertex geosetVertex : item.getVerts()) {
				double distance = center.distance(geosetVertex.getTVertex(tvertexLayerId));
				if (distance >= radius) {
					radius = distance;
				}
			}
		}
		return radius;
	}

	@Override
	public Vec2 getUVCenter(int tvertexLayerId) {
		Set<Vec2> selectedVertices = new HashSet<>();
		for (Triangle triangle : modelView.getSelectedTriangles()) {
			for (GeosetVertex geosetVertex : triangle.getVerts()) {
				if (tvertexLayerId < geosetVertex.getTverts().size()) {
					selectedVertices.add(geosetVertex.getTVertex(tvertexLayerId));
				}
			}
		}
		return Vec2.centerOfGroup(selectedVertices);
	}

	@Override
	public Collection<? extends Vec2> getSelectedTVertices(int tvertexLayerId) {
		Set<Vec2> selectedVertices = new HashSet<>();
		for (Triangle triangle : modelView.getSelectedTriangles()) {
			for (GeosetVertex geosetVertex : triangle.getVerts()) {
				if (tvertexLayerId < geosetVertex.getTverts().size()) {
					selectedVertices.add(geosetVertex.getTVertex(tvertexLayerId));
				}
			}
		}
		return selectedVertices;
	}

	@Override
	public void renderUVSelection(TVertexModelElementRenderer renderer, ModelView modelView, int tvertexLayerId) {
		for (Geoset geoset : modelView.getEditableGeosets()) {
			for (Triangle triangle : geoset.getTriangles()) {
				Color outlineColor;
				Color fillColor;
				if (geoset == modelView.getHighlightedGeoset()) {
					outlineColor = ProgramGlobals.getPrefs().getHighlighTriangleColor();
					fillColor = FACE_HIGHLIGHT_COLOR;
				} else if (selection.contains(triangle)) {
					outlineColor = ProgramGlobals.getPrefs().getSelectColor();
					fillColor = FACE_SELECTED_COLOR;
				} else {
					outlineColor = Color.BLUE;
					fillColor = FACE_NOT_SELECTED_COLOR;
					continue;
				}
				if ((tvertexLayerId < triangle.get(0).getTverts().size())
						&& (tvertexLayerId < triangle.get(1).getTverts().size())
						&& (tvertexLayerId < triangle.get(2).getTverts().size())) {
					renderer.renderFace(outlineColor, fillColor, triangle.get(0).getTVertex(tvertexLayerId), triangle.get(1).getTVertex(tvertexLayerId), triangle.get(2).getTVertex(tvertexLayerId));
				}
			}
		}
	}

}
