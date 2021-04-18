package com.hiveworkshop.rms.ui.application.edit.mesh.types.tpose;

import com.hiveworkshop.rms.editor.model.EventObject;
import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.animflag.Vec3AnimFlag;
import com.hiveworkshop.rms.editor.model.visitor.IdObjectVisitor;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.animation.WrongModeException;
import com.hiveworkshop.rms.ui.application.edit.mesh.AbstractModelEditor;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.ui.gui.modeledit.cutpaste.CopiedModelData;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.nodes.DeleteNodesAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.selection.MakeNotEditableAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.tools.AutoCenterBonesAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.tools.RenameBoneAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.tools.SetParentAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.CompoundAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.actions.util.DoNothingAction;
import com.hiveworkshop.rms.ui.gui.modeledit.newstuff.listener.EditabilityToggleHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectableComponent;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionManager;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.VertexSelectionHelper;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.util.Vec3;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.*;

public class TPoseModelEditor extends AbstractModelEditor<IdObject> {
	private final ProgramPreferences programPreferences;
	private final GenericSelectorVisitor genericSelectorVisitor;
	private final SelectionAtPointTester selectionAtPointTester;

	public TPoseModelEditor(ModelView model,
	                        ProgramPreferences programPreferences,
	                        SelectionManager<IdObject> selectionManager,
	                        ModelStructureChangeListener structureChangeListener) {
		super(selectionManager, model, structureChangeListener);
		this.programPreferences = programPreferences;
		genericSelectorVisitor = new GenericSelectorVisitor();
		selectionAtPointTester = new SelectionAtPointTester();
	}

	public static void hitTest(List<IdObject> selectedItems, Rectangle2D area, Vec3 geosetVertex, CoordinateSystem coordinateSystem, double vertexSize, IdObject node) {
		byte dim1 = coordinateSystem.getPortFirstXYZ();
		byte dim2 = coordinateSystem.getPortSecondXYZ();
		double minX = coordinateSystem.viewX(area.getMinX());
		double minY = coordinateSystem.viewY(area.getMinY());
		double maxX = coordinateSystem.viewX(area.getMaxX());
		double maxY = coordinateSystem.viewY(area.getMaxY());
		double vertexX = geosetVertex.getCoord(dim1);
		double x = coordinateSystem.viewX(vertexX);
		double vertexY = geosetVertex.getCoord(dim2);
		double y = coordinateSystem.viewY(vertexY);
		if ((distance(x, y, minX, minY) <= (vertexSize / 2.0))
				|| (distance(x, y, maxX, maxY) <= (vertexSize / 2.0))
				|| area.contains(vertexX, vertexY)) {
			selectedItems.add(node);
		}
	}

	public static boolean hitTest(Vec3 vertex, Point2D point, CoordinateSystem coordinateSystem, double vertexSize) {
		double x = coordinateSystem.viewX(vertex.getCoord(coordinateSystem.getPortFirstXYZ()));
		double y = coordinateSystem.viewY(vertex.getCoord(coordinateSystem.getPortSecondXYZ()));
		double px = coordinateSystem.viewX(point.getX());
		double py = coordinateSystem.viewY(point.getY());
		return Point2D.distance(px, py, x, y) <= (vertexSize / 2.0);
	}

	public static double distance(double vertexX, double vertexY, double x, double y) {
		double dx = x - vertexX;
		double dy = y - vertexY;
		return Math.sqrt((dx * dx) + (dy * dy));
	}

	@Override
	public UndoAction setParent(IdObject node) {
		Map<IdObject, IdObject> nodeToOldParent = new HashMap<>();
		for (IdObject b : model.getEditableIdObjects()) {
			if (selectionManager.getSelection().contains(b.getPivotPoint())) {
				nodeToOldParent.put(b, b.getParent());
			}
		}
		SetParentAction setParentAction = new SetParentAction(nodeToOldParent, node, structureChangeListener);
		setParentAction.redo();
		return setParentAction;
	}

	@Override
	public UndoAction addTeamColor() {
		return new DoNothingAction("add team color");
	}

	@Override
	public UndoAction splitGeoset() {
		return new DoNothingAction("split geoset");
	}

