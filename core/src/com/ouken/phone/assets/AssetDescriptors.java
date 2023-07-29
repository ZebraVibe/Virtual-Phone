package com.ouken.phone.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class AssetDescriptors {
	
	// -- constants --
	public static AssetDescriptor<TextureAtlas> PHONE = 
			new AssetDescriptor<>(AssetPaths.PHONE, TextureAtlas.class);
	
	public static AssetDescriptor<Skin> DEFAULT_SKIN = 
			new AssetDescriptor<>(AssetPaths.DEFAULT_SKIN, Skin.class);
	
	public static AssetDescriptor<TextureAtlas> APP_ICONS = 
			new AssetDescriptor<>(AssetPaths.APP_ICONS, TextureAtlas.class);
	
	
	private AssetDescriptors() {}
	
	// -- all descriptors --
	public static Array<AssetDescriptor<?>> ALL = new Array<AssetDescriptor<?>>();
	
	// -- static init --
	static {	
		ALL.addAll(
				PHONE,
				DEFAULT_SKIN,
				APP_ICONS
				);
	}
	
}
