package com.hiveworkshop.rms.editor.actions.nodes;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.actions.editor.AbstractTransformAction;
import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.util.Mat4;
import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vec3;

import java.util.*;
import java.util.stream.Collectors;

public class TranslateNodesTPoseAction extends AbstractTransformAction {
	private final UndoAction addingTimelinesOrKeyframesAction;
	private final ModelStructureChangeListener changeListener;
	private final List<TranslateNodeTPoseAction> translNodeActions = new ArrayList<>();
	private final Vec3 translation = new Vec3();
	private final Map<GeosetVertex, Vec3[]> vertToLocNormTang = new HashMap<>();
	private final Map<GeosetVertex, Vec3[]> vertToOldLocNormTang = new HashMap<>();
	private final Map<IdObject, Mat4> nodeToWorldMat = new LinkedHashMap<>();
	private final Set<IdObject> topNodes = new LinkedHashSet<>();

	private final Mat4 invRotMat = new Mat4();
	private final Mat4 rotMat = new Mat4();

	public TranslateNodesTPoseAction(UndoAction addingTimelinesOrKeyframesAction,
	                                 Collection<IdObject> nodeSelection,
//	                               Collection<CameraNode> camSelection,
                                     RenderModel editorRenderModel,
                                     Vec3 translation,
                                     Mat4 rotMat,
                                     ModelStructureChangeListener changeListener){
		this.addingTimelinesOrKeyframesAction = addingTimelinesOrKeyframesAction;
		this.changeListener = changeListener;
		this.rotMat.set(rotMat);
		this.invRotMat.set(rotMat).invert();
		topNodes.addAll(getTopNodes(nodeSelection));

		fillTransformMap(topNodes);
		if(!translation.equalLocs(Vec3.ZERO)) {
			updateTransformMap(translation, Quat.IDENTITY);
		}

		fillVertexMaps(editorRenderModel.getModel().getGeosets());
		calcVertexLocs();
		for (IdObject node2 : nodeSelection) {
			translNodeActions.add(new TranslateNodeTPoseAction(node2, translation, rotMat, null));
		}
	}

	public TranslateNodesTPoseAction(UndoAction addingTimelinesOrKeyframesAction,
	                                 Collection<IdObject> nodeSelection,
	                                 Collection<CameraNode> camSelection,
	                                 RenderModel editorRenderModel,
	                                 Vec3 translation,
	                                 Mat4 rotMat){
		this.addingTimelinesOrKeyframesAction = addingTimelinesOrKeyframesAction;
		this.changeListener = null;
		this.rotMat.set(rotMat);
		this.invRotMat.set(rotMat).invert();
		topNodes.addAll(getTopNodes(nodeSelection));
		fillTransformMap(topNodes);
		if(!translation.equalLocs(Vec3.ZERO)) {
			updateTransformMap(translation, Quat.IDENTITY);
		}
		fillVertexMaps(editorRenderModel.getModel().getGeosets());
		calcVertexLocs();
		for (IdObject node2 : nodeSelection) {
			translNodeActions.add(new TranslateNodeTPoseAction(node2, translation, rotMat, null));
		}
	}

	private List<IdObject> getTopNodes(Collection<IdObject> selection) {
		return selection.stream()
				.filter(idObject -> idObject.getParent() == null || !selection.contains(idObject.getParent()))
				.collect(Collectors.toList());
	}

	private void fillTransformMap(Collection<IdObject> topNodes){
		for(IdObject node : topNodes){
//			System.out.println("topNode: " + node.getName());
			fillTransformMap(node);
		}
	}

	private void fillTransformMap(IdObject node){
		nodeToWorldMat.put(node, new Mat4());
		for(IdObject child : node.getChildrenNodes()){
			fillTransformMap(child);
		}
	}

