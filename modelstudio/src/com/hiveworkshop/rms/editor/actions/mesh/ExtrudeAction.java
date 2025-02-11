package com.hiveworkshop.rms.editor.actions.mesh;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.Triangle;
import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.util.Pair;
import com.hiveworkshop.rms.util.Vec3;

import java.util.*;

public class ExtrudeAction implements UndoAction {
	MoveAction baseMovement;
	List<Vec3> selection;
	List<Triangle> addedTriangles = new ArrayList<>();
	Set<Triangle> selectedEdgeTriangles = new HashSet<>();
	Set<Triangle> notSelectedEdgeTriangles = new HashSet<>();
	List<GeosetVertex> affectedVertices = new ArrayList<>();
	Set<GeosetVertex> orgEdgeVertices = new HashSet<>();
	Map<GeosetVertex, GeosetVertex> oldToNew = new HashMap<>();
	Set<Pair<GeosetVertex, GeosetVertex>> edges;

	public ExtrudeAction(Collection<? extends Vec3> selection, Vec3 moveVector) {
		selection.forEach(vert -> affectedVertices.add((GeosetVertex) vert));
		this.selection = new ArrayList<>(selection);

		baseMovement = new MoveAction(this.selection, moveVector, VertexActionType.UNKNOWN);
		findInternalEdgeVerts();
		edges = ModelUtils.getEdges(affectedVertices);
		findEdgeTris();
		makeVertCopies();
	}

	private void findInternalEdgeVerts() {
		for (GeosetVertex geosetVertex : affectedVertices) {
			for (Triangle triangle : geosetVertex.getTriangles()) {
				if (!affectedVertices.containsAll(Arrays.asList(triangle.getVerts()))) {
					orgEdgeVertices.add(geosetVertex);
				}
			}
		}
	}

	private void findEdgeTris() {
		for (GeosetVertex geosetVertex : orgEdgeVertices) {
			for (Triangle triangle : geosetVertex.getTriangles()) {
				if (!affectedVertices.containsAll(Arrays.asList(triangle.getVerts()))) {
					notSelectedEdgeTriangles.add(triangle);
				} else {
					selectedEdgeTriangles.add(triangle);
				}
			}
		}
	}

	private void makeVertCopies() {
		for (GeosetVertex geosetVertex : orgEdgeVertices) {
			oldToNew.put(geosetVertex, geosetVertex.deepCopy());
		}
	}

	private void splitEdge() {
		for (GeosetVertex geosetVertex : orgEdgeVertices) {
			GeosetVertex newVertex = oldToNew.get(geosetVertex);
			List<Triangle> trisToRemove = new ArrayList<>();
			for (Triangle triangle : geosetVertex.getTriangles()) {
				if (selectedEdgeTriangles.contains(triangle)) {
					newVertex.removeTriangle(triangle);
				} else if (notSelectedEdgeTriangles.contains(triangle)) {
					trisToRemove.add(triangle);
					triangle.replace(geosetVertex, newVertex);
				}
			}
			trisToRemove.forEach(t -> geosetVertex.removeTriangle(t));
		}
	}

	private void unSplitEdge() {
		for (GeosetVertex geosetVertex : orgEdgeVertices) {
			GeosetVertex newVertex = oldToNew.get(geosetVertex);
			for (Triangle triangle : newVertex.getTriangles()) {

				geosetVertex.addTriangle(triangle);
				triangle.replace(newVertex, geosetVertex);
			}
		}
	}

	private void fillGap() {
		for (Pair<GeosetVertex, GeosetVertex> edge : edges) {
			GeosetVertex org1 = edge.getFirst();
			GeosetVertex org2 = edge.getSecond();
			if (orgEdgeVertices.contains(org1) && orgEdgeVertices.contains(org2)) {
				GeosetVertex new1 = oldToNew.get(edge.getFirst());
				GeosetVertex new2 = oldToNew.get(edge.getSecond());

				Triangle tri1 = new Triangle(org1, new1, org2);
				tri1.setGeoset(org1.getGeoset());
				Triangle tri2 = new Triangle(new1, new2, org2);
				tri2.setGeoset(org1.getGeoset());
				addedTriangles.add(tri1);
				addedTriangles.add(tri2);
			}
		}
	}

	private void removeGapFill() {
		for (GeosetVertex geosetVertex : orgEdgeVertices) {
			geosetVertex.getTriangles().removeAll(addedTriangles);
			oldToNew.get(geosetVertex).getTriangles().removeAll(addedTriangles);
		}
	}


	@Override
	public UndoAction redo() {
		splitEdge();
		baseMovement.redo();
		fillGap();
		for (GeosetVertex newVert : oldToNew.values()) {
			newVert.getGeoset().add(newVert);
			for (Triangle triangle : newVert.getTriangles()) {
				newVert.getGeoset().add(triangle);
			}
		}
		return this;
	}

	@Override
	public UndoAction undo() {
		for (GeosetVertex newVert : oldToNew.values()) {
			newVert.getGeoset().remove(newVert);
		}
		for (Triangle triangle : addedTriangles) {
			triangle.getGeoset().remove(triangle);
		}
		removeGapFill();
		baseMovement.undo();
		unSplitEdge();
		return this;
	}

	@Override
	public String actionName() {
		return "extrude";
	}

}
