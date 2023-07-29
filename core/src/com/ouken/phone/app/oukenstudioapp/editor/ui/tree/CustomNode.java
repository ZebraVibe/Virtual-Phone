package com.ouken.phone.app.oukenstudioapp.editor.ui.tree;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;

/**
 * 
 * @param <N> the node type
 * @param <V> the value type
 */
public abstract class CustomNode<N extends CustomNode, V> extends Node<N, V, Table> {

	private Skin skin;
	private Label label;
	
	public CustomNode(Skin skin, TextureRegion iconRegion) {
		this.skin = skin;
		
		Table content = new Table();
		content.setTouchable(Touchable.enabled);
		content.defaults().padLeft(Pane.PAD_4);
		
		setIcon(new TextureRegionDrawable(iconRegion));
		label = new Label("New Node", skin.get(SkinNames.LABEL_STYLE_10, LabelStyle.class));
		label.setTouchable(Touchable.disabled);
		
		content.add(label).expandX().row();
		content.pack();
		setActor(content);
		
		
		
		
	}
	
	public void updateTextAndValue(String text, V value) {
		setLabelText(text);
		setValue(value);
	}
	
	
	public void setLabelText(String text) {
		getLabel().setText(text);
	}
	
	public Label getLabel() {
		return label;
	}
	
	public String getLabelText() {
		return getLabel().getText().toString();
	
	}
	
	public Skin getSkin() {
		return skin;
	}

}
