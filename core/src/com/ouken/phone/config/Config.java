package com.ouken.phone.config;

public class Config {
	
	// -- paths -- 
	public static final String PHONE_FOLDER_PATH = ".oukenphone/";
	public static final String APPS_DIRECTORY_NAME = "apps/";
	public static final String APPS_FOLDER_PATH = PHONE_FOLDER_PATH + APPS_DIRECTORY_NAME;
	
	public static final String INSTALLED_APP_CLASSES_PREFS_NAME = "installedApps";
	
	
	// -- values --
	
	/**wu (1:1)*/
	public static final float 
	PHONE_GAP_LEFT = 0,
	PHONE_GAP_RIGHT = 0,
	PHONE_GAP_TOP = 0,
	PHONE_GAP_BOTTOM = 0,
	PHONE_WIDTH = 448, 
	PHONE_HEIGHT = 832;
	
	/**wu (1:1)*/
	public static final float 
	WORLD_WIDTH = PHONE_GAP_LEFT + PHONE_GAP_RIGHT+ PHONE_WIDTH, 
	WORLD_HEIGHT = PHONE_GAP_TOP + PHONE_GAP_BOTTOM + PHONE_HEIGHT;
	
	//pxl (1 wu:1 pxl)
	public static final float WIDTH = WORLD_WIDTH, HEIGHT = WORLD_HEIGHT; // 1:1 (wu : pxl)
	
	public static final float FRAME_THICKNESS = 21;//33;
	
	public static final float FRAME_CORNER_OUTTER_RADIUS = 64;
	
	public static final float FRAME_CORNER_INNER_RADIUS = FRAME_CORNER_OUTTER_RADIUS - FRAME_THICKNESS;
	
	/**The entire phone screen widht & height, including & overlapping portions with rounded corners.<br>
	 * [!] In ScreenCoord!  */
	public static final float 
	SCREEN_X = PHONE_GAP_LEFT + 21, 
	SCREEN_Y = PHONE_GAP_BOTTOM + 21, 
	SCREEN_WIDTH = 406,
	SCREEN_HEIGHT = 790;

	/**The INNER portion of the phone screen in which the screen is rectangular, exluding portions 
	 * with rounded corners<br>
	 * [!] In ScreenCoord! */ 
	public static final float 
	SCREEN_RECT_X = PHONE_GAP_LEFT + 21, 
	SCREEN_RECT_Y = PHONE_GAP_BOTTOM +64, 
	SCREEN_RECT_WIDTH = 406, 
	SCREEN_RECT_HEIGHT = 704; 
	
	
	
	public static final float APP_ICON_SIZE = 64, APP_ICON_WIDTH = APP_ICON_SIZE, APP_ICON_HEIGHT = APP_ICON_SIZE; //wu
	public static final int SPACE_BETWEEN_APP_ICONS = 64;
	public static final int MAX_APP_ICONS_PER_ROW = 3;
	public static final int MAX_APPS_PER_PAGE = 12;
	
	//overlay
	public static final float OVERLAY_MOVE_DURATION = 0.1f;
	public static final float OVERLAY_BACKGROUND_ALPHA = 0.7f;
	
	//nav bar
	public static final float PAGE_INDICATOR_Y = 156;
	public static final float PHONE_NAV_Y = 43;//0
	public static final float PHONE_NAV_BG_CENTER_WIDTH = 256;
	
	//status bar
//	public static final float STATUS_BAR_HEIGHT = 36;
	public static final float STATUS_BAR_TOP_Y_SPACE = 20;
	public static final float STATUS_BAR_SIDE_RECT_WIDTH = 64;
	public static final float STATUS_BAR_CENTER_BG_RECT_WIDTH = 374, STATUS_BAR_BG_HEIGHT = 16;
	
	private Config() {}
	
}
