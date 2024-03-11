package com.hiveworkshop.rms.ui.browsers.jworldedit.models;

import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.DoodadTabTreeBrowserBuilder;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.UnitEditorSettings;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.util.WE_Field;
import com.hiveworkshop.rms.util.TwiComboBox;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;


public class BetterDoodadModelSelector extends BetterSelector {
	private ArrayList<Integer> variants;
	private TwiComboBox<Integer> variantBox;
	private Integer variant = 0;

	public BetterDoodadModelSelector(UnitEditorSettings unitEditorSettings) {
		super(new DoodadTabTreeBrowserBuilder(), unitEditorSettings, WE_Field.DOODAD_FILE.getId(), WE_Field.DOODAD_VARIATIONS_FIELD.getId());
	}

	protected JPanel getRightPanel() {
		JPanel rightPanel = new JPanel(new MigLayout("fill, ins 0", "", ""));
		rightPanel.add(viewportPanel, "growx, growy, spanx, wrap");
		variants = new ArrayList<>();
		variantBox = new TwiComboBox<>(variants, 10000000);
		variantBox.addOnSelectItemListener(this::selectVariant);
		variantBox.addMouseWheelListener(e -> variantBox.incIndex(e.getWheelRotation()));
		rightPanel.add(variantBox);
		rightPanel.add(animationChooser);
		return rightPanel;
	}

	protected void loadUnitPreview() {
		variants.clear();
		int numberOfVariations = currentUnit.getFieldAsInteger(WE_Field.DOODAD_VARIATIONS_FIELD.getId(), 0);
		for (int i = 0; i < numberOfVariations; i++) {
			variants.add(i + 1);
		}
		variantBox.selectFirst();
		variantBox.setEnabled(1 < numberOfVariations);
		selectVariant(1);
	}

	protected void selectVariant(Integer selected) {
		variant = selected == null ? 0 : selected - 1;
		if (currentUnit != null) {
			String filepath = getFilePath(currentUnit, (variant));
			String gameObjectName = currentUnit.getName();
			openModel(filepath, gameObjectName);
		}
	}
	public String getCurrentFilePath() {
		if (currentUnit != null) {
			return getFilePath(currentUnit, variant);
		} else {
			return null;
		}
	}
}
