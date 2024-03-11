package com.hiveworkshop.rms.ui.browsers.jworldedit.triggers.gui;

import com.hiveworkshop.rms.parsers.slk.DataTableHolder;
import com.hiveworkshop.rms.ui.browsers.jworldedit.WorldEditArt;
import com.hiveworkshop.rms.ui.browsers.jworldedit.WorldEditorSettings;
import com.hiveworkshop.rms.ui.browsers.jworldedit.triggers.TriggerTreeCellEditor;
import com.hiveworkshop.rms.ui.browsers.jworldedit.triggers.TriggerTreeCellRenderer;
import com.hiveworkshop.rms.ui.browsers.jworldedit.triggers.impl.Trigger;
import com.hiveworkshop.rms.ui.browsers.jworldedit.triggers.impl.TriggerCategory;
import com.hiveworkshop.rms.ui.browsers.jworldedit.triggers.impl.TriggerEnvironment;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Function;

public class TriggerTree extends JTree {
	private final TriggerEnvironment triggerEnvironment;
	private final TriggerEnvironmentRootNode root;
	private final GUIModelTriggerTreeController controller;

	public TriggerTree(final TriggerEnvironment triggerEnvironment) {
		super(new TriggerEnvironmentRootNode(triggerEnvironment));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		root = (TriggerEnvironmentRootNode) getModel().getRoot();
		this.triggerEnvironment = triggerEnvironment;
		WorldEditArt worldEditArt = new WorldEditArt(DataTableHolder.getWorldEditorData());
		WorldEditorSettings settings = new WorldEditorSettings();
		TriggerTreeCellRenderer triggerTreeCellRenderer = new TriggerTreeCellRenderer(settings, worldEditArt);
		setCellRenderer(triggerTreeCellRenderer);

		TriggerTreeCellEditor treeCellEditor = new TriggerTreeCellEditor(this, triggerTreeCellRenderer, settings, worldEditArt, triggerEnvironment);
		setCellEditor(treeCellEditor);
		controller = new GUIModelTriggerTreeController(this, triggerEnvironment, root, ((DefaultTreeModel) getModel()));
		setEditable(true);
		setDragEnabled(true);
		setDropMode(DropMode.INSERT);
		setTransferHandler(new TriggerTreeTransferHandler(controller));

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteNode");
		getActionMap().put("deleteNode", new AbstractAction() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				if (getSelectionCount() == 1) {
					Object lastPathComponent = getSelectionPath().getLastPathComponent();
					if (lastPathComponent instanceof TriggerTreeNode triggerNode) {
						controller.deleteTrigger(triggerNode.getTrigger());
					} else if (lastPathComponent instanceof TriggerCategoryTreeNode categoryNode) {
						controller.deleteCategory(categoryNode.getCategory());
					}
				}
			}
		});
	}

	public GUIModelTriggerTreeController getController() {
		return controller;
	}

	public void select(final TriggerCategory category) {
		final TriggerCategoryTreeNode node = root.getNode(category);
		stopEditing();
		setSelectionPath(new TreePath(new Object[] { root, node }));
	}

	public void select(final Trigger trigger) {
		final TriggerCategoryTreeNode categoryNode = root.getNode(trigger.getCategory());
		final TriggerTreeNode triggerNode = categoryNode.getNode(trigger);
		stopEditing();
		setSelectionPath(new TreePath(new Object[] { root, categoryNode, triggerNode }));
	}

	public Trigger createTrigger() {
		return createTrigger(controller::createTrigger);
	}

	public Trigger createTriggerComment() {
		return createTrigger(controller::createTriggerComment);
	}

	private Trigger createTrigger(Function<TriggerCategory, Trigger> triggerFunction) {
		TreePath selectionPath = getSelectionPath();
		if (selectionPath == null || !canCreateTrigger(selectionPath)) {
			throw new IllegalStateException("Cannot create trigger at selection");
		}
		final Object lastPathComponent = selectionPath.getLastPathComponent();
		if (lastPathComponent instanceof TriggerCategoryTreeNode node) {
			// category
			return triggerFunction.apply(node.getCategory());
		} else if (lastPathComponent instanceof TriggerTreeNode node) {
			int newTriggerIndex = node.getParent().getIndex(node) + 1;
			Trigger trigger = triggerFunction.apply(node.getTrigger().getCategory());
			controller.moveTrigger(trigger, trigger.getCategory(), newTriggerIndex);
			return trigger;
		} else {
			throw new IllegalStateException("Cannot create trigger with selection");
		}
	}

	public boolean canCreateTrigger(final TreePath selectionPath) {
		return selectionPath.getPathCount() >= 2;
	}

}