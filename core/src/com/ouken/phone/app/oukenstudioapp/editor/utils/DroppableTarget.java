package com.ouken.phone.app.oukenstudioapp.editor.utils;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Null;

public class DroppableTarget extends TextButton {
	
	private @Null Object obj;
	
	public DroppableTarget(String text, TextButtonStyle style) {
		super(text, style);
		getLabel().setTouchable(Touchable.disabled);
	}
	
	public void drop(@Null Object obj) {
		this.obj = obj;
	}
	
	public void drop(@Null Object obj, String newLabelText) {
		drop(obj);
		setText(newLabelText);
	}

	
	public @Null Object getDrop() {
		return obj;
	}
	
}
