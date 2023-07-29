package com.ouken.phone.app.oukenstudioapp.editor.ui.tabs;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.TreeStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Null;
import com.ouken.phone.app.oukenstudioapp.assets.RegionNames;
import com.ouken.phone.app.oukenstudioapp.assets.SkinNames;
import com.ouken.phone.app.oukenstudioapp.editor.ui.tree.CustomNode;
import com.ouken.phone.app.oukenstudioapp.editor.utils.Pane;
import com.ouken.phone.app.oukenstudioapp.editor.utils.PaneSpriteDrawable;

/**
 * Contains a splitpain as a child with its left child as the explorer and the right as the content browser
 * @param <N> the node type
 * @param <V> the value type
 */
public abstract class Explorer<N extends CustomNode<N, V>, V> extends Table{
	
	private static final Logger log = new Logger(Explorer.class.getName(), Logger.DEBUG);
	
	private static final float MIN_SPLIT_AMOUNT = 0.28f;
	private static final float MAX_SPLIT_AMOUNT = 0.5f;
	
	private final Table NULL_TABLE = new Table();
	
	private ShapeRenderer renderer;
	private Skin skin;
	private Table explorerTable, contentBrowserContainer, currentContentBrowserTable;
	private Tree<N, V> explorerTree;
	private N lastOver;

	
	

	public Explorer(Skin skin, ShapeRenderer renderer) {
		this.renderer = renderer;
		this.skin = skin;

		initSplitPane();

	}

	// -- init --

	private void initSplitPane() {
		PaneSpriteDrawable pane = new PaneSpriteDrawable(skin.getRegion(RegionNames.PANE), Pane.DARK_GRAY);
		
		explorerTable = new Table();
		explorerTable.setBackground(pane);
		fillExplorer();
		
		contentBrowserContainer = new Table();
		setContentBrowserActor(new Table());
		
		SplitPane splitPane = new SplitPane(explorerTable, contentBrowserContainer, false, skin, SkinNames.SPLIT_PANE_STYLE);
		splitPane.addCaptureListener(new InputListener() {
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				event.getStage().setScrollFocus(event.getTarget());
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				event.getStage().setScrollFocus(null);
			}
			
		});
		splitPane.setSplitAmount(MIN_SPLIT_AMOUNT);
		splitPane.setMinSplitAmount(MIN_SPLIT_AMOUNT);
		splitPane.setMaxSplitAmount(MAX_SPLIT_AMOUNT);
		