	@Override
	public UndoAction autoCenterSelectedBones() {
		Set<IdObject> selBones = new HashSet<>();
		for (IdObject b : model.getEditableIdObjects()) {
			if (selectionManager.getSelection().contains(b.getPivotPoint())) {
				selBones.add(b);
			}
		}

		Map<Bone, Vec3> boneToOldPosition = new HashMap<>();
		for (IdObject obj : selBones) {
			if (Bone.class.isAssignableFrom(obj.getClass())) {
				Bone bone = (Bone) obj;
				List<GeosetVertex> childVerts = new ArrayList<>();
				for (Geoset geo : model.getModel().getGeosets()) {
					childVerts.addAll(geo.getChildrenOf(bone));
				}
				if (childVerts.size() > 0) {
					Vec3 pivotPoint = bone.getPivotPoint();
					boneToOldPosition.put(bone, new Vec3(pivotPoint));
					pivotPoint.set(Vec3.centerOfGroup(childVerts));
				}
			}
		}
		return new AutoCenterBonesAction(boneToOldPosition);
	}

	@Override
	public UndoAction expandSelection() {
		throw new WrongModeException("Not supported in T-Pose mode");
	}

	@Override
	public UndoAction setSelectedBoneName(String name) {
		if (selectionManager.getSelection().size() != 1) {
			throw new IllegalStateException("Only one bone can be renamed at a time.");
		}
		IdObject node = selectionManager.getSelection().iterator().next();
		if (node == null) {
			throw new IllegalStateException("Selection is not a node");
		}
		RenameBoneAction renameBoneAction = new RenameBoneAction(node.getName(), name, node);
		renameBoneAction.redo();
		return renameBoneAction;
	}

	@Override
	public UndoAction addSelectedBoneSuffix(String name) {
		Set<IdObject> selection = selectionManager.getSelection();
		List<RenameBoneAction> actions = new ArrayList<>();
		for (IdObject bone : selection) {
			RenameBoneAction renameBoneAction = new RenameBoneAction(bone.getName(), bone.getName() + name, bone);
			renameBoneAction.redo();
			actions.add(renameBoneAction);
		}
		return new CompoundAction("add selected bone suffix", actions);
	}

	private void toggleSelection(Set<Vec3> selection, Vec3 position) {
		if (selection.contains(position)) {
			selection.remove(position);
		} else {
			selection.add(position);
		}
	}

	@Override
	public void selectByVertices(Collection<? extends Vec3> newSelection) {
		Set<IdObject> newlySelectedPivots = new HashSet<>();
		for (IdObject object : model.getEditableIdObjects()) {
			if (newSelection.contains(object.getPivotPoint())) {
				newlySelectedPivots.add(object);
			}
			object.apply(new IdObjectVisitor() {
				@Override
				public void ribbonEmitter(RibbonEmitter particleEmitter) {
				}

				@Override
				public void particleEmitter2(ParticleEmitter2 particleEmitter) {
				}

				@Override
				public void particleEmitter(ParticleEmitter particleEmitter) {
				}

				@Override
				public void popcornFxEmitter(ParticleEmitterPopcorn popcornFxEmitter) {
				}

				@Override
				public void light(Light light) {
				}

				@Override
				public void helper(Helper object) {
				}

				@Override
				public void eventObject(EventObject eventObject) {
				}

				@Override
				public void collisionShape(CollisionShape collisionShape) {
					for (Vec3 vertex : collisionShape.getVertices()) {
						if (newSelection.contains(vertex)) {
							newlySelectedPivots.add(collisionShape);
						}
					}
				}

				@Override
				public void camera(Camera camera) {
				}

				@Override
				public void bone(Bone object) {
				}

				@Override
				public void attachment(Attachment attachment) {
				}
			});
		}
		selectionManager.setSelection(newlySelectedPivots);
	}

