package com.ouken.phone.app.oukenstudioapp.editor.ui.window;

import java.util.function.Consumer;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton.ImageTextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.editor.utils.WindowPane;

public class TextFieldWindow extends WindowPane{
	
	private static final Logger log = new Logger(TextFieldWindow.class.getName(), Logger.DEBUG);
	
	private static final String INVALID = "Invalid!";
	
	private Skin skin;
	
	private TextField tf;
	private Label message;
	private Array<Consumer<TextField>> 
		onSuccValidation = new Array<Consumer<TextField>>(),
		onFailedValidation = new Array<Consumer<TextField>>();
	private TFValidatable validatable;
	
	/**closes the window afterwards (removes it from stage)*/
	public TextFieldWindow(String header, String textFieldPreText, Skin skin, ShapeRenderer renderer) {
		super(header, null, renderer);
		this.skin = skin;

		addContent(createContent(textFieldPreText));
	}

	private Table createContent(String textFieldPreText) {
		Table container = new Table();
		container.pad(Pane.PAD_2);
		container.center();
		container.defaults().space(PAD_4);
		
		float tfWidth = 128;
		float bW = 44, bH = bW /2;
		
		Label name = new Label("Name: ", skin, SkinNames.LABEL_STYLE_12);
		tf = new TextField(textFieldPreText, skin, SkinNames.TEXT_FIELD_STYLE12);
		
		message = new Label("", skin, SkinNames.LABEL_STYLE_12);
		
		ImageTextButtonStyle style = new ImageTextButtonStyle(skin.get(SkinNames.IMAGE_TEXT_BUTTON_STYLE_GRAY10, ImageTextButtonStyle.class));
		style.checked = null;
		ImageTextButton okButton = new ImageTextButton("OK",style);
		
		container.add(name);
		container.add(tf).width(tfWidth).row();
		container.add(message).colspan(2).row();
		container.add(okButton).size(bW, bH).colspan(2);
		
		
		okButton.addListener(new ClickListener() {
			
			@Override
			public void clicked(InputEvent event, float x, float y) {
				doValidate();
			}
			
		});
		
		
		//stage.setKeyboardFocus(tf); // stage not initialized yet
		this.addListener(new InputListener() {
			
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if(keycode == Keys.ENTER) {
					doValidate();
					return true;
				}else if(keycode == Keys.ESCAPE) {
					closeWindow();
					return true;
				}
				return false;
			}
		});
		
		return container;
	}
	
	
	
	public TextField getTextField() {
		return tf;
	}
	
	public String getTextFieldText() {
		return tf.getText();
	}
	
	public void setTextFieldText(String text) {
		tf.setText(text);
	}
	
	public Label getMessageLabel() {
		return message;
	}
	
	/**
	 * 
	 * @param run called if the project name input validation was sucessful and
	 * the project dir could be created
	 */
	public void addOnSuccValidation(Consumer<TextField> consume) {
		this.onSuccValidation.add(consume);
	}
	
	public void addOnFailedValidation(Consumer<TextField> consume) {
		this.onSuccValidation.add(consume);
	}
	
	public void setOnTextFieldValidation(TFValidatable validatable) {
		this.validatable = validatable;
	}
	
	public static interface TFValidatable{
		boolean isValid(TextField textField);
	}
	
	
	
	// -- private --
	
	private boolean isValid() {
		boolean isValid = validatable == null ? true : validatable.isValid(tf);

		if (!isValid) {
			message.setText(INVALID);
			return false;
		}

		message.setText("");
		return true;
	}
	
	private boolean doValidate() {
		if(isValid()) {
			log.debug("is valid!");
			onSuccess();
			closeWindow();
			return true;
		}
		log.debug("not valid!");
		onFailure();
		return false;
	}
	
	private void onFailure() {
		if(onFailedValidation.notEmpty())onFailedValidation.forEach(e -> e.accept(tf));
	}
	
	private void onSuccess() {
		if(onSuccValidation.notEmpty())onSuccValidation.forEach(e -> e.accept(tf));
	}
	
}