		add(splitPane).expand().fill();
	}
	
	protected void fillExplorer() {
		explorerTree = createExplorerTree();
		ScrollPane scrollPane = createScrollPane(explorerTree);
		explorerTable.add(scrollPane).expand().fill();
	}
	
	
	private Tree<N, V> createExplorerTree() {
		TreeStyle style = new TreeStyle(skin.get(SkinNames.TREE_STYLE, TreeStyle.class));
		Tree<N, V> tree = new Tree<>(style);
		tree.setPadding(Pane.PAD_4);
		tree.getSelection().setMultiple(false);
		
		// double click
		tree.addListener(new ClickListener() { // expand / collapse on double click
			@Override
			public void clicked(InputEvent event, float x, float y) {
				N node = explorerTree.getOverNode(); // can be null when clicking void
				N selected = explorerTree.getSelectedNode();// selected node doesnt turn null when clicking void
				boolean hasNodeChanged = node != lastOver || selected != node;
				if(hasNodeChanged)setTapCount(1);
				updateTreeOnClick(getTapCount(), node, hasNodeChanged);
				lastOver = explorerTree.getOverNode();
			}
		});
		
		
		// insert drag line
		Image line = new Image(skin.getRegion(SkinNames.PIXEL_REGION));
		line.setColor(Pane.GREEN);
		line.setTouchable(Touchable.disabled);
		line.setHeight(1);
		line.setVisible(false);
		tree.addActor(line);
		
		// drag into node actor over Image
		Image overDrag = new Image(new PaneSpriteDrawable(skin.getRegion(RegionNames.PANE), Color.WHITE));
		overDrag.setColor(1,1,1, 0.1f);
		overDrag.setTouchable(Touchable.disabled);
		overDrag.setVisible(false);
		tree.addActor(overDrag);
		
		// drag indicator icon next to mouse
		Image dragIcon = new Image();
		//dragIcon.setColor(1,1,1, 0.5f);
		dragIcon.setTouchable(Touchable.disabled);
		dragIcon.setVisible(false);
		tree.addActor(dragIcon);
		
		// dragg listener
		DragListener dragListener = new DragListener() {
			Rectangle tmpRect = new Rectangle();
			Drawable overDrawable;
			N dragNode, prevDragNode, nextDragNode, prevNode, nextNode, insertPrevNode, insertParentNode;
			boolean addToInsertParent;
			
			@Override
			public void dragStart(InputEvent event, float x, float y, int pointer) {
				N overNode = explorerTree.getOverNode();
				if(overNode != null ) {
					log.debug("Drag Start!");
					dragNode = overNode;
					// drag icon is node icon
					dragIcon.setDrawable(dragNode.getIcon());
					dragIcon.pack();
					
					// dont display over drawable while dragging
					overDrawable = tree.getStyle().over;
					tree.getStyle().over = null;
					
					
					float ySpacing = tree.getYSpacing();
					float bottomYSpacing = ySpacing / 2;// in tree ySpacing / 2 is used (rounds down)
					float nodeY = overNode.getActor().getY() - bottomYSpacing;
					float nodeHeight = overNode.getHeight();
					prevDragNode = tree.getNodeAt(nodeY + nodeHeight + nodeHeight /2);
					nextDragNode = tree.getNodeAt(nodeY - nodeHeight /2);
					
					// avoid being able to drag parent into children
					overNode.collapseAll();
					
					//detect escape press
					event.getStage().setKeyboardFocus(tree);
				}
			}
			

			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				line.setVisible(false);
				overDrag.setVisible(false);
				if(dragNode == null)return;
				
				dragIcon.setVisible(true);
				dragIcon.setPosition(x + dragIcon.getWidth(), y - 2*dragIcon.getHeight());
				
				
				// since while dragging no new overNode is Set and therefore no over drawable
				N overNode = tree.getNodeAt(y); //tree.getOverNode(); 
				if(overNode == null)return;
				

				
				Table overActor = overNode.getActor();
				line.setWidth(tree.getWidth());
				//overActor.setDebug(true);
				
				
				GlyphLayout layout = overNode.getLabel().getGlyphLayout();
				LabelStyle style = overNode.getLabel().getStyle();
				BitmapFont font = style.font;
				// node center rect
				float capHeight = font.getCapHeight();
				float centerWidth = tree.getWidth();
				float centerHeight = layout.height;//overActor.getHeight() * 3/4f;
				float centerX = 0;
				float centerY = overActor.getY() + (overActor.getHeight() - centerHeight) / 2f;
				float centerYTop = centerY + centerHeight;
				
				float iconWidth = overNode.getIcon().getMinWidth();
				float ySpacing = tree.getYSpacing();
				float bottomYSpacing = ySpacing / 2;// in tree ySpacing / 2 is used (rounds down)
				float nodeHeight = overNode.getHeight() + ySpacing;
				
				
				//float gap = (nodeHeight - overActor.getHeight()) / 2; 
				float nodeY = overActor.getY() - bottomYSpacing;//gap;
				float nodeYTop = nodeY + nodeHeight;
				float iconX = overActor.getX() - iconWidth;
				N nextNode = tree.getNodeAt(nodeY - nodeHeight /2);//getPrevNodeOf(overNode);
				N prevNode = tree.getNodeAt(nodeYTop + nodeHeight /2);//getNextNodeOf(overNode);
				
				float nextIconX = nextNode == null ? iconX : nextNode.getActor().getX() - iconWidth;
				float prevIconX = prevNode == null ? iconX : prevNode.getActor().getX() - iconWidth;
				
				
				// is in center of node
				tmpRect.set(centerX, centerY, centerWidth, centerHeight);
				if(tmpRect.contains(x,y)) {
					if(overNode == dragNode)return;
					//explorerTree.setOverNode(overNode);
					
					// else lighten the overNode color
					overDrag.setVisible(true);
					overDrag.setBounds(0, nodeY, tree.getWidth(), nodeHeight);
					
					// insert node
					insertParentNode = overNode;
					insertPrevNode = null;
					addToInsertParent = true;

				}else if(y <= centerY) { // lower part
					
					float lineX = iconX > nextIconX ? iconX : nextIconX;
					float lineY = nodeY;
					line.setPosition(lineX, lineY);
					line.setVisible(true);
					
					
					if(nextIconX > iconX)insertParentNode = overNode;
					else insertParentNode = overNode.getParent();
					insertPrevNode = overNode;
					addToInsertParent = false;
					

				}else if(y >= centerYTop) { // upper part

					float lineX = iconX > prevIconX ? iconX : prevIconX;
					float lineY = nodeYTop;
					line.setPosition(lineX, lineY);
					line.setVisible(true);
					
					if(iconX > prevIconX) {
						insertParentNode = prevNode;
						insertPrevNode = null;
					}else {
						insertParentNode = prevNode == null ? null : prevNode.getParent();
						insertPrevNode = prevNode;
					}
					addToInsertParent = false;
				}
			}
			
			@Override
			public void dragStop(InputEvent event, float x, float y, int pointer) {
				if(dragNode == null)return;
				if(!insertDraggedNodeAt(dragNode, insertParentNode, insertPrevNode, addToInsertParent)) {
					// ...
				}
				reset();
				log.debug("Drag Stop!");
			}
			
			
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if(keycode == Keys.ESCAPE) {
					reset();
					return true;
				}
				return false;
			}
			
			private void reset() {
				dragNode = null;
				prevDragNode = null;
				nextDragNode = null;
				prevNode = null;
				nextNode = null;
				insertParentNode = null;
				insertPrevNode = null;
				addToInsertParent = false;
				tree.getStyle().over = overDrawable;
				dragIcon.setVisible(false);
				line.setVisible(false);
				overDrag.setVisible(false);
				log.debug("Drag RESET!");
			}
			

		};
		
		dragListener.setTapSquareSize(4);
		tree.addListener(dragListener);
		return tree;
	}
	
	
	/**
	 * @param node
	 * @param newParent null if tree is parent
	 * @param prevNode null to just add to children
	 * @param add if true - ignores the prev node and just adds to the parents children
	 * @return true if node could get inserted
	 */
	protected boolean insertDraggedNodeAt(N node, @Null N newParent, @Null N prevNode, boolean add) {
		if(node == null || node == newParent || node == prevNode ) {
			log.error("not inserting node");
			return false;
		}
		
		log.debug("inserting node");
		
		// add to parent node
		if(newParent != null) { 
			node.remove();
			int index = add ? -1 : (prevNode == null ? Math.max(0, newParent.getChildren().size - 1): newParent.getChildren().indexOf(prevNode, true) + 1);
			if(index >= 0)newParent.insert(index, node);
			else newParent.add(node);
			node.expandTo();
		}else { // add to tree
			explorerTree.remove(node);
			int index = add ? -1 : (prevNode == null ? Math.max(0,explorerTree.getRootNodes().size -1) : explorerTree.getRootNodes().indexOf(prevNode, true) + 1);
			if(index >= 0)explorerTree.insert(index, node);
			else explorerTree.add(node);
		}
		return true;
	
	}
	
	
	
	
	
	
	/**called whenever a node is clicked.
	 * Collapses or expands the double clicked node by defaul.t*/
	private void updateTreeOnClick(int tapCount, N node, boolean hasNodeChanged) {
		if(node == null)return;
		//log.debug("updating node: " + node.getLabelText() );
		
		if (tapCount % 2 == 0)node.setExpanded(!node.isExpanded());
		else if(tapCount == 1 && hasNodeChanged)setContentBrowserOf(node);
	}
	
	
	private ScrollPane createScrollPane(Actor actor) {
		ScrollPaneStyle scrollStyle = new ScrollPaneStyle(skin.get(SkinNames.SCROLL_PANE_STYLE, ScrollPaneStyle.class));
		scrollStyle.background = new PaneSpriteDrawable(skin.getRegion(RegionNames.PANE), Pane.DARK_GRAY);
		ScrollPane scrollPane = new ScrollPane(/*actor == null ? new Table() :*/ actor, scrollStyle);
		scrollPane.setFadeScrollBars(true);
		scrollPane.setScrollbarsOnTop(true);
		scrollPane.setFlickScroll(false);
		return scrollPane;
	}
	
	
	/**maybe null to remove the current actor*/
	private void setContentBrowserActor(Actor actor) {
		contentBrowserContainer.clearChildren();
		contentBrowserContainer.add(createScrollPane(actor)).expand().fill();
	}
	
	
	// -- public methods --
	
	/**returns the table that contains the content browser table created by {@link #createContentBrowserOf(CustomNode)}*/
	public Table getContentBrowserContainer() {
		return contentBrowserContainer;
	}
	
	/**returns the currently displayed content browser table created by {@link #createContentBrowserOf(CustomNode)}*/
	public @Null Table getCurrentContentBrowserTable() {
		return currentContentBrowserTable;
	}
	
	public Table getExplorerTable() {
		return explorerTable;
	}
	
	public Tree<N, V> getExplorerTree(){
		return explorerTree;
	}
	
	
	
	public void setContentBrowserOf(@Null N node) {
		Table content = node == null ?  NULL_TABLE : createContentBrowserOf(node);
		currentContentBrowserTable = content;
		setContentBrowserActor(content);
	}
	
	
	
	
	/**selects the node, expands the tree to it and always updates the content browser*/
	public void chooseNode(@Null N node) {
		chooseNode(node, true);
	}
	
	/**selects the node, expands the tree to it and updates the content browser if chosen*/
	public void chooseNode(@Null N node, boolean updateBrowser) {
		if(node != null) {
			explorerTree.getSelection().choose(node);
			node.expandTo();
			
		}else explorerTree.getSelection().clear();
		
		if(updateBrowser)setContentBrowserOf(node);
		lastOver = explorerTree.getOverNode();
	}
	
	
	/**asRootOnly = false*/
	public N addNodeToSelectedOrAsRoot(V value, String label) {
		return addNodeToSelectedOrAsRoot(value, label, false);
	}
	
	public N addNodeToSelectedOrAsRoot(V value, String label, boolean asRootOnly) {
		N node = createNode(value, label);
		N parentNode = explorerTree.getSelectedNode();
		if(asRootOnly || parentNode == null)explorerTree.add(node);
		else parentNode.add(node);
		return node;
	}
	
	public N addNodeToOrAsRoot(@Null N parentNode, V value, String label, boolean asRootOnly) {
		N node = createNode(value, label);
		if(asRootOnly || parentNode == null)explorerTree.add(node);
		else parentNode.add(node);
		return node;
	}
	
	
	/** updates the node icon, the content browser and the tree if necessary*/
	public void removeNodeFromExplorer(N node, boolean reInitBrowser) {
		if(node == null)return;
		N parent = node.getParent();
		N selectedNode = getExplorerTree().getSelectedNode();
		
		explorerTree.remove(node);
		explorerTree.getSelection().remove(node);
		
		// deleting selected from tree
		if(node == selectedNode) { 
			setContentBrowserOf(null);

		// deleting selected node item from browser from selected node
		}else if(parent != null && parent == getExplorerTree().getSelectedNode()) {
			if(reInitBrowser)setContentBrowserOf(parent);
		}
		
		// update parent icon
		if(parent != null && parent.getChildren().size == 0)parent.setExpanded(false);
	
	}
	
	
	/**creates a new node using the node factory, and initializes the value and label*/
	public final N createNode(V value, String label) {
		N node = getNodeFactory();
		node.setValue(value);
		node.setLabelText(label);
		return node;
	}
	
	/**selected is never null*/
	public abstract @Null Table createContentBrowserOf(N selected);
	
	/**used to distribute new untouched instances*/
	public abstract N getNodeFactory();
	
	
}
