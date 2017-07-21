package heuristics;

import model.Grid;
import model.Node;

public class ChebyshevDistance extends Heuristic {

	private int dx;
	private int dy;
	
	public ChebyshevDistance(Grid grid) {
		super(grid);
	}

	@Override
	public float h(Node n) {
		dx = Math.abs(n.x - endCell.x);
		dy = Math.abs(n.y - endCell.y);
		float D = 1;
		float D2 = 1;

		return (float) D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
	}

}
