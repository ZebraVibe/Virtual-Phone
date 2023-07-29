package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.CustomNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.window.TextFieldWindow;

public class RenameNodeMenuItem<N extends CustomNode<N, V>, V> extends RenameMenuItem<N>{

	public RenameNodeMenuItem(String text, Skin skin, ShapeRenderer renderer) {
		this(text, null, skin, renderer);
	}
	
	public RenameNodeMenuItem(String text, TextureRegion icon, Skin skin, ShapeRenderer renderer) {
		super(text, icon, skin, renderer);
	}
	
	@Override
	public void onWindowCreated(N selected, TextFieldWindow window, String header, String preText) {
		super.onWindowCreated(selected, window, header, selected == null ? "null" : selected.getLabelText());
	}
	
}
