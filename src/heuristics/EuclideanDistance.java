package heuristics;

import model.Grid;
import model.Node;

public class EuclideanDistance extends Heuristic {

	private int dx;
	private int dy;
	
	public EuclideanDistance(Grid grid) {
		super(grid);
	}

	@Override
	public float h(Node n) {
		
		dx = Math.abs(n.x - endCell.x);
		dy = Math.abs(n.y - endCell.y);
		float D = 0.25f;
		
		return D * (float) Math.sqrt(dx * dx + dy * dy);
	}
	
}
