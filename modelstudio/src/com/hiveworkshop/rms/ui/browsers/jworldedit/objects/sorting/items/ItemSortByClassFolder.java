package com.hiveworkshop.rms.ui.browsers.jworldedit.objects.sorting.items;

import com.hiveworkshop.rms.parsers.slk.DataTable;
import com.hiveworkshop.rms.parsers.slk.DataTableHolder;
import com.hiveworkshop.rms.parsers.slk.Element;
import com.hiveworkshop.rms.ui.browsers.jworldedit.WEString;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.MutableGameObject;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.sorting.AbstractSortingFolderTreeNode;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.sorting.SortingFolderTreeNode;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.sorting.general.BottomLevelCategoryFolder;
import com.hiveworkshop.rms.util.War3ID;

import javax.swing.tree.TreeNode;
import java.util.*;

public final class ItemSortByClassFolder extends AbstractSortingFolderTreeNode {
	/**
	 * default generated id to stop warnings, not going to serialize these folders
	 */
	private static final long serialVersionUID = 1L;
	private static final War3ID ITEM_CLASS_FIELD = War3ID.fromString("icla");
	private static final Comparator<MutableGameObject> ITEM_NAME_COMPARATOR = Comparator.comparing(MutableGameObject::getName);
	private final Map<String, BottomLevelCategoryFolder> itemClassToTreeNode = new LinkedHashMap<>();
	private final List<BottomLevelCategoryFolder> itemClassesList = new ArrayList<>();

	public ItemSortByClassFolder(String displayName) {
		super(displayName);
		DataTable unitEditorData = DataTableHolder.getWorldEditorData();
		Element itemClasses = unitEditorData.get("itemClass");
		int numClasses = itemClasses.getFieldValue("NumValues");
		for (int i = 0; i < numClasses; i++) {
			String typeName = itemClasses.getField(String.format("%2d", i).replace(' ', '0'), 0);
			String tag = itemClasses.getField(String.format("%2d", i).replace(' ', '0'), 1);
			BottomLevelCategoryFolder classFolder = new BottomLevelCategoryFolder(WEString.getString(tag), ITEM_NAME_COMPARATOR);
			itemClassToTreeNode.put(typeName, classFolder);
			itemClassesList.add(classFolder);
		}
	}

	@Override
	public SortingFolderTreeNode getNextNode(MutableGameObject object) {
		String itemClass = object.getFieldAsString(ITEM_CLASS_FIELD, 0);
		if (!itemClassToTreeNode.containsKey(itemClass)) {
			return itemClassesList.get(itemClassesList.size() - 1);
		}
		return itemClassToTreeNode.get(itemClass);
	}

	//	@Override
	public int getSortIndex(SortingFolderTreeNode childNode) {
//		return itemClassesList.indexOf(childNode);

		if (childNode != null) {
			return itemClassesList.indexOf(childNode);
		}
		return -1;

	}

	@Override
	public int getSortIndex(TreeNode childNode) {
//		return itemClassesList.indexOf(childNode);

		if (childNode != null) {
			return itemClassesList.indexOf(childNode);
		}
		return -1;

	}
}
