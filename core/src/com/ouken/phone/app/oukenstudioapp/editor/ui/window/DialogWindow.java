package com.ouken.phone.app.oukenstudioapp.editor.ui.window;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.editor.utils.WindowPane;

public class DialogWindow extends WindowPane{

	private Skin skin;
	private TextButtonStyle style;
	private Table buttonContainer;
	
	
	public DialogWindow(String headerLabel, String message, Skin skin,ShapeRenderer renderer) {
		super(headerLabel, null, renderer);
		this.skin = skin;
		init(message);
	}

	private void init(String message) {		
		Table content = new Table();
		content.center();
		content.defaults().pad(PAD_4);
		
		style = new TextButtonStyle(skin.get(SkinNames.IMAGE_TEXT_BUTTON_STYLE_GRAY10, ImageTextButtonStyle.class));
		Label label = new Label(message, skin.get(SkinNames.LABEL_STYLE_12, LabelStyle.class));
		
		float bW = 44, bH = bW /2;
		buttonContainer = new Table();
		buttonContainer.defaults().size(bW, bH).pad(PAD_4*2);
		
		content.add(label).row();
		content.add(buttonContainer);
		
		addContent(content);
	}
	
	public TextButton addButton(String text) {
		return addButton(text, null);
	}
	
	public TextButton addButton(String text, Runnable onClick) {
		TextButton button = new TextButton(text,style);
		button.center();
		buttonContainer.add(button);
		buttonContainer.pack();
		
		if(onClick != null) {
			button.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					onClick.run();
				}
			});
		}
		return button;
	}
	
	
	


}
