package com.hiveworkshop.rms.ui.application.tools;

import com.hiveworkshop.rms.editor.actions.UndoAction;
import com.hiveworkshop.rms.editor.actions.animation.animFlag.AddAnimFlagAction;
import com.hiveworkshop.rms.editor.actions.animation.animFlag.RemoveAnimFlagAction;
import com.hiveworkshop.rms.editor.actions.util.CompoundAction;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.GeosetAnim;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.editor.model.animflag.Entry;
import com.hiveworkshop.rms.editor.model.animflag.FloatAnimFlag;
import com.hiveworkshop.rms.editor.model.animflag.Vec3AnimFlag;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.edit.mesh.activity.UndoManager;
import com.hiveworkshop.rms.util.FramePopup;
import com.hiveworkshop.rms.util.TwiComboBox;
import com.hiveworkshop.rms.util.Vec3;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class GeosetAnimEditPanel extends JPanel {
	private GeosetAnim donGeosetAnim;
	private boolean invertVis = false;
	private boolean copyVis = true;
	private boolean copyColor = true;
	private FlipColor flipColor = FlipColor.DONT_FLIP;

	private final UndoManager undoManager;

	/**
	 * Create the panel.
	 */
	public GeosetAnimEditPanel(EditableModel model, GeosetAnim recGeosetAnim, UndoManager undoManager) {
		this.undoManager = undoManager;
		setLayout(new MigLayout("fill", "[grow][grow]"));

		JTextArea info = new JTextArea("Copies all animation data from the chosen GeosetAnim to this GeosetAnim.");
		info.setEditable(false);
		info.setOpaque(false);
		info.setLineWrap(true);
		info.setWrapStyleWord(true);

		List<GeosetAnim> geosetAnims = model.getGeosetAnims();

		JCheckBox copy_alpha = new JCheckBox("copy visibility", true);
		copy_alpha.addActionListener(e -> copyVis = copy_alpha.isSelected());
		JCheckBox invert_alpha = new JCheckBox("invert visibility");
		invert_alpha.addActionListener(e -> invertVis = invert_alpha.isSelected());

		JCheckBox copy_color = new JCheckBox("copy color", true);
		copy_color.addActionListener(e -> copyColor = copy_color.isSelected());
		TwiComboBox<FlipColor> flipColorBox = new TwiComboBox<>(FlipColor.values());
		flipColorBox.addOnSelectItemListener(fc -> flipColor = fc);

		JButton copyButton = new JButton("Copy Animation Data");
		copyButton.addActionListener(e -> doCopy(recGeosetAnim, donGeosetAnim));

		add(info, "spanx, growx, wrap");
		add(getDonGeoAnimPanel(geosetAnims), "growx, aligny top, wrap");
		add(copy_alpha);
		add(copy_color, "wrap");
		add(invert_alpha);
		add(flipColorBox, "wrap");
		add(copyButton, "spanx, align center, wrap");
	}

	public static void show(JComponent parent, EditableModel model, GeosetAnim geosetAnim, UndoManager undoManager) {
		GeosetAnimEditPanel animCopyPanel = new GeosetAnimEditPanel(model, geosetAnim, undoManager);
		FramePopup.show(animCopyPanel, parent, geosetAnim.getName());
	}

	private JPanel getDonGeoAnimPanel(List<GeosetAnim> geosetAnims) {
		JPanel donGeoAnimPanel = new JPanel(new MigLayout("fill, gap 0"));
		donGeoAnimPanel.add(new JLabel("From:"), "wrap");


		TwiComboBox<GeosetAnim> comboBox = new TwiComboBox<>(geosetAnims);
		comboBox.addOnSelectItemListener(ga -> donGeosetAnim = ga);
		comboBox.setStringFunctionRender((ga) -> ((GeosetAnim) ga).getName());

		donGeosetAnim = geosetAnims.get(0);
		donGeoAnimPanel.add(comboBox, "wrap, growx");
		return donGeoAnimPanel;
	}

	private void doCopy(GeosetAnim recGeosetAnim, GeosetAnim donGeosetAnim) {
		ArrayList<UndoAction> actions = new ArrayList<>();

		for (AnimFlag<?> animFlag : donGeosetAnim.getAnimFlags()){
			if (copyVis && (animFlag.getName().equals(MdlUtils.TOKEN_ALPHA) || animFlag.getName().equals(MdlUtils.TOKEN_VISIBILITY))){
				AnimFlag<?> newAnimFlag = animFlag.deepCopy();
				if(recGeosetAnim.has(animFlag.getName())){
					actions.add(new RemoveAnimFlagAction(recGeosetAnim, recGeosetAnim.find(animFlag.getName()), null));
				}
				if(invertVis && animFlag instanceof FloatAnimFlag){
					FloatAnimFlag floatAnimFlag = (FloatAnimFlag) newAnimFlag;
					for(TreeMap<Integer, Entry<Float>> entryMap : floatAnimFlag.getAnimMap().values()){
						for(Entry<Float> entry : entryMap.values()){
							entry.setValue(Math.min(1f, Math.max(0f, 1f-entry.getValue())));
						}
					}
				}
				actions.add(new AddAnimFlagAction<>(recGeosetAnim, newAnimFlag, null));
			} else if (copyColor && animFlag.getName().equals(MdlUtils.TOKEN_COLOR)){
				AnimFlag<?> newAnimFlag = animFlag.deepCopy();
				if(recGeosetAnim.has(animFlag.getName())){
					actions.add(new RemoveAnimFlagAction(recGeosetAnim, recGeosetAnim.find(animFlag.getName()), null));
				}
				if(flipColor != FlipColor.DONT_FLIP && animFlag instanceof Vec3AnimFlag){
					Vec3AnimFlag floatAnimFlag = (Vec3AnimFlag) newAnimFlag;
					for(TreeMap<Integer, Entry<Vec3>> entryMap : floatAnimFlag.getAnimMap().values()){
						for(Entry<Vec3> entry : entryMap.values()){
							Vec3 value = entry.getValue();
							switch (flipColor){
								case DONT_FLIP  -> value.set(value.x, value.y, value.z);
								case RED_GREEN  -> value.set(value.y, value.x, value.z);
								case RED_BLUE   -> value.set(value.z, value.y, value.x);
								case GREEN_BLUE -> value.set(value.x, value.z, value.y);
								case RGB_BRG    -> value.set(value.z, value.x, value.y);
								case RGB_GBR    -> value.set(value.y, value.z, value.x);
							}
						}
					}
				}
				actions.add(new AddAnimFlagAction<>(recGeosetAnim, newAnimFlag, null));
			} else if (!animFlag.getName().equals(MdlUtils.TOKEN_COLOR)
					&& !animFlag.getName().equals(MdlUtils.TOKEN_ALPHA)
					&& !animFlag.getName().equals(MdlUtils.TOKEN_VISIBILITY)){
				AnimFlag<?> newAnimFlag = animFlag.deepCopy();
				if(recGeosetAnim.has(animFlag.getName())){
					actions.add(new RemoveAnimFlagAction(recGeosetAnim, recGeosetAnim.find(animFlag.getName()), null));
				}
				actions.add(new AddAnimFlagAction<>(recGeosetAnim, newAnimFlag, null));
			}
		}
		if(!actions.isEmpty()){
			undoManager.pushAction(new CompoundAction("Copy Geoset anim data", actions, ModelStructureChangeListener.changeListener::geosetsUpdated).redo());
		}
	}

	//RGB
	private enum FlipColor{
		DONT_FLIP   ("Don't flip"),
		RED_GREEN   ("red - green"),
		RED_BLUE    ("red - blue"),
		GREEN_BLUE  ("green - blue"),
		RGB_BRG     ("RGB - BRG"),
		RGB_GBR     ("RGB - GBR"),
		;
		String name;
		FlipColor(String name){
			this.name = name;
		}
	}

}
