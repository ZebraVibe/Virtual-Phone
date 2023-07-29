package com.ouken.phone.app.oukenstudioapp.editor.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

public class DynamicTable extends Table {
	
	private static final Logger log = new Logger(DynamicTable.class.getName(), Logger.DEBUG);
	
	private final Array<Actor> tmpActors = new Array<>();
	private int maxCols = 1;
	private int prefCols = maxCols;
	
	public DynamicTable() {
	}
	
	public DynamicTable(int maxCols) {
		maxCols(maxCols);
	}
	
	public DynamicTable maxCols(int max) {
		if(max < 1)return this;
		maxCols = max;
		prefCols = max;
		return this;
	}
	
	@Override
	protected void sizeChanged() {
		tmpActors.addAll(getChildren());
		
		recalcPrefCols();
		
		clearChildren();
		add(tmpActors.toArray(Actor.class));
		tmpActors.clear();
		super.sizeChanged(); // invalidates
	}

	
	private void recalcPrefCols() {
		float totalMinWidth = 0;
		for(int i= 0, n = getColumns(); i < n; i++) {
			totalMinWidth += getColumnMinWidth(i);
		}
		
		if(Float.compare(getWidth(), totalMinWidth) <= 0) {// "<" wird nie passieren, da parent min so groß wie summe aus minWidths
			if(prefCols > 1)prefCols--;
		}
	}



	/**call this to remove a cell and reposition the children*/
	public <T extends Actor> void removeCellOfAndReposition(T actor){
		Cell<T> cell = getCell(actor);
		int index = getCells().indexOf(cell, true);
		cell.setActor(null);
		for(int i = index, n = getCells().size; i < n; i++) {
			if(i < n-1)swapActor(i, i+1);
		}
		Cell<Actor> lastCell = getCells().pop(); // has to be null actor
		log.debug("Removing last call since actor has to be null. Actor NULL ? " + (lastCell.getActor() == null));
		
		invalidate(); // resize & position the cells based on new actor in cell
	}
	
	@Override
	public <T extends Actor> Cell<T> add(T actor) {
		Cell<T> cell = super.add(actor);
		int size = getCells().size;
		if(size > 0 && size % prefCols == 0)cell.row();
		return cell;
	}
	
}
