package com.ouken.phone.app.crawlapp.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class AssetDescriptors {
	
	// -- constants --
	public static AssetDescriptor<TextureAtlas> GAMEPLAY = 
			new AssetDescriptor<>(AssetPaths.GAMEPLAY, TextureAtlas.class);
	
	public static AssetDescriptor<TextureAtlas> EDITOR_UI = 
			new AssetDescriptor<>(AssetPaths.EDITOR_UI, TextureAtlas.class);
	
	
	
	private AssetDescriptors() {}
	
	// -- all descriptors --
	public static Array<AssetDescriptor<?>> ALL = new Array<AssetDescriptor<?>>();
	
	// -- static init --
	static {	
		ALL.addAll(
				GAMEPLAY,
				EDITOR_UI
				);
	}
	
}
