package com.hiveworkshop.rms.ui.application.MenuBar1;

import com.hiveworkshop.rms.ui.application.*;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.DisplayViewUgg;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.PerspectiveViewUgg;
import com.hiveworkshop.rms.ui.application.viewer.CameraPreviewView;
import com.hiveworkshop.rms.ui.application.viewer.EditUVsView;
import com.hiveworkshop.rms.ui.application.viewer.PreviewView;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.DoodadBrowserView;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.ObjectEditorFrame;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.UnitBrowserView;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelPanel;
import com.hiveworkshop.rms.ui.gui.modeledit.creator.ModelingCreatorToolsView;
import com.hiveworkshop.rms.ui.gui.modeledit.modelcomponenttree.ModelComponentsView;
import com.hiveworkshop.rms.ui.gui.modeledit.modelviewtree.ModelViewManagingView;
import com.hiveworkshop.rms.util.ModelDependentView;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.View;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import static com.hiveworkshop.rms.ui.application.MenuCreationUtils.createMenu;
import static com.hiveworkshop.rms.ui.application.MenuCreationUtils.createMenuItem;

public class WindowsMenu extends JMenu {

	private Map<ModelPanel, JMenuItem> modelMenuItemMap = new HashMap<>();

	public WindowsMenu() {
		super("Window");
		setMnemonic(KeyEvent.VK_W);
		getAccessibleContext().setAccessibleDescription("Allows the user to open various windows containing the program features.");

		JMenuItem resetViewButton = new JMenuItem("Reset Layout");
		resetViewButton.addActionListener(e -> ProgramGlobals.getRootWindowUgg().resetView());
		add(resetViewButton);

//		JMenuItem saveViewButton = new JMenuItem("Save Layout");
////		resetViewButton.addActionListener(e -> WindowHandler.resetView());
//		saveViewButton.addActionListener(e -> ProgramGlobals.getPrefs().saveViewMap());
//		add(saveViewButton);
		add(createMenuItem("Preview of current model", KeyEvent.VK_P, e -> FloatingWindowFactory.openNewWindowWithKB(new PreviewView().setModelPanel(ProgramGlobals.getCurrentModelPanel()), ProgramGlobals.getRootWindowUgg())));

		JMenu viewsMenu = getViewsMenu();
		add(viewsMenu);

		JMenu browsersMenu = createMenu("Browsers", KeyEvent.VK_B);
		add(browsersMenu);

		browsersMenu.add(createMenuItem("Data Browser", KeyEvent.VK_A, e -> openViewer(new MPQBrowserView())));
		browsersMenu.add(createMenuItem("Unit Browser", KeyEvent.VK_U, e -> openViewer(new UnitBrowserView())));
		browsersMenu.add(createMenuItem("Doodad Browser", KeyEvent.VK_D, e -> openViewer(new DoodadBrowserView())));

		JMenuItem hiveViewer = new JMenuItem("Hive Browser");
		hiveViewer.setMnemonic(KeyEvent.VK_H);
		hiveViewer.addActionListener(e -> MenuBarActions.openHiveViewer());


		add(createMenuItem("Object Editor", KeyEvent.VK_D, e -> ObjectEditorFrame.showObjectEditor()));
		addSeparator();
	}

	public void addModelPanelItem(ModelPanel modelPanel) {
		JMenuItem menuItem = new JMenuItem(modelPanel.getModel().getName());
		menuItem.setIcon(modelPanel.getIcon());
		menuItem.addActionListener(e -> ModelLoader.setCurrentModel(modelPanel));
		modelPanel.setJMenuItem(menuItem);
		modelMenuItemMap.put(modelPanel, menuItem);
		add(modelPanel.getMenuItem());
	}

	public void removeModelPanelItem(ModelPanel modelPanel) {
		remove(modelMenuItemMap.get(modelPanel));
		modelMenuItemMap.remove(modelPanel);
//		remove(modelPanel.getMenuItem());
	}

	public static void openViewer(View view) {
//		RootWindow rootWindow = ProgramGlobals.getMainPanel().getRootWindow();
		RootWindow rootWindow = ProgramGlobals.getRootWindowUgg();
		rootWindow.setWindow(new SplitWindow(true, 0.75f, rootWindow.getWindow(), view));
	}

