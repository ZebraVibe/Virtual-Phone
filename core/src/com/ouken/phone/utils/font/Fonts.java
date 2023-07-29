package com.ouken.phone.utils.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Logger;
import com.ouken.phone.assets.AssetPaths;

public class Fonts{

	private static final Logger log = new Logger(Fonts.class.getName(), Logger.DEBUG);
	
	private static final ArrayMap<FontSize, BitmapFont> FONTS = new ArrayMap<>();
	
	private Fonts() {}

	// -- font --
	public static FreeTypeFontGenerator createNewDefaultFontGenerator() {
		return new FreeTypeFontGenerator(Gdx.files.internal(AssetPaths.PHONE_DEFAULT_FONT));
	}

	public static BitmapFont createNewDefaultFont(FreeTypeFontParameter param) {
		FreeTypeFontGenerator gen = createNewDefaultFontGenerator();
		BitmapFont font = gen.generateFont(param);
		gen.dispose();
		return font;
	}

	/**returns a premade font, which is auto. registered for disposal in {@link Fonts#dispose()}*/
	public static BitmapFont getDefaultFontBySize(FontSize size) {
		return FONTS.get(size);
	}

	/**loads all premade fonts*/
	public static void load() {
		for(FontSize fontSize : FontSize.values()) {
			FreeTypeFontParameter param = new FreeTypeFontParameter();
			param.size = fontSize.toInt();
			BitmapFont font = createNewDefaultFont(param);
			FONTS.put(fontSize, font);
		}
	}
	
	/**disposes all premade fonts*/
	public static void dispose() {
		log.debug("disposing fonts.");
		FONTS.forEach(a -> {
			a.value.dispose();
		});
	}


}
