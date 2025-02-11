package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Camera;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.ui.application.model.ComponentCameraPanel;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelPanel;
import com.hiveworkshop.rms.util.ModelDependentView;
import com.hiveworkshop.rms.util.TwiComboBox;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class CameraPreviewView extends ModelDependentView {
	private AnimationController animationController;
	private JScrollPane scrollPane;
	private PreviewPanel previewPanel;
	private JPanel smartPanel;
	private JPanel topLeftPanel;
	private JPanel viewMainPanel;
	private ComponentCameraPanel cameraPanel;
	private Camera chosenCamera;
	private RenderModel renderModel;
	private final CameraHandler cameraHandler;

	public CameraPreviewView() {
		super("Camera Preview", null, new JPanel());
		smartPanel = getSpecialPane();
		topLeftPanel = new JPanel(new MigLayout("fill", "", ""));
		viewMainPanel = new JPanel(new MigLayout("fill, ins 0, gap 0","[][]", "[grow][]"));
		previewPanel = new PreviewPanel();
		cameraHandler = previewPanel.getPerspectiveViewport().getCameraHandler();
		animationController = new AnimationController(previewPanel);
		this.setComponent(viewMainPanel);
	}

	@Override
	public CameraPreviewView setModelPanel(ModelPanel modelPanel) {
		viewMainPanel.removeAll();
		topLeftPanel.removeAll();
		if (modelPanel == null || modelPanel.getModelHandler().getModel().getCameras().isEmpty()) {
			scrollPane.setViewportView(new JPanel());
			previewPanel.setModel(null, false, null);
			animationController.setModel(null, true, null);
			renderModel = null;
		} else {
			ModelHandler modelHandler = modelPanel.getModelHandler();
			renderModel = modelHandler.getPreviewRenderModel();
			previewPanel.setModel(modelHandler, true, modelPanel.getViewportActivityManager());
			animationController.setModel(modelHandler, true, previewPanel.getCurrentAnimation());
			topLeftPanel.add(previewPanel, "wrap");
			topLeftPanel.add(getCameraChooserPanel(modelHandler.getModel()), "spanx, growx, wrap");
			cameraPanel = new ComponentCameraPanel(modelHandler);

			chosenCamera = modelHandler.getModel().getCameras().get(0);
			if(chosenCamera != null) {
				cameraPanel.setSelectedItem(chosenCamera);
				cameraHandler.setCamera(renderModel.getRenderNode(chosenCamera.getSourceNode()));
			}

			viewMainPanel.add(topLeftPanel, "");
			viewMainPanel.add(smartPanel, "wrap, growx");
			viewMainPanel.add(cameraPanel, "spanx, wrap");

			scrollPane.setViewportView(animationController);
		}
		reload();
		return this;
	}

	private TwiComboBox<Camera> getCameraChooserPanel(EditableModel model){
		TwiComboBox<Camera> cameraJComboBox = new TwiComboBox<>(model.getCameras());
		cameraJComboBox.addOnSelectItemListener(this::setChoosenCamera);
		cameraJComboBox.setStringFunctionRender(this::getCameraName);

		return cameraJComboBox;
	}

	private String getCameraName(Object object){
		if (object instanceof Camera){
			return ((Camera) object).getName();
		}
		return "null";
	}

	private void setChoosenCamera(Camera chosenCamera) {
		this.chosenCamera = chosenCamera;
		cameraHandler.setCamera(renderModel.getRenderNode(chosenCamera.getSourceNode()));
		if (cameraPanel != null){
			cameraPanel.setSelectedItem(chosenCamera);
		}
	}

	private JPanel getSpecialPane(){
		JPanel panel = new JPanel(new MigLayout("fill, gap 0, ins 0"));
		scrollPane = new JScrollPane(new JPanel());
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				scrollPane.setPreferredSize(panel.getSize());
			}
		});
		panel.add(scrollPane, "growx, growy");
		return panel;
	}

	@Override
	public CameraPreviewView reload() {
		if (animationController != null) {
			animationController.reload().repaint();
		}
		if (previewPanel != null) {
			previewPanel.reload().repaint();
		}
		if (cameraPanel != null && chosenCamera != null){
			cameraPanel.setSelectedItem(chosenCamera);
		}
		return this;
	}
}