	private JMenu getViewsMenu() {
		JMenu viewsMenu = createMenu("Views", KeyEvent.VK_V);

		RootWindowUgg rootWindow = ProgramGlobals.getRootWindowUgg();
		WindowHandler2 windowHandler2 = rootWindow.getWindowHandler2();

		viewsMenu.add(createMenuItem("Animation Preview", KeyEvent.VK_A, e -> windowHandler2.openNewWindowWithKB(new PreviewView().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
		viewsMenu.add(createMenuItem("Camera Preview", KeyEvent.VK_C, e -> windowHandler2.openNewWindowWithKB(new CameraPreviewView().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
		viewsMenu.add(createMenuItem("Modeling", KeyEvent.VK_M, e -> windowHandler2.openNewWindowWithKB(new ModelingCreatorToolsView().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
		viewsMenu.add(createMenuItem("Outliner", KeyEvent.VK_O, e -> windowHandler2.openNewWindowWithKB(new ModelViewManagingView().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
		viewsMenu.add(createMenuItem("Perspective", KeyEvent.VK_P, e -> windowHandler2.openNewWindowWithKB(new PerspectiveViewUgg().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
		viewsMenu.add(createMenuItem("Front", KeyEvent.VK_F, e -> windowHandler2.openNewWindowWithKB(new DisplayViewUgg("Front").setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
		viewsMenu.add(createMenuItem("Side", KeyEvent.VK_S, e -> windowHandler2.openNewWindowWithKB(new DisplayViewUgg("Side").setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
		viewsMenu.add(createMenuItem("Bottom", KeyEvent.VK_B, e -> windowHandler2.openNewWindowWithKB(new DisplayViewUgg("Bottom").setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));


		viewsMenu.add(createMenuItem("Edit UV's", KeyEvent.VK_U, e -> openEditUVsView(rootWindow, windowHandler2)));

		viewsMenu.add(createMenuItem("Contents", KeyEvent.VK_T, e -> windowHandler2.openNewWindowWithKB(new ModelComponentsView().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
		viewsMenu.add(createMenuItem("Footer", KeyEvent.VK_F, e -> FloatingWindowFactory.openNewWindowWithKB(windowHandler2.getTimeSliderView(), rootWindow)));
//
//		viewsMenu.add(createMenuItem("Animation Preview", KeyEvent.VK_A, e -> FloatingWindowFactory.openNewWindow(new PreviewView().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
//		viewsMenu.add(createMenuItem("Modeling", KeyEvent.VK_M, e -> FloatingWindowFactory.openNewWindow(new ModelingCreatorToolsView(windowHandler2.getViewportListener()).setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
//		viewsMenu.add(createMenuItem("Outliner", KeyEvent.VK_O, e -> FloatingWindowFactory.openNewWindow(new ModelViewManagingView().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
//		viewsMenu.add(createMenuItem("Perspective", KeyEvent.VK_P, e -> FloatingWindowFactory.openNewWindow(new PerspectiveViewUgg().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
//		viewsMenu.add(createMenuItem("Front", KeyEvent.VK_F, e -> FloatingWindowFactory.openNewWindow(new DisplayViewUgg("Front").setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
//		viewsMenu.add(createMenuItem("Side", KeyEvent.VK_S, e -> FloatingWindowFactory.openNewWindow(new DisplayViewUgg("Side").setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
//		viewsMenu.add(createMenuItem("Bottom", KeyEvent.VK_B, e -> FloatingWindowFactory.openNewWindow(new DisplayViewUgg("Bottom").setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
//
//		viewsMenu.add(createMenuItem("Contents", KeyEvent.VK_C, e -> FloatingWindowFactory.openNewWindow(new ModelComponentsView().setModelPanel(ProgramGlobals.getCurrentModelPanel()), rootWindow)));
//		viewsMenu.add(createMenuItem("Footer", KeyEvent.VK_F, e -> FloatingWindowFactory.openNewWindow(windowHandler2.getTimeSliderView(), rootWindow)));

		viewsMenu.add(createMenuItem("Matrix Eater Script", KeyEvent.VK_H, KeyStroke.getKeyStroke("control P"), e -> ScriptView.openScriptView()));
		return viewsMenu;
	}

	private void openEditUVsView(RootWindowUgg rootWindow, WindowHandler2 windowHandler2) {
		ModelDependentView view = new EditUVsView();
		System.out.println("sat model panel, opening window");
		windowHandler2.openNewWindowWithKB(view, rootWindow);
		view.setModelPanel(ProgramGlobals.getCurrentModelPanel());
		System.out.println("done opening window");
	}
}
