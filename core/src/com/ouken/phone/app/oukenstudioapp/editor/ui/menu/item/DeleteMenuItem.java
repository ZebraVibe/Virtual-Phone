package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.CustomNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.DeleteWindow;

public abstract class DeleteMenuItem<T> extends WindowMenuItem<T, DeleteWindow>{

	public DeleteMenuItem(String text, Skin skin, ShapeRenderer renderer) {
		this(text, null, skin, renderer);
	}

	public DeleteMenuItem(String text, TextureRegion icon, Skin skin, ShapeRenderer renderer) {
		super(text, icon, skin, renderer);
	}
	
	@Override
	public DeleteWindow createWindow(T selected, ShapeRenderer renderer) {
		return new DeleteWindow(getDeletedObjectName(selected), getSkin(), renderer) {
			@Override
			public void onYesClick() {
				onDelete(selected);
			}
		};

	}

	@Override
	public void onClick(T selected, DeleteWindow window) {}
	
	@Override
	public boolean openWindowIf(T selected) {
		return selected != null;
	}
	
	/**called when clicking on yes in the delete window. By default the window is opened
	 *  ( i.e. this is method is called) if the selected value != null*/
	public abstract void onDelete(T selected);
	
	public abstract String getDeletedObjectName(T selected);
	
	

}
