package com.ouken.phone.utils.sprite;

public enum Listenable {
	enabled, disabled, childrenOnly;
	
	public boolean isEnabled() { return this == enabled; }
	public boolean isDisabled() { return this == disabled; }
	public boolean isChildrenOnly() { return this == childrenOnly; }
}
