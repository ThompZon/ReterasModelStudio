package com.hiveworkshop.rms.ui.browsers.jworldedit.objects.better.fields.builders;

import com.hiveworkshop.rms.parsers.slk.GameObject;
import com.hiveworkshop.rms.parsers.slk.ObjectData;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.better.fields.AbstractObjectField;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.MutableGameObject;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.WorldEditorDataType;
import com.hiveworkshop.rms.util.War3ID;

import java.util.List;

public abstract class AbstractLevelsFieldBuilder extends AbstractFieldBuilder {
	private final War3ID levelField;

	public AbstractLevelsFieldBuilder(WorldEditorDataType worldEditorDataType, War3ID levelField) {
		super(worldEditorDataType);
		this.levelField = levelField;
	}

	@Override
	protected final void makeAndAddFields(List<AbstractObjectField> fields, War3ID metaKey,
	                                      GameObject metaDataField, MutableGameObject gameObject, ObjectData metaData) {
		int repeatCount = metaDataField.getFieldValue("repeat");
		int actualRepeatCount = gameObject.getFieldAsInteger(levelField, 0);
		if (repeatCount >= 1 && actualRepeatCount > 1) {
			for (int level = 1; level <= actualRepeatCount; level++) {
				fields.add(create(gameObject, metaData, metaKey, level, true));
			}
		} else {
			fields.add(create(gameObject, metaData, metaKey, repeatCount >= 1 ? 1 : 0, false));
		}
	}
}
