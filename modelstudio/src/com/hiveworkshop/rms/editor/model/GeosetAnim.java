package com.hiveworkshop.rms.editor.model;

import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlagUtils;
import com.hiveworkshop.rms.editor.model.animflag.FloatAnimFlag;
import com.hiveworkshop.rms.ui.application.edit.animation.TimeEnvironmentImpl;
import com.hiveworkshop.rms.util.Vec3;

import java.util.List;

/**
 * The geoset anims, heaven forbid they be forgotten.
 * <p>
 * Eric Theller 11/10/2011
 */
public class GeosetAnim extends TimelineContainer implements Named {
	private double staticAlpha = 1;
	private Vec3 staticColor = new Vec3(1, 1, 1);
	private Geoset geoset;
	private boolean dropShadow = false;

	public GeosetAnim(AnimFlag<?> flag) {
		add(flag);
	}

	public GeosetAnim(List<AnimFlag<?>> flags) {
		setAnimFlags(flags);
	}

	public GeosetAnim(Geoset g) {
		geoset = g;
	}

	private GeosetAnim(GeosetAnim other) {
		copyTimelines(other);
		geoset = other.geoset;
		staticAlpha = other.staticAlpha;
		staticColor = other.staticColor;
		dropShadow = other.dropShadow;
	}

	public String getVisTagname() {
		return geoset.getName();
	}

	@Override
	public String getName() {
		return geoset.getName() + "'s Anim";
	}

	@Override
	public void setName(String text) {
	}

	public GeosetAnim getMostVisible(GeosetAnim partner) {
		if ((getVisibilityFlag() != null) && (partner != null)) {
			FloatAnimFlag selfFlag = (FloatAnimFlag) getVisibilityFlag();
			FloatAnimFlag partnerFlag = (FloatAnimFlag) partner.getVisibilityFlag();
			if (partnerFlag != null) {
				FloatAnimFlag result = selfFlag.getMostVisible(partnerFlag);
				if (result == selfFlag) {
					return this;
				} else if (result == partnerFlag) {
					return partner;
				}
			}
		}
		return null;
	}

	@Override
	public String visFlagName() {
		return "Alpha";
	}

	public double getStaticAlpha() {
		return staticAlpha;
	}

	public void setStaticAlpha(double staticAlpha) {
		this.staticAlpha = staticAlpha;
	}

	public Vec3 getStaticColor() {
		return staticColor;
	}

	public void setStaticColor(Vec3 staticColor) {
		this.staticColor = staticColor;
	}

	public Geoset getGeoset() {
		return geoset;
	}

	public GeosetAnim setGeoset(Geoset geoset) {
		this.geoset = geoset;
		return this;
	}

	public boolean isDropShadow() {
		return dropShadow;
	}

	public void setDropShadow(boolean dropShadow) {
		this.dropShadow = dropShadow;
	}

	@Override
	public float getRenderVisibility(TimeEnvironmentImpl animatedRenderEnvironment) {
		return getRenderVisibility(animatedRenderEnvironment, (float) staticAlpha);
	}

	public Vec3 getRenderColor(TimeEnvironmentImpl animatedRenderEnvironment) {
		return getInterpolatedVector(animatedRenderEnvironment, "Color", staticColor);
	}

	public void copyVisibilityFrom(VisibilitySource other, EditableModel mdlr) {
		VisibilitySource temp = this;
		AnimFlag<?> visFlag = getVisibilityFlag();// might be null
		FloatAnimFlag newVisFlag;
		if (visFlag != null) {
			newVisFlag = (FloatAnimFlag) visFlag.getEmptyCopy();
		} else {
			newVisFlag = new FloatAnimFlag(temp.visFlagName());
		}
		// newVisFlag = new AnimFlag(temp.visFlagName());
		FloatAnimFlag flagNew = (FloatAnimFlag) other.getVisibilityFlag();
		// this is an element not favoring existing over imported
		for (Animation a : mdlr.getAnims()) {
			if (newVisFlag != null) {
				if (!newVisFlag.hasGlobalSeq()) {
					newVisFlag.deleteAnim(a);
					// All entries for visibility are deleted from original-based sources during imported animation times
				}
			}
		}
		if (flagNew != null && newVisFlag != null) {
			AnimFlagUtils.copyFrom(newVisFlag, flagNew);
		}
		setVisibilityFlag(newVisFlag);
	}

	public GeosetAnim deepCopy(){
		return new GeosetAnim(this);
	}
}
