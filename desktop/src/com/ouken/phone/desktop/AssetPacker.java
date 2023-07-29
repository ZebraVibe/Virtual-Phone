package com.ouken.phone.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class AssetPacker {

	private static final String RAW_ASSETS = "C:\\Users\\sebas\\Documents\\Eclipse\\libgdx\\ouken platform workspace\\"
			+ "ouken platform project\\desktop\\assets-raw";

	private static final String ASSETS = "C:\\Users\\sebas\\Documents\\Eclipse\\libgdx\\ouken platform workspace\\"
			+ "ouken platform project\\core\\assets";

	public static void main(String[] args) {
		
//		Settings settings = new Settings();
//		settings.combineSubdirectories = true;// adds sub dir immages to parent dirs images into the atlas
//		settings.flattenPaths = true; // strips sub dir prefixes ( names must be unique)
//		TexturePacker.process(settings, "from", "to", "fileName", processListener);
//		TexturePacker.processIfModified(null, ASSETS, RAW_ASSETS, ASSETS);
		
		packPhoneAssets();
		packAppIconAssets();
		packCrawlAppAssets();
		packOukenStudioAssets();
	}

	
	public static void packPhoneAssets() {
		TexturePacker.process(RAW_ASSETS + "/phone", ASSETS + "/phone", "phone");
	}
	
	public static void packAppIconAssets() {
		TexturePacker.process(RAW_ASSETS + "/appicons", ASSETS + "/appicons", "appicons");
	}
	
	
	public static void packCrawlAppAssets() {
		TexturePacker.process(RAW_ASSETS + "/app/crawlapp/gameplay", ASSETS + "/app/crawlapp", "gameplay");
		TexturePacker.process(RAW_ASSETS + "/app/crawlapp/editorui", ASSETS + "/app/crawlapp", "editor_ui");
	}
	
	public static void packOukenStudioAssets() {
		TexturePacker.process(RAW_ASSETS + "/app/oukenstudioapp/gameplay", ASSETS + "/app/oukenstudioapp", "gameplay");
		TexturePacker.process(RAW_ASSETS + "/app/oukenstudioapp/editorui", ASSETS + "/app/oukenstudioapp", "editor_ui");
	}
}
