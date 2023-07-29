package com.ouken.phone.app.oukenstudioapp.editor.utils;

import java.io.File;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.EntityNode;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.FileHandleNode;

/**
 * 
 * @author sebas
 *
 * @param value type
 */
public class ValueButton extends Button {
	
	private Object value;
	
	public ValueButton(ButtonStyle style) {
		super(style);
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public boolean hasValue(ValueType type) {
		if(value == null)return false;
		switch (type) {
		case FILE_HANDLE:
			return value instanceof FileHandle;
		case ENTITY:
			return value instanceof Entity ;
		default:
			return false;
		}
	}
	
	public boolean hasFileHandleValue() {
		return hasValue(ValueType.FILE_HANDLE);
	}
	
	public boolean hasEntityValue() {
		return hasValue(ValueType.ENTITY);
	}
	
	public static enum ValueType{
		FILE_HANDLE, ENTITY;
	}
	
	
}
