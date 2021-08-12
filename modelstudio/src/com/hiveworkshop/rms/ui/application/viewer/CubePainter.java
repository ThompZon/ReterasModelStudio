package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.GeosetVertex;
import com.hiveworkshop.rms.editor.model.IdObject;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.render3d.RenderNode;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.renderers.renderparts.RenderGeoset;
import com.hiveworkshop.rms.util.Mat4;
import com.hiveworkshop.rms.util.Quat;
import com.hiveworkshop.rms.util.Vec3;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class CubePainter {
	static final Vec3 xAxis = new Vec3(1, 0, 0);
	static final Vec3 xAxis_isolate = new Vec3(0, 1, 1);
	static final Vec3 yAxis = new Vec3(0, 1, 0);
	static final Vec3 yAxis_isolate = new Vec3(1, 0, 1);
	static final Vec3 zAxis = new Vec3(0, 0, 1);
	static final Vec3 zAxis_isolate = new Vec3(1, 1, 0);
	static double A90 = (Math.PI/2.0);

	public static void paintVertCubes(ModelView modelView, RenderModel renderModel, Geoset geo) {

//		glBegin(GL11.GL_TRIANGLES);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glBegin(GL_QUADS);
		RenderGeoset renderGeoset = renderModel.getRenderGeoset(geo);
		float boxRad = .5f;
		float frnt = 1;
		float left = 1;
		float uppp = 1;
		float back = -frnt;
		float rght = -left;
		float down = -uppp;

		Vec3 upBackRght_adj = new Vec3(frnt * boxRad, rght * boxRad, uppp * boxRad);
		Vec3 dwBackRght_adj = new Vec3(frnt * boxRad, rght * boxRad, down * boxRad);
		Vec3 upFrntRght_adj = new Vec3(back * boxRad, rght * boxRad, uppp * boxRad);
		Vec3 dwFrntRght_adj = new Vec3(back * boxRad, rght * boxRad, down * boxRad);
		Vec3 upBackLeft_adj = new Vec3(frnt * boxRad, left * boxRad, uppp * boxRad);
		Vec3 dwBackLeft_adj = new Vec3(frnt * boxRad, left * boxRad, down * boxRad);
		Vec3 upFrntLeft_adj = new Vec3(back * boxRad, left * boxRad, uppp * boxRad);
		Vec3 dwFrntLeft_adj = new Vec3(back * boxRad, left * boxRad, down * boxRad);

		// uppp (0,0, 1)
		// down (0,0,-1)
		// back (-1,0,0)
		// frnt ( 1,0,0)
		// left (0, 1,0)
		// rght (0,-1,0)

		Vec3 upFrntRght = new Vec3(0, 0, 0);
		Vec3 upBackRght = new Vec3(0, 0, 0);
		Vec3 dwFrntRght = new Vec3(0, 0, 0);
		Vec3 dwBackRght = new Vec3(0, 0, 0);
		Vec3 upFrntLeft = new Vec3(0, 0, 0);
		Vec3 upBackLeft = new Vec3(0, 0, 0);
		Vec3 dwFrntLeft = new Vec3(0, 0, 0);
		Vec3 dwBackLeft = new Vec3(0, 0, 0);
		if (renderGeoset != null) {
			for (GeosetVertex vertex : geo.getVertices()) {
				if (modelView.isSelected(vertex)) {
					glColor4f(1f, .0f, .0f, .7f);
				} else {
					glColor4f(.5f, .3f, .7f, .7f);
				}
				RenderGeoset.RenderVert renderVert = renderGeoset.getRenderVert(vertex);
				if (renderVert != null) {
					Vec3 renderPos = renderVert.getRenderPos();

					upBackRght.set(renderPos).add(upBackRght_adj);
					upBackLeft.set(renderPos).add(upBackLeft_adj);
					upFrntRght.set(renderPos).add(upFrntRght_adj);
					upFrntLeft.set(renderPos).add(upFrntLeft_adj);
					dwBackRght.set(renderPos).add(dwBackRght_adj);
					dwBackLeft.set(renderPos).add(dwBackLeft_adj);
					dwFrntRght.set(renderPos).add(dwFrntRght_adj);
					dwFrntLeft.set(renderPos).add(dwFrntLeft_adj);


//				//Up
					GL11.glNormal3f(0, uppp, 0);
					doGlQuad(upBackRght, upBackLeft, upFrntRght, upFrntLeft);
//
//				//Down
					GL11.glNormal3f(0, down, 0);
					doGlQuad(dwFrntRght, dwFrntLeft, dwBackRght, dwBackLeft);
//
//				glColor4f(.7f, .7f, .0f, .7f);
//				//Back
					GL11.glNormal3f(0, 0, back);
					doGlQuad(upFrntRght, upFrntLeft, dwFrntRght, dwFrntLeft);
//
//				glColor4f(.0f, .7f, .7f, .7f);
//				//Front
					GL11.glNormal3f(0, 0, frnt);
					doGlQuad(dwBackRght, dwBackLeft, upBackRght, upBackLeft);
//
//				glColor4f(.7f, .0f, .0f, .7f);
//				//Right
					GL11.glNormal3f(rght, 0, 0);
					doGlQuad(dwFrntRght, dwBackRght, upFrntRght, upBackRght);
//
//				glColor4f(0.f, .7f, .0f, .7f);
//				//Left
					GL11.glNormal3f(left, 0, 0);
					doGlQuad(upFrntLeft, upBackLeft, dwFrntLeft, dwBackLeft);

					// uppp (0,0, 1)
					// down (0,0,-1)
					// back (-1,0,0)
					// frnt ( 1,0,0)
					// left (0, 1,0)
					// rght (0,-1,0)
				}
			}
		}
		glEnd();
	}

	public static void paintVertCubes3(Vec3 vec2, CameraHandler cameraHandler) {

//		glBegin(GL11.GL_TRIANGLES);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glBegin(GL_QUADS);
		float boxRadLength = 100f;
		float boxRadHeight = 5f;
		float boxRadWidth = 2f;
		float frnt = 1;
		float left = 1;
		float uppp = 1;
		float back = -frnt;
		float rght = -left;
		float down = -uppp;

		Vec3 upBackRght_adj = new Vec3(frnt * boxRadLength, rght * boxRadWidth, uppp * boxRadHeight);
		Vec3 dwBackRght_adj = new Vec3(frnt * boxRadLength, rght * boxRadWidth, down * boxRadHeight);
		Vec3 upFrntRght_adj = new Vec3(back * boxRadLength, rght * boxRadWidth, uppp * boxRadHeight);
		Vec3 dwFrntRght_adj = new Vec3(back * boxRadLength, rght * boxRadWidth, down * boxRadHeight);
		Vec3 upBackLeft_adj = new Vec3(frnt * boxRadLength, left * boxRadWidth, uppp * boxRadHeight);
		Vec3 dwBackLeft_adj = new Vec3(frnt * boxRadLength, left * boxRadWidth, down * boxRadHeight);
		Vec3 upFrntLeft_adj = new Vec3(back * boxRadLength, left * boxRadWidth, uppp * boxRadHeight);
		Vec3 dwFrntLeft_adj = new Vec3(back * boxRadLength, left * boxRadWidth, down * boxRadHeight);

		// uppp (0,0, 1)
		// down (0,0,-1)
		// back (-1,0,0)
		// frnt ( 1,0,0)
		// left (0, 1,0)
		// rght (0,-1,0)

		Vec3 upFrntRght = new Vec3(0, 0, 0);
		Vec3 upBackRght = new Vec3(0, 0, 0);
		Vec3 dwFrntRght = new Vec3(0, 0, 0);
		Vec3 dwBackRght = new Vec3(0, 0, 0);
		Vec3 upFrntLeft = new Vec3(0, 0, 0);
		Vec3 upBackLeft = new Vec3(0, 0, 0);
		Vec3 dwFrntLeft = new Vec3(0, 0, 0);
		Vec3 dwBackLeft = new Vec3(0, 0, 0);


		glColor4f(1f, .2f, 1f, .7f);


		Vec3 renderPos = new Vec3(vec2.x, vec2.y, vec2.z);

//		upBackRght.set(upBackRght_adj).add(renderPos);
//		upBackLeft.set(upBackLeft_adj).add(renderPos);
//		upFrntRght.set(upFrntRght_adj).add(renderPos);
//		upFrntLeft.set(upFrntLeft_adj).add(renderPos);
//		dwBackRght.set(dwBackRght_adj).add(renderPos);
//		dwBackLeft.set(dwBackLeft_adj).add(renderPos);
//		dwFrntRght.set(dwFrntRght_adj).add(renderPos);
//		dwFrntLeft.set(dwFrntLeft_adj).add(renderPos);

		upBackRght.set(upBackRght_adj).transform(cameraHandler.getViewPortAntiRotMat2()).add(renderPos);
		upBackLeft.set(upBackLeft_adj).transform(cameraHandler.getViewPortAntiRotMat2()).add(renderPos);
		upFrntRght.set(upFrntRght_adj).transform(cameraHandler.getViewPortAntiRotMat2()).add(renderPos);
		upFrntLeft.set(upFrntLeft_adj).transform(cameraHandler.getViewPortAntiRotMat2()).add(renderPos);
		dwBackRght.set(dwBackRght_adj).transform(cameraHandler.getViewPortAntiRotMat2()).add(renderPos);
		dwBackLeft.set(dwBackLeft_adj).transform(cameraHandler.getViewPortAntiRotMat2()).add(renderPos);
		dwFrntRght.set(dwFrntRght_adj).transform(cameraHandler.getViewPortAntiRotMat2()).add(renderPos);
		dwFrntLeft.set(dwFrntLeft_adj).transform(cameraHandler.getViewPortAntiRotMat2()).add(renderPos);


//				//Top
		GL11.glNormal3f(0, uppp, 0);
		doGlQuad(upBackRght, upBackLeft, upFrntRght, upFrntLeft);
//
//				//Bottom
		GL11.glNormal3f(0, down, 0);
		doGlQuad(dwFrntRght, dwFrntLeft, dwBackRght, dwBackLeft);
//
//				glColor4f(.7f, .7f, .0f, .7f);
//				//South
		GL11.glNormal3f(0, 0, back);
		doGlQuad(upFrntRght, upFrntLeft, dwFrntRght, dwFrntLeft);
//
//				glColor4f(.0f, .7f, .7f, .7f);
//				//North
		GL11.glNormal3f(0, 0, frnt);
		doGlQuad(dwBackRght, dwBackLeft, upBackRght, upBackLeft);
//
//				glColor4f(.7f, .0f, .0f, .7f);
//				//West
		GL11.glNormal3f(rght, 0, 0);
		doGlQuad(dwFrntRght, dwBackRght, upFrntRght, upBackRght);
//
//				glColor4f(0.f, .7f, .0f, .7f);
//				//East
		GL11.glNormal3f(left, 0, 0);
		doGlQuad(upFrntLeft, upBackLeft, dwFrntLeft, dwBackLeft);

		// uppp (0,0, 1)
		// down (0,0,-1)
		// back (-1,0,0)
		// frnt ( 1,0,0)
		// left (0, 1,0)
		// rght (0,-1,0)
		glEnd();
	}


	public static void paintRekt(Vec3 start, Vec3 end1, Vec3 end2, Vec3 end3, CameraHandler cameraHandler) {

//		glBegin(GL11.GL_TRIANGLES);
//		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glBegin(GL11.GL_LINES);

		float frnt = 1;

		glColor4f(1f, .2f, 1f, .7f);

		GL11.glNormal3f(0, frnt, 0);
		doGlQuad(start, end1, end2, end1);

		doGlQuad(end2, end3, start, end3);

		glEnd();
	}

	public static void paintVertCubes2(ModelView modelView, RenderModel renderModel, Geoset geo, CameraHandler cameraHandler) {

//		glBegin(GL11.GL_TRIANGLES);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glBegin(GL_QUADS);
		RenderGeoset renderGeoset = renderModel.getRenderGeoset(geo);
		float boxRad = .5f;
		float frnt = 1;
		float left = 1;
		float uppp = 1;
		float back = -frnt;
		float rght = -left;
		float down = -uppp;


		Vec3 upBackRght_adj = new Vec3(frnt * boxRad, rght * boxRad, uppp * boxRad);
		Vec3 dwBackRght_adj = new Vec3(frnt * boxRad, rght * boxRad, down * boxRad);
		Vec3 upFrntRght_adj = new Vec3(back * boxRad, rght * boxRad, uppp * boxRad);
		Vec3 dwFrntRght_adj = new Vec3(back * boxRad, rght * boxRad, down * boxRad);
		Vec3 upBackLeft_adj = new Vec3(frnt * boxRad, left * boxRad, uppp * boxRad);
		Vec3 dwBackLeft_adj = new Vec3(frnt * boxRad, left * boxRad, down * boxRad);
		Vec3 upFrntLeft_adj = new Vec3(back * boxRad, left * boxRad, uppp * boxRad);
		Vec3 dwFrntLeft_adj = new Vec3(back * boxRad, left * boxRad, down * boxRad);

		// uppp (0,0, 1)
		// down (0,0,-1)
		// back (-1,0,0)
		// frnt ( 1,0,0)
		// left (0, 1,0)
		// rght (0,-1,0)

		Mat4 wM = new Mat4().setIdentity().fromQuat(cameraHandler.getInverseCameraRotation());

		Vec3 upFrntRght = new Vec3(0, 0, 0);
		Vec3 upBackRght = new Vec3(0, 0, 0);
		Vec3 dwFrntRght = new Vec3(0, 0, 0);
		Vec3 dwBackRght = new Vec3(0, 0, 0);
		Vec3 upFrntLeft = new Vec3(0, 0, 0);
		Vec3 upBackLeft = new Vec3(0, 0, 0);
		Vec3 dwFrntLeft = new Vec3(0, 0, 0);
		Vec3 dwBackLeft = new Vec3(0, 0, 0);
		if (renderGeoset != null) {
			for (GeosetVertex vertex : geo.getVertices()) {
				if (modelView.isSelected(vertex)) {
					glColor4f(1f, .0f, .0f, .7f);
				} else {
					glColor4f(.5f, .3f, .7f, .7f);
				}
				RenderGeoset.RenderVert renderVert = renderGeoset.getRenderVert(vertex);
				if (renderVert != null) {
					Vec3 renderPos = renderVert.getRenderPos();

//					upBackRght.set(renderPos).add(upBackRght_adj).transform(wM);
//					upBackLeft.set(renderPos).add(upBackLeft_adj).transform(wM);
//					upFrntRght.set(renderPos).add(upFrntRght_adj).transform(wM);
//					upFrntLeft.set(renderPos).add(upFrntLeft_adj).transform(wM);
//					dwBackRght.set(renderPos).add(dwBackRght_adj).transform(wM);
//					dwBackLeft.set(renderPos).add(dwBackLeft_adj).transform(wM);
//					dwFrntRght.set(renderPos).add(dwFrntRght_adj).transform(wM);
//					dwFrntLeft.set(renderPos).add(dwFrntLeft_adj).transform(wM);
					upBackRght.set(upBackRght_adj).transform(wM).add(renderPos);
					upBackLeft.set(upBackLeft_adj).transform(wM).add(renderPos);
					upFrntRght.set(upFrntRght_adj).transform(wM).add(renderPos);
					upFrntLeft.set(upFrntLeft_adj).transform(wM).add(renderPos);
					dwBackRght.set(dwBackRght_adj).transform(wM).add(renderPos);
					dwBackLeft.set(dwBackLeft_adj).transform(wM).add(renderPos);
					dwFrntRght.set(dwFrntRght_adj).transform(wM).add(renderPos);
					dwFrntLeft.set(dwFrntLeft_adj).transform(wM).add(renderPos);


//				//Up
					GL11.glNormal3f(0, uppp, 0);
					doGlQuad(upBackRght, upBackLeft, upFrntRght, upFrntLeft);

					glColor4f(.0f, .0f, .7f, .7f);
//				//Down
					GL11.glNormal3f(0, down, 0);
					doGlQuad(dwFrntRght, dwFrntLeft, dwBackRght, dwBackLeft);
//
					glColor4f(.7f, .7f, .0f, .7f);
//				//Back
					GL11.glNormal3f(0, 0, back);
					doGlQuad(upFrntRght, upFrntLeft, dwFrntRght, dwFrntLeft);
//
					glColor4f(.0f, .7f, .7f, .7f);
//				//Front
					GL11.glNormal3f(0, 0, frnt);
					doGlQuad(dwBackRght, dwBackLeft, upBackRght, upBackLeft);
//
					glColor4f(.7f, .0f, .0f, .7f);
//				//Right
					GL11.glNormal3f(rght, 0, 0);
					doGlQuad(dwFrntRght, dwBackRght, upFrntRght, upBackRght);
//
					glColor4f(0.f, .7f, .0f, .7f);
//				//Left
					GL11.glNormal3f(left, 0, 0);
					doGlQuad(upFrntLeft, upBackLeft, dwFrntLeft, dwBackLeft);

					// uppp (0,0, 1)
					// down (0,0,-1)
					// back (-1,0,0)
					// frnt ( 1,0,0)
					// left (0, 1,0)
					// rght (0,-1,0)
				}
			}
		}
		glEnd();
	}
	public static void paintBones1(ModelView modelView, RenderModel renderModel, IdObject idObject, CameraHandler cameraHandler) {
		RenderNode renderNode = renderModel.getRenderNode(idObject);
		if (renderNode != null) {
//			float boxRadLength = .5f;
//			float boxRadHeight = .5f;
//			float boxRadWidth = .5f;
			float boxRadLength = 1.5f;
			float boxRadHeight = 1.5f;
//			float boxRadHeight = 0f;
			float boxRadWidth = 1.5f;
			float frnt = 1;
			float left = 1;
			float uppp = 1;
			float back = -frnt;
			float rght = -left;
			float down = -uppp;

			Vec3 ugg = new Vec3(0,0,20);

			Vec3 upBackRght_adj = new Vec3(frnt * boxRadLength, rght * boxRadWidth, uppp * boxRadHeight);
			Vec3 dwBackRght_adj = new Vec3(frnt * boxRadLength, rght * boxRadWidth, down * boxRadHeight);
			Vec3 upFrntRght_adj = new Vec3(back * boxRadLength, rght * boxRadWidth, uppp * boxRadHeight);
			Vec3 dwFrntRght_adj = new Vec3(back * boxRadLength, rght * boxRadWidth, down * boxRadHeight);
			Vec3 upBackLeft_adj = new Vec3(frnt * boxRadLength, left * boxRadWidth, uppp * boxRadHeight);
			Vec3 dwBackLeft_adj = new Vec3(frnt * boxRadLength, left * boxRadWidth, down * boxRadHeight);
			Vec3 upFrntLeft_adj = new Vec3(back * boxRadLength, left * boxRadWidth, uppp * boxRadHeight);
			Vec3 dwFrntLeft_adj = new Vec3(back * boxRadLength, left * boxRadWidth, down * boxRadHeight);

	//		Vec3 upBackRght_adj = new Vec3(frnt * boxRadLength, rght * boxRadWidth, 0 * uppp * boxRadHeight);
	//		Vec3 dwBackRght_adj = new Vec3(frnt * boxRadLength, rght * boxRadWidth, down * boxRadHeight);
	//		Vec3 upFrntRght_adj = new Vec3(back * boxRadLength, rght * boxRadWidth, 0 * uppp * boxRadHeight);
	//		Vec3 dwFrntRght_adj = new Vec3(back * boxRadLength, rght * boxRadWidth, down * boxRadHeight);
	//		Vec3 upBackLeft_adj = new Vec3(frnt * boxRadLength, left * boxRadWidth, 0 * uppp * boxRadHeight);
	//		Vec3 dwBackLeft_adj = new Vec3(frnt * boxRadLength, left * boxRadWidth, down * boxRadHeight);
	//		Vec3 upFrntLeft_adj = new Vec3(back * boxRadLength, left * boxRadWidth, 0 * uppp * boxRadHeight);
	//		Vec3 dwFrntLeft_adj = new Vec3(back * boxRadLength, left * boxRadWidth, down * boxRadHeight);


			Vec3 upFrntRght = new Vec3(0, 0, 0);
			Vec3 upBackRght = new Vec3(0, 0, 0);
			Vec3 dwFrntRght = new Vec3(0, 0, 0);
			Vec3 dwBackRght = new Vec3(0, 0, 0);
			Vec3 upFrntLeft = new Vec3(0, 0, 0);
			Vec3 upBackLeft = new Vec3(0, 0, 0);
			Vec3 dwFrntLeft = new Vec3(0, 0, 0);
			Vec3 dwBackLeft = new Vec3(0, 0, 0);



//			Vec3 renderPosNode = new Vec3(idObject.getPivotPoint()).transform(renderNode.getWorldMatrix());
			Vec3 renderPosNode = new Vec3(renderNode.getPivot());

			RenderNode parentNode = renderModel.getRenderNode(idObject.getParent());
			Vec3 parentPivot = new Vec3(0,0,0);
			if(idObject.getParent() != null && parentNode != null){
//				parentPivot.set(-1,1,1).multiply(parentNode.getPivot()).transform(parentNode.getWorldMatrix());
//				parentPivot.set(renderModel.getRenderNode(idObject.getParent()).getWorldLocation());
//				parentPivot.set(idObject.getParent().getPivotPoint()).transform(parentNode.getWorldMatrix());
				parentPivot.set(parentNode.getPivot());
			} else {
				parentPivot.set(renderPosNode).add(zAxis);
			}


//			Vec3 diffVec = new Vec3(renderPosNode).sub(parentPivot);
			Vec3 diffVec = new Vec3(parentPivot).sub(renderPosNode);
			Vec3 tempVec = new Vec3();
			Vec3 tempAxis = new Vec3();

//			tempVec.set(diffVec).multiply(xAxis_isolate).normalize();
////			Quat difRotX = new Quat().setFromAxisAngle(xAxis, (float) (Math.PI/2 - tempVec.radAngleTo(xAxis)));
//			Quat difRotX = new Quat().setFromAxisAngle(xAxis, (float) (tempVec.radAngleTo(xAxis)));
//			difRotX.normalize();
//			tempVec.set(diffVec).cross(zAxis).normalize();
			tempVec.set(zAxis).cross(diffVec).normalize();

//			tempVec.set(diffVec).multiply(xAxis_isolate).normalize();
//			Quat difRotX = new Quat().setFromAxisAngle(xAxis, (float) (Math.PI/2 - tempVec.radAngleTo(xAxis)));
//			Quat difRotX = new Quat().setFromAxisAngle(tempVec, (float) (tempVec.radAngleTo(zAxis)));
			Quat difRotX = new Quat().setFromAxisAngle(tempVec, (float) (diffVec.getAngleToZaxis())).normalize();
			Quat difRotX2 = new Quat().setFromAxisAngle(tempVec, (float) (Math.PI/2)).normalize();
			difRotX.mul(difRotX2).normalize();


//			tempVec.set(diffVec).multiply(yAxis_isolate).normalize();
			tempVec.set(diffVec).normalize();
			tempAxis.set(diffVec).multiply(zAxis_isolate).normalize();
//			Quat difRotY = new Quat().setFromAxisAngle(yAxis, (float) (Math.PI/2 - tempVec.radAngleTo(yAxis)));
//			Quat difRotY = new Quat().setFromAxisAngle(yAxis, (float) (tempVec.radAngleTo(zAxis)));
//			Quat difRotY = new Quat().setFromAxisAngle(-tempVec.y, tempVec.x, 0, (float) (tempVec.radAngleTo(tempAxis)));
//			Quat difRotY = new Quat().setFromAxisAngle(-tempVec.y, tempVec.x, 0, (float) (tempVec.getAngleToZaxis()));
			Quat difRotY = new Quat().setFromAxisAngle(-tempVec.y, tempVec.x, 0, (float) (tempVec.radAngleTo(zAxis)));
//			Quat difRotY = new Quat().setFromAxisAngle(-tempVec.y, tempVec.x, 0, (float) (Math.min(A90*2.0-tempVec.radAngleTo(tempAxis),tempVec.radAngleTo(tempAxis))));
			difRotY.normalize();


			tempVec.set(diffVec).multiply(zAxis_isolate).normalize();
			tempAxis.set(diffVec).normalize();
//			tempVec.set(diffVec).normalize();
//			Quat difRotZ = new Quat().setFromAxisAngle(zAxis, (float) (Math.PI/2 - tempVec.radAngleTo(zAxis)));
//			Quat difRotZ = new Quat().setFromAxisAngle(zAxis, (float) (Math.min(A90*2-tempVec.radAngleTo(yAxis),tempVec.radAngleTo(yAxis))));
//			Quat difRotZ = new Quat().setFromAxisAngle(zAxis, (float) (tempVec.radAngleTo(yAxis)));
//			Quat difRotZ = new Quat().setFromAxisAngle(zAxis, (float) (tempVec.radAngleTo2(yAxis)));
//			Quat difRotZ = new Quat().setFromAxisAngle(zAxis, (float) (tempAxis.getZrotToYaxis()));
			Quat difRotZ = new Quat().setFromAxisAngle(zAxis, (float) (tempVec.getZrotToYaxis()));
			difRotZ.normalize();

//			Quat temp1 = new Quat().setFromAxisAngle(diffVec, (float) (Math.PI/2 - tempVec.radAngleTo(zAxis))).invertRotation();
//			Quat temp2 = new Quat().setFromAxisAngle(zAxis, (float) 0);
//			temp1.normalize();
//			temp2.normalize();
//			System.out.println("axis angle: " + temp1.toAxisWithAngle());

//			Quat difRotR = new Quat().setFromAxisAngle(zAxis, (float) 0).mul(difRotX).mul(difRotY).mul(difRotZ);
//			Quat difRotR = new Quat().setFromAxisAngle(zAxis, (float) A90).mul(difRotZ);//.mul(difRot2);
//			Quat difRotR = new Quat().setFromAxisAngle(yAxis, (float) 0.0).mul(difRotY).mul(difRotZ);
//			Quat difRotR = new Quat(difRotY).mul(difRotZ);
//			Quat difRotR = new Quat(difRotZ).mul(difRotY);
//			Quat difRotR = new Quat(difRotY);
			Quat difRotR = new Quat(difRotX);
//			difRotR.set(temp1);
			difRotR.normalize();


//			upBackRght.set(upBackRght_adj).transform(difRotR).add(renderPosNode);
//			upBackLeft.set(upBackLeft_adj).transform(difRotR).add(renderPosNode);
//			upFrntRght.set(upFrntRght_adj).transform(difRotR).add(renderPosNode);
//			upFrntLeft.set(upFrntLeft_adj).transform(difRotR).add(renderPosNode);

//			upBackRght.set(upBackRght_adj).transform(difRotR).add(parentPivot);
//			upBackLeft.set(upBackLeft_adj).transform(difRotR).add(parentPivot);
//			upFrntRght.set(upFrntRght_adj).transform(difRotR).add(parentPivot);
//			upFrntLeft.set(upFrntLeft_adj).transform(difRotR).add(parentPivot);

//			upBackRght.set(upBackRght_adj).add(ugg).transform(difRotR).add(renderPosNode);
//			upBackLeft.set(upBackLeft_adj).add(ugg).transform(difRotR).add(renderPosNode);
//			upFrntRght.set(upFrntRght_adj).add(ugg).transform(difRotR).add(renderPosNode);
//			upFrntLeft.set(upFrntLeft_adj).add(ugg).transform(difRotR).add(renderPosNode);

			dwBackRght.set(dwBackRght_adj).transform(difRotR).add(renderPosNode);
			dwBackLeft.set(dwBackLeft_adj).transform(difRotR).add(renderPosNode);
			dwFrntRght.set(dwFrntRght_adj).transform(difRotR).add(renderPosNode);
			dwFrntLeft.set(dwFrntLeft_adj).transform(difRotR).add(renderPosNode);

//			dwBackRght.set(dwBackRght_adj).transform(difRotR).add(renderPosNode);
//			dwBackLeft.set(dwBackLeft_adj).transform(difRotR).add(renderPosNode);
//			dwFrntRght.set(dwFrntRght_adj).transform(difRotR).add(renderPosNode);
//			dwFrntLeft.set(dwFrntLeft_adj).transform(difRotR).add(renderPosNode);

			upBackRght.set(upBackRght_adj).transform(difRotR).add(parentPivot);
			upBackLeft.set(upBackLeft_adj).transform(difRotR).add(parentPivot);
			upFrntRght.set(upFrntRght_adj).transform(difRotR).add(parentPivot);
			upFrntLeft.set(upFrntLeft_adj).transform(difRotR).add(parentPivot);


			if (true){
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
				glBegin(GL_QUADS);
				if (modelView.isSelected(idObject)) {
					glColor4f(1f, .0f, .0f, .7f);
				} else {
					glColor4f(.5f, .3f, .7f, .7f);
				}
				//Up
				GL11.glNormal3f(0, uppp, 0);
				doGlQuad(upBackRght, upBackLeft, upFrntRght, upFrntLeft);

				glColor4f(.0f, .0f, .7f, .7f);
				//Down
				GL11.glNormal3f(0, down, 0);
				doGlQuad(dwFrntRght, dwFrntLeft, dwBackRght, dwBackLeft);


				glColor4f(.7f, .7f, .0f, .4f);
				//Back
				GL11.glNormal3f(0, 0, back);
				doGlQuad(upFrntRght, upFrntLeft, dwFrntRght, dwFrntLeft);

				//Front
				GL11.glNormal3f(0, 0, frnt);
				doGlQuad(dwBackRght, dwBackLeft, upBackRght, upBackLeft);

				//Right
				GL11.glNormal3f(rght, 0, 0);
				doGlQuad(dwFrntRght, dwBackRght, upFrntRght, upBackRght);

				//Left
				GL11.glNormal3f(left, 0, 0);
				doGlQuad(upFrntLeft, upBackLeft, dwFrntLeft, dwBackLeft);

				glEnd();
			}



			if (idObject.getParent() != null) {

				glBegin(GL_LINES);
				glColor4f(.9f, .7f, 1f, .9f);
				Vec3 renderPos2 = new Vec3(renderNode.getPivot());//.transform(renderNode.getWorldMatrix());
//				GL11.glVertex3f(renderPos2.x, renderPos2.y, renderPos2.z);
				GL11.glVertex3f(renderPosNode.x, renderPosNode.y, renderPosNode.z);
				GL11.glVertex3f(parentPivot.x, parentPivot.y, parentPivot.z);
//				GL11.glVertex3f(idObject.getPivotPoint().x, idObject.getPivotPoint().y, idObject.getPivotPoint().z);
//				GL11.glVertex3f(idObject.getParent().getPivotPoint().x, idObject.getParent().getPivotPoint().y, idObject.getParent().getPivotPoint().z);

				glEnd();
			}

//			if (true) {
//
//				glBegin(GL_LINES);
//				glColor4f(1f, .7f, .7f, .9f);
////				Vec3 uggP1 = new Vec3(0,0,0);
//				Vec3 uggP2 = new Vec3(20.0,0,0);
////				Quat rotUggR = new Quat().setFromAxisAngle(zAxis, (float) A90/2).normalize();
////				Quat rotUggR1 = new Quat().setFromAxisAngle(xAxis, (float) A90/2).normalize();
////				uggP2.transform(rotUggR).transform(rotUggR1);

//				glEnd();
//				glBegin(GL_LINES);
//				glColor4f(1f, .7f, .7f, .9f);
//				uggP2.set(20,0,0).transform(difRotY);
//				GL11.glVertex3f(renderPosNode.x, renderPosNode.y, renderPosNode.z);
//				GL11.glVertex3f(uggP2.x + renderPosNode.x, uggP2.y + renderPosNode.y, uggP2.z + renderPosNode.z);
//
//				glColor4f(1f, .3f, .3f, .8f);
//				uggP2.set(20,0,0);
//				GL11.glVertex3f(renderPosNode.x, renderPosNode.y, renderPosNode.z);
//				GL11.glVertex3f(uggP2.x + renderPosNode.x, uggP2.y + renderPosNode.y, uggP2.z + renderPosNode.z);
//
//				glColor4f(.7f, 1f, .7f, .9f);
//				uggP2.set(0,20.0,0).transform(difRotY);
//				GL11.glVertex3f(renderPosNode.x, renderPosNode.y, renderPosNode.z);
//				GL11.glVertex3f(uggP2.x + renderPosNode.x, uggP2.y + renderPosNode.y, uggP2.z + renderPosNode.z);
//
////				glColor4f(.9f, 1f, .7f, .9f);
////				uggP2.transform(difRotY);
////				GL11.glVertex3f(renderPosNode.x, renderPosNode.y, renderPosNode.z);
////				GL11.glVertex3f(uggP2.x + renderPosNode.x, uggP2.y + renderPosNode.y, uggP2.z + renderPosNode.z);
//
//				glColor4f(.3f, 1f, .3f, .8f);
//				uggP2.set(0,20.0,0);
//				GL11.glVertex3f(renderPosNode.x, renderPosNode.y, renderPosNode.z);
//				GL11.glVertex3f(uggP2.x + renderPosNode.x, uggP2.y + renderPosNode.y, uggP2.z + renderPosNode.z);
//
//				glColor4f(.7f, .7f, 1f, .9f);
//				uggP2.set(0,0,20).transform(difRotY);
//				GL11.glVertex3f(renderPosNode.x, renderPosNode.y, renderPosNode.z);
//				GL11.glVertex3f(uggP2.x + renderPosNode.x, uggP2.y + renderPosNode.y, uggP2.z + renderPosNode.z);
//
//				glColor4f(.3f, .3f, 1f, .8f);
//				uggP2.set(0,0,20.0);
//				GL11.glVertex3f(renderPosNode.x, renderPosNode.y, renderPosNode.z-20.0f);
//				GL11.glVertex3f(uggP2.x + renderPosNode.x, uggP2.y + renderPosNode.y, uggP2.z + renderPosNode.z);

//				glEnd();
//			}
		}
	}

	private static void doGlQuad(Vec3 RT, Vec3 LT, Vec3 RB, Vec3 LB) {
		GL11.glVertex3f(RT.x, RT.y, RT.z);
		GL11.glVertex3f(LT.x, LT.y, LT.z);
		GL11.glVertex3f(LB.x, LB.y, LB.z);
		GL11.glVertex3f(RB.x, RB.y, RB.z);
	}

	public static void paintVertSquares(ModelView modelView, RenderModel renderModel, Geoset geo, CameraHandler cameraHandler) {

//		glBegin(GL11.GL_TRIANGLES);
		float v = (float) ((cameraHandler.geomX(4) - cameraHandler.geomX(0))*cameraHandler.getZoom());

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glBegin(GL_QUADS);
		RenderGeoset renderGeoset = renderModel.getRenderGeoset(geo);
		float boxRadLength = .001f;
		float boxRadHeight = .5f * v;
		float boxRadWidth = .5f * v;
		float frnt = 1;
		float left = 1;
		float uppp = 1;
		float back = -frnt;
		float rght = -left;
		float down = -uppp;


		Vec3 upFrntRght_adj = new Vec3(frnt * boxRadLength, rght * boxRadWidth, uppp * boxRadHeight);
		Vec3 dwFrntRght_adj = new Vec3(frnt * boxRadLength, rght * boxRadWidth, down * boxRadHeight);
		Vec3 upFrntLeft_adj = new Vec3(frnt * boxRadLength, left * boxRadWidth, uppp * boxRadHeight);
		Vec3 dwFrntLeft_adj = new Vec3(frnt * boxRadLength, left * boxRadWidth, down * boxRadHeight);
		Vec3 upBackRght_adj = new Vec3(back * boxRadLength, rght * boxRadWidth, uppp * boxRadHeight);
		Vec3 dwBackRght_adj = new Vec3(back * boxRadLength, rght * boxRadWidth, down * boxRadHeight);
		Vec3 upBackLeft_adj = new Vec3(back * boxRadLength, left * boxRadWidth, uppp * boxRadHeight);
		Vec3 dwBackLeft_adj = new Vec3(back * boxRadLength, left * boxRadWidth, down * boxRadHeight);

		// uppp (0,0, 1)
		// down (0,0,-1)
		// back (-1,0,0)
		// frnt ( 1,0,0)
		// left (0, 1,0)
		// rght (0,-1,0)

		Mat4 wM = new Mat4().setIdentity().fromQuat(cameraHandler.getInverseCameraRotation());

		Vec3 upBackRght = new Vec3(0, 0, 0);
		Vec3 upFrntRght = new Vec3(0, 0, 0);
		Vec3 dwBackRght = new Vec3(0, 0, 0);
		Vec3 dwFrntRght = new Vec3(0, 0, 0);
		Vec3 upBackLeft = new Vec3(0, 0, 0);
		Vec3 upFrntLeft = new Vec3(0, 0, 0);
		Vec3 dwBackLeft = new Vec3(0, 0, 0);
		Vec3 dwFrntLeft = new Vec3(0, 0, 0);
		if (renderGeoset != null) {
			for (GeosetVertex vertex : geo.getVertices()) {
				if (modelView.isSelected(vertex)) {
					glColor4f(1f, .0f, .0f, .7f);
				} else if (modelView.isEditable(vertex)){
					glColor4f(.5f, .3f, .7f, .7f);
				} else {
					glColor4f(.4f, .3f, .7f, .4f);
				}
				RenderGeoset.RenderVert renderVert = renderGeoset.getRenderVert(vertex);
				if (renderVert != null) {
					Vec3 renderPos = renderVert.getRenderPos();

//					upFrntRght.set(renderPos).add(upFrntRght_adj).transform(wM);
//					upFrntLeft.set(renderPos).add(upFrntLeft_adj).transform(wM);
//					upBackRght.set(renderPos).add(upBackRght_adj).transform(wM);
//					upBackLeft.set(renderPos).add(upBackLeft_adj).transform(wM);
//					dwFrntRght.set(renderPos).add(dwFrntRght_adj).transform(wM);
//					dwFrntLeft.set(renderPos).add(dwFrntLeft_adj).transform(wM);
//					dwBackRght.set(renderPos).add(dwBackRght_adj).transform(wM);
//					dwBackLeft.set(renderPos).add(dwBackLeft_adj).transform(wM);
					upFrntRght.set(upFrntRght_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(renderPos);
					upFrntLeft.set(upFrntLeft_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(renderPos);
					upBackRght.set(upBackRght_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(renderPos);
					upBackLeft.set(upBackLeft_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(renderPos);
					dwFrntRght.set(dwFrntRght_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(renderPos);
					dwFrntLeft.set(dwFrntLeft_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(renderPos);
					dwBackRght.set(dwBackRght_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(renderPos);
					dwBackLeft.set(dwBackLeft_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(renderPos);

//
//				glColor4f(.0f, .7f, .7f, .7f);
//				//Front
					GL11.glNormal3f(frnt, 0, 0);
					doGlQuad(dwFrntRght, dwFrntLeft, upFrntRght, upFrntLeft);
//
				}
			}
		}
		glEnd();
	}

	public static void paintSquare(Vec3 vec3, CameraHandler cameraHandler) {

//		glBegin(GL11.GL_TRIANGLES);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		glBegin(GL_QUADS);
		float boxRad = .5f;
		float frnt = 1;
		float left = 1;
		float uppp = 1;
		float back = -frnt;
		float rght = -left;
		float down = -uppp;


		Vec3 upFrntRght_adj = new Vec3(frnt * boxRad, rght * boxRad, uppp * boxRad);
		Vec3 dwFrntRght_adj = new Vec3(frnt * boxRad, rght * boxRad, down * boxRad);
		Vec3 upBackRght_adj = new Vec3(back * boxRad, rght * boxRad, uppp * boxRad);
		Vec3 dwBackRght_adj = new Vec3(back * boxRad, rght * boxRad, down * boxRad);
		Vec3 upFrntLeft_adj = new Vec3(frnt * boxRad, left * boxRad, uppp * boxRad);
		Vec3 dwFrntLeft_adj = new Vec3(frnt * boxRad, left * boxRad, down * boxRad);
		Vec3 upBackLeft_adj = new Vec3(back * boxRad, left * boxRad, uppp * boxRad);
		Vec3 dwBackLeft_adj = new Vec3(back * boxRad, left * boxRad, down * boxRad);
		// uppp (0,0, 1)
		// down (0,0,-1)
		// frnt (-1,0,0)
		// back ( 1,0,0)
		// left (0, 1,0)
		// rght (0,-1,0)

		Mat4 wM = new Mat4().setIdentity().fromQuat(cameraHandler.getInverseCameraRotation());

		Vec3 upBackRght = new Vec3(0, 0, 0);
		Vec3 upFrntRght = new Vec3(0, 0, 0);
		Vec3 dwBackRght = new Vec3(0, 0, 0);
		Vec3 dwFrntRght = new Vec3(0, 0, 0);
		Vec3 upBackLeft = new Vec3(0, 0, 0);
		Vec3 upFrntLeft = new Vec3(0, 0, 0);
		Vec3 dwBackLeft = new Vec3(0, 0, 0);
		Vec3 dwFrntLeft = new Vec3(0, 0, 0);


		glColor4f(1f, .3f, 1f, .7f);
		if (vec3 != null) {


			upFrntRght.set(upFrntRght_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(vec3);
			upFrntLeft.set(upFrntLeft_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(vec3);
			upBackRght.set(upBackRght_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(vec3);
			upBackLeft.set(upBackLeft_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(vec3);
			dwFrntRght.set(dwFrntRght_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(vec3);
			dwFrntLeft.set(dwFrntLeft_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(vec3);
			dwBackRght.set(dwBackRght_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(vec3);
			dwBackLeft.set(dwBackLeft_adj).scale((float) (1f / cameraHandler.getZoom())).transform(wM).add(vec3);

//
//				glColor4f(.0f, .7f, .7f, .7f);
//				//Front
			GL11.glNormal3f(frnt, 0, 0);
			doGlQuad(dwFrntRght, dwFrntLeft, upFrntRght, upFrntLeft);
//
		}
		glEnd();
	}

	// uppp (0,0, 1)
	// down (0,0,-1)
	// back (-1,0,0)
	// frnt ( 1,0,0)
	// left (0, 1,0)
	// rght (0,-1,0)
}
