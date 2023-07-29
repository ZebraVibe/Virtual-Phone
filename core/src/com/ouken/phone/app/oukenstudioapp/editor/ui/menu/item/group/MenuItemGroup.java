package com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.group;

import com.badlogic.gdx.utils.Array;
import com.ouken.phone.app.oukenstudioapp.editor.ui.menu.item.MenuItem;

/**
 * 
 * @param <T> the selected object type
 */
public class MenuItemGroup<T> {
	private final Array<MenuItem<T>> items = new Array<>();
	
	public Array<MenuItem<T>> getItems(){
		return items;
	}
	
	public void add(MenuItem<T> item) {
		if(item != null)items.add(item);
	}
	
	public void insert(int index, MenuItem<T> item) {
		if(item != null)items.insert(index, item);
	}
	
}
