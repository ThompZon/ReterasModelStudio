package com.hiveworkshop.rms.ui.gui.modeledit.creator;

import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.actionfunctions.CreateFace;
import com.hiveworkshop.rms.ui.application.edit.animation.Sequence;
import com.hiveworkshop.rms.ui.application.edit.animation.mdlvisripoff.TSpline;
import com.hiveworkshop.rms.ui.application.edit.mesh.ModelEditorManager;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.ViewportPopupMenu;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelPanel;
import com.hiveworkshop.rms.ui.gui.modeledit.creator.activity.DrawBoneActivity;
import com.hiveworkshop.rms.ui.gui.modeledit.creator.activity.DrawBoxActivity;
import com.hiveworkshop.rms.ui.gui.modeledit.creator.activity.DrawPlaneActivity;
import com.hiveworkshop.rms.ui.gui.modeledit.creator.activity.DrawVertexActivity;
import com.hiveworkshop.rms.ui.gui.modeledit.toolbar.ModeButton2;
import com.hiveworkshop.rms.ui.gui.modeledit.toolbar.ModelEditorActionType3;
import com.hiveworkshop.rms.ui.gui.modeledit.toolbar.ToolbarButtonGroup2;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.ModeButton;
import com.hiveworkshop.rms.util.TwiCardPanel;
import com.hiveworkshop.rms.util.TwiComboBoxModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CreatorModelingPanel extends JPanel {
	private static final String ANIMATIONBASICS = "ANIMATIONBASICS";

	private ModelEditorManager modelEditorManager;
	private final ProgramPreferences programPreferences;
	private JComboBox<Sequence> animationChooserBox;
	//	private JComboBox<String> modelingOptionsBox;
//	private CardLayout modelingOptionsCardLayout;
	private TwiCardPanel modelingOptionsCardPanel;
	private ModelHandler modelHandler;
	//	private final CardLayout modeCardLayout;
	private final TwiCardPanel modeCardPanel;
	private ManualTransformPanel transformPanel;


	private JPopupMenu contextMenu;

	public CreatorModelingPanel() {
		this.programPreferences = ProgramGlobals.getPrefs();
		setLayout(new MigLayout("ins 0", "", "[grow][][]"));


		animationChooserBox = getAnimationChooserBox();
		JPanel animationPanel = new JPanel(new MigLayout("ins 0, fill", "[]", "[][grow]"));
		animationPanel.add(animationChooserBox, "wrap, growx");
//		animationPanel.add(getAnimationBasicsPanel(), "wrap, growx, growy");
//		animationPanel.add(getAnimationBasicsPanel(), "wrap, growx");


		modelingOptionsCardPanel = new TwiCardPanel();

		modelingOptionsCardPanel.add(getMeshBasicsPanel(), "Mesh Basics");
		modelingOptionsCardPanel.add(getStandardPrimitivesPanel(), "Standard Primitives");
//		modelingOptionsCardPanel.add(getStandardPrimitivesPanel(), "Extended Primitives");
//		modelingOptionsCardPanel.add(getStandardPrimitivesPanel(), "Animation Nodes");


		JPanel modelingPanel = new JPanel(new MigLayout("ins 0"));
		modelingPanel.add(modelingOptionsCardPanel.getCombobox(), "wrap");
		modelingPanel.add(modelingOptionsCardPanel);


		modeCardPanel = new TwiCardPanel();
		modeCardPanel.add(animationPanel, "ANIM");
		modeCardPanel.add(modelingPanel, "MESH");

//		add(transformPanel, BorderLayout.CENTER);

		JButton popupMenuButton = new JButton("show popup menu");
		popupMenuButton.addActionListener(e -> showVPPopup(popupMenuButton));

//		add(modeCardPanel, "wrap, growx, growy");
		add(modeCardPanel, "wrap, growx");
		transformPanel = new ManualTransformPanel();
//		add(transformPanel, "wrap");
		add(popupMenuButton, "");
	}

	private JPanel getStandardPrimitivesPanel() {
		JPanel drawPrimitivesPanel = new JPanel(new MigLayout("ins 0, gap 0, wrap 1, fill", "[grow]", ""));
		drawPrimitivesPanel.setBorder(BorderFactory.createTitledBorder("Draw"));
		drawPrimitivesPanel.add(getModeButton("Plane", this::drawPlane));
		drawPrimitivesPanel.add(getModeButton("Box", this::drawBox));

		JPanel spOptionsPanel = new JPanel(new MigLayout("ins 0, gap 0, fill", "[grow]"));
		spOptionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));

		JPanel standardPrimitivesPanel = new JPanel(new MigLayout("fill, ins 0, gap 0, wrap 1", "[grow]"));
		standardPrimitivesPanel.add(drawPrimitivesPanel, "growx");
		standardPrimitivesPanel.add(spOptionsPanel, "growx");
