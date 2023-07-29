package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Null;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.ContextMenu;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;

/**
 * 
 * @author sebas
 *
 * @param <T> selected object type
 */
public abstract class MenuItem<T> extends Button {
	
	private static final Logger log = new Logger(MenuItem.class.getName(), Logger.DEBUG);
	
	private static final float MAX_ICON_SIZE = 20;
	
	private Label label;
	
	private boolean hide;
	private boolean itemChanged;
	
	
	public MenuItem(String text, Skin skin) {
		this(text, null, skin);
	}
	
	public MenuItem(String text, TextureRegion icon, Skin skin) {
		Table iconTable = new Table();
		if(icon != null) {
			Image image = new Image(icon);
			iconTable.add(image);
		}
		label = new Label(text, skin.get(SkinNames.LABEL_STYLE_10, LabelStyle.class));
		setSkin(skin);
		setStyle(skin.get(SkinNames.PRESSED_BUTTON_STYLE_DARKEST_GRAY, ButtonStyle.class));
		left();
		defaults().pad(Pane.PAD_2);
		add(iconTable).size(MAX_ICON_SIZE);
		add(label);
	}
	
	public Label getLabel() {
		return label;
	}
	
	public String getLabelText() {
		return label.getText().toString();
	}
	
	public boolean hasItemChanged() {
		return itemChanged;
	}
	
	
	public void setItemChanged(boolean changed) {
		itemChanged = changed;
	}
	
	/**hide the menu when the context is shown*/
	public void setHideItem(boolean hide) {
		if(this.hide != hide)itemChanged = true;
		//log.debug("hide : " + hide + ". changed? " + itemChanged);
		this.hide = hide;
	}
	
	/**weather or not the menu item is shown*/
	public boolean isHidden() {
		return hide;
	}

	/**True by default*/
	public boolean hideMenuOnClick() {
		return true;
	}
	
	/**called whenever the menu is made visible. Therefore before {@link #onClick(Object)} */
	public void onMenuMadeVisible(ContextMenu<T> menu, T selected) {}
	
	/**called when the menu item is clicked*/
	public abstract void onClick(@Null T selected);

	
	
	
	
}