	@Override
	protected List<IdObject> genericSelect(Rectangle2D region, CoordinateSystem coordinateSystem) {
		List<IdObject> selectedItems = new ArrayList<>();
		double startingClickX = region.getX();
		double startingClickY = region.getY();
		double endingClickX = region.getX() + region.getWidth();
		double endingClickY = region.getY() + region.getHeight();

		double minX = Math.min(startingClickX, endingClickX);
		double minY = Math.min(startingClickY, endingClickY);
		double maxX = Math.max(startingClickX, endingClickX);
		double maxY = Math.max(startingClickY, endingClickY);
		Rectangle2D area = new Rectangle2D.Double(minX, minY, (maxX - minX), (maxY - minY));
		IdObjectVisitor visitor = genericSelectorVisitor.reset(selectedItems, area, coordinateSystem);
		for (IdObject object : model.getEditableIdObjects()) {
			object.apply(visitor);
		}
		for (Camera camera : model.getEditableCameras()) {
			visitor.camera(camera);
		}
		return selectedItems;
	}

	@Override
	public boolean canSelectAt(Point point, CoordinateSystem axes) {
		IdObjectVisitor visitor = selectionAtPointTester.reset(axes, point);
		for (IdObject object : model.getEditableIdObjects()) {
			object.apply(visitor);
		}
		for (Camera camera : model.getEditableCameras()) {
			visitor.camera(camera);
		}
		return selectionAtPointTester.isMouseOverVertex();
	}

	@Override
	public UndoAction invertSelection() {
		throw new WrongModeException("Not supported in T-Pose mode");
		// List<Vertex> oldSelection = new
		// ArrayList<>(selectionManager.getSelection());
		// Set<Vertex> invertedSelection = new
		// HashSet<>(selectionManager.getSelection());
		// IdObjectVisitor visitor = new IdObjectVisitor() {
		// @Override
		// public void ribbonEmitter(RibbonEmitter particleEmitter) { toggleSelection(invertedSelection, particleEmitter.getPivotPoint()); }
		// @Override
		// public void particleEmitter2(ParticleEmitter2 particleEmitter) { toggleSelection(invertedSelection, particleEmitter.getPivotPoint()); }
		// @Override
		// public void particleEmitter(ParticleEmitter particleEmitter) { toggleSelection(invertedSelection, particleEmitter.getPivotPoint()); }
		// @Override
		// public void light(Light light) { toggleSelection(invertedSelection, light.getPivotPoint()); }
		// @Override
		// public void helper(Helper object) { toggleSelection(invertedSelection, object.getPivotPoint()); }
		// @Override
		// public void eventObject(EventObject eventObject) { toggleSelection(invertedSelection, eventObject.getPivotPoint()); }
		// @Override
		// public void collisionShape(CollisionShape collisionShape) {
		// toggleSelection(invertedSelection, collisionShape.getPivotPoint());
		// for (Vertex vertex : collisionShape.getVertices()) { toggleSelection(invertedSelection, vertex); } }
		// @Override
		// public void camera(Camera camera) {
		// toggleSelection(invertedSelection, camera.getPosition()); toggleSelection(invertedSelection, camera.getTargetPosition()); }
		// @Override
		// public void bone(Bone object) { toggleSelection(invertedSelection, object.getPivotPoint()); }
		// @Override
		// public void attachment(Attachment attachment) { toggleSelection(invertedSelection, attachment.getPivotPoint()); } };
		// for (IdObject node : model.getEditableIdObjects()) { node.apply(visitor); }
		// for (Camera object : model.getEditableCameras()) { visitor.camera(object); }
		// selectionManager.setSelection(invertedSelection);
		// return (new SetSelectionAction<>(invertedSelection, oldSelection,
		// selectionManager, "invert selection"));
	}

