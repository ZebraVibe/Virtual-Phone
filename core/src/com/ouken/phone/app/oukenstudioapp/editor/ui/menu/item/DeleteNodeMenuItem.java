package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.CustomNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;

public class DeleteNodeMenuItem<N extends CustomNode<N, V>, V> extends DeleteMenuItem<N> {

	public DeleteNodeMenuItem(String text, Skin skin, ShapeRenderer renderer) {
		this(text, null, skin, renderer);
	}

	public DeleteNodeMenuItem(String text, TextureRegion icon, Skin skin, ShapeRenderer renderer) {
		super(text, icon, skin, renderer);
	}

	@Override
	public void onDelete(N selected) {
		if (selected.getTree() != null)selected.getTree().getSelection().remove(selected);
		selected.remove();
	}

	@Override
	public String getDeletedObjectName(N selected) {
		return selected.getLabelText();
	}
	


}
