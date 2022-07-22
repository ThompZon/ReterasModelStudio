package com.hiveworkshop.rms.ui.preferences.dataSourceChooser;

import com.hiveworkshop.rms.filesystem.sources.CascDataSourceDescriptor;
import com.hiveworkshop.rms.filesystem.sources.DataSourceDescriptor;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class DataSourceTree extends JTree {
	private final DefaultMutableTreeNode root;
	private final DefaultTreeModel model;
	private final List<DataSourceDescriptor> dataSourceDescriptors;
	private final Component popupParent;

	private JButton addDefaultCascPrefixes;
	private JButton addSpecificCascPrefix;
	private JButton deleteButton;
	private JButton moveUpButton;
	private JButton moveDownButton;

	public DataSourceTree(List<DataSourceDescriptor> dataSourceDescriptors, Component popupParent){
		this.dataSourceDescriptors = dataSourceDescriptors;
		this.root = new DefaultMutableTreeNode();
		this.model = new DefaultTreeModel(this.root);
		this.popupParent = popupParent;
		setModel(this.model);

		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setRootVisible(false);
		createButtons();
		addTreeSelectionListener(this::updateButtons);
	}

	public void move(boolean up) {
		TreePath[] selectionPaths = getSelectionPaths();
		int dir = up ? -1 : 1;
		for (TreePath selectionPath : selectionPaths) {
			DefaultMutableTreeNode lastComp = getNode(selectionPath);
			if (lastComp != null) {
				if (lastComp instanceof DataSourceDescTreeNode) {
					DataSourceDescriptor descriptor = ((DataSourceDescTreeNode) lastComp).getDescriptor();
					int indexOf = dataSourceDescriptors.indexOf(descriptor);
					if (indexOf + dir >= 0 && indexOf + dir < dataSourceDescriptors.size()) {
						Collections.swap(dataSourceDescriptors, indexOf, indexOf + dir);
					}
				} else {
					TreeNode parent = lastComp.getParent();
					if (parent instanceof DataSourceDescTreeNode) {
						DataSourceDescriptor parentDescriptor = ((DataSourceDescTreeNode) parent).getDescriptor();
						if (parentDescriptor instanceof CascDataSourceDescriptor) {
							((CascDataSourceDescriptor) parentDescriptor).movePrefix(parent.getIndex(lastComp), dir);
						}
					}
				}
				reloadTree();
			}
		}
	}

	private void createButtons(){
		addDefaultCascPrefixes = getButton("Add Default CASC Mod", e -> addDefaultCASCMod());
		addSpecificCascPrefix = getButton("Add Specific CASC Mod", e -> addSpecificCASCMod());
		deleteButton = getButton("Delete Selection", e -> deleteSelection());
		moveUpButton = getButton("Move Up", e -> move(true));
		moveDownButton = getButton("Move Down", e -> move(false));
	}

	private void updateButtons(TreeSelectionEvent e){
		TreePath selectionPath = e.getNewLeadSelectionPath();
		boolean cascSelected = isCascSelected(selectionPath);
		addDefaultCascPrefixes.setEnabled(cascSelected);
		addSpecificCascPrefix.setEnabled(cascSelected);

		deleteButton.setEnabled(selectionPath != null);

		moveUpButton.setEnabled(canMoveUp(selectionPath));
		moveDownButton.setEnabled(canMoveDown(selectionPath));
	}
	public JButton getAddDefaultCascButton(){
		return addDefaultCascPrefixes;
	}
	public JButton getAddSpecificCascButton(){
		return addSpecificCascPrefix;
	}
	public JButton getDeleteButton(){
		return deleteButton;
	}
	public JButton getMoveUpButton(){
		return moveUpButton;
	}
	public JButton getMoveDownButton(){
		return moveDownButton;
	}
	private JButton getButton(String buttonText, ActionListener actionListener) {
		JButton button = new JButton(buttonText);
		button.addActionListener(actionListener);
		button.setEnabled(false);
		return button;
	}



	public void deleteSelection() {
		TreePath[] selectionPaths = getSelectionPaths();
		for (TreePath selectionPath : selectionPaths) {
			DefaultMutableTreeNode lastComp = getNode(selectionPath);
			if (lastComp != null) {
				if (lastComp instanceof DataSourceDescTreeNode) {
					DataSourceDescriptor descriptor = ((DataSourceDescTreeNode) lastComp).getDescriptor();
					dataSourceDescriptors.remove(descriptor);
				} else {
					TreeNode parent = lastComp.getParent();
					if (parent instanceof DataSourceDescTreeNode) {
						final DataSourceDescriptor parentDescriptor = ((DataSourceDescTreeNode) parent).getDescriptor();
						if (parentDescriptor instanceof CascDataSourceDescriptor) {
							((CascDataSourceDescriptor) parentDescriptor).deletePrefix(parent.getIndex(lastComp));
						}
					}
				}
				reloadTree();
			}
		}
	}


	public void addDefaultCASCMod() {
		TreePath selectionPath = getSelectionPath();
		DefaultMutableTreeNode lastComp = getNode(selectionPath);
		if (lastComp instanceof DataSourceDescTreeNode) {
			DataSourceDescriptor descriptor = ((DataSourceDescTreeNode) lastComp).getDescriptor();
			if (descriptor instanceof CascDataSourceDescriptor) {
				CascDataSourceDescriptor casc = (CascDataSourceDescriptor) descriptor;
				casc.addPrefixes(CascPrefixChooser.addDefaultCASCPrefixes(Paths.get(casc.getGameInstallPath()), true, popupParent));
				reloadTree();
			}
		}
	}


	public void addSpecificCASCMod() {
		//JOptionPane.showInputDialog(DataSourceChooserPanel.this, "Enter the name of a CASC Mod:");
		final TreePath selectionPath = getSelectionPath();
		DefaultMutableTreeNode lastComp = getNode(selectionPath);
		if (lastComp != null && lastComp.getParent() instanceof DataSourceDescTreeNode) {
			lastComp = (DefaultMutableTreeNode) lastComp.getParent();
		}
		if (lastComp instanceof DataSourceDescTreeNode) {
			DataSourceDescriptor descriptor = ((DataSourceDescTreeNode) lastComp).getDescriptor();
			if (descriptor instanceof CascDataSourceDescriptor) {
				CascDataSourceDescriptor casc = (CascDataSourceDescriptor) descriptor;
				String prefix = CascPrefixChooser.getSpecificPrefix(Paths.get(casc.getGameInstallPath()), popupParent);
				if(prefix != null){
					casc.addPrefix(prefix);
				}
				reloadTree();
			}
		}
	}

	public void reloadTree() {
		Set<String> selectionSet = new HashSet<>();
		TreePath[] selectionPaths = getSelectionPaths();
		int rowCount = getRowCount();

		if (selectionPaths != null) {
			Arrays.stream(selectionPaths).forEach(sp -> selectionSet.add(sp.toString()));
		}
		for (int i = root.getChildCount() - 1; i >= 0; i--) {
			model.removeNodeFromParent((MutableTreeNode) root.getChildAt(i));
		}
		for (DataSourceDescriptor descriptor : dataSourceDescriptors) {
			DataSourceDescTreeNode newChild = new DataSourceDescTreeNode(descriptor);
			if (descriptor instanceof CascDataSourceDescriptor) {
				final CascDataSourceDescriptor cascDescriptor = (CascDataSourceDescriptor) descriptor;
				if (cascDescriptor.getPrefixes().isEmpty()) {
					newChild.setUserObject(newChild.getUserObject() + " (WARNING: No Mods Selected)");
				}
				for (final String prefix : cascDescriptor.getPrefixes()) {
					DefaultMutableTreeNode cascChild = new DefaultMutableTreeNode(prefix);
					model.insertNodeInto(cascChild, newChild, newChild.getChildCount());
					if(selectionSet.contains(new TreePath(model.getPathToRoot(cascChild)).toString())){
						addSelectionPath(new TreePath(model.getPathToRoot(cascChild)));
					}
				}
			}
			model.insertNodeInto(newChild, root, root.getChildCount());

			if(selectionSet.contains(new TreePath(model.getPathToRoot(newChild)).toString())){
				addSelectionPath(new TreePath(model.getPathToRoot(newChild)));
			}
		}
		expandPath(new TreePath(root));
		for (int i = 0; i < getRowCount(); i++) {
			expandRow(i);
		}
		if (rowCount < getRowCount()){
			setSelectionRow(getRowCount()-1);
		}
	}

	public boolean isCascSelected(TreePath selectionPath) {
		DefaultMutableTreeNode lastComp = getNode(selectionPath);
		if (lastComp != null && lastComp.getParent() instanceof DataSourceDescTreeNode) {
			lastComp = (DefaultMutableTreeNode) lastComp.getParent();
		}
		return lastComp instanceof DataSourceDescTreeNode
				&& ((DataSourceDescTreeNode) lastComp).getDescriptor() instanceof CascDataSourceDescriptor;
	}

	public boolean canMoveUp(TreePath selectionPath) {
		DefaultMutableTreeNode lastComp = getNode(selectionPath);
		if (lastComp != null && lastComp.getParent() instanceof DataSourceDescTreeNode) {
			DataSourceDescTreeNode parent = (DataSourceDescTreeNode) lastComp.getParent();
			DataSourceDescriptor descriptor = parent.getDescriptor();

			if (descriptor instanceof CascDataSourceDescriptor) {
				return 0 < parent.getIndex(lastComp);
			}
		} else if (lastComp instanceof DataSourceDescTreeNode){
			return 0 < dataSourceDescriptors.indexOf(((DataSourceDescTreeNode)lastComp).getDescriptor());
		}
		return false;
	}

	public boolean canMoveDown(TreePath selectionPath) {
		DefaultMutableTreeNode lastComp = getNode(selectionPath);
		if (lastComp != null && lastComp.getParent() instanceof DataSourceDescTreeNode) {
			DataSourceDescTreeNode parent = (DataSourceDescTreeNode) lastComp.getParent();
			DataSourceDescriptor descriptor = parent.getDescriptor();

			if (descriptor instanceof CascDataSourceDescriptor) {
				return parent.getIndex(lastComp) < parent.getChildCount()-1;
			}
		} else if (lastComp instanceof DataSourceDescTreeNode){
			return dataSourceDescriptors.indexOf(((DataSourceDescTreeNode)lastComp).getDescriptor()) < dataSourceDescriptors.size()-1;
		}
		return false;
	}


	public void showMessage(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	public DefaultMutableTreeNode getNode(TreePath selectionPath) {
		if (selectionPath != null) {
			Object lastPathComponent = selectionPath.getLastPathComponent();
			if (lastPathComponent instanceof DefaultMutableTreeNode) {
				return  (DefaultMutableTreeNode) lastPathComponent;
			}
		}
		return null;
	}
}
