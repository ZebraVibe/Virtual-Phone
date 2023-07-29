package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.group;

import java.util.function.Consumer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.utils.Pools;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.ContextMenu;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.CreateMenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.DeleteNodeMenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.MenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.RenameNodeMenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tabs.Explorer;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.CustomNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow.TFValidatable;

/**
 * 
 * @author sebas
 *
 * @param <E> explorer type
 * @param <N> custom node type
 * @param <V> value of node type
 */
public class EditNodeMenuItemGroup<E extends Explorer<N, V>, N extends CustomNode<N, V>, V> extends MenuItemGroup<N> {
	
	private E explorer;
	
	public EditNodeMenuItemGroup(@Null E explorer, Skin skin, ShapeRenderer renderer) {
		
		this.explorer = explorer;
		
		add(new CreateMenuItem<N>(getCreateMenuItemName(), skin.getRegion(getCreateIconRegionName()), skin, renderer) {
			@Override
			public void onClick(N selected, TextFieldWindow window) {
				onCreateClick(selected, window, null, null);
			}
		});
		
		add(new RenameNodeMenuItem<N, V>("Rename", skin, renderer) {
			@Override
			public void onClick(N selected, TextFieldWindow window) {
				onRenameClick(selected, window, null, null);
				//super.onClick(selected, window);
			}
		});
		
		add(new MenuItem<N>("Duplictae", skin) {
			@Override
			public void onClick(N selected) {
				onDupliateClick(selected);
			}
		});
		
		add(new DeleteNodeMenuItem<N, V>("Delete", skin, renderer) {
			
			@Override
			public boolean openWindowIf(N selected) {
				return openDeleteWindowOn(selected);
			}
			
			@Override
			public void onDelete(N selected) {
				onDeleteClick(selected);
				//super.onDelete(selected);
				explorer.removeNodeFromExplorer(selected, true);
			}
		});
	}
	
	public final Tree<N,V> getTree(){
		return explorer.getExplorerTree();
	}
	
	public final @Null E getExplorer(){
		return explorer;
	}
	
	public final void setExplorer(E explorer) {
		this.explorer = explorer;
	}
	
	public String getCreateMenuItemName() {
		return "Create Obj";
	}
	
	public String getCreateIconRegionName() {
		return null;
	}
	
	
	
	public boolean openDeleteWindowOn(N selected) {
		return selected != null;
	}
	
	public void onCreateClick(N selected, TextFieldWindow window, TFValidatable validate, Consumer<TextField> onSuccess) {
		addToWindow(window, validate, onSuccess);
	}
	
	public void onRenameClick(N selected, TextFieldWindow window, TFValidatable validate,Consumer<TextField> onSuccess) {
		addToWindow(window, validate, onSuccess);
	}
	
	
	
	public void onDupliateClick(N selected) {}
	
	/**@deprecated override {@link Explorer#removeNodeFromExplorer(CustomNode, boolean)} 
	 * to add logic when deleting a node. since when dragging and dropping node removeNode and addNode is called*/
	public void onDeleteClick(N selected) {}
	
	
	private void changed(Actor actor) {
		ChangeEvent event = Pools.obtain(ChangeEvent.class);
		actor.fire(event);
		Pools.free(event);
	}
	
	private  void addToWindow(TextFieldWindow window, TFValidatable validate,Consumer<TextField> onSuccess) {
		if(validate != null)window.setOnTextFieldValidation(validate);
		if(onSuccess != null)window.addOnSuccValidation(onSuccess);
	}
	
}
