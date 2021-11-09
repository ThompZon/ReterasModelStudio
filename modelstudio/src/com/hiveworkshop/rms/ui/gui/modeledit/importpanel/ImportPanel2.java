package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlagUtils;
import com.hiveworkshop.rms.editor.model.animflag.Entry;
import com.hiveworkshop.rms.editor.model.animflag.FloatAnimFlag;
import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.editor.model.util.TempSaveModelStuff;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.hiveworkshop.rms.util.Vec3;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The panel to handle the import function.
 *
 * Eric Theller 6/11/2012
 */
public class ImportPanel2 {
	public static final ImageIcon animIcon = RMSIcons.animIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/anim_small.png"));
	public static final ImageIcon boneIcon = RMSIcons.boneIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Bone_small.png"));
	public static final ImageIcon geoIcon = RMSIcons.geoIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/geo_small.png"));
	public static final ImageIcon objIcon = RMSIcons.objIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Obj_small.png"));
	public static final ImageIcon greenIcon = RMSIcons.greenIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Blank_small.png"));
	public static final ImageIcon redIcon = RMSIcons.redIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankRed_small.png"));
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));
	public static final ImageIcon cyanIcon = RMSIcons.cyanIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankCyan_small.png"));
	public static final ImageIcon redXIcon = RMSIcons.redXIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/redX.png"));
	public static final ImageIcon greenArrowIcon = RMSIcons.greenArrowIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/greenArrow.png"));
	public static final ImageIcon moveUpIcon = RMSIcons.moveUpIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/moveUp.png"));
	public static final ImageIcon moveDownIcon = RMSIcons.moveDownIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/moveDown.png"));

	boolean importSuccess = false;
	boolean importStarted = false;
	boolean importEnded = false;

	private ModelStructureChangeListener changeListener1;

	ModelHolderThing mht;

	public ImportPanel2(final EditableModel receivingModel, final EditableModel donatingModel) {
		this(new ModelHolderThing(receivingModel, donatingModel));

	}

	public ImportPanel2(ModelHolderThing mht) {
		super();
		this.mht = mht;
		TempSaveModelStuff.doSavePreps(mht.receivingModel);

		String receivingModelName = mht.receivingModel.getName();
		String donatingModelName = mht.donatingModel.getName();


		if (receivingModelName.equals(donatingModelName)) {
			mht.donatingModel.setFileRef(new File(mht.donatingModel.getFile().getParent() + "/" + donatingModelName + " (Imported)" + ".mdl"));
		}
	}

	public void doImport() {
		importStarted = true;
		try {
			// The engine for actually performing the model to model import.

			if (mht.receivingModel == mht.donatingModel) {
				JOptionPane.showMessageDialog(null, "The program has confused itself.");
			}

			addChosenGeosets();
			getGeosetsRemoved();

			List<Animation> oldAnims = new ArrayList<>(mht.receivingModel.getAnims());

			List<AnimFlag<?>> recModFlags = ModelUtils.getAllAnimFlags(mht.receivingModel);
			List<AnimFlag<?>> donModFlags = ModelUtils.getAllAnimFlags(mht.donatingModel);

			List<EventObject> recModEventObjs = mht.receivingModel.getEvents();
			List<EventObject> donModEventObjs = mht.donatingModel.getEvents();

//			if (mht.clearRecModAnims.isSelected()) {
//				doClearAnims(recModFlags, recModEventObjs);
//			}

			fixRecAnims(recModFlags, recModEventObjs);
			List<Animation> newAnims = getNewAnimations(donModFlags, donModEventObjs);

			if (mht.clearExistingBones.isSelected()) {
				clearRecModBoneAndHelpers();
			}

			addChosenNewBones();

			if (!mht.clearExistingBones.isSelected()) {
				copyMotionFromBones();
			}
			// IteratableListModel<BoneShell> bones = getFutureBoneList();
			applyNewMatrixBones();

			// Objects!
			addChosenObjects();
			addChosenCameras();

			setNewVisSources(oldAnims, mht.clearRecModAnims.isSelected(), newAnims);

			importSuccess = true;


			if (changeListener1 != null) {
				changeListener1.geosetsUpdated();
//				changeListener1.geosetsUpdated();
//				changeListener1.nodesUpdated();
//				changeListener1.camerasUpdated();
			}
		} catch (final Exception e) {
			e.printStackTrace();
			ExceptionPopup.display(e);
		}

		importEnded = true;
	}

	private void doClearAnims(List<AnimFlag<?>> recModFlags, List<EventObject> recModEventObjs) {
		for (Animation anim : mht.receivingModel.getAnims()) {
			for (AnimFlag<?> af : recModFlags) {
				if (((af.getTypeId() == 1) || (af.getTypeId() == 2) || (af.getTypeId() == 3))) {
					// !af.hasGlobalSeq && was above before
					af.deleteAnim(anim);
				}
			}
			for (EventObject e : recModEventObjs) {
				e.deleteAnim(anim);
			}
		}
		mht.receivingModel.clearAnimations();
	}

	private void fixRecAnims(List<AnimFlag<?>> recModFlags, List<EventObject> recModEventObjs) {
		for (AnimShell animShell : mht.recModAnims) {
			if (animShell.getImportType() == AnimShell.ImportType.DONTIMPORT || mht.clearRecModAnims.isSelected()) {
				Animation anim = animShell.getAnim();
				for (AnimFlag<?> af : recModFlags) {
					if (((af.getTypeId() == 1) || (af.getTypeId() == 2) || (af.getTypeId() == 3))) {
						// !af.hasGlobalSeq && was above before
						af.deleteAnim(anim);
					}
				}
				for (EventObject e : recModEventObjs) {
					e.deleteAnim(anim);
				}
				mht.receivingModel.remove(anim);
			}
		}
	}

	private void setNewVisSources(List<Animation> oldAnims, boolean clearAnims, List<Animation> newAnims) {
		List<AnimFlag<Float>> finalVisFlags = new ArrayList<>();

		for (VisibilityShell visibilityShell : mht.futureVisComponents) {
			VisibilitySource temp = ((VisibilitySource) visibilityShell.getSource());
			AnimFlag<Float> visFlag = temp.getVisibilityFlag();// might be null
			AnimFlag<Float> newVisFlag;

			if (visFlag != null) {
				newVisFlag = visFlag.getEmptyCopy();
			} else {
				newVisFlag = new FloatAnimFlag(temp.visFlagName());
			}

			FloatAnimFlag flagOld = getFloatAnimFlag(newVisFlag.tans(), oldAnims, visibilityShell.getOldVisSource());
			FloatAnimFlag flagNew = getFloatAnimFlag(newVisFlag.tans(), newAnims, visibilityShell.getNewVisSource());

			if (flagNew != null
					&& ((visibilityShell.isFavorOld() && !visibilityShell.isFromDonating() && !clearAnims)
					|| (!visibilityShell.isFavorOld() && visibilityShell.isFromDonating()))) {
				// this is an element favoring existing animations over imported
				for (Animation a : oldAnims) {
					flagNew.deleteAnim(a);
				}
			} else if (flagOld != null) {
				// this is an element not favoring existing over imported
				for (Animation a : newAnims) {
					flagOld.deleteAnim(a);
				}
			}
			if (flagOld != null) {
				AnimFlagUtils.copyFrom(newVisFlag, flagOld);
			}
			if (flagNew != null) {
				AnimFlagUtils.copyFrom(newVisFlag, flagNew);
			}
			finalVisFlags.add(newVisFlag);
		}

		for (int i = 0; i < mht.futureVisComponents.size(); i++) {
			VisibilitySource visSource = ((VisibilitySource) mht.futureVisComponents.get(i).getSource());
			AnimFlag<Float> visFlag = finalVisFlags.get(i);
			if (visFlag.size() > 0) {
				visSource.setVisibilityFlag(visFlag);
			} else {
				visSource.setVisibilityFlag(null);
			}
		}
	}

	private FloatAnimFlag getFloatAnimFlag(boolean tans, List<Animation> anims, VisibilityShell source) {
		if (source != null) {
			if (source.isNeverVisible()) {
				return getNeverVisFlag(tans, anims);
			} else if (!source.isAlwaysVisible()) {
				return (FloatAnimFlag) ((VisibilitySource) source.getSource()).getVisibilityFlag();
			}
		}
		return null;
	}

	private FloatAnimFlag getNeverVisFlag(boolean tans, List<Animation> anims) {
		FloatAnimFlag tempFlag = new FloatAnimFlag("temp");
		for (Animation a : anims) {
			tempFlag.setOrAddEntryT(0, new Entry<>(0, 0f), a);
		}
		if (tans) tempFlag.unLinearize();
		return tempFlag;
	}

	private List<IdObject> addChosenObjects() {
		List<IdObject> objectsAdded = new ArrayList<>();
		for (ObjectShell objectShell : mht.donModObjectShells) {
			if (objectShell.getShouldImport() && objectShell.getIdObject() != null) {
				BoneShell parentBs = objectShell.getNewParentBs();
				if (parentBs != null) {
					objectShell.getIdObject().setParent(parentBs.getBone());
				} else {
					objectShell.getIdObject().setParent(null);
				}
				// later make a name field?
				mht.receivingModel.add(objectShell.getIdObject());
				objectsAdded.add(objectShell.getIdObject());
			} else if (objectShell.getIdObject() != null) {
				objectShell.getIdObject().setParent(null);
				// Fix cross-model referencing issue (force clean parent node's list of children)

			}
		}

		return objectsAdded;
	}

	private List<Camera> addChosenCameras() {
		List<Camera> camerasAdded = new ArrayList<>();
		for (CameraShell cameraShell : mht.donModCameraShells) {
			if (cameraShell.getShouldImport() && cameraShell.getCamera() != null) {
				mht.receivingModel.add(cameraShell.getCamera());
				camerasAdded.add(cameraShell.getCamera());
			}
		}
		return camerasAdded;
	}

	private void applyNewMatrixBones() {
		boolean shownEmpty = false;
		boolean shownDestroyed = false;
		Bone dummyBone = null;
		// ToDo this needs a map matrices to vertices or something to update vertex-bones correctly...
		for (GeosetShell geosetShell : mht.allGeoShells) {
			if (geosetShell.isDoImport()) {
				Map<Matrix, List<GeosetVertex>> matrixVertexMap = new HashMap<>();
				for (GeosetVertex vertex : geosetShell.getGeoset().getVertices()) {
					matrixVertexMap.computeIfAbsent(vertex.getMatrix(), k -> new ArrayList<>()).add(vertex);
				}
				for (MatrixShell ms : geosetShell.getMatrixShells()) {
					List<GeosetVertex> vertexList = matrixVertexMap.get(ms.getMatrix());
					ms.getMatrix().clear();
					for (final BoneShell bs : ms.getNewBones()) {
						if (mht.receivingModel.contains(bs.getBone())) {
							ms.getMatrix().add(bs.getBone());
						} else {
							System.out.println("Boneshaving " + bs.getBone().getName() + " out of use");
						}
					}
					if (ms.getMatrix().size() == 0 && !shownDestroyed) {
						JOptionPane.showMessageDialog(null,
								"Error: A matrix was functionally destroyed while importing, and may take the program with it!");
						shownDestroyed = true;
					}
					if (ms.getMatrix().getBones().size() < 1) {
						if (dummyBone == null) {
							dummyBone = new Bone();
							dummyBone.setName("Bone_MatrixEaterDummy" + (int) (Math.random() * 2000000000));
							dummyBone.setPivotPoint(new Vec3(0, 0, 0));
							if (!mht.receivingModel.contains(dummyBone)) {
								mht.receivingModel.add(dummyBone);
							}
						}
						if (!shownEmpty) {
							JOptionPane.showMessageDialog(null,
									"Warning: You left some matrices empty. This was detected, and a dummy bone at { 0, 0, 0 } has been generated for them named "
											+ dummyBone.getName()
											+ "\nMultiple geosets may be attached to this bone, and the error will only be reported once for your convenience.");
							shownEmpty = true;
						}
						if (!ms.getMatrix().getBones().contains(dummyBone)) {
							ms.getMatrix().getBones().add(dummyBone);
						}
					}
					ms.getMatrix().cureBones(mht.receivingModel);
					if (vertexList != null) {
						for (GeosetVertex vertex : vertexList) {
							vertex.clearBoneAttachments();
							for (Bone bone : ms.getMatrix().getBones()) {
								vertex.addBoneAttachment(bone);
							}
						}
					} else {
						System.out.println("couldn't find vertices for Matrix " + ms.getMatrix());
					}
				}
			}
		}
////			mht.receivingModel.updateObjectIds();
//		for (Geoset g : mht.receivingModel.getGeosets()) {
//			applyMatricesToVertices(g, mht.receivingModel);
//		}
	}

