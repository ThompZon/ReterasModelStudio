package com.hiveworkshop.rms.ui.browsers.jworldedit.objects.better.fields.builders;

import com.hiveworkshop.rms.parsers.slk.GameObject;
import com.hiveworkshop.rms.parsers.slk.ObjectData;
import com.hiveworkshop.rms.ui.browsers.jworldedit.WEString;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.better.fields.AbstractObjectField;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.MutableGameObject;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.WorldEditorDataType;
import com.hiveworkshop.rms.util.War3ID;

import java.util.List;

public abstract class AbstractNoLevelsFieldBuilder extends AbstractFieldBuilder {

	public AbstractNoLevelsFieldBuilder(WorldEditorDataType worldEditorDataType) {
		super(worldEditorDataType);
	}

	@Override
	protected void makeAndAddFields(List<AbstractObjectField> fields, War3ID metaKey,
	                                GameObject metaDataField, MutableGameObject gameObject,
	                                ObjectData metaData) {
		fields.add(create(gameObject, metaData, metaKey, 0, false));
	}


	@Override
	protected String getDisplayName(ObjectData metaData, War3ID metaKey, int level, MutableGameObject gameObject) {
		GameObject metaDataFieldObject = metaData.get(metaKey.toString());
		String category = metaDataFieldObject.getField("category");
		String prefix = categoryName(category) + " - ";
		String displayName = metaDataFieldObject.getField("displayName");
		return prefix + WEString.getString(displayName);
	}

	@Override
	protected String getDisplayPrefix(ObjectData metaData, War3ID metaKey, int level, MutableGameObject gameObject) {
		return "";
	}
}
