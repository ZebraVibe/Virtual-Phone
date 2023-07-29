package com.ouken.phone.app.oukenstudioapp.editor.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.Explorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.CustomNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;
/**
 * 
 * @author sebas
 *
 * @param <E> explorer type
 * @param <N> custom node type
 * @param <V> node value type
 */
public abstract class ExplorerTreeContextMenu<E extends Explorer<N, V>, N extends CustomNode<N, V>, V> extends ContextMenu<N>{
	
	private static final Logger log = new Logger(ExplorerTreeContextMenu.class.getName(), Logger.DEBUG);

	private E explorer;
	
	public ExplorerTreeContextMenu(E explorer, ShapeRenderer renderer, Skin skin) {
		super(renderer, skin);
		this.explorer = explorer;
	}

	public E getExplorer() {
		return explorer;
	}
	
	@Override
	public final N getSelected() {
		return explorer.getExplorerTree().getSelectedNode();
	}
	

	
}
