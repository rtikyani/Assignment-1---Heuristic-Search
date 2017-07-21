package heuristics;

import model.*;

public abstract class Heuristic {
	Grid grid;
	Cell endCell;
	
	public Heuristic(Grid grid) {
		this.grid = grid;
		this.endCell = grid.endCell;
	}
	
	public abstract float h(Node n);
}
