package model;

public class Node extends Cell {

	private Node parentCell;
	public float gCost;
	public float hCost;
	public boolean visited;
	public boolean inFringe;
	public Node[] neighborCells;
	public boolean inAncList;
	public boolean inInadList;
	
	public Node(Cell cell) {
		super(cell.x, cell.y, cell.celltype, cell.dir);
		this.gCost = 0;
		this.hCost = 0;
		this.visited = false;	
		this.inFringe = false;
	}
	
	public Node (Cell cell, Cell[] neighborCells) {
		this(cell);
		setNeighbors(neighborCells);
	}
	
	public void setNeighbors(Cell[] neighborCells) {
		int length = neighborCells.length;
		this.neighborCells = new Node[length];
		for (int i = 0; i < length; i++) {
			this.neighborCells[i] = new Node(neighborCells[i]);
		}
	}
	
	public void setParent(Node parentCell) {
		this.parentCell = parentCell;
	}
	
	public Node getParent() {
		return this.parentCell;
	}
	
}
