package com.ouken.phone.app.oukenstudioapp.editor.ui.menu;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.group.EditEntityNodeMenuItemGroup;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.EntityExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.EntityNode;

public class EntityExplorerTreeContextMenu extends ExplorerTreeContextMenu<EntityExplorer, EntityNode, Entity> {

	public EntityExplorerTreeContextMenu(EntityExplorer explorer, ShapeRenderer renderer, Skin skin) {
		super(explorer, renderer, skin);
		
		addItemGroup(new EditEntityNodeMenuItemGroup(explorer, skin, renderer));
	}


}
