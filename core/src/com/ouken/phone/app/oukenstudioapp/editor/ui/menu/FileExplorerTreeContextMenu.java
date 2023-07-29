package com.ouken.phone.app.oukenstudioapp.editor.ui.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.group.EditFileNodeMenuItemGroup;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.FileExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;

public class FileExplorerTreeContextMenu extends ExplorerTreeContextMenu<FileExplorer, FileHandleNode, FileHandle>{

	public FileExplorerTreeContextMenu(FileExplorer explorer, ShapeRenderer renderer, Skin skin) {
		super(explorer, renderer, skin);
		addItemGroup(new EditFileNodeMenuItemGroup(explorer, skin, renderer));
	}

}
