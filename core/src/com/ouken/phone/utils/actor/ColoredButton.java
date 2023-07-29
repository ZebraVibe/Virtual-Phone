package com.ouken.phone.utils.actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ColoredButton extends Button{
	private Color tmp = new Color();
	private Color disabledColor, pressedColor;
	
	public ColoredButton(ButtonStyle style, Color pressedColor) {
		super(style);
		this.pressedColor = pressedColor;
	}
	
	public ColoredButton(TextureRegion up, Color pressedColor) {
		super(new TextureRegionDrawable(up));
		this.pressedColor = pressedColor;
	}
	
	public ColoredButton(TextureRegion up, Color pressedColor, float disabledColorAlpha) {
		super(new TextureRegionDrawable(up));
		this.pressedColor = pressedColor;
		disabledColor = getColor().cpy();
		disabledColor.a = disabledColorAlpha;
	}
	

	@Override
	public void draw(Batch batch, float parentAlpha) {
			Color old = tmp.set(getColor());
			if(isDisabled() && disabledColor != null) {
				setColor(disabledColor);
			}else if(isPressed() && pressedColor != null){
				setColor(pressedColor);
			}
			super.draw(batch, parentAlpha);
			setColor(old);
	}
}
