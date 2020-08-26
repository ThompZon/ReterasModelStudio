package com.hiveworkshop.rms.editor.model;

import com.hiveworkshop.rms.editor.model.util.ModelUtils;
import com.hiveworkshop.rms.editor.model.visitor.IdObjectVisitor;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLight;
import com.hiveworkshop.rms.parsers.mdlx.MdlxLight.Type;
import com.hiveworkshop.rms.ui.application.edit.mesh.viewport.axes.CoordinateSystem;
import com.hiveworkshop.rms.util.Vector3;

/**
 * Write a description of class Light here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Light extends IdObject {
	Type type = Type.OMNIDIRECTIONAL;
	int attenuationStart = 0;
	int attenuationEnd = 0;
	double intensity = 0;
	Vector3 staticColor = new Vector3();
	double ambIntensity = 0;
	Vector3 staticAmbColor = new Vector3();

	public Light() {

	}

	public Light(final String name) {
		this.name = name;
	}

	public Light(final Light light) {
		copyObject(light);

		type = light.type;
		attenuationStart = light.attenuationStart;
		attenuationEnd = light.attenuationEnd;
		intensity = light.intensity;
		staticColor = light.staticColor;
		ambIntensity = light.ambIntensity;
		staticAmbColor = light.staticAmbColor;
	}

	public Light(final MdlxLight light) {
		if ((light.flags & 512) != 512) {
			System.err.println("MDX -> MDL error: A light '" + light.name + "' not flagged as light in MDX!");
		}

		loadObject(light);

		type = light.type;
		setAttenuationStart((int)light.attenuation[0]);
		setAttenuationEnd((int)light.attenuation[1]);
		setStaticColor(new Vector3(light.color, true));
		setIntensity(light.intensity);
		setStaticAmbColor(new Vector3(light.ambientColor, true));
		setAmbIntensity(light.ambientIntensity);
	}

	public MdlxLight toMdlx() {
		final MdlxLight light = new MdlxLight();

		objectToMdlx(light);

		light.type = type;
		light.attenuation[0] = getAttenuationStart();
		light.attenuation[1] = getAttenuationEnd();
		light.color = ModelUtils.flipRGBtoBGR(getStaticColor().toFloatArray());
		light.intensity = (float)getIntensity();
		light.ambientColor = ModelUtils.flipRGBtoBGR(getStaticAmbColor().toFloatArray());
		light.ambientIntensity = (float)getAmbIntensity();
		
		return light;
	}

	@Override
	public Light copy() {
		return new Light(this);
	}

	public String getVisTagname() {
		return "light";// geoset.getName();
	}

	public int getAttenuationStart() {
		return attenuationStart;
	}

	public void setAttenuationStart(final int attenuationStart) {
		this.attenuationStart = attenuationStart;
	}

	public int getAttenuationEnd() {
		return attenuationEnd;
	}

	public void setAttenuationEnd(final int attenuationEnd) {
		this.attenuationEnd = attenuationEnd;
	}

	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(final double intensity) {
		this.intensity = intensity;
	}

	public Vector3 getStaticColor() {
		return staticColor;
	}

	public void setStaticColor(final Vector3 staticColor) {
		this.staticColor = staticColor;
	}

	public double getAmbIntensity() {
		return ambIntensity;
	}

	public void setAmbIntensity(final double ambIntensity) {
		this.ambIntensity = ambIntensity;
	}

	public Vector3 getStaticAmbColor() {
		return staticAmbColor;
	}

	public void setStaticAmbColor(final Vector3 staticAmbColor) {
		this.staticAmbColor = staticAmbColor;
	}

	@Override
	public void apply(final IdObjectVisitor visitor) {
		visitor.light(this);
	}

	@Override
	public double getClickRadius(final CoordinateSystem coordinateSystem) {
		return DEFAULT_CLICK_RADIUS / CoordinateSystem.Util.getZoom(coordinateSystem);
	}
}
