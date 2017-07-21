package heuristics;

import model.Grid;
import model.Node;

public class OctileDistance extends Heuristic {
	
	private int dx;
	private int dy;
	
	public OctileDistance(Grid grid) {
		super(grid);
	}

	@Override
	public float h(Node n) {
		
		dx = Math.abs(n.x - endCell.x);
		dy = Math.abs(n.y - endCell.y);
		float D = 0.25f;
		float D2 = (float) Math.sqrt(2);
		
		return (float) D * (dx + dy) + (D2 - 2 * D) * Math.min(dx, dy);
	}

}

