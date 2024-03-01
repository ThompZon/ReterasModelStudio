package com.hiveworkshop.rms.ui.preferences.panels;

import com.hiveworkshop.rms.filesystem.GameDataFileSystem;
import com.hiveworkshop.rms.filesystem.sources.DataSourceDescriptor;
import com.hiveworkshop.rms.parsers.blp.BLPHandler;
import com.hiveworkshop.rms.parsers.slk.DataTableHolder;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.browsers.jworldedit.WEString;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.UnitEditorTree;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.WorldEditorDataType;
import com.hiveworkshop.rms.ui.browsers.model.ModelOptionPanel;
import com.hiveworkshop.rms.ui.browsers.mpq.MPQBrowser;
import com.hiveworkshop.rms.ui.browsers.unit.UnitOptionPanel;
import com.hiveworkshop.rms.ui.preferences.GUITheme;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.preferences.SaveProfileNew;
import com.hiveworkshop.rms.util.ThemeLoadingUtils;
import com.hiveworkshop.rms.util.uiFactories.Button;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.View;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProgramPrefWindow extends JFrame {
	private final ProgramPreferences programPreferences;
	private final ProgramPreferencesPanel programPreferencesPanel;

	public ProgramPrefWindow() {
		super("Preferences");
		programPreferences = ProgramGlobals.getPrefs().deepCopy();
		List<DataSourceDescriptor> priorDataSources = new ArrayList<>(SaveProfileNew.get().getDataSources());
		programPreferencesPanel = new ProgramPreferencesPanel(this, programPreferences, priorDataSources);

		JPanel prefPanel = new JPanel(new MigLayout("fill"));
		prefPanel.add(programPreferencesPanel, "growx, growy, spanx, wrap");
		prefPanel.add(Button.create("OK", e -> onOK()));
		prefPanel.add(Button.create("Cancel", e -> closeWindow()));

		setContentPane(prefPanel);
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	private void onOK() {
		saveSettings(programPreferences, programPreferencesPanel.getDataSources());
		closeWindow();
	}

	private void closeWindow() {
		updateThemeIfNotEqual(ProgramGlobals.getPrefs().getTheme(), programPreferences.getTheme());
		setVisible(false);
		dispose();
	}

	private static void updateThemeIfNotEqual(GUITheme wantedTheme, GUITheme otherTheme) {
		if (wantedTheme != otherTheme) {
			ThemeLoadingUtils.setTheme(wantedTheme);
			SwingUtilities.updateComponentTreeUI(ProgramGlobals.getMainPanel().getRootPane());
		}
	}

	public static void showPanel() {
		ProgramPrefWindow programPrefWindow = new ProgramPrefWindow();
		programPrefWindow.setLocationRelativeTo(ProgramGlobals.getMainPanel());

		programPrefWindow.setVisible(true);
	}


	private static void saveSettings(ProgramPreferences programPreferences,
	                                 List<DataSourceDescriptor> newDataSources) {
		ensurePrefsSet(programPreferences);

		ProgramPreferences realPrefs = ProgramGlobals.getPrefs();

		updateThemeIfNotEqual(programPreferences.getTheme(), realPrefs.getTheme());
		realPrefs.setFromOther(programPreferences);

//		ProgramGlobals.linkActions(ProgramGlobals.getMainPanel());
		ProgramGlobals.getUndoHandler().refreshUndo();

		boolean changedDataSources = (newDataSources != null) && SaveProfileNew.get().setDataSources(newDataSources);
		SaveProfileNew.save();
		if (changedDataSources) {
			updateDataSource();
		}
	}

	private static void ensurePrefsSet(ProgramPreferences programPreferences) {
		// sets ProgramPreference's corresponding strings of these settings
		// (which is the part that is saved)
		programPreferences.setKeyBindings(programPreferences.getKeyBindingPrefs());
		programPreferences.setEditorColors(programPreferences.getEditorColorPrefs());
		programPreferences.setCameraControlPrefs(programPreferences.getCameraControlPrefs());
		programPreferences.setNav3DMousePrefs(programPreferences.getNav3DMousePrefs());
		programPreferences.setUiElementColors(programPreferences.getUiElementColorPrefs());
	}

	public static void updateDataSource() {
		GameDataFileSystem.refresh(SaveProfileNew.get().getDataSources());
		// cache priority order...
		UnitOptionPanel.dropRaceCache();
		DataTableHolder.dropCache();
		ModelOptionPanel.dropCache();
		WEString.dropCache();
		BLPHandler.get().dropCache();
		ProgramGlobals.getMenuBar().updateTeamColors();
		traverseAndReloadData(ProgramGlobals.getRootWindowUgg());
	}


	public static void traverseAndReloadData(DockingWindow window) {
		int childWindowCount = window.getChildWindowCount();
		for (int i = 0; i < childWindowCount; i++) {
			DockingWindow childWindow = window.getChildWindow(i);
			traverseAndReloadData(childWindow);
			if (childWindow instanceof View view) {
				Component component = view.getComponent();
				if (component instanceof JScrollPane pane) {
					Component viewportView = pane.getViewport().getView();
					if (viewportView instanceof UnitEditorTree unitEditorTree) {
						WorldEditorDataType dataType = unitEditorTree.getDataType();
						if (dataType == WorldEditorDataType.UNITS) {
							System.out.println("saw unit tree");
							unitEditorTree.setUnitDataAndReloadVerySlowly();
						} else if (dataType == WorldEditorDataType.DOODADS) {
							System.out.println("saw doodad tree");
							unitEditorTree.setUnitDataAndReloadVerySlowly();
						}
					}
				} else if (component instanceof MPQBrowser comp) {
					System.out.println("saw mpq tree");
					comp.refreshTree();
				}
			}
		}
	}
}
