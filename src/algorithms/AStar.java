package algorithms;

import heuristics.Heuristic;
import model.Grid;
import model.Node;

public class AStar extends UniformCostSearch {

	private Heuristic function;
	
	public AStar(Node startNode, Node endNode, Grid grid, Heuristic function) {
		super(startNode, endNode, grid);
		this.function = function;
	}
	
	@Override
	public float hCostFunction(Node n) {	
		return function.h(n);
	}
	
}