	@Override
	public UndoAction selectAll() {
		throw new WrongModeException("Not supported in T-Pose mode");
		// List<Vertex> oldSelection = new
		// ArrayList<>(selectionManager.getSelection());
		// Set<Vertex> allSelection = new HashSet<>();
		// IdObjectVisitor visitor = new IdObjectVisitor() {
		// @Override
		// public void ribbonEmitter(RibbonEmitter particleEmitter) { allSelection.add(particleEmitter.getPivotPoint());}
		// @Override
		// public void particleEmitter2(ParticleEmitter2 particleEmitter) { allSelection.add(particleEmitter.getPivotPoint());}
		// @Override
		// public void particleEmitter(ParticleEmitter particleEmitter) { allSelection.add(particleEmitter.getPivotPoint());}
		// @Override
		// public void light(Light light) { allSelection.add(light.getPivotPoint());}
		// @Override
		// public void helper(Helper object) { allSelection.add(object.getPivotPoint());}
		// @Override
		// public void eventObject(EventObject eventObject) { allSelection.add(eventObject.getPivotPoint());}
		// @Override
		// public void collisionShape(CollisionShape collisionShape) {
		// allSelection.add(collisionShape.getPivotPoint());
		// for (Vertex vertex : collisionShape.getVertices()) { allSelection.add(vertex);}}
		// @Override
		// public void camera(Camera camera) {
		// allSelection.add(camera.getPosition()); allSelection.add(camera.getTargetPosition());}
		// @Override
		// public void bone(Bone object) { allSelection.add(object.getPivotPoint());}
		// @Override
		// public void attachment(Attachment attachment) { allSelection.add(attachment.getPivotPoint());}};
		// for (IdObject node : model.getEditableIdObjects()) { node.apply(visitor);}
		// for (Camera object : model.getEditableCameras()) { visitor.camera(object);}
		// selectionManager.setSelection(allSelection);
		// return (new SetSelectionAction<>(allSelection, oldSelection,
		// selectionManager, "select all"));
	}

	@Override
	protected UndoAction buildHideComponentAction(List<? extends SelectableComponent> selectableComponents, EditabilityToggleHandler editabilityToggleHandler, Runnable refreshGUIRunnable) {
		List<IdObject> previousSelection = new ArrayList<>(selectionManager.getSelection());
		Runnable truncateSelectionRunnable = () -> selectionManager.removeSelection(model.getModel().getIdObjects());
		Runnable unTruncateSelectionRunnable = () -> selectionManager.setSelection(previousSelection);
		return new MakeNotEditableAction(editabilityToggleHandler, truncateSelectionRunnable, unTruncateSelectionRunnable, refreshGUIRunnable);
	}

	@Override
	public void rawScale(double centerX, double centerY, double centerZ, double scaleX, double scaleY, double scaleZ) {
		super.rawScale(centerX, centerY, centerZ, scaleX, scaleY, scaleZ);
		for (IdObject b : model.getEditableIdObjects()) {
			if (selectionManager.getSelection().contains(b.getPivotPoint())) {
				b.apply(new IdObjectVisitor() {
					@Override
					public void ribbonEmitter(RibbonEmitter particleEmitter) {
					}

					@Override
					public void particleEmitter2(ParticleEmitter2 particleEmitter) {
					}

					@Override
					public void particleEmitter(ParticleEmitter particleEmitter) {
					}

					@Override
					public void popcornFxEmitter(ParticleEmitterPopcorn popcornFxEmitter) {
					}

					@Override
					public void light(Light light) {
					}

					@Override
					public void helper(Helper object) {
						Vec3AnimFlag translation = (Vec3AnimFlag) object.find("Translation");
						if (translation != null) {
							for (int i = 0; i < translation.size(); i++) {
								Vec3 scaleData = translation.getValues().get(i);
								scaleData.scale(0, 0, 0, scaleX, scaleY, scaleZ);
								if (translation.tans()) {
									Vec3 inTanData = translation.getInTans().get(i);
									inTanData.scale(0, 0, 0, scaleX, scaleY, scaleZ);
									Vec3 outTanData = translation.getInTans().get(i);
									outTanData.scale(0, 0, 0, scaleX, scaleY, scaleZ);
								}
							}
						}
					}

					@Override
					public void eventObject(EventObject eventObject) {
					}

					@Override
					public void collisionShape(CollisionShape collisionShape) {
						ExtLog extents = collisionShape.getExtents();
						if ((extents != null) && (scaleX == scaleY) && (scaleY == scaleZ)) {
							extents.setBoundsRadius(extents.getBoundsRadius() * scaleX);
						}
					}

					@Override
					public void camera(Camera camera) {
					}

					@Override
					public void bone(Bone object) {
						Vec3AnimFlag translation = (Vec3AnimFlag) object.find("Translation");
						if (translation != null) {
							for (int i = 0; i < translation.size(); i++) {
								Vec3 scaleData = translation.getValues().get(i);
								scaleData.scale(0, 0, 0, scaleX, scaleY, scaleZ);
								if (translation.tans()) {
									Vec3 inTanData = translation.getInTans().get(i);
									inTanData.scale(0, 0, 0, scaleX, scaleY, scaleZ);
									Vec3 outTanData = translation.getInTans().get(i);
									outTanData.scale(0, 0, 0, scaleX, scaleY, scaleZ);
								}
							}
						}
					}

					@Override
					public void attachment(Attachment attachment) {
					}
				});
			}
		}
	}