//		standardPrimitivesPanel.add(drawPrimitivesPanel, "");
//		standardPrimitivesPanel.add(spOptionsPanel, "");

		return standardPrimitivesPanel;
	}

	private ModeButton getModeButton(String text, Consumer<ModeButton> action) {
		ModeButton modeButton = new ModeButton(text);
		modeButton.addActionListener(e -> action.accept(modeButton));
		return modeButton;
	}

	private JComboBox<Sequence> getAnimationChooserBox() {
		final TwiComboBoxModel<Sequence> animationChooserBoxModel = new TwiComboBoxModel<>();
		JComboBox<Sequence> animationChooserBox = new JComboBox<>(animationChooserBoxModel);
//		animationChooserBox.setPrototypeDisplayValue(new Animation("temporary prototype animation", 0, 1));
		animationChooserBox.addItemListener(this::chooseAnimation);
		return animationChooserBox;
	}

	private void chooseAnimation(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			Sequence selectedItem = (Sequence) animationChooserBox.getSelectedItem();
			if (selectedItem != null && modelHandler != null) {
				modelHandler.getEditTimeEnv().setSequence(selectedItem);
			}
		}
	}

	public JPanel getMeshBasicsPanel() {
		JPanel meshBasicsPanel = new JPanel(new MigLayout("wrap 1, ins 0, fill", "[grow]", "[][][grow]"));
		meshBasicsPanel.add(getDrawToolsPanel(), "growx");
//		meshBasicsPanel.add(getEditToolsPanel(), "growx");
		return meshBasicsPanel;

	}

	private JPanel getEditToolsPanel() {
		JPanel editToolsPanel = new JPanel(new MigLayout("wrap 1, gap 0, ins 0, fill", "[grow]", ""));
		editToolsPanel.setBorder(BorderFactory.createTitledBorder("Manipulate"));

		for (ModeButton2 modeButton2 : ProgramGlobals.getActionTypeGroup().getModeButtons()) {
			editToolsPanel.add(modeButton2);
		}
		return editToolsPanel;
	}

	private JPanel getDrawToolsPanel() {
		JPanel drawToolsPanel = new JPanel(new MigLayout("wrap 1, gap 0, ins 0, fill"));
		drawToolsPanel.setBorder(BorderFactory.createTitledBorder("Draw"));

		drawToolsPanel.add(getModeButton2("Vertex", this::addVertex));
		drawToolsPanel.add(getModeButton2("Face from Selection", this::createFace));
		drawToolsPanel.add(getModeButton2("Bone", this::addBone));
		return drawToolsPanel;
	}

	private ModeButton2 getModeButton2(String text, Consumer<ModeButton2> action) {
		ModeButton2 modeButton = new ModeButton2(text, programPreferences.getActiveColor1(), programPreferences.getActiveColor2());
		modeButton.addActionListener(e -> action.accept(modeButton));
		return modeButton;
	}

	private void createFace(ModeButton2 modeButton) {
		CreateFace.createFace(modelHandler);
	}

	private void addBone(ModeButton2 modeButton) {
		ModelPanel modelPanel = ProgramGlobals.getCurrentModelPanel();
		if (modelPanel != null) {
			DrawBoneActivity activity = new DrawBoneActivity(modelHandler, modelEditorManager);
			modeButton.setColors(programPreferences.getActiveColor1(), programPreferences.getActiveColor2());
			modelPanel.changeActivity(activity);
		}
	}

	private void addVertex(ModeButton2 modeButton) {
		ModelPanel modelPanel = ProgramGlobals.getCurrentModelPanel();
		if (modelPanel != null) {
			DrawVertexActivity activity = new DrawVertexActivity(modelHandler, modelEditorManager, modelPanel.getEditorActionType());
			modeButton.setColors(programPreferences.getActiveColor1(), programPreferences.getActiveColor2());
			modelPanel.changeActivity(activity);
		}
	}

	private void drawBox(ModeButton modeButton) {
		ModelPanel modelPanel = ProgramGlobals.getCurrentModelPanel();
		if (modelPanel != null) {
			DrawBoxActivity activity = new DrawBoxActivity(modelHandler, modelEditorManager, 1, 1, 1);
			modeButton.setColors(programPreferences.getActiveColor1(), programPreferences.getActiveColor2());
			modelPanel.changeActivity(activity);
		}
	}

	private void drawPlane(ModeButton modeButton) {
		ModelPanel modelPanel = ProgramGlobals.getCurrentModelPanel();
		if (modelPanel != null) {
			DrawPlaneActivity activity = new DrawPlaneActivity(modelHandler, modelEditorManager, 1, 1, 1);
			modeButton.setColors(programPreferences.getActiveColor1(), programPreferences.getActiveColor2());
			modelPanel.changeActivity(activity);
		}
	}

	public JPanel getAnimationBasicsPanel() {
		JPanel animationBasicsPanel = new JPanel(new MigLayout("fill"));

		ToolbarButtonGroup2<ModelEditorActionType3> actionTypeGroup = ProgramGlobals.getActionTypeGroup();

//		JPanel editToolsPanel = new JPanel(new MigLayout("debug, fill"));
		JPanel editToolsPanel = new JPanel(new MigLayout("fill"));
		editToolsPanel.setBorder(BorderFactory.createTitledBorder("Manipulate"));
		editToolsPanel.add(actionTypeGroup.getModeButton(ModelEditorActionType3.TRANSLATION), "wrap");
		editToolsPanel.add(actionTypeGroup.getModeButton(ModelEditorActionType3.ROTATION), "wrap");
		editToolsPanel.add(actionTypeGroup.getModeButton(ModelEditorActionType3.SCALING), "wrap");
		editToolsPanel.add(actionTypeGroup.getModeButton(ModelEditorActionType3.SQUAT), "wrap");

		animationBasicsPanel.add(editToolsPanel, "growx, growy, wrap");

		boolean temp = false;
//		boolean temp = true;
		if (temp) {
			TSpline tSpline = new TSpline();
			animationBasicsPanel.add(tSpline, "wrap, growy");
		}

//		editToolsPanel.add(new JLabel("UGG"), "wrap");
		return animationBasicsPanel;
	}

	private void showVPPopup(JButton button) {
		if (contextMenu != null) {
			contextMenu.show(button, 0, 0);
		}
	}

	public void setAnimationModeState(boolean animationModeState) {
		modeCardPanel.show(animationModeState ? "ANIM" : "MESH");
//		if (animationModeState) {
////			modelingOptionsCardLayout.show(modelingOptionsCardPanel, ANIMATIONBASICS);
//			modelingOptionsCardPanel.show(ANIMATIONBASICS);
//		} else {
////			modelingOptionsCardLayout.show(modelingOptionsCardPanel, modelingOptionsBox.getSelectedItem().toString());
////			modelingOptionsCardPanel.show(modelingOptionsBox.getSelectedItem().toString());
//			modelingOptionsCardPanel.show("Mesh Basics");
//		}
	}

	public void setModelPanel(ModelPanel modelPanel) {
		if (modelPanel != null) {
			this.modelHandler = modelPanel.getModelHandler();
			this.modelEditorManager = modelPanel.getModelEditorManager();
//			contextMenu = new ViewportPopupMenu(viewportListener.getViewport(), ProgramGlobals.getMainPanel(), modelPanel.getModelHandler(), modelPanel.getModelEditorManager());
			contextMenu = new ViewportPopupMenu(null, ProgramGlobals.getMainPanel(), modelPanel.getModelHandler(), modelPanel.getModelEditorManager());

			transformPanel.setModel(modelHandler, modelEditorManager);
			reloadAnimationList();
		} else {
			this.modelHandler = null;
			this.modelEditorManager = null;
			contextMenu = null;
			transformPanel.setModel(null, null);
		}
	}

	public void reloadAnimationList() {
		Sequence selectedItem = (Sequence) animationChooserBox.getSelectedItem();
//		animationChooserBoxModel.getSelectedItem()
		List<Sequence> allSequences = new ArrayList<>();
		allSequences.addAll(modelHandler.getModel().getAnims());
		allSequences.addAll(modelHandler.getModel().getGlobalSeqs());
		TwiComboBoxModel<Sequence> animationChooserBoxModel = new TwiComboBoxModel<>(allSequences);
		animationChooserBox.setModel(animationChooserBoxModel);
//		animationChooserBoxModel.removeAllElements();

//		EditableModel model = modelHandler.getModel();
//		for (Animation animation : model.getAnims()) {
//			animationChooserBoxModel.addElement(animation);
//		}

//		for (GlobalSeq globalSeq : model.getGlobalSeqs()) {
//			animationChooserBoxModel.addElement(globalSeq);
//		}
		if (animationChooserBoxModel.getSize() >= 1) {
			if (selectedItem != null && allSequences.contains(selectedItem)) {
//			animationChooserBoxModel.setSelectedItem(selectedItem);
				animationChooserBox.setSelectedItem(selectedItem);
			} else {
//				animationChooserBoxModel.setSelectedItem(0);
				animationChooserBox.setSelectedIndex(0);
			}
		}
	}
}
