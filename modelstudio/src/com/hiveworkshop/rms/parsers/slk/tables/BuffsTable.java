package com.hiveworkshop.rms.parsers.slk.tables;

import com.hiveworkshop.rms.filesystem.GameDataFileSystem;
import com.hiveworkshop.rms.parsers.slk.DataTable;
import com.hiveworkshop.rms.parsers.slk.DataTableUtils;
import com.hiveworkshop.rms.parsers.slk.StringKey;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class BuffsTable extends DataTable {
	//	Map<StringKey, Element> dataTable = new LinkedHashMap<>();
	String[] sklDatafiles = {"Units\\AbilityBuffData.slk"};
	String[] txtFiles = {
			"Units\\CampaignAbilityFunc.txt",
			"Units\\CampaignAbilityStrings.txt",
			"Units\\CommonAbilityFunc.txt",
			"Units\\CommonAbilityStrings.txt",
			"Units\\HumanAbilityFunc.txt",
			"Units\\HumanAbilityStrings.txt",
			"Units\\NeutralAbilityFunc.txt",
			"Units\\NeutralAbilityStrings.txt",
			"Units\\NightElfAbilityFunc.txt",
			"Units\\NightElfAbilityStrings.txt",
			"Units\\OrcAbilityFunc.txt",
			"Units\\OrcAbilityStrings.txt",
			"Units\\UndeadAbilityFunc.txt",
			"Units\\UndeadAbilityStrings.txt",
			"Units\\ItemAbilityFunc.txt",
			"Units\\ItemAbilityStrings.txt"};

	public BuffsTable() {
//		loadBuffs();
		loadStuff(sklDatafiles, txtFiles, true);
	}

	@Override
	public Set<String> keySet() {
		Set<String> outputKeySet = new HashSet<>();
		Set<StringKey> internalKeySet = dataTable.keySet();
		for (StringKey key : internalKeySet) {
			outputKeySet.add(key.getString());
		}
		return outputKeySet;
	}

	public void loadBuffs() {
		try {
			for (String sklData : sklDatafiles) {
				DataTableUtils.readSLK(this, GameDataFileSystem.getDefault().getResourceAsStream(sklData));
			}
			for (String txt : txtFiles) {
				DataTableUtils.readTXT(this, GameDataFileSystem.getDefault().getResourceAsStream(txt), true);
			}
		} catch (final IOException e) {
			ExceptionPopup.display(e);
		}
	}

//	@Override
//	public Element get(final String id) {
//		return dataTable.get(new StringKey(id));
//	}
//
//	@Override
//	public void setValue(final String id, final String field, final String value) {
//		get(id).setField(field, value);
//	}
//
//	public void put(final String id, final Element e) {
//		dataTable.put(new StringKey(id), e);
//	}

}
