package com.ouken.phone.app.oukenstudioapp.editor.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

public class TabPane extends Pane {
	
	public static final int DEFAULT_PAD = PAD_2;
	public static  final int DEFAULT_TAB_WIDTH = 64, DEFAULT_TAB_HEIGHT = 24, DEFAULT_TAB_GAP = 1;
	
	private ShapeRenderer renderer;

	private final Table tabTable;
	private final Pane contentPane;

	private float tabWidth, tabHeight;
	private float tabGap;
	private boolean tabsAtBottom;

	
	
	// -- constructors --
	/**Creates a TabPane with tabsAtBottom to false and contentPad of {@value TabPane#DEFAULT_PAD}*/
	public TabPane(String label, Actor content, ShapeRenderer renderer) {
		this(label, content, DEFAULT_PAD, false, renderer);
	}
	/**Creates a TabPane with contentPad of {@value TabPane#DEFAULT_PAD}*/
	public TabPane(String label, Actor content, boolean tabsAtBottom,ShapeRenderer renderer) {
		this(label, content, DEFAULT_PAD, tabsAtBottom, renderer);
	}
	
	public TabPane(String label, Actor content, float contentPad, boolean tabsAtBottom,ShapeRenderer renderer) {
		this(label, DEFAULT_TAB_WIDTH, DEFAULT_TAB_HEIGHT, content, DEFAULT_PAD, tabsAtBottom, renderer);
	}
	
	public TabPane(String label, float tabWidth, float tabHeight, Actor content, 
			float contentPad, boolean tabsAtBottom,ShapeRenderer renderer) {
		this(label, tabWidth, tabHeight, DEFAULT_TAB_GAP, content, DEFAULT_PAD, tabsAtBottom, renderer);
	}
	
	/**the content pad sets the padding for the content pane's content*/
	public TabPane(String label, float tabWidth, float tabHeight, float tabGap, Actor content, 
			float contentPad, boolean tabsAtBottom,ShapeRenderer renderer) {
		super(renderer);
		this.renderer = renderer;
		this.tabsAtBottom = tabsAtBottom;
		this.tabWidth = tabWidth;
		this.tabHeight = tabHeight;
		this.tabGap = tabGap;
		
		tabTable = new Table();
		contentPane = new Pane(contentPad, renderer); // contentpanes content pad
		
		init(label, content);
	}
	
	// -- init --
	
	private void init(String firstLabel, Actor content) {
		initPane();
		initTabTable();
		initContentPane();
		
		initFirstTab(firstLabel, content);
	}
	
	private void initPane() {
		paneColor(DARK_GRAY.cpy()).smoothEdges();
		float width = tabWidth * 2;
		float height = tabHeight *2;
		
		setSize(width, height);

		if(tabsAtBottom) {
			add(contentPane).expand().fill().row();
			add(tabTable).left().height(height/2f).expandX();
			
		}else {
			add(tabTable).left().height(height/2f).expandX().row();
			add(contentPane).expand().fill();
		}
		
//		pack();
		
	}
	
	private void initTabTable() {
		tabTable.defaults().padRight(tabGap).padTop(tabGap).padBottom(tabGap);
	}
	
	private void initContentPane() {
		contentPane.paneColor(GRAY.cpy());
		if(tabsAtBottom) contentPane.smoothEdges(true, true, false, true);
		else contentPane.smoothEdges(false, true, true, true);
		contentPane.setTransform(true);
	}
	
	private void initFirstTab(String label, Actor content) {
		addTab(label, content);
		handleCheck((Pane)tabTable.getCells().first().getActor(), content);
		tabTable.pack();
	}
	
	
	
	// -- public methods --

	
	
	public Pane getContentPane() {
		return contentPane;
	}
	
	
	
	/** Tabs can only be added as long their total width dont exceed 
	 * the content pane's width.<br>
	 * Note that the tabContent is added as an Actor not within a cell*/
	public TabPane addTab(String label, Actor tabContent) {
		Pane tab = new Pane(renderer).paneColor(GRAY.cpy());
		if(tabsAtBottom)tab.smoothEdges(false, false, true, true);
		else tab.smoothEdges(true, true, false, false);
		
		tab.setSize(tabWidth, tabHeight);
		tab.center();
		tab.clip();
		
		LabelStyle style = new LabelStyle();
		style.font = Fonts.getDefaultFontBySize(FontSize.x12);
		style.fontColor = new Color().set(GREEN);
		Label l = new Label(label, style);
		
		tab.add(l);
		disableTab(tab);
		
		Widget touchDetector = new Widget(); // since tables are a group that dont detect themselves
		touchDetector.setFillParent(true);
		tab.addActor(touchDetector);
		addTabListener(touchDetector, tabContent);
		
		
//		if((tabTable.getCells().size + 1) * (tabWidth + tabGap) + tabGap <= getWidth()) {
//			tabTable.add(tab).size(tab.getWidth(), tab.getHeight());
//
//		}
		tabTable.add(tab).size(tab.getWidth(), tab.getHeight());
//		tabTable.pack();
		return this;
	}
	
	
	private void addTabListener(Widget touchDetector, Actor tabContent) {
		touchDetector.addListener(new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				handleCheck((Pane)touchDetector.getParent(), tabContent);
				return true;
			}
			
		});
	}
	
	private void handleCheck(Pane tab, Actor tabContent) {
		// check
		enableTab(tab);
		contentPane.clear();
		if(tabContent != null) {
			contentPane.add(tabContent).expand().fill();
		}
		
		// uncheck
		for(Cell<?> cell : tabTable.getCells()) {
			Pane t = (Pane)cell.getActor();
			if(t != tab)disableTab(t);
		}
		
		// smooth content pane edge if not first tab
		Pane first = getFirstTab();
		if(tab != first) {
			// smoothes if the opposite side at the bottom(/top) is smoothed as well
			if(tabsAtBottom)contentPane.smoothBottomLeft = smoothBottomRight;
			else contentPane.smoothTopLeft = smoothTopRight;
		}else {
			if(tabsAtBottom)contentPane.smoothBottomLeft = false;
			else contentPane.smoothTopLeft = false;
		}
		
	}
	
	private void enableTab(Pane tab) {
		tab.disableShapeDrawing(false);
	}
	
	private void disableTab(Pane tab) {
		tab.disableShapeDrawing(true);
	}

	private Pane getFirstTab() {
		return (Pane) tabTable.getCells().first().getActor();
	}
	
	@Override
	public Pane smoothEdges(boolean topLeft, boolean topRight, boolean bottomLeft, boolean bottomRight) {
		if (contentPane != null) {
			if (tabsAtBottom) {
				contentPane.smoothTopLeft = topLeft;
				contentPane.smoothTopRight = topRight;
			} else {
				contentPane.smoothBottomLeft = bottomLeft;
				contentPane.smoothBottomRight = bottomRight;
			}
		}
		return super.smoothEdges(topLeft, topRight, bottomLeft, bottomRight);
	}

	

	
}
