package com.ouken.phone.app.oukenstudioapp.editor.ui.menu;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Null;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.MenuItem;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.group.MenuItemGroup;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.editor.utils.PaneSpriteDrawable;

public abstract class ContextMenu<T> extends Pane{
	
	private static final Logger log = new Logger(ContextMenu.class.getName(), Logger.DEBUG);
	
	private static final float 
		WIDTH = 128,//192, 
		HEIGHT = 128;

	private Skin skin;
	private Array<MenuItem<T>> items = new Array<>();
	
	private Vector2 visiblePos = new Vector2();
	private T selected;
	
	public ContextMenu(ShapeRenderer renderer, Skin skin) {
		super(renderer);
		this.skin = skin;
		
		paneColor(DARK_GRAY).addShadow();
		setSize(WIDTH, HEIGHT);
		top().left();
		defaults().width(WIDTH);
		//addActor(getInputListenerActor());
		setTouchable(Touchable.enabled);
		setVisible(false);
	}
	
	// -- public methods --
	
	public Skin getSkin() {
		return skin;
	}
	

	
	
	/**returns the selected object upon which changes are performed or null*/
	public abstract T getSelected();
	
	
	public void addItem(MenuItem<T> item) {
		if(item == null)return;
		items.add(item);
		if(!item.isHidden())add(item).row();
		item.addListener(createContentClickListener(item));
		pack();
	}
	
	public void addItemGroup(MenuItemGroup<T> group) {
		if(group == null)return;
		for(MenuItem<T> item : group.getItems())addItem(item);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		//log.debug("menu visibility set to " + visible);
		
		if(visible && getStage() != null) {
			// update visible pos
			visiblePos.set(Gdx.input.getX(), Gdx.input.getY());
			getStage().screenToStageCoordinates(visiblePos);
			
			updateSelected();
			
			// to not select invisible item (even though they should ne touchable when invisible)
			setTouchable(Touchable.enabled);
			
			// update items and table
			boolean changed = false;
			for(MenuItem<T> item : items) {
				item.onMenuMadeVisible(this, selected);
				// after onMenuShown since there one can disable items
				if(item.hasItemChanged()) {
					changed = true;
					item.setItemChanged(false);
				}
			}
			if(changed)updateMenuTable();
		}else if(!visible) {
			setTouchable(Touchable.disabled);
		}
		
	}
	
	/**mouse stage position when the menu was set to visible = true*/
	public float getVisibleStageX() {
		return visiblePos.x;
	}
	
	/**mouse stage position when the menu was set to visible = true*/
	public float getVisibleStageY() {
		return visiblePos.y;
	}


	// -- private methods --
	private ClickListener createContentClickListener(MenuItem<T> item) {
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				item.onClick(selected);
				if(item.hideMenuOnClick())setVisible(false);
			}
		};
	}
	
	private void updateSelected() {
		selected = getSelected();
	}

	private void updateMenuTable() {
		for(MenuItem<T> item : items)item.remove();
		
		items.forEach(item -> {
			if(!item.isHidden())add(item).row();
		});
		pack();
	}


	
}
