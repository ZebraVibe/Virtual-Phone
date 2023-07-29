package com.ouken.phone.app.oukenstudioapp.editor.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

public class ButtonPane extends Pane {
	
	
	private Color upColor = new Color(DARK_GRAY.cpy()), overColor,
			checkedColor = new Color(LIGHT_GRAY.cpy()), labelColor = new Color(GREEN.cpy());
	private ButtonPaneType type = ButtonPaneType.CHECKABLE;
	private ButtonPaneGroup group;
	private Widget inputActor;
	private Label buttonLabel;

	
	public ButtonPane(ShapeRenderer renderer) {
		this(null, renderer);
	}
	
	public ButtonPane(String label, ShapeRenderer renderer) {
		this(label, 0, 0, renderer);
	}
	
	public ButtonPane(String label, float height, float pad, ShapeRenderer renderer) {
		super(renderer);
		paneColor(upColor);
	
		inputActor = getInputListenerActor();
		inputActor.setFillParent(true);
		inputActor.addListener(new InputListener() {
	

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(button != Buttons.LEFT)return false;
		
				if(type.isCheckable()) {
					if(group == null)setChecked(true);
					else group.handleCheck(ButtonPane.this);
					
				}else if(type.isPressable()) {
					setChecked(true);
					return true;
				}
				return false;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				setChecked(false);
			}
			

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if(overColor != null)enableBorder(true).borderColor(overColor);
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				if(overColor != null)enableBorder(false);
			}

			
		});
		
		pad(pad);
		
		if(label != null) {
			LabelStyle style = new LabelStyle();
			style.font = Fonts.getDefaultFontBySize(FontSize.x12);
			style.fontColor = labelColor;
			buttonLabel = new Label(label, style);
			buttonLabel.setTouchable(Touchable.disabled);
			
			if(height  > 0) {
				add(buttonLabel).height(height).expandX().center();
				pack();
			}
			else add(buttonLabel).expand().center();
		}
		inputActor.toFront();
	}
	
	

	public void setChecked(boolean checked) {
		if(checked)paneColor(checkedColor);
		else paneColor(upColor);
	}


	public ButtonPane setUpColor(Color up) {
		if(up == null)return this;
		upColor.set(up);
		paneColor(upColor);
		return this;
	}
	
	public ButtonPane setCheckedColor(Color checked) {
		if(checked == null)return this;
		checkedColor.set(checked);
		return this;
	}
	
	public ButtonPane setOverColor(Color color) {
		if(color == null)return this;
		if (overColor == null)overColor = new Color(color);
		else overColor.set(color);
		return this;
	}
	
	public ButtonPane setLabelColor(Color color) {
		if(color == null)return this;
		labelColor.set(color);
		return this;
	}
	
	
	

	public void setType(ButtonPaneType type) {
		this.type = type;
	}

	public void setGroup(ButtonPaneGroup group) {
		this.group = group;
	}

	public void setInputActor(Widget inputActor) {
		this.inputActor = inputActor;
	}

	/**buttons are {@link ButtonPaneType#CHECKABLE} by default*/
	public ButtonPane setButtonPaneType(ButtonPaneType type) {
		if(type != null)this.type = type;
		return this;
	}
	
	public ButtonPaneType getButtonType() {
		return type;
	}
	
	public Label getLabel() {
		return buttonLabel;
	}
	
	/**adds this button to the button group if its not contained*/
	public ButtonPane setButtonGroup(ButtonPaneGroup group) {
		this.group = group;
		if(!group.contains(this))group.add(this);
		return this;
	}
	
	
	public static enum ButtonPaneType{
		CHECKABLE, PRESSABLE;
		
		public boolean isCheckable() { return this == CHECKABLE;}
		
		public boolean isPressable() { return this == PRESSABLE;}
	}
	
	
	/**This class helps checking and unchecking buttons from a group 
	 * checks only one button at a time*/
	public static class ButtonPaneGroup{
		
		private Array<ButtonPane> buttons = new Array<ButtonPane>();
		
		public void add(ButtonPane ...buttons) {
			if(buttons != null) {
				
				for(ButtonPane button : buttons) {
					if(button == null)continue;
					this.buttons.add(button);
				}
				
			}
		}
		
		public boolean removeValue(ButtonPane value) {
			return buttons.removeValue(value, true);
		}
		
		public ButtonPane removeIndex(int index) {
			return buttons.removeIndex(index);
		}
		
		public boolean contains(ButtonPane vlaue) {
			return buttons.contains(vlaue, true);
		}
		
		private void handleCheck(ButtonPane value) {
			if (value == null)
				return;
			ButtonPaneType type = value.getButtonType();

			if (type.isCheckable()) {
				
				for (ButtonPane button : buttons) {
					
					if (button != value) {
						if (button.getButtonType().isCheckable()) {
							button.setChecked(false);
						}

					} else {
						button.setChecked(true);
					}
				}
				
			} else if (type.isPressable()) {
				value.setChecked(true);
			}
		}
		
	}
	
	

	
}
