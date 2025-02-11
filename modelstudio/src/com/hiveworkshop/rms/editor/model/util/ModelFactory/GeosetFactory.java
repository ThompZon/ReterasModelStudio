package com.hiveworkshop.rms.editor.model.util.ModelFactory;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

import java.util.ArrayList;
import java.util.List;

public class GeosetFactory {
	public static Geoset createGeoset(MdlxGeoset mdlxGeoset, ModelInfoHolder infoHolder, EditableModel model) {
		Geoset geoset = new Geoset();
		geoset.setExtLog(new ExtLog(mdlxGeoset.extent));

		for (int i = 0; i < mdlxGeoset.sequenceExtents.size() && i<model.getAnimsSize(); i++) {
			ExtLog extents = new ExtLog(mdlxGeoset.sequenceExtents.get(i));
			geoset.add(model.getAnim(i), extents);
		}

		geoset.setMaterial(infoHolder.materials.get((int) mdlxGeoset.materialId));


		if (mdlxGeoset.selectionFlags == 4) {
			geoset.setUnselectable(true);
		}

		geoset.setSelectionGroup((int) mdlxGeoset.selectionGroup);
		geoset.setLevelOfDetail(mdlxGeoset.lod);
		geoset.setLevelOfDetailName(mdlxGeoset.lodName);

		int index = 0;
		for (long size : mdlxGeoset.matrixGroups) {
			Matrix m = new Matrix();
			for (int i = 0; i < size; i++) {
				m.add((Bone) infoHolder.idObjMap.get((int) mdlxGeoset.matrixIndices[index]));
//				m.addId((int) mdlxGeoset.matrixIndices[index]);
				index++;
			}
			geoset.addMatrix(m);
		}


		final short[] vertexGroups = mdlxGeoset.vertexGroups;
		final float[] vertices = mdlxGeoset.vertices;
		final float[] normals = mdlxGeoset.normals;
		final float[][] uvSets = mdlxGeoset.uvSets;

		final float[] tangents = mdlxGeoset.tangents;
		final short[] skin = mdlxGeoset.skin;
		ArrayList<short[]> skinList = new ArrayList<>();
		ArrayList<float[]> tangentList = new ArrayList<>();

		List<GeosetVertex> vertexList = new ArrayList<>();
		List<Triangle> triangleList = new ArrayList<>();

		for (int i = 0; i < vertices.length / 3; i++) {
			GeosetVertex gv = new GeosetVertex(vertices[(i * 3)], vertices[(i * 3) + 1], vertices[(i * 3) + 2]);
			vertexList.add(gv);
			gv.setGeoset(geoset);

			geoset.add(gv);

//			if (vertexGroups == null || i >= vertexGroups.length) {
//				gv.setVertexGroup(-1);
//			} else {
//				gv.setVertexGroup((256 + vertexGroups[i]) % 256);
//				Matrix matrix = geoset.getMatrix((256 + vertexGroups[i]) % 256);
//				if (matrix != null) {
//					for (Bone bone : matrix.getBones()) {
//						gv.addBoneAttachment(bone);
//					}
//				}
//			}
			if (vertexGroups != null && i < vertexGroups.length) {
				Matrix matrix = geoset.getMatrix((256 + vertexGroups[i]) % 256);
				if (matrix != null) {
					for (Bone bone : matrix.getBones()) {
						gv.addBoneAttachment(bone);
					}
				}
			}
			// this is an unsigned byte, the other guys java code will read as signed
			if (normals.length > 0) {
				gv.setNormal(new Vec3(normals[(i * 3)], normals[(i * 3) + 1], normals[(i * 3) + 2]));
			}

			for (float[] uvSet : uvSets) {
				gv.addTVertex(new Vec2(uvSet[(i * 2)], uvSet[(i * 2) + 1]));
			}


//			if (ModelUtils.isTangentAndSkinSupported(model.getFormatVersion()) && tangents != null && (i*4 + 3) < tangents.length) {
			if (infoHolder.isTangentAndSkinSupported() && tangents != null) {
				// version 900
				float[] tang = {tangents[(i * 4)], tangents[(i * 4) + 1], tangents[(i * 4) + 2], tangents[(i * 4) + 3]};
				gv.initV900();
				gv.setTangent(tang);

				Bone[] bones = {(Bone) infoHolder.idObjMap.get(((skin[(i * 8)] + 256) % 256)),
						(Bone) infoHolder.idObjMap.get(((skin[(i * 8) + 1] + 256) % 256)),
						(Bone) infoHolder.idObjMap.get(((skin[(i * 8) + 2] + 256) % 256)),
						(Bone) infoHolder.idObjMap.get(((skin[(i * 8) + 3] + 256) % 256))};

//				short[] weights = {skin[(i * 8) + 4], skin[(i * 8) + 5], skin[(i * 8) + 6], skin[(i * 8) + 7]};
				short[] weights = {
						(short)((skin[(i * 8) + 4] + 256) % 256),
						(short)((skin[(i * 8) + 5] + 256) % 256),
						(short)((skin[(i * 8) + 6] + 256) % 256),
						(short)((skin[(i * 8) + 7] + 256) % 256)};

				gv.setSkinBones(bones, weights);

				tangentList.add(tang);
				skinList.add(new short[] {skin[(i * 8)], skin[(i * 8) + 1], skin[(i * 8) + 2], skin[(i * 8) + 3], skin[(i * 8) + 4], skin[(i * 8) + 5], skin[(i * 8) + 6], skin[(i * 8) + 7]});

			}

//			if (!(gv.getVertexGroup() == -1 && infoHolder.isTangentAndSkinSupported())) {
//				Matrix matrix = geoset.getMatrix(gv.getVertexGroup());
//				if (matrix != null) {
//					for (Bone bone : matrix.getBones()) {
//						gv.addBoneAttachment(bone);
//					}
//				}
//			}

		}
		geoset.setTangents(tangentList);
//		geoset.setSkin(skinList);
		// guys I didn't code this to allow experimental non-triangle faces that were suggested to exist
		// on the web (i.e. quads). if you wanted to fix that, you'd want to do it below
		final int[] facesVertIndices = mdlxGeoset.faces;

		for (int i = 0; i < facesVertIndices.length; i += 3) {
//			Triangle triangle = new Triangle(facesVertIndices[i], facesVertIndices[i + 1], facesVertIndices[i + 2], geoset);
			if(facesVertIndices[i] < 0 || facesVertIndices[i + 1] < 0 || facesVertIndices[i + 2] < 0 ||
					facesVertIndices[i] > vertexList.size() || facesVertIndices[i + 1] > vertexList.size() || facesVertIndices[i + 2] > vertexList.size()){
				continue;
			}
			Triangle triangle = new Triangle(
					vertexList.get(facesVertIndices[i]),
					vertexList.get(facesVertIndices[i + 1]),
					vertexList.get(facesVertIndices[i + 2]),
					geoset);
			triangleList.add(triangle);
			geoset.add(triangle);
		}

		return geoset;
	}
}
