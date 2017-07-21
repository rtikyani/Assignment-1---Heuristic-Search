package model;

public class Cell {

	public enum CellType {
		UNBLOCKED, BLOCKED, HARD, UNBLOCKED_HIGHWAY, HARD_HIGHWAY, UNBLOCKED_START, UNBLOCKED_END, HARD_START, HARD_END, AGENT
	}

	public enum Direction {
		NONE, UP, DOWN, LEFT, RIGHT
	}

	public int x, y;
	public CellType celltype;
	Direction dir;

	public Cell(int x, int y, CellType celltype, Direction dir) {
		this.x = x;
		this.y = y;
		this.celltype = celltype;
		this.dir = dir;
	}

	public Cell(int x, int y, CellType celltype) {
		this(x, y, celltype, Direction.NONE);
	}

	public Cell(int x, int y) {
		this(x, y, CellType.UNBLOCKED, Direction.NONE);
	}

	public void convertTo(CellType ct) {
		this.celltype = ct;
		if (!isHighway()) {
			this.dir = Direction.NONE;
		}
	}

	public void makeHighway(Direction hd) {
		if (this.celltype == CellType.UNBLOCKED) {
			this.celltype = CellType.UNBLOCKED_HIGHWAY;
		} else {
			this.celltype = CellType.HARD_HIGHWAY;
		}
		this.dir = hd;
	}

	public boolean isHighway() {
		return this.celltype == CellType.HARD_HIGHWAY || this.celltype == CellType.UNBLOCKED_HIGHWAY;
	}

}