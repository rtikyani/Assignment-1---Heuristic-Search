package algorithms;

import heuristics.Heuristic;
import model.Grid;
import model.Node;

public class WeightedAStar extends UniformCostSearch {

	private Heuristic function;
	private float weight;
	
	public WeightedAStar(Node startNode, Node endNode, Grid grid, Heuristic function, float weight) {
		super(startNode, endNode, grid);	
		this.function = function;
		this.weight = weight;
	}

	@Override
	public float hCostFunction(Node n) {
		return weight * function.h(n);
	}
}