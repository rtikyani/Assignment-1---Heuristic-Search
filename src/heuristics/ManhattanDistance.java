package heuristics;


import model.Grid;
import model.Node;

public class ManhattanDistance extends Heuristic {

	private int dx;
	private int dy;
	
	public ManhattanDistance(Grid grid) {
		super(grid);
		this.grid = grid;
		endCell = grid.endCell;
	}

	@Override
	public float h(Node n) {
		
		dx = Math.abs(n.x - endCell.x);
		dy = Math.abs(n.y - endCell.y);
		float D = 0.25f;

		return (float) D * (dx + dy);
	}

}

