package com.ouken.phone.app.oukenstudioapp.editor.ui.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.Explorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.FileExplorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.CustomNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;
import com.ouken.phone.app.oukenstudioapp.editor.utils.EditorUtils;
import com.ouken.phone.app.oukenstudioapp.editor.utils.ValueButton;

public class ExplorerBrowserContextMenu<E extends Explorer<N, V>, N extends CustomNode<N, V>, V> extends ContextMenu<ValueButton>{

	private static final Logger log = new Logger(ExplorerBrowserContextMenu.class.getName(), Logger.DEBUG);
	
	private E explorer;
	
	public ExplorerBrowserContextMenu(E explorer, ShapeRenderer renderer, Skin skin) {
		super(renderer, skin);
		this.explorer = explorer;
	}
	
	public E getExplorer() {
		return explorer;
	}

	@Override
	public ValueButton getSelected() {
		if(explorer.getStage() == null)throw new NullPointerException("explorer stage must not be null");
		Actor a = explorer.getStage().hit(getVisibleStageX(), getVisibleStageY(), true);
		
//		EditorUtils.mark(getVisibleStageX(), getVisibleStageY(), getStage(), getSkin());
//		log.error( a instanceof ValueButton ? "selected is " + ValueButton.class.getSimpleName() :  
//			("selected is no " + ValueButton.class.getSimpleName() + ". NULL ? " + (a == null ? true : "false -> " + a)));
		
		if(a != null && a instanceof ValueButton) {//the button must be selected to be edited
			((ValueButton) a).setChecked(true);
			return (ValueButton)a;
		}
		return null;
	}

}
