package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.Matrix;
import com.hiveworkshop.rms.ui.gui.modeledit.renderers.BoneShellListCellRenderer;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;

public class BoneAttachmentEditPanel extends JPanel {

	JCheckBox displayParents;
	BoneShellListCellRenderer renderer;
	ModelHolderThing mht;
	public JTabbedPane geosetAnimTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

	public BoneAttachmentEditPanel(ModelHolderThing mht) {
		setLayout(new MigLayout("gap 0, fill", "[grow]", "[][grow]"));
		this.mht = mht;

		renderer = new BoneShellListCellRenderer(mht.receivingModel, mht.donatingModel);

		add(getTopPanel(), "align center, wrap");

		for (GeosetShell geosetShell : mht.allGeoShells) {
			final BoneAttachmentPanel geoPanel = new BoneAttachmentPanel(mht, renderer);
			geoPanel.setGeoset(geosetShell);
			ImageIcon imageIcon = ImportPanel.greenIcon;
			if (geosetShell.isFromDonating()) {
				imageIcon = ImportPanel.orangeIcon;
			}
			geosetAnimTabs.addTab(geosetShell.getModelName() + " " + (geosetShell.getIndex() + 1), imageIcon, geoPanel, "Click to modify animation data for Geoset " + geosetShell.getIndex() + " from " + geosetShell.getModelName() + ".");
		}

		add(geosetAnimTabs, "growx, growy");

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				super.componentShown(e);
				for (GeosetShell geosetShell : mht.allGeoShells) {
					for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
						final BoneAttachmentPanel geoPanel = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
						if (geoPanel.getSelectedGeoset() == geosetShell) {
							geosetAnimTabs.setEnabledAt(i, geosetShell.isDoImport());
						}
					}
				}
			}
		});
	}

	private JPanel getTopPanel() {
		JPanel topPanel = new JPanel(new MigLayout("gap 0", "[][][]"));

		JButton allMatrOriginal = new JButton("Reset all Matrices");
		allMatrOriginal.addActionListener(e -> allMatrOriginal());
		topPanel.add(allMatrOriginal);

		JButton allMatrSameName = new JButton("Set all to available, original names");
//		allMatrSameName.setToolTipText("matches matrix bones in the receiving model to bones with the same name (ignoring case) in the donating model");
		allMatrSameName.setToolTipText("matches matrices bones to bones with the same name (ignoring case)");
		allMatrSameName.addActionListener(e -> allMatrSameName());
		topPanel.add(allMatrSameName);

		JButton allMatrSameishName = new JButton("Set all to similar names");
		allMatrSameishName.setToolTipText("ignores \"bone\", \"helper\", \"_\" and space.");
		allMatrSameishName.addActionListener(e -> allMatrSameishName());
		topPanel.add(allMatrSameishName, "wrap");

		displayParents = new JCheckBox("Display parent names");
		displayParents.addActionListener(e -> showParents(renderer, displayParents));
		topPanel.add(displayParents, "spanx, align center, wrap");

		return topPanel;
	}

	private void showParents(BoneShellListCellRenderer renderer, JCheckBox checkBox) {
		renderer.setShowParent(checkBox.isSelected());
		repaint();
	}

	public void allMatrOriginal() {
		for (GeosetShell geosetShell : mht.allGeoShells) {
			if (geosetShell.isDoImport()) {
				for (MatrixShell ms : geosetShell.getMatrixShells()) {
					ms.resetMatrix();
				}
			}
		}
	}

	public void allMatrSameName() {
		IterableListModel<IdObjectShell<?>> futureBoneList = mht.getFutureBoneList();

		Map<String, IdObjectShell<?>> nameMap = new HashMap<>();
		for (IdObjectShell<?> boneShell : futureBoneList) {
			if (boneShell.getShouldImport()) {
				nameMap.put(boneShell.getName(), boneShell);
			}
		}

		for (GeosetShell geosetShell : mht.allGeoShells) {
			if (geosetShell.isDoImport()) {
				for (MatrixShell matrixShell : geosetShell.getMatrixShells()) {
					matrixShell.clearNewBones();
					final Matrix matrix = matrixShell.getMatrix();
					// For look to find similarly named stuff and add it
					for (final Bone bone : matrix.getBones()) {
						final String mName = bone.getName();
						if (nameMap.get(mName) != null) {
							matrixShell.addNewBone(nameMap.get(mName));
						}
					}
				}
			}
		}
	}
	public void allMatrSameishName() {
		// this will disregard some parts of bone names, increasing numbers of matched for some models
		// placeholder "UGG" is used to make sure words isn't  merged and potentially losing more parts than necessary
		// "ShelpBoneEric" -> "shelpUGGeric" -> "shelperic" and not "ShelpBoneEric" -> "shelperic" -> "sic"
		IterableListModel<IdObjectShell<?>> futureBoneList = mht.getFutureBoneList();

		Map<String, IdObjectShell<?>> nameMap = new HashMap<>();
		String placeholder = "UGG";
		for (IdObjectShell<?> boneShell : futureBoneList) {
			if (boneShell.getShouldImport()) {
				String name = boneShell.getName().toLowerCase()
						.replaceAll("bone", placeholder)
						.replaceAll("helper", placeholder)
						.replaceAll("_", "")
						.replaceAll(" ", "")
						.replaceAll(placeholder, "");
				nameMap.put(name, boneShell);
			}
		}

		for (GeosetShell geosetShell : mht.allGeoShells) {
			if (geosetShell.isDoImport()) {
				for (MatrixShell matrixShell : geosetShell.getMatrixShells()) {
					matrixShell.clearNewBones();
					final Matrix matrix = matrixShell.getMatrix();
					// For look to find similarly named stuff and add it
					for (final Bone bone : matrix.getBones()) {
						final String mName = bone.getName().toLowerCase()
								.replaceAll("bone", placeholder)
								.replaceAll("helper", placeholder)
								.replaceAll("_", "")
								.replaceAll(" ", "")
								.replaceAll(placeholder, "");
						if (nameMap.get(mName) != null) {
							matrixShell.addNewBone(nameMap.get(mName));
						}
					}
				}
			}
		}
	}
}
