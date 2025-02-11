package com.hiveworkshop.rms.ui.application.tools;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.actions.mesh.SnapCloseVertsAction;
import com.hiveworkshop.rms.editor.actions.nodes.BakeAndRebindAction;
import com.hiveworkshop.rms.editor.actions.selection.SetSelectionUggAction;
import com.hiveworkshop.rms.editor.actions.util.CompoundAction;
import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.FileDialog;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.actionfunctions.WeldVerts;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.gui.modeledit.KeybindingPrefPanel;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelPanel;
import com.hiveworkshop.rms.ui.gui.modeledit.selection.SelectionBundle;
import com.hiveworkshop.rms.util.FramePopup;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TwilacPanel extends JPanel {
	public TwilacPanel() {
		super(new MigLayout("", "", ""));
		JButton geosetsUneditable = new JButton("Geoset Uneditable");
		geosetsUneditable.addActionListener(e -> ProgramGlobals.getCurrentModelPanel().getModelView().setGeosetsEditable(false));
		add(geosetsUneditable, "wrap");
		JButton geosetsEditable = new JButton("Geoset Editable");
		geosetsEditable.addActionListener(e -> ProgramGlobals.getCurrentModelPanel().getModelView().setGeosetsEditable(true));
		add(geosetsEditable, "wrap");

		JButton hideVerts = new JButton("Hide Selected Verts");
		hideVerts.addActionListener(e -> ProgramGlobals.getCurrentModelPanel().getModelView().hideVertices(ProgramGlobals.getCurrentModelPanel().getModelView().getSelectedVertices()));
		add(hideVerts, "wrap");

		JButton unHideVerts = new JButton("Unhide Verts");
		unHideVerts.addActionListener(e -> ProgramGlobals.getCurrentModelPanel().getModelView().unHideAllVertices());
		add(unHideVerts, "wrap");

		JButton bakeAndRebindToNull = new JButton("BakeAndRebindToNull");
		bakeAndRebindToNull.addActionListener(e -> rebindToNull());
		add(bakeAndRebindToNull, "wrap");

		JButton snapCloseVerts = new JButton("Snap Close Verts");
		snapCloseVerts.addActionListener(e ->
				ProgramGlobals.getCurrentModelPanel().getUndoManager().pushAction(new SnapCloseVertsAction(ProgramGlobals.getCurrentModelPanel().getModelView().getSelectedVertices(), 1, ModelStructureChangeListener.changeListener).redo()));
		add(snapCloseVerts, "wrap");

		JButton weldCloseVerts = new JButton("Weld Close Verts");
		weldCloseVerts.addActionListener(e ->
//				ProgramGlobals.getCurrentModelPanel().getUndoManager().pushAction(new WeldVertsAction(ProgramGlobals.getCurrentModelPanel().getModelView().getSelectedVertices(), 1, ModelStructureChangeListener.changeListener).redo()));
				WeldVerts.doWeld(ProgramGlobals.getCurrentModelPanel().getModelHandler()));
		add(weldCloseVerts, "wrap");

		JButton renameBoneChain = new JButton("Rename Bone Chain");
		renameBoneChain.addActionListener(e -> RenameBoneChainPanel.show(ProgramGlobals.getMainPanel()));
		add(renameBoneChain, "wrap");

		JButton selectNodeGeometry = new JButton("selectNodeGeometry");
		selectNodeGeometry.addActionListener(e -> selectNodeGeometry());
		add(selectNodeGeometry, "wrap");

//		JButton editParticle = new JButton("editParticle");
//		editParticle.addActionListener(e -> viewParticlePanel());
//		add(editParticle, "wrap");

		JButton reorder_animations = new JButton("Reorder Animations");
		reorder_animations.addActionListener(e -> viewReOrderAnimsPanel());
		add(reorder_animations, "wrap");

		JButton edit_keybindings = new JButton("Edit Keybindings");
		edit_keybindings.addActionListener(e -> viewKBPanel());
		add(edit_keybindings, "wrap");

		JButton importModelPart = new JButton("importModelPart");
		importModelPart.addActionListener(e -> impModPart());
		add(importModelPart, "wrap");

		JButton importModelSubAnim = new JButton("importModelSubAnim");
		importModelSubAnim.addActionListener(e -> impModSubAnim());
		add(importModelSubAnim, "wrap");

		JButton spliceModelMeshPart = new JButton("splice mesh");
		spliceModelMeshPart.addActionListener(e -> spliceModPart());
		add(spliceModelMeshPart, "wrap");

		JButton mergeBoneHelpers = new JButton("mergeBoneHelpers");
		mergeBoneHelpers.addActionListener(e -> mergeUnnecessaryBonesWithHelpers());
		add(mergeBoneHelpers, "wrap");

		JButton button = new JButton("button");
		button.addActionListener(e -> button.setText(button.getText().equalsIgnoreCase("butt-on") ? "Butt-Off" : "Butt-On"));
		add(button, "wrap");

	}

	private void impModPart() {
		ModelHandler modelHandler = ProgramGlobals.getCurrentModelPanel().getModelHandler();
		FileDialog fileDialog = new FileDialog();
		EditableModel donModel = fileDialog.chooseModelFile(FileDialog.OPEN_WC_MODEL);
		if (donModel != null) {
			ImportModelPartPanel panel = new ImportModelPartPanel(donModel, modelHandler);
			FramePopup.show(panel, ProgramGlobals.getMainPanel(), "Import model Part");
		}
	}
	private void impModSubAnim() {
		ModelHandler modelHandler = ProgramGlobals.getCurrentModelPanel().getModelHandler();
		FileDialog fileDialog = new FileDialog();
		EditableModel donModel = fileDialog.chooseModelFile(FileDialog.OPEN_WC_MODEL);
		if (donModel != null) {
			ImportBoneChainAnimationPanel panel = new ImportBoneChainAnimationPanel(donModel, modelHandler);
			FramePopup.show(panel, ProgramGlobals.getMainPanel(), "Import bone chain animation");
		}
	}
	private void spliceModPart() {
		ModelHandler modelHandler = ProgramGlobals.getCurrentModelPanel().getModelHandler();
		FileDialog fileDialog = new FileDialog();
		EditableModel donModel = fileDialog.chooseModelFile(FileDialog.OPEN_WC_MODEL);
		if (donModel != null) {
			SpliceModelPartPanel panel = new SpliceModelPartPanel(donModel, modelHandler);
			FramePopup.show(panel, ProgramGlobals.getMainPanel(), "Splice mesh");
		}
	}

	public void makeGeosetUneditable() {
		ModelView modelView = ProgramGlobals.getCurrentModelPanel().getModelView();
		modelView.setGeosetsEditable(true);
	}

	public void rebindToNull() {
		ModelHandler modelPanel = ProgramGlobals.getCurrentModelPanel().getModelHandler();
		ModelView modelView = modelPanel.getModelView();
		List<UndoAction> rebindActions = new ArrayList<>();
		for (IdObject idObject : modelView.getSelectedIdObjects()) {
			System.out.println("rebinding " + idObject.getName());
//			UndoAction action = new BakeAndRebindActionTwi(idObject, null, modelPanel);
			UndoAction action = new BakeAndRebindAction(idObject, null, modelPanel);
			rebindActions.add(action);
		}
		modelPanel.getUndoManager().pushAction(new CompoundAction("Baked and changed Parent", rebindActions, ModelStructureChangeListener.changeListener::nodesUpdated).redo());
	}

	//	public void snapClose(){
//		ModelPanel modelPanel = ProgramGlobals.getCurrentModelPanel();
//		UndoAction action = new SnapCloseVertsAction(modelPanel.getModelView().getSelectedVertices(), 1);
//		modelPanel.getUndoManager().pushAction(action.redo());
//		modelPanel.getUndoManager().pushAction(new SnapCloseVertsAction(ProgramGlobals.getCurrentModelPanel().getModelView().getSelectedVertices(), 1).redo());
//	}
	public static void showPopup(JComponent parent) {
		FramePopup.show(new TwilacPanel(), parent, "Twilac's new tools");
	}

	public static void showPopup() {
		FramePopup.show(new TwilacPanel(), ProgramGlobals.getMainPanel(), "Twilac's new tools");
	}

	private void selectNodeGeometry() {
		if (ProgramGlobals.getCurrentModelPanel() != null) {
			ModelHandler modelHandler = ProgramGlobals.getCurrentModelPanel().getModelHandler();
			ModelView modelView = modelHandler.getModelView();

			Set<Bone> selectedBones = new HashSet<>();
			Set<GeosetVertex> vertexList = new HashSet<>();
			for (IdObject idObject : modelView.getSelectedIdObjects()) {
				if (idObject instanceof Bone) {
					selectedBones.add((Bone) idObject);
				}
			}
			for (Geoset geoset : modelView.getEditableGeosets()) {
				for (Bone bone : selectedBones) {
					List<GeosetVertex> vertices = geoset.getBoneMap().get(bone);
					if (vertices != null) {
						vertexList.addAll(vertices);
					}
				}
			}
			if (!vertexList.isEmpty()) {
				UndoAction action = new SetSelectionUggAction(new SelectionBundle(vertexList), modelView, "Select", ModelStructureChangeListener.changeListener);
				modelHandler.getUndoManager().pushAction(action.redo());
			}
		}
	}

	private void viewParticlePanel(){
		ModelPanel currentModelPanel = ProgramGlobals.getCurrentModelPanel();
		if (currentModelPanel != null) {
			List<ParticleEmitter2> particleEmitter2s = currentModelPanel.getModel().getParticleEmitter2s();
			if(!particleEmitter2s.isEmpty()) {
				ParticleEditPanel panel = new ParticleEditPanel(particleEmitter2s.get(particleEmitter2s.size()/2));
				FramePopup.show(panel, null, "Edit Particle2 Emitter");
			}
		}
	}
	private void viewReOrderAnimsPanel(){
		ModelPanel currentModelPanel = ProgramGlobals.getCurrentModelPanel();
		if (currentModelPanel != null) {
			ModelHandler modelHandler = ProgramGlobals.getCurrentModelPanel().getModelHandler();
			ReorderAnimationsPanel panel = new ReorderAnimationsPanel(modelHandler);
			FramePopup.show(panel, null, "Re-order Animations");
		}
	}
	private void viewKBPanel(){
		KeybindingPrefPanel keybindingPrefPanel = new KeybindingPrefPanel();
//		keybindingPrefPanel.setPreferredSize(ScreenInfo.getSmallWindow());
		FramePopup.show(keybindingPrefPanel, null, "Edit Keybindings");
	}

	private void mergeUnnecessaryBonesWithHelpers(){
		EditableModel model = ProgramGlobals.getCurrentModelPanel().getModel();
		Set<Bone> bonesWOMotion = new HashSet<>();
		model.getBones().stream()
				.filter(b -> b.getAnimFlags().isEmpty() && b.getChildrenNodes().isEmpty() && b.getParent() instanceof Helper)
				.forEach(bonesWOMotion::add);

//		Set<IdObject> decomParents = new HashSet<>();
		for (Bone bone : bonesWOMotion){
			IdObject parent = bone.getParent();
			bone.setPivotPoint(parent.getPivotPoint());
			bone.setAnimFlags(parent.getAnimFlags());
			List<IdObject> childList = new ArrayList<>(parent.getChildrenNodes());
			for(IdObject child : childList){
				if(child != bone){
					child.setParent(bone);
				}
			}
			bone.setParent(parent.getParent());
//			decomParents.add(parent);
		}



//		for (IdObject parent)
	}
}
