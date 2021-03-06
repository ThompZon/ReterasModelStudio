package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.Material;

public class GeosetShell {
	private EditableModel model;
	private String modelName;
	private String name;
	private int index;
	private boolean isImported;
	private Geoset geoset;
	private Geoset importGeoset;
	private boolean doImport = true;
	private Material oldMaterial;
	private Material newMaterial;
	private boolean isEnabled = true;

	public GeosetShell(Geoset geoset, EditableModel model, boolean isImported) {
		this.geoset = geoset;
		this.model = model;
		this.isImported = isImported;
		modelName = model.getName();
		name = geoset.getName();
		index = model.getGeosetId(geoset);
		oldMaterial = geoset.getMaterial();
	}

	public Geoset getGeoset() {
		return geoset;
	}

	public boolean isDoImport() {
		return doImport;
	}

	public GeosetShell setDoImport(boolean doImport) {
		this.doImport = doImport;
		return this;
	}

	public Material getMaterial() {
		if (newMaterial != null) {
			return newMaterial;
		}
		return oldMaterial;
	}

	public Material getOldMaterial() {
		return oldMaterial;
	}

	public GeosetShell setOldMaterial(Material oldMaterial) {
		this.oldMaterial = oldMaterial;
		return this;
	}

	public Geoset getImportGeoset() {
		return importGeoset;
	}

	public GeosetShell setImportGeoset(Geoset importGeoset) {
		this.importGeoset = importGeoset;
		return this;
	}

	@Override
	public String toString() {
		return model.getName() + ": " + geoset.getName();
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public GeosetShell setEnabled(boolean enabled) {
		isEnabled = enabled;
		return this;
	}

	public EditableModel getModel() {
		return model;
	}

	public GeosetShell setModel(EditableModel model) {
		this.model = model;
		return this;
	}

	public String getName() {
		return name;
	}

	public GeosetShell setName(String name) {
		this.name = name;
		return this;
	}

	public int getIndex() {
		return index;
	}

	public GeosetShell setIndex(int index) {
		this.index = index;
		return this;
	}

	public boolean isImported() {
		return isImported;
	}

	public GeosetShell setImported(boolean imported) {
		isImported = imported;
		return this;
	}

	public Material getNewMaterial() {
		return newMaterial;
	}

	public GeosetShell setNewMaterial(Material newMaterial) {
		this.newMaterial = newMaterial;
		return this;
	}

	public String getModelName() {
		return modelName;
	}

	public GeosetShell setModelName(String modelName) {
		this.modelName = modelName;
		return this;
	}
}