	private void fillVertexMaps(Collection<Geoset> geosets) {
		for (Geoset geoset : geosets) {
			for (GeosetVertex vertex : geoset.getVertices()){
				for (Bone bone : vertex.getAllBones()){
					if(nodeToWorldMat.containsKey(bone)){
						Vec3 tang = vertex.getTang() == null ? new Vec3(Vec3.Z_AXIS) : vertex.getTang().getVec3();
						Vec3[] oldLocNormTan = new Vec3[] {new Vec3(vertex), new Vec3(vertex.getNormal()), tang};
						vertToOldLocNormTang.put(vertex, oldLocNormTan);

						Vec3[] locNormTan = new Vec3[] {new Vec3(vertex), new Vec3(vertex.getNormal()), new Vec3(tang)};
						vertToLocNormTang.put(vertex, locNormTan);
						break;
					}
				}
			}
		}
	}
	public TranslateNodesTPoseAction doSetup() {
		if(addingTimelinesOrKeyframesAction != null){
			addingTimelinesOrKeyframesAction.redo();
		}
		calcAndApplyVertexLocs();
		for(TranslateNodeTPoseAction action : translNodeActions){
			action.doSetup();
		}
		return this;
	}

	public TranslateNodesTPoseAction updateTranslation(Vec3 delta){
		updateTransformMap(delta, Quat.IDENTITY);
		calcAndApplyVertexLocs();
		for(TranslateNodeTPoseAction action : translNodeActions){
			action.updateTranslation(delta);
		}
		return this;
	}
//	public TranslateNodesTPoseAction setTranslation(Vec3 delta) {
//		double rotDiff = radians - this.radians;
//		this.radians = radians;
//		updateTransformMap(-rotDiff);
//		calcAndApplyVertexLocs();
//		for(TranslateNodeTPoseAction action : translNodeActions){
//			action.updateRotation(rotDiff);
//		}
//		return this;
//	}

	@Override
	public TranslateNodesTPoseAction undo() {
		for (GeosetVertex vertex : vertToOldLocNormTang.keySet()){
			Vec3[] vec3s = vertToOldLocNormTang.get(vertex);
			vertex.set(vec3s[0]);
			vertex.setNormal(vec3s[1]);
			if(vertex.getTangent() != null){
				vertex.getTangent().set(vec3s[2]);
			}
		}
		for(TranslateNodeTPoseAction action : translNodeActions){
			action.undo();
		}
		if(addingTimelinesOrKeyframesAction != null){
			addingTimelinesOrKeyframesAction.undo();
		}
		if(changeListener != null){
			changeListener.nodesUpdated();
		}
		return this;
	}

	@Override
	public TranslateNodesTPoseAction redo() {
		if(addingTimelinesOrKeyframesAction != null){
			addingTimelinesOrKeyframesAction.redo();
		}
		for(TranslateNodeTPoseAction action : translNodeActions){
			action.redo();
		}

		for (GeosetVertex vertex : vertToLocNormTang.keySet()){
			Vec3[] vec3s = vertToLocNormTang.get(vertex);
			vertex.set(vec3s[0]);
			vertex.setNormal(vec3s[1]);
			if(vertex.getTangent() != null){
				vertex.getTangent().set(vec3s[2]);
			}
		}
		if(changeListener != null){
			changeListener.nodesUpdated();
		}
		return this;
	}

	@Override
	public String actionName() {
		return "Rotate " + "node.getName()";
	}



	Mat4 locMat = new Mat4();
	private void updateTransformMap(Vec3 transl, Quat rot){
		for(IdObject node : topNodes){
			this.translation.set(setTranslationHeap(node, transl));
			updateTransform(node, translation, rot);
		}
	}
	private void updateTransform(IdObject node, Vec3 delta, Quat rot){
		Mat4 parentMat = nodeToWorldMat.getOrDefault(node.getParent(), Mat4.IDENTITY);
		locMat.fromRotationTranslationScaleOrigin(rot, delta, Vec3.ONE, node.getPivotPoint());
		nodeToWorldMat.get(node).set(parentMat).mul(locMat);
		for(IdObject child : node.getChildrenNodes()){
			updateTransform(child, Vec3.ZERO, Quat.IDENTITY);
		}
	}

