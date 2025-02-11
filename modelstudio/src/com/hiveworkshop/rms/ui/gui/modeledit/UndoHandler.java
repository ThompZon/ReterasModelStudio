package com.hiveworkshop.rms.ui.gui.modeledit;

import com.hiveworkshop.rms.ui.application.MainPanel;
import com.hiveworkshop.rms.ui.application.ProgramGlobals;
import com.hiveworkshop.rms.ui.application.actionfunctions.ActionFunction;
import com.hiveworkshop.rms.ui.application.edit.RedoActionImplementation;
import com.hiveworkshop.rms.ui.application.edit.UndoActionImplementation;
import com.hiveworkshop.rms.ui.language.TextKey;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;

import javax.swing.*;
import java.util.NoSuchElementException;

public class UndoHandler {
	UndoMenuItem undo;
	RedoMenuItem redo;
	UndoActionFunction undoFunc;
	RedoActionFunction redoFunc;

	AbstractAction undoAction;
	AbstractAction redoAction;

	public UndoHandler() {
		undoAction = new UndoActionImplementation(TextKey.UNDO.toString());
		redoAction = new RedoActionImplementation(TextKey.REDO.toString());

		undoFunc = new UndoActionFunction();
		redoFunc = new RedoActionFunction();

		undo = (UndoMenuItem) undoFunc.getMenuItem();

//		undo = new UndoMenuItem("Undo");
////		undo.addActionListener(undoAction);
//		undo.addActionListener(e -> undo());
////		undo.setAccelerator(KeyStroke.getKeyStroke("control Z"));
		undo.setEnabled(undo.funcEnabled());

		redo = (RedoMenuItem) redoFunc.getMenuItem();
//		redo = new RedoMenuItem("Redo");
////		redo.addActionListener(redoAction);
//		redo.addActionListener(e -> redo());
////		redo.setAccelerator(KeyStroke.getKeyStroke("control Y"));
		redo.setEnabled(redo.funcEnabled());
	}

	public void refreshUndo() {
		undo.setAccelerator(ProgramGlobals.getKeyBindingPrefs().getKeyStroke(TextKey.UNDO));
		redo.setAccelerator(ProgramGlobals.getKeyBindingPrefs().getKeyStroke(TextKey.REDO));
		undo.setEnabled(undo.funcEnabled());
		redo.setEnabled(redo.funcEnabled());
	}

	public UndoMenuItem getUndo() {
		return undo;
	}

	public RedoMenuItem getRedo() {
		return redo;
	}

	public AbstractAction getUndoAction() {
		return undoAction;
	}

	public AbstractAction getRedoAction() {
		return redoAction;
	}

	static class UndoMenuItem extends JMenuItem {

		public UndoMenuItem(final String text) {
			super(text);
		}

		@Override
		public String getText() {
			if (funcEnabled()) {
				return "Undo " + ProgramGlobals.getCurrentModelPanel().getUndoManager().getUndoText();
			} else {
				return "Can't undo";
			}
		}

		public boolean funcEnabled() {
			try {
				return !ProgramGlobals.getCurrentModelPanel().getUndoManager().isUndoListEmpty();
			} catch (final NullPointerException e) {
				return false;
			}
		}
	}

	static class RedoMenuItem extends JMenuItem {

		public RedoMenuItem(final String text) {
			super(text);
		}

		@Override
		public String getText() {
			if (funcEnabled()) {
				return "Redo " + ProgramGlobals.getCurrentModelPanel().getUndoManager().getRedoText();
			} else {
				return "Can't redo";
			}
		}

		public boolean funcEnabled() {
			try {
				return !ProgramGlobals.getCurrentModelPanel().getUndoManager().isRedoListEmpty();
			} catch (final NullPointerException e) {
				return false;
			}
		}
	}
	class UndoActionFunction extends ActionFunction {

		public UndoActionFunction() {
			super(TextKey.UNDO, UndoHandler.this::undo);
			this.menuItem = new UndoMenuItem(TextKey.UNDO.toString());
			menuItem.addActionListener(e -> undo());
//			menuItem.setAccelerator(getKeyStroke());
			setKeyStroke("control Z");
		}
	}

	class RedoActionFunction extends ActionFunction {

		public RedoActionFunction() {
			super(TextKey.REDO, UndoHandler.this::redo);
			this.menuItem = new RedoMenuItem(TextKey.REDO.toString());
			menuItem.addActionListener(e -> redo());
//			menuItem.setAccelerator(getKeyStroke());
			setKeyStroke("control Y");
		}
	}

	public void undo() {
		final ModelPanel mpanel = ProgramGlobals.getCurrentModelPanel();
		final MainPanel mainPanel = ProgramGlobals.getMainPanel();
		if (mpanel != null) {
			try {
				mpanel.getUndoManager().undo();
			} catch (final NoSuchElementException exc) {
				JOptionPane.showMessageDialog(mainPanel, "Nothing to undo!");
			} catch (final Exception exc) {
				ExceptionPopup.display(exc);
			}
			mpanel.repaintSelfAndRelatedChildren();
		}
		ProgramGlobals.getUndoHandler().refreshUndo();
		mainPanel.repaintSelfAndChildren();
	}

	public void redo() {
		final ModelPanel mpanel = ProgramGlobals.getCurrentModelPanel();
		final MainPanel mainPanel = ProgramGlobals.getMainPanel();
		if (mpanel != null) {
			try {
				mpanel.getUndoManager().redo();
			} catch (final NoSuchElementException exc) {
				JOptionPane.showMessageDialog(mainPanel, "Nothing to redo!");
			} catch (final Exception exc) {
				ExceptionPopup.display(exc);
			}
			mpanel.repaintSelfAndRelatedChildren();
		}
		ProgramGlobals.getUndoHandler().refreshUndo();
		mainPanel.repaintSelfAndChildren();
	}
}