	@Override
	public UndoAction deleteSelectedComponents() {
		List<IdObject> deletedIdObjects = new ArrayList<>();
		for (IdObject object : model.getEditableIdObjects()) {
			if (selectionManager.getSelection().contains(object.getPivotPoint())) {
				deletedIdObjects.add(object);
			}
		}
		List<Camera> deletedCameras = new ArrayList<>();
		for (Camera camera : model.getEditableCameras()) {
			if (selectionManager.getSelection().contains(camera.getPosition()) || selectionManager.getSelection().contains(camera.getTargetPosition())) {
				deletedCameras.add(camera);
			}
		}
		DeleteNodesAction deleteNodesAction = new DeleteNodesAction(selectionManager.getSelectedVertices(), deletedIdObjects, deletedCameras, structureChangeListener, model, vertexSelectionHelper);
		deleteNodesAction.redo();
		return deleteNodesAction;
	}

	@Override
	public CopiedModelData copySelection() {
		Collection<? extends Vec3> selection = selectionManager.getSelectedVertices();
		Set<IdObject> clonedNodes = new HashSet<>();
		Set<Camera> clonedCameras = new HashSet<>();
		for (IdObject b : model.getEditableIdObjects()) {
			if (selection.contains(b.getPivotPoint())) {
				clonedNodes.add(b.copy());
			}
		}
		for (IdObject obj : clonedNodes) {
			if (!clonedNodes.contains(obj.getParent())) {
				obj.setParent(null);
			}
		}
		for (Camera camera : model.getEditableCameras()) {
			if (selection.contains(camera.getTargetPosition()) || selection.contains(camera.getPosition())) {
				clonedCameras.add(camera);
			}
		}
		return new CopiedModelData(new ArrayList<>(), clonedNodes, clonedCameras);
	}

	@Override
	public UndoAction addVertex(double x, double y, double z, Vec3 preferredNormalFacingVector) {
		return new DoNothingAction("add vertex");
	}

	@Override
	public UndoAction createFaceFromSelection(Vec3 preferredFacingVector) {
		return new DoNothingAction("create face");
	}

	private class SelectionAtPointTester implements IdObjectVisitor {
		private CoordinateSystem axes;
		private Point point;
		private boolean mouseOverVertex;

		private SelectionAtPointTester reset(CoordinateSystem axes, Point point) {
			this.axes = axes;
			this.point = point;
			mouseOverVertex = false;
			return this;
		}

		@Override
		public void ribbonEmitter(RibbonEmitter particleEmitter) {
			handleDefaultNode(point, axes, particleEmitter);
		}

		private void handleDefaultNode(Point point, CoordinateSystem axes, IdObject node) {
			if (hitTest(node.getPivotPoint(), CoordinateSystem.Util.geom(axes, point), axes, node.getClickRadius(axes) * CoordinateSystem.Util.getZoom(axes) * 2)) {
				mouseOverVertex = true;
			}
		}

		@Override
		public void particleEmitter2(ParticleEmitter2 particleEmitter) {
			handleDefaultNode(point, axes, particleEmitter);
		}

		@Override
		public void particleEmitter(ParticleEmitter particleEmitter) {
			handleDefaultNode(point, axes, particleEmitter);
		}

		@Override
		public void popcornFxEmitter(ParticleEmitterPopcorn particleEmitter) {
			handleDefaultNode(point, axes, particleEmitter);
		}

		@Override
		public void light(Light light) {
			handleDefaultNode(point, axes, light);
		}

		@Override
		public void helper(Helper node) {
			if (hitTest(node.getPivotPoint(), CoordinateSystem.Util.geom(axes, point), axes, node.getClickRadius(axes) * CoordinateSystem.Util.getZoom(axes))) {
				mouseOverVertex = true;
			}
		}

		@Override
		public void eventObject(EventObject eventObject) {
			handleDefaultNode(point, axes, eventObject);
		}

