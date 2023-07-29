package com.ouken.phone.app.oukenstudioapp.editor.utils;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.ouken.phone.app.oukenstudioapp.config.EditorConfig;
import com.ouken.phone.app.oukenstudioapp.editor.utils.ButtonPane.ButtonPaneType;
import com.ouken.phone.utils.font.FontSize;
import com.ouken.phone.utils.font.Fonts;

public class WindowPane extends Pane {
	
	private static final Logger log = new Logger(WindowPane.class.getName(), Logger.DEBUG);
	
	public static float DEFAULT_WIDTH = 192, DEFAULT_HEIGHT = 96;

	public static final int HEADER_HEIGHT = EditorConfig.WINDOW_HEADER_HEIGHT;
	public static final int HEADER_PAD = EditorConfig.WINDOW_HEADER_PAD;//4;
	
	private ShapeRenderer renderer;
	private Table headerTable;
	private Pane contentPane;
	private Label headerLabel;
	private ButtonPane closeButton;
	
	private Array<Runnable> closedRunnables = new Array<Runnable>();
	
	/**
	 * 
	 * @param headerLabel null to remove the header entirely
	 * @param content
	 * @param renderer
	 */
	public WindowPane(String headerLabel, Actor content, ShapeRenderer renderer) {
		super(0, renderer);
		this.renderer = renderer;
		paneColor(GRAY.cpy()).smoothEdges();
		Color borderC = Color.BLACK.cpy();
		borderC.a = 0.27f; 
		//enableBorder(true).borderColor(borderC);
		addShadow();
		clip();
		
		addCaptureListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				toFront();
				return true;
			}
		});
		
		headerTable = new Table();
		contentPane = new Pane(renderer).smoothEdges().paneColor(DARK_GRAY);
		
		initHeader(headerLabel);
		initContentPane(content);
		
		if(headerLabel != null)add(headerTable).left().height(HEADER_HEIGHT).expandX().fillX().row();
		add(contentPane).expand().fill();
		
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	// -- init --
	
	private void initHeader(String label) {
		headerTable.padLeft(HEADER_PAD).padRight(HEADER_PAD);
		
		LabelStyle style = new LabelStyle();
		style.font = Fonts.getDefaultFontBySize(FontSize.x10);
		style.fontColor = GREEN.cpy();
		
		
		headerLabel = new Label(label, style);

		closeButton = new ButtonPane("[x]", renderer);
		closeButton.smoothEdges(false);
		closeButton.padLeft(PAD_2).padRight(PAD_2);
		closeButton.pack();
		closeButton.getInputListenerActor().addListener(createWindowClosingListener());
		closeButton.setButtonPaneType(ButtonPaneType.PRESSABLE);
		closeButton.setUpColor(GRAY);
		closeButton.setCheckedColor(LIGHT_GRAY);
		closeButton.setOverColor(GREEN);
		
		
		Widget draggableWidget = new Widget();
		draggableWidget.addCaptureListener(createWindowDraggingListener());
		draggableWidget.setFillParent(true);
		
		headerTable.addActor(draggableWidget);
		headerTable.left().add(headerLabel).left().expandX();
		headerTable.right().add(closeButton);
//		headerTable.pack();
	}
	
	private InputListener createWindowClosingListener() {
		return new InputListener() {
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return button == Buttons.LEFT;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				closeWindow();
			}
			
		};
	}
	
	
	
	private InputListener createWindowDraggingListener() {
		return new InputListener() {
			
			WindowPane w = WindowPane.this;
			Vector2 stamp = new Vector2(), tmp = new Vector2();
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				stamp.set(event.getStageX(), event.getStageY()); 
				w.stageToLocalCoordinates(stamp);
				return true;
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				tmp.set(event.getStageX() - stamp.x, event.getStageY() - stamp.y);
				w.stageToLocalCoordinates(tmp);
				w.localToParentCoordinates(tmp);
		
				// keep within stage
				float newX = MathUtils.clamp(tmp.x, 0, event.getStage().getWidth() - w.getWidth());
				float newY = MathUtils.clamp(tmp.y, 0, event.getStage().getHeight() - w.getHeight());
				
				w.setPosition(newX, newY);
				
			}
			
		};
	}
	
	
	private void initContentPane(Actor content) {
		if(content != null)contentPane.add(content).expand().fill();
	}
	
	

	
	// -- public methods --
	

	public Label getHeaderLabel() {
		return headerLabel;
	}
	
	
	public String getHeaderText() {
		return headerLabel.getText().toString();
	}
	
	public void setHeaderText(String text) {
		headerLabel.setText(text);
	}
	
	
	
	public void closeWindow() {
		WindowPane.this.remove();
		WindowPane.this.windowClosed();
	}
	
	public Pane getContentPane() {
		return contentPane;
	}
	
	public void addContent(Actor content) {
		initContentPane(content);
	}
	
	/**called when the window is closed*/
	public void windowClosed() {
		closedRunnables.forEach( r -> r.run() );
	}
	
	public void addWindowClosedRunnable(Runnable runnable) {
		closedRunnables.add(runnable);
	}
	
	public void disableCloseButton(boolean disable) {
		closeButton.setVisible(!disable);
	}
	

	

}
