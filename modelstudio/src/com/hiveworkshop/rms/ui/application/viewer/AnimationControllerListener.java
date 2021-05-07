package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.model.Animation;
import com.hiveworkshop.rms.ui.application.edit.animation.TimeEnvironmentImpl;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import org.lwjgl.LWJGLException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AnimationControllerListener extends JPanel {
	private final PerspectiveViewport perspectiveViewport;
	TimeEnvironmentImpl renderEnv;
	private ModelHandler modelHandler;

	public AnimationControllerListener(ModelHandler modelHandler, ProgramPreferences programPreferences, boolean doDefaultCamera) {
		this.modelHandler = modelHandler;
		try {
			renderEnv = modelHandler.getPreviewTimeEnv();
			modelHandler.getModelView().setVetoOverrideParticles(true);
			perspectiveViewport = new PerspectiveViewport(modelHandler.getModelView(), modelHandler.getPreviewRenderModel(), programPreferences, modelHandler.getPreviewTimeEnv(), doDefaultCamera);
			perspectiveViewport.setMinimumSize(new Dimension(200, 200));
			renderEnv.setAnimationTime(0);
			renderEnv.setLive(true);
		} catch (LWJGLException e) {
			throw new RuntimeException(e);
		}
		setLayout(new BorderLayout());
		add(perspectiveViewport, BorderLayout.CENTER);
	}

//	public void setModel(ModelView modelView) {
//		this.modelView = modelView;
//		perspectiveViewport.setModel(modelView);
//		reload();
//	}

	public void setTitle(String title) {
		setBorder(BorderFactory.createTitledBorder(title));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		perspectiveViewport.paint(perspectiveViewport.getGraphics());
	}

	public void reloadAllTextures() {
		perspectiveViewport.reloadAllTextures();
	}

	public void reload() {
		perspectiveViewport.reloadTextures();
	}

	public void setAnimation(Animation animation) {
		renderEnv.setAnimation(animation);
	}

	public void setAnimationTime(int time) {
		renderEnv.setAnimationTime(time);
	}

	public void playAnimation() {
		renderEnv.setRelativeAnimationTime(0);
		renderEnv.setLive(true);
	}

	public void setLoop(AnimationControllerListener.LoopType loopType) {
		renderEnv.setLoopType(loopType);
	}

	public void setSpeed(float speed) {
		renderEnv.setAnimationSpeed(speed);
	}

	public Animation getCurrentAnimation() {
		return renderEnv.getCurrentAnimation();
	}

	public void setLevelOfDetail(int levelOfDetail) {
		perspectiveViewport.setLevelOfDetail(levelOfDetail);
	}

	public BufferedImage getBufferedImage() {
		return perspectiveViewport.getBufferedImage();
	}

	public enum LoopType {
		DEFAULT_LOOP, ALWAYS_LOOP, NEVER_LOOP
	}
}
