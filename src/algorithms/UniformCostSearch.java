package algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import model.Cell.CellType;
import model.Grid;
import model.Node;

public class UniformCostSearch {

	Node[] path;
	private PriorityQueue<Node> fringe;
	private Node[][] visitedNodes;
	Node endNode;
	Node startNode;
	Grid grid;
	public long totalMemory;

	public UniformCostSearch(Node start, Node goal, Grid grid) {
		this.fringe = new PriorityQueue<Node>( new Comparator<Node>() {
			public int compare(Node o1, Node o2) {
				return Float.compare(o1.gCost + o1.hCost, o2.gCost + o2.hCost);
			}
		});
		
		this.grid = grid;
		this.visitedNodes = new Node[grid.columns][grid.rows];
		this.startNode = start;
		this.endNode = goal;
		this.path = null;	
	}

	public Node[] run() {
		System.out.println("------");
		
		Runtime runtime = Runtime.getRuntime();
	    long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
		
		Node n = startNode;
		addToFringe(n, null, gCostFunction(startNode, startNode), hCostFunction(startNode));
		
		int i = 0;
		
		while(!isFringeEmpty()) {
			n = getNextNode();
			exploreNode(n);
			i++;
			if(n.celltype == CellType.UNBLOCKED_END || n.celltype == CellType.HARD_END) {
				long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
				this.totalMemory = memoryAfter - memoryBefore; 
				System.out.println("Explored " + i + " Nodes");
				return unravelPath(n);
			}
			
			for(Node nc : n.neighborCells) {
				if(nc.celltype == CellType.BLOCKED) {
					continue;
				}
				
				float childCost = n.gCost + gCostFunction(n, nc);
				
				if(!isExplored(nc)) {
					if(!inFringe(nc)) {
						addToFringe(nc, n, Integer.MAX_VALUE, 0);
					}
					
					if(childCost < nc.gCost) {
						addToFringe(nc, n, childCost, hCostFunction(nc));
					}
				}
			}
		}
		return null;
	}

	public Node[] unravelPath(Node a) {
		ArrayList<Node> pathList = new ArrayList<Node>();
		Node n = a;
		Node p;

		int i = 1;
		while (n != null) {
			p = n.getParent();
			pathList.add(n);
			n = p;
			i++;
		}
		path = new Node[pathList.size()];
		path = pathList.toArray(path);

		System.out.println("Path Size: " + (path.length-1));
		return path;
	}

	public void addToFringe(Node n, Node p, float gcost, float hcost) {

		n.gCost = gcost;
		n.hCost = hcost;
		n.setParent(p); 
		if (inFringe(n)) {
			fringe.remove(n);
		}
		fringe.add(n);
		n.inFringe = true;
		this.visitedNodes[n.x][n.y] = n;
	}

	public Node getNextNode() {
		return fringe.remove();
	}

	public void popFromFringe(Node n) {
		fringe.remove(n);
	}

	public float gCostFunction(Node parent, Node child) {
		return grid.getCost(parent, child);
	}

	public float hCostFunction(Node n) {
		return 0f;
	}

	public boolean isFringeEmpty() {
		return fringe.isEmpty();
	}

	public void exploreNode(Node n) {
		n.setNeighbors(grid.getNeighborCells(n.x, n.y));
		n.visited = true;
		this.visitedNodes[n.x][n.y] = n;
	}

	public boolean isExplored(Node n) {
		Node visitedNode = this.visitedNodes[n.x][n.y];
		return (visitedNode == null) ? false : visitedNode.visited;
	}

	public boolean inFringe(Node n) {
		Node isThere = this.visitedNodes[n.x][n.y];
		return (isThere == null) ? false : isThere.inFringe;
	}

	public float calculateCosts() {
		if (path == null) {
			throw new NullPointerException("There is no path to be found.");
		}

		float cost = 0;
		Node p = path[0];
		for (Node n : path) {
			cost += grid.getCost(n, p);
			p = n;
		}
		return cost;
	}

}