		@Override
		public void collisionShape(CollisionShape collisionShape) {
			handleDefaultNode(point, axes, collisionShape);
			for (Vec3 vertex : collisionShape.getVertices()) {
				if (hitTest(vertex, CoordinateSystem.Util.geom(axes, point), axes, IdObject.DEFAULT_CLICK_RADIUS)) {
					mouseOverVertex = true;
				}
			}
		}

		@Override
		public void camera(Camera camera) {
			if (hitTest(camera.getPosition(), CoordinateSystem.Util.geom(axes, point), axes, programPreferences.getVertexSize())) {
				mouseOverVertex = true;
			}
			if (hitTest(camera.getTargetPosition(), CoordinateSystem.Util.geom(axes, point), axes, programPreferences.getVertexSize())) {
				mouseOverVertex = true;
			}
		}

		@Override
		public void bone(Bone node) {
			if (hitTest(node.getPivotPoint(), CoordinateSystem.Util.geom(axes, point), axes, node.getClickRadius(axes) * CoordinateSystem.Util.getZoom(axes))) {
				mouseOverVertex = true;
			}
		}

		@Override
		public void attachment(Attachment attachment) {
			handleDefaultNode(point, axes, attachment);
		}

		public boolean isMouseOverVertex() {
			return mouseOverVertex;
		}
	}

	private class GenericSelectorVisitor implements IdObjectVisitor {
		private List<IdObject> selectedItems;
		private Rectangle2D area;
		private CoordinateSystem coordinateSystem;

		private GenericSelectorVisitor reset(List<IdObject> selectedItems, Rectangle2D area, CoordinateSystem coordinateSystem) {
			this.selectedItems = selectedItems;
			this.area = area;
			this.coordinateSystem = coordinateSystem;
			return this;
		}

		@Override
		public void ribbonEmitter(RibbonEmitter particleEmitter) {
			hitTest(selectedItems, area, particleEmitter.getPivotPoint(), coordinateSystem, particleEmitter.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem) * 2, particleEmitter);
		}

		@Override
		public void particleEmitter2(ParticleEmitter2 particleEmitter) {
			hitTest(selectedItems, area, particleEmitter.getPivotPoint(), coordinateSystem, particleEmitter.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem) * 2, particleEmitter);
		}

		@Override
		public void particleEmitter(ParticleEmitter particleEmitter) {
			hitTest(selectedItems, area, particleEmitter.getPivotPoint(), coordinateSystem, particleEmitter.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem) * 2, particleEmitter);
		}

		@Override
		public void popcornFxEmitter(ParticleEmitterPopcorn particleEmitter) {
			hitTest(selectedItems, area, particleEmitter.getPivotPoint(), coordinateSystem, particleEmitter.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem) * 2, particleEmitter);
		}

		@Override
		public void light(Light light) {
			hitTest(selectedItems, area, light.getPivotPoint(), coordinateSystem, light.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem) * 2, light);
		}

		@Override
		public void helper(Helper object) {
			hitTest(selectedItems, area, object.getPivotPoint(), coordinateSystem, object.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem), object);
		}

		@Override
		public void eventObject(EventObject eventObject) {
			hitTest(selectedItems, area, eventObject.getPivotPoint(), coordinateSystem, eventObject.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem) * 2, eventObject);
		}

		@Override
		public void collisionShape(CollisionShape collisionShape) {
			hitTest(selectedItems, area, collisionShape.getPivotPoint(), coordinateSystem, collisionShape.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem) * 2, collisionShape);
			for (Vec3 vertex : collisionShape.getVertices()) {
				hitTest(selectedItems, area, vertex, coordinateSystem, IdObject.DEFAULT_CLICK_RADIUS, collisionShape);
			}
		}

		@Override
		public void camera(Camera camera) {
		}

		@Override
		public void bone(Bone object) {
			hitTest(selectedItems, area, object.getPivotPoint(), coordinateSystem, object.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem), object);
		}

		@Override
		public void attachment(Attachment attachment) {
			hitTest(selectedItems, area, attachment.getPivotPoint(), coordinateSystem, attachment.getClickRadius(coordinateSystem) * CoordinateSystem.Util.getZoom(coordinateSystem) * 2, attachment);
		}
	}

	public VertexSelectionHelper getVertexSelectionHelper() {
		return vertexSelectionHelper;
	}
}
