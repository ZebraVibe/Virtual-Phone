package com.ouken.phone.app.oukenstudioapp.editor.ui.menu;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.group.EditFileBrowserMenuItemGroup;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.FileExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;

public class FileExplorerBrowserContextMenu extends ExplorerBrowserContextMenu<FileExplorer,FileHandleNode, FileHandle>{

	public FileExplorerBrowserContextMenu(FileExplorer explorer, ShapeRenderer renderer, Skin skin) {
		super(explorer, renderer, skin);
		
		addItemGroup(new EditFileBrowserMenuItemGroup(explorer, skin, renderer));
	}

	
	
}
