package com.hiveworkshop.rms.editor.model.util.ModelSaving;


import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeoset;
import com.hiveworkshop.rms.parsers.mdlx.MdlxGeosetAnimation;
import com.hiveworkshop.rms.util.Vec2;
import com.hiveworkshop.rms.util.Vec3;

public class GeosetToMdlx {

	public static MdlxGeoset toMdlx(Geoset geoset, EditableModel model) {
		final MdlxGeoset mdlxGeoset = new MdlxGeoset();

		if (geoset.getExtents() != null) {
			mdlxGeoset.extent = geoset.getExtents().toMdlx();
		}

		for (Animation anim : model.getAnims()) {
			if(geoset.getAnimExtent(anim) != null){
				mdlxGeoset.sequenceExtents.add(geoset.getAnimExtent(anim).toMdlx());
			}
		}

		mdlxGeoset.materialId = model.computeMaterialID(geoset.getMaterial());
		if (mdlxGeoset.materialId == -1) {
			mdlxGeoset.materialId = 0;
		}

		final int numVertices = geoset.getVertices().size();
		final int nrOfTextureVertexGroups = geoset.numUVLayers();

		mdlxGeoset.vertices = new float[numVertices * 3];
		mdlxGeoset.normals = new float[numVertices * 3];

		mdlxGeoset.vertexGroups = new short[numVertices];
		mdlxGeoset.uvSets = new float[nrOfTextureVertexGroups][numVertices * 2];

		for (int vId = 0; vId < numVertices; vId++) {
			final GeosetVertex vertex = geoset.getVertex(vId);
			if(!vertex.isValid()){
				vertex.set(0,0,0);
			}

			mdlxGeoset.vertices[(vId * 3) + 0] = vertex.x;
			mdlxGeoset.vertices[(vId * 3) + 1] = vertex.y;
			mdlxGeoset.vertices[(vId * 3) + 2] = vertex.z;

			final Vec3 norm = vertex.getNormal();

			mdlxGeoset.normals[(vId * 3) + 0] = norm.x;
			mdlxGeoset.normals[(vId * 3) + 1] = norm.y;
			mdlxGeoset.normals[(vId * 3) + 2] = norm.z;

			for (int uvLayerIndex = 0; uvLayerIndex < nrOfTextureVertexGroups; uvLayerIndex++) {
				final Vec2 uv = vertex.getTVertex(uvLayerIndex);

				mdlxGeoset.uvSets[uvLayerIndex][(vId * 2) + 0] = uv.x;
				mdlxGeoset.uvSets[uvLayerIndex][(vId * 2) + 1] = uv.y;
			}

//			mdlxGeoset.vertexGroups[vId] = (byte) vertex.getVertexGroup();
//			mdlxGeoset.vertexGroups[vId] = (short) (geoset.getMatrixId(vertex.getMatrix())%256);
			mdlxGeoset.vertexGroups[vId] = (short) (geoset.getMatrixId(vertex.getMatrix()));
		}

		// Again, the current implementation of my mdl code is that it only handles triangle face types
		// (there's another note about this in the MDX -> MDL mdlxGeoset code)
		mdlxGeoset.faceGroups = new long[] {geoset.getTriangles().size() * 3L};
		mdlxGeoset.faceTypeGroups = new long[] {4}; // triangles!
		mdlxGeoset.faces = new int[geoset.getTriangles().size() * 3]; // triangles!

		int faceIndex = 0;
		for (final Triangle tri : geoset.getTriangles()) {
			for (int v = 0; v < /* tri.size() */3; v++) {
				mdlxGeoset.faces[faceIndex++] = tri.getId(v);
				if(tri.getId(v) < 0){
					mdlxGeoset.faces[faceIndex-1] = 0;
				}
			}
		}

		if (geoset.getUnselectable()) {
			mdlxGeoset.selectionFlags = 4;
		}

		mdlxGeoset.selectionGroup = geoset.getSelectionGroup();

		mdlxGeoset.matrixIndices = new long[getMatrixIndexesSize(geoset)];
		mdlxGeoset.matrixGroups = new long[geoset.getMatrices().size()];
		int matrixIndex = 0;
		int groupIndex = 0;
		for (final Matrix matrix : geoset.getMatrices()) {
			for (int index = 0; index < matrix.size() && matrixIndex < mdlxGeoset.matrixIndices.length; index++) {
				mdlxGeoset.matrixIndices[matrixIndex++] = model.getObjectId(matrix.get(index));
			}
			if (matrix.size() <= 0) {
				mdlxGeoset.matrixIndices = new long[1];
				mdlxGeoset.matrixIndices[matrixIndex++] = -1;
			}
			int size = matrix.size();
			if (size == -1) {
				size = 1;
			}
			mdlxGeoset.matrixGroups[groupIndex++] = size;
		}

		mdlxGeoset.lod = geoset.getLevelOfDetail();
		mdlxGeoset.lodName = geoset.getLevelOfDetailName();

		if ((numVertices > 0) && (geoset.getVertex(0).getSkinBoneBones() != null)) {
			// v900
			mdlxGeoset.vertexGroups = new short[0];
			mdlxGeoset.skin = new short[8 * numVertices];
			mdlxGeoset.tangents = new float[4 * numVertices];

			for (int i = 0; i < numVertices; i++) {
				final GeosetVertex vertex = geoset.getVertex(i);
				short[] skinBoneWeights = vertex.getSkinBoneWeights();
				Bone[] skinBoneBones = vertex.getSkinBoneBones();
				for (int j = 0; j < 4; j++) {
//					int index = skinBoneBones[j] == null ? 0 : skinBoneBones[j].getObjectId(model);
					int index = skinBoneBones[j] == null ? 0 : model.getObjectId(skinBoneBones[j]);
					mdlxGeoset.skin[(i * 8) + j] = (short) index;
					mdlxGeoset.skin[(i * 8) + j + 4] = (short) (skinBoneWeights[j]);
					mdlxGeoset.tangents[(i * 4) + j] = vertex.getTangent().toFloatArray()[j];
				}
			}
		} else {
			System.out.println("no skin bones! :O");
		}

		return mdlxGeoset;
	}

	public static int getMatrixIndexesSize(Geoset geoset) {
		int matrixIndexesSize = 0;
		for (final Matrix matrix : geoset.getMatrices()) {
			int size = matrix.size();
			if (size == -1) {
				size = 1;
			}
			matrixIndexesSize += size;
		}

		if (matrixIndexesSize == -1) {
			matrixIndexesSize = 1;
		}
		return matrixIndexesSize;
	}

	public static MdlxGeosetAnimation toMdlx(GeosetAnim geosetAnim, EditableModel model) {
		MdlxGeosetAnimation animation = new MdlxGeosetAnimation();

		animation.geosetId = model.computeGeosetID(geosetAnim.getGeoset());

		if (geosetAnim.isDropShadow()) {
			animation.flags |= 1;
		}
		if (geosetAnim.find("Color") != null || !geosetAnim.getStaticColor().equals(new Vec3(1, 1, 1))) {
			animation.flags |= 0x2;
		}

//		animation.color = ModelUtils.flipRGBtoBGR(geosetAnim.getStaticColor().toFloatArray());
		animation.color = geosetAnim.getStaticColor().toFloatArray();

		geosetAnim.timelinesToMdlx(animation, model);

		return animation;
	}
}