//	public void applyMatricesToVertices(Geoset geoset, EditableModel mdlr) {
////		System.out.println("applyMatricesToVertices");
//		for (GeosetVertex gv : geoset.getVertices()) {
//			gv.clearBoneAttachments(); //Todo check if this is broken
//			int vertexGroup = gv.getVertexGroup();
//			Matrix mx = geoset.getMatrix(vertexGroup);
//			if (((vertexGroup == -1) || (mx == null))) {
//				if (!ModelUtils.isTangentAndSkinSupported(mdlr.getFormatVersion())) {
//					throw new IllegalStateException("You have empty vertex groupings but FormatVersion is 800. Did you load HD mesh into an SD model?");
//				}
//			} else {
////				mx.updateIds(mdlr);
//				mx.cureBones(mdlr);
//				for (Bone bone : mx.getBones()) {
//					gv.addBoneAttachment(bone);
//				}
//			}
//		}
//	}

	private void copyMotionFromBones() {
		for (BoneShell bs : mht.recModBoneShells) {
			if (bs.getImportBoneShell() != null && bs.getImportBoneShell().getImportStatus() == BoneShell.ImportType.MOTIONFROM) {
				bs.getBone().copyMotionFrom(bs.getImportBone());
			}
		}
	}

	private List<IdObject> addChosenNewBones() {
		List<IdObject> objectsAdded = new ArrayList<>();
		for (BoneShell boneShell : mht.donModBoneShells) {
			// we will go through all bone shells for this
			// Fix cross-model referencing issue (force clean parent node's list of children)
			switch (boneShell.getImportStatus()) {
				case IMPORT -> {
					System.out.println("adding bone: " + boneShell);
					mht.receivingModel.add(boneShell.getBone());
					objectsAdded.add(boneShell.getBone());
					if (boneShell.getNewParentBs() != null) {
						boneShell.getBone().setParent(boneShell.getNewParentBs().getBone());
					} else {
						boneShell.getBone().setParent(null);
					}
				}
				case MOTIONFROM, DONTIMPORT -> boneShell.getBone().setParent(null);
			}
		}
		return objectsAdded;
	}

	private void clearRecModBoneAndHelpers() {
		for (IdObject o : mht.receivingModel.getBones()) {
			mht.receivingModel.remove(o);
		}
		for (IdObject o : mht.receivingModel.getHelpers()) {
			mht.receivingModel.remove(o);
		}
	}

	private List<Animation> getNewAnimations(List<AnimFlag<?>> donModFlags, List<EventObject> donModEventObjs) {


		// note to self: remember to scale event objects with time
//		List<AnimFlag<?>> newImpFlags = new ArrayList<>();
		Map<AnimFlag<?>, AnimFlag<?>> flagMap = new HashMap<>();
		for (AnimFlag<?> af : donModFlags) {
			if (!af.hasGlobalSeq()) {
//				newImpFlags.add(af.getEmptyCopy());
				flagMap.put(af, af.getEmptyCopy());
			} else {
//				newImpFlags.add(af.deepCopy());
				flagMap.put(af, af.deepCopy());
			}
		}
//		List<EventObject> newImpEventObjs = new ArrayList<>();
		Map<EventObject, EventObject> eventMap = new HashMap<>();
		for (EventObject e : donModEventObjs) {
//			newImpEventObjs.add(EventObject.buildEmptyFrom(e));
			eventMap.put(e, EventObject.buildEmptyFrom(e));
		}


		List<Animation> newAnims = new ArrayList<>();
//		for (AnimShell animShell : mht.allAnimShells) {
		for (AnimShell animShell : mht.donModAnims) {
			if (animShell.getImportType() != AnimShell.ImportType.DONTIMPORT) {
//				int newStart = ModelUtils.animTrackEnd(mht.receivingModel) + 300;

				Animation anim1 = animShell.getAnim();
				if (animShell.isReverse()) {
					reverseAnim(donModFlags, donModEventObjs, anim1);
				}
				switch (animShell.getImportType()) {
					case IMPORTBASIC:
					case CHANGENAME:

						//todo things here is probably broken...
//						anim1.setStart(newStart);
//						animCopyToInterv1(donModFlags, donModEventObjs, newImpFlags, newImpEventObjs, anim1, anim1);
						animCopyToInterv1(flagMap, eventMap, anim1, anim1);
						if (animShell.getImportType() == AnimShell.ImportType.CHANGENAME) {
							anim1.setName(animShell.getName());
						}
						mht.receivingModel.add(anim1);
						newAnims.add(anim1);
						break;
					case TIMESCALE:
						if (!mht.clearRecModAnims.isSelected()) {
							for (AnimShell recAnimShell : mht.recModAnims) {
								AnimShell importAnimShell = recAnimShell.getImportAnimShell();
								if (importAnimShell == animShell) {
									Animation importAnim = importAnimShell.getAnim();
									animCopyToInterv1(flagMap, eventMap, anim1, importAnim);

									newAnims.add(new Animation("temp", anim1.getStart(), anim1.getEnd()));

									if (!mht.clearExistingBones.isSelected()) {
										for (BoneShell bs : mht.recModBoneShells) {
											if (bs.getImportBoneShell() != null && bs.getImportBoneShell().getImportStatus() == BoneShell.ImportType.MOTIONFROM) {
												System.out.println("Attempting to clear animation for " + bs.getBone().getName() + " values " + anim1.getStart() + ", " + anim1.getEnd());
												bs.getBone().clearAnimation(anim1);
											}
										}
									}
								}
							}
						}
//						AnimShell importAnimShell = animShell.getImportAnimShell();
//
//						if (importAnimShell != null && importAnimShell.getImportType() == AnimShell.ImportType.TIMESCALE) {
//
//							Animation importAnim = importAnimShell.getAnim();
//							animCopyToInterv1(flagMap, eventMap, anim1, importAnim);
//
//							newAnims.add(new Animation("temp", anim1.getStart(), anim1.getEnd()));
//
//							if (!mht.clearExistingBones.isSelected()) {
//								for (BoneShell bs : mht.recModBoneShells) {
//									if (bs.getImportBoneShell() != null && bs.getImportBoneShell().getImportStatus() == BoneShell.ImportType.MOTIONFROM) {
//										System.out.println("Attempting to clear animation for " + bs.getBone().getName() + " values " + anim1.getStart() + ", " + anim1.getEnd());
//										bs.getBone().clearAnimation(anim1);
//									}
//								}
//							}
//						}
						break;
					case GLOBALSEQ:
						buildGlobSeqFrom(mht.donatingModel, anim1, donModFlags);
						break;
				}
			}
		}
//		if (!mht.clearRecModAnims.isSelected()) {
//			addNewAnimsIntoOldAnims(donModFlags, donModEventObjs, newImpFlags, newImpEventObjs, newAnims);
//		}
//		if (!mht.clearRecModAnims.isSelected()) {
//			for (AnimShell animShell : mht.recModAnims) {
//
//				AnimShell importAnimShell = animShell.getImportAnimShell();
//				if (importAnimShell != null && importAnimShell.getImportType() == AnimShell.ImportType.TIMESCALE) {
//					Animation anim1 = animShell.getAnim();
//
//					Animation importAnim = importAnimShell.getAnim();
//					animCopyToInterv1(flagMap, eventMap, anim1, importAnim);
//
//					newAnims.add(new Animation("temp", anim1.getStart(), anim1.getEnd()));
//
//					if (!mht.clearExistingBones.isSelected()) {
//						for (BoneShell bs : mht.recModBoneShells) {
//							if (bs.getImportBoneShell() != null && bs.getImportBoneShell().getImportStatus() == BoneShell.ImportType.MOTIONFROM) {
//								System.out.println("Attempting to clear animation for " + bs.getBone().getName() + " values " + anim1.getStart() + ", " + anim1.getLength());
//								bs.getBone().clearAnimation(anim1);
//							}
//						}
//					}
//				}
//			}
//		}

		// Now, rebuild the old animflags with the new
		for (AnimFlag<?> af : donModFlags) {
//			af.setValuesTo(newImpFlags.get(donModFlags.indexOf(af)));
			AnimFlagUtils.setValuesTo(af, flagMap.get(af));
		}
		for (EventObject e : donModEventObjs) {
//			e.setValuesTo(newImpEventObjs.get(donModEventObjs.indexOf(e)));
			e.setValuesTo(eventMap.get(e));
		}

		return newAnims;
	}

	public static void buildGlobSeqFrom(EditableModel model, Animation anim, List<AnimFlag<?>> flags) {
		GlobalSeq newSeq = new GlobalSeq(anim.getLength());
		for (AnimFlag<?> af : flags) {
			if (!af.hasGlobalSeq()) {
				AnimFlag<?> newGlobalSeqFlag = af.deepCopy();
				newGlobalSeqFlag.setGlobSeq(newSeq);
				AnimFlagUtils.copyFrom(newGlobalSeqFlag, af, anim, newSeq);
				addFlagToParent(model, af, newGlobalSeqFlag);
			}
		}
	}

	public static void addFlagToParent(EditableModel model, AnimFlag<?> orgFlag, AnimFlag<?> newGlobalSeqFlag) {
		// orgFlag is the original flag that should exist in the parent
		// ADDS "newGlobalSeqFlag" TO THE PARENT OF "orgFlag"
		for (Material material : model.getMaterials()) {
			material.getLayers().stream().filter(layer -> layer.owns(orgFlag)).forEach(layer -> layer.add(newGlobalSeqFlag));
		}

		model.getTexAnims().stream().filter(o -> o.owns(orgFlag)).forEach(o -> o.add(newGlobalSeqFlag));
		model.getGeosetAnims().stream().filter(o -> o.owns(orgFlag)).forEach(o -> o.add(newGlobalSeqFlag));
		model.getIdObjects().stream().filter(o -> o.owns(orgFlag)).forEach(o -> o.add(newGlobalSeqFlag));
		model.getCameras().stream().filter(o -> o.getSourceNode().owns(orgFlag)).forEach(o -> o.getSourceNode().add(newGlobalSeqFlag));

	}

	private void addNewAnimsIntoOldAnims(List<AnimFlag<?>> donModFlags, List<EventObject> donModEventObjs, List<AnimFlag<?>> newImpFlags, List<EventObject> newImpEventObjs, List<Animation> newAnims) {
		for (AnimShell animShell : mht.recModAnims) {

			AnimShell importAnimShell = animShell.getImportAnimShell();
			if (importAnimShell != null && importAnimShell.getImportType() == AnimShell.ImportType.TIMESCALE) {
				Animation anim1 = animShell.getAnim();

				Animation importAnim = importAnimShell.getAnim();
				animCopyToInterv1(donModFlags, donModEventObjs, newImpFlags, newImpEventObjs, anim1, importAnim);

				newAnims.add(new Animation("temp", anim1.getStart(), anim1.getEnd()));

				if (!mht.clearExistingBones.isSelected()) {
					for (BoneShell bs : mht.recModBoneShells) {
						if (bs.getImportBoneShell() != null && bs.getImportBoneShell().getImportStatus() == BoneShell.ImportType.MOTIONFROM) {
							System.out.println("Attempting to clear animation for " + bs.getBone().getName() + " values " + anim1.getStart() + ", " + anim1.getLength());
							bs.getBone().clearAnimation(anim1);
						}
					}
				}
			}
		}
	}

	private void reverseAnim(List<AnimFlag<?>> donModFlags, List<EventObject> donModEventObjs, Animation anim1) {
		// reverse the animation
		int length = anim1.getLength();
		for (AnimFlag<?> af : donModFlags) {
			if (!af.hasGlobalSeq() && ((af.getTypeId() == 1) || (af.getTypeId() == 2) || (af.getTypeId() == 3))) {
				AnimFlagUtils.timeScale2(af, anim1, -length, length);
			}
		}
		for (EventObject e : donModEventObjs) {
			e.timeScale(anim1, -length, length);
		}
	}

	private void animCopyToInterv1(List<AnimFlag<?>> animFlags, List<EventObject> eventObjects, List<AnimFlag<?>> newImpFlags, List<EventObject> newImpEventObjs, Animation anim1, Animation importAnim) {
//		importAnim.copyToInterval(start, start + length, anim1, animFlags, eventObjects, newImpFlags, newImpEventObjs);
		for (AnimFlag<?> af : newImpFlags) {
			if (!af.hasGlobalSeq()) {
				AnimFlag<?> source = animFlags.get(newImpFlags.indexOf(af));
				AnimFlagUtils.copyFrom(af, source, importAnim, anim1);
			}
		}
		for (EventObject e : newImpEventObjs) {
			if (!e.hasGlobalSeq()) {
				EventObject source = eventObjects.get(newImpEventObjs.indexOf(e));
				e.copyFrom(source, importAnim, anim1);
			}
		}
	}

	private void animCopyToInterv1(Map<AnimFlag<?>, AnimFlag<?>> flagMap, Map<EventObject, EventObject> eventMap, Animation anim1, Animation importAnim) {
		for (AnimFlag<?> source : flagMap.keySet()) {
			AnimFlag<?> af = flagMap.get(source);
			if (af != null && !af.hasGlobalSeq()) {
				AnimFlagUtils.copyFrom(af, source, importAnim, anim1);
			}
		}
		for (EventObject source : eventMap.keySet()) {
			EventObject e = eventMap.get(source);
			if (e != null && !e.hasGlobalSeq()) {
				e.copyFrom(source, importAnim, anim1);
			}
		}
	}

	private List<Geoset> addChosenGeosets() {
		List<Geoset> geosetsAdded = new ArrayList<>();

		for (GeosetShell geoShell : mht.donModGeoShells) {

			if (geoShell.isDoImport()) {
				geoShell.getGeoset().setMaterial(geoShell.getMaterial());
				mht.receivingModel.add(geoShell.getGeoset());

				geosetsAdded.add(geoShell.getGeoset());

				if (geoShell.getGeoset().getGeosetAnim() != null) {
					mht.receivingModel.add(geoShell.getGeoset().getGeosetAnim());
				}
			}
		}
		return geosetsAdded;
	}

	private List<Geoset> getGeosetsRemoved() {
		List<Geoset> geosetsRemoved = new ArrayList<>();

		for (GeosetShell geoShell : mht.recModGeoShells) {

			if (!geoShell.isDoImport()) {
				if (geoShell.getGeoset().getGeosetAnim() != null) {
					mht.receivingModel.remove(geoShell.getGeoset().getGeosetAnim());
				}
				geosetsRemoved.add(geoShell.getGeoset());
				mht.receivingModel.remove(geoShell.getGeoset());
			} else {
				geoShell.getGeoset().setMaterial(geoShell.getMaterial());
			}
		}
		return geosetsRemoved;
	}

	public boolean importSuccessful() {
		return importSuccess;
	}

	public boolean importStarted() {
		return importStarted;
	}

	public boolean importEnded() {
		return importEnded;
	}
}

