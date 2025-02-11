package com.hiveworkshop.rms.ui.browsers.jworldedit.objects.sorting.units;

import com.hiveworkshop.rms.ui.browsers.jworldedit.WEString;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.datamodel.MutableGameObject;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.sorting.AbstractSortingFolderTreeNode;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.sorting.SortingFolderTreeNode;
import com.hiveworkshop.rms.ui.browsers.jworldedit.objects.sorting.general.SortRace;
import com.hiveworkshop.rms.util.War3ID;

import javax.swing.tree.TreeNode;

public class UnitRaceLevelFolder extends AbstractSortingFolderTreeNode {
	/**
	 * default generated id to stop warnings, not going to serialize these folders
	 */
	private static final long serialVersionUID = 1L;
	private static final War3ID UNIT_CATEGORIZE_CAMPAIGN_FIELD = War3ID.fromString("ucam");
	private final UnitMeleeLevelFolder melee;
	private final UnitMeleeLevelFolder campaign;
	private final UnitMeleeLevelFolder hidden;

	public UnitRaceLevelFolder(SortRace race) {
		super(WEString.getString(race.getDisplayName()));
		this.melee = new UnitMeleeLevelFolder(WEString.getString("WESTRING_MELEE"));
		this.campaign = new UnitMeleeLevelFolder(WEString.getString("WESTRING_CAMPAIGN"));
		this.hidden = new UnitMeleeLevelFolder(WEString.getString("WESTRING_ITEMSTATUS_HIDDEN"));
	}

	@Override
	public SortingFolderTreeNode getNextNode(MutableGameObject object) {
		if (object.readSLKTagBoolean("hiddenInEditor")) {
			return hidden;
		}
		boolean isCampaign = object.getFieldAsBoolean(UNIT_CATEGORIZE_CAMPAIGN_FIELD, 0);
		return isCampaign ? campaign : melee;
	}

	//	@Override
	public int getSortIndex(SortingFolderTreeNode childNode) {
		if (childNode == hidden) {
			return 2;
		}
		return childNode == melee ? 0 : 1;
	}

	@Override
	public int getSortIndex(TreeNode childNode) {
		if (childNode == hidden) {
			return 2;
		}
		return childNode == melee ? 0 : 1;
	}
}