	private void calcVertexLocs() {
		for (GeosetVertex vertex : vertToLocNormTang.keySet()){
			Vec3[] locNormTan = vertToLocNormTang.get(vertex);
			Vec3[] ogrLocNormTan = vertToOldLocNormTang.get(vertex);
			update(getTransform(vertex), ogrLocNormTan, locNormTan);
//			update(getTransform(vertex), locNormTan);
		}
	}
	private void calcAndApplyVertexLocs() {
		for (GeosetVertex vertex : vertToLocNormTang.keySet()){
			Vec3[] locNormTan = vertToLocNormTang.get(vertex);
			Vec3[] ogrLocNormTan = vertToOldLocNormTang.get(vertex);
			update(getTransform(vertex), ogrLocNormTan, locNormTan);
//			update(getTransform(vertex), locNormTan);

			applyVertTransform(vertex, locNormTan);
		}
	}

	private void applyVertTransform(GeosetVertex vertex, Vec3[] locNormTan) {
		vertex.set(locNormTan[0]);
		vertex.setNormal(locNormTan[1]);
		if(vertex.getTangent() != null){
			vertex.getTangent().set(locNormTan[2]);
		}
	}
	private void update(Mat4 mat4, Vec3[] orgLocNormTan, Vec3[] locNormTan) {
		locNormTan[0].set(orgLocNormTan[0]);
		locNormTan[1].set(orgLocNormTan[1]);
		locNormTan[2].set(orgLocNormTan[2]);
		if (mat4 != null) {
			locNormTan[0].transform(mat4);
			locNormTan[1].transform(0, mat4).normalize();
			locNormTan[2].transform(0, mat4).normalize();
		}
	}
	private void update(Mat4 mat4, Vec3[] locNormTan) {
		if (mat4 != null) {
			locNormTan[0].transform(mat4);
			locNormTan[1].transform(0, mat4).normalize();
			locNormTan[2].transform(0, mat4).normalize();
		}
	}

	private Mat4 getTransform(GeosetVertex vertex) {
		if (vertex.getSkinBones() != null) {
			return processHdBones(vertex.getSkinBones());
		} else {
			return processSdBones(vertex.getMatrix().getBones());
		}
	}

	Mat4 matrixSumHeap = new Mat4();
	public Mat4 processHdBones(SkinBone[] skinBones) {
		boolean foundValidBones = false;
		matrixSumHeap.setZero();

		for (int boneIndex = 0; boneIndex < 4; boneIndex++) {
			SkinBone skinBone = skinBones[boneIndex];
			if (skinBone != null && skinBone.getBone() != null) {
				Bone bone = skinBone.getBone();
				foundValidBones = true;
				Mat4 worldMatrix = nodeToWorldMat.getOrDefault(bone, Mat4.IDENTITY);
				matrixSumHeap.addScaled(worldMatrix, skinBone.getWeightFraction());
			}
		}
		if (!foundValidBones) {
			matrixSumHeap.setIdentity();
		}
		return matrixSumHeap;
	}

	public Mat4 processSdBones(List<Bone> bones) {
		matrixSumHeap.setZero();
		if (bones.size() > 0) {
			for (Bone bone : bones) {
				Mat4 worldMatrix = nodeToWorldMat.getOrDefault(bone, Mat4.IDENTITY);
				matrixSumHeap.add(worldMatrix);
			}
			return matrixSumHeap.uniformScale(1f / bones.size());
		}
		return matrixSumHeap.setIdentity();
	}


	Vec3 tempVec = new Vec3();

	private Vec3 setTranslationHeap(IdObject idObject, Vec3 newDelta) {
		tempVec.set(idObject.getPivotPoint())
				.transform(rotMat, 1, true)
				.add(newDelta)
				.transform(invRotMat, 1, true)
				.sub(idObject.getPivotPoint());

		return tempVec;
	}
}
