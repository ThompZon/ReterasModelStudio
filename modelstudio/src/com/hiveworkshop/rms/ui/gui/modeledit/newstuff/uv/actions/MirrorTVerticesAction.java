package com.hiveworkshop.rms.ui.gui.modeledit.newstuff.uv.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.hiveworkshop.rms.ui.gui.modeledit.UndoAction;
import com.hiveworkshop.rms.util.Vector2;

public final class MirrorTVerticesAction implements UndoAction {
	private final char[] DIMENSION_NAMES = { 'X', 'Y' };
	private final List<Vector2> selection;
	private final byte mirrorDim;
	private final double centerX;
	private final double centerY;

	public MirrorTVerticesAction(final Collection<? extends Vector2> selection, final byte mirrorDim,
			final double centerX, final double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.selection = new ArrayList<>(selection);
		this.mirrorDim = mirrorDim;
	}

	@Override
	public void undo() {
		doMirror();
	}

	@Override
	public void redo() {
		doMirror();
	}

	private void doMirror() {
		final Vector2 center = new Vector2(centerX, centerY);
		for (final Vector2 vert : selection) {
			vert.setCoord(mirrorDim, 2 * center.getCoord(mirrorDim) - vert.getCoord(mirrorDim));
		}
	}

	@Override
	public String actionName() {
		return "mirror UV " + DIMENSION_NAMES[mirrorDim];
	}

}
