package model;

import java.util.*;

import model.Cell;
import model.Cell.CellType;
import model.Cell.Direction;

public class Grid {

	final int MIN_HIGHWAY_LENGTH = 100;
	final double CHANCE_CHANGE_DIR = 0.2;
	final int NUM_HARD_CENTERS = 8;
	final int NUM_BLOCKED_CELLS = 3840;

	Cell[][] grid;
	public Cell startCell;
	public Cell endCell;
	public int columns;
	public int rows;
	public static Random rand = new Random();
	public int[][] hardCenters = new int[NUM_HARD_CENTERS][2];
	double chance = rand.nextDouble();
	ArrayList<Node> oldPath = new ArrayList<Node> ();

	int highwayCount = 0;

	public Grid(int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
		grid = new Cell[rows][columns];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				this.grid[i][j] = new Cell(j, i);
			}
		}
	}

	public Cell getCell(int x, int y) {
		
		return grid[y][x];
	}

	public void setHardCells() {
		for (int i = 0; i < 8; i++) {
			int center_x = rand.nextInt(160) + 1;
			int center_y = rand.nextInt(120) + 1;

			hardCenters[i][0] = center_x;
			hardCenters[i][1] = center_y;

			int x_min = center_x - 15;
			int x_max = x_min + 31;
			int y_min = center_y - 15;
			int y_max = y_min + 31;

			if (x_min < 0) {
				x_min = 0;
			}

			if (y_min < 0) {
				y_min = 0;
			}

			if (x_max > 160) {
				x_max = 160;
			}

			if (y_max > 120) {
				y_max = 120;
			}

			for (int a = x_min; a < x_max; a++) {
				for (int b = y_min; b < y_max; b++) {
					double half_chance = Math.random();
					if (half_chance <= 0.5) {
						grid[b][a].convertTo(CellType.HARD);
					}
				}
			}
		}
	}

	public void makeHighways() {
		while (highwayCount < 4) {
			while (makeHighway() != false) {
			}
		}
	}

	public boolean makeHighway() {

		int[] highwaystart = this.getHighwayStart();
		int x = highwaystart[0];
		int y = highwaystart[1];

		Cell cell = grid[y][x];

		if (cell.isHighway())
			return false;

		Direction dir = Direction.NONE;

		switch (x) {
		case 0:
			dir = Direction.RIGHT;
			break;
		case 159:
			dir = Direction.LEFT;
			break;
		default:
			break;
		}
		switch (y) {
		case 0:
			dir = Direction.DOWN;
			break;
		case 119:
			dir = Direction.UP;
			break;
		default:
			break;
		}

		cell.makeHighway(dir);

		LinkedList<Cell> highwayCells = new LinkedList<Cell>();
		highwayCells.add(cell);

		while (true) {
			for (int i = 0; i < 20; i++) {
				Cell newCell = pave(x, y, dir);
				if (newCell == null) {
					if (countHighwayCells(highwayCells) >= MIN_HIGHWAY_LENGTH) {

						highwayCount++;
						return true;
					} else
						removeHighways(highwayCells);
					return false;
				}

				x = newCell.x;
				y = newCell.y;

				if (grid[y][x].isHighway()) {
					removeHighways(highwayCells);
					return false;
				}

				newCell.makeHighway(dir);
				highwayCells.add(newCell);

				grid[y][x].makeHighway(dir);

				if (isBoundaryCell(grid[y][x])) {
					if (countHighwayCells(highwayCells) >= MIN_HIGHWAY_LENGTH) {

						highwayCount++;
						return true;
					} else {
						removeHighways(highwayCells);
						return false;
					}
				}
			}

			chance = rand.nextDouble();
			if (chance <= CHANCE_CHANGE_DIR) {
				if (dir == Direction.UP || dir == Direction.DOWN) {
					dir = Direction.LEFT;
				} else
					dir = Direction.UP;
			} else if (chance <= CHANCE_CHANGE_DIR + CHANCE_CHANGE_DIR) {
				if (dir == Direction.UP || dir == Direction.DOWN) {
					dir = Direction.RIGHT;
				} else
					dir = Direction.DOWN;
			}

		}
	}

	public int[] getHighwayStart() {

		int randBoundary = randominclusive(0, 4);
		int x = 0;
		int y = 0;

		if (randBoundary < 1) { // top
			x = randominclusive(1, 158);
		} else if (randBoundary < 2) { // right
			x = 159;
			y = randominclusive(1, 118);
		} else if (randBoundary < 3) { // bottom
			y = 119;
			x = randominclusive(1, 158);
		} else { // left
			y = randominclusive(1, 118);
		}

		return new int[] { x, y };
	}

	public void makeBlockedCells() {

		for (int i = 0; i < NUM_BLOCKED_CELLS; i++) {
			int x = randominclusive(0, 159);
			int y = randominclusive(0, 119);
			Cell cell = grid[y][x];

			if (!cell.isHighway()) {
				cell.convertTo(CellType.BLOCKED);
			}
		}

	}

	public void makeStartEndCells() {
		newStartEndCells();

		int start_x = 0;
		int start_y = 0;
		int end_x = 0;
		int end_y = 0;

		chance = rand.nextDouble();

		if (chance <= 0.5) {
			start_y = randominclusive(0, 19); // chance of start being on top
		} else
			start_y = randominclusive(99, 119); // chance of start being on
												// bottom

		start_x = randominclusive(0, 159);

		startCell = grid[start_y][start_x];

		do {
			while (startCell.celltype == CellType.BLOCKED || startCell.isHighway()) {

				if (chance <= 0.5) {
					start_y = randominclusive(0, 19);
				} else
					start_y = randominclusive(99, 119);

				start_x = randominclusive(0, 159);
				startCell = grid[start_y][start_x];

			}

			if (chance <= 0.5) {
				end_x = randominclusive(0, 19); // chance of end being on left
			} else {
				end_x = randominclusive(139, 159); // chance of end being on
													// right
			}

			end_y = randominclusive(0, 119);
			endCell = grid[end_y][end_x];

			while (endCell.celltype == CellType.BLOCKED || endCell.isHighway()) {

				if (chance <= 0.5) {
					end_x = randominclusive(0, 19);
				} else {
					end_x = randominclusive(139, 159);
				}

				end_y = randominclusive(0, 119);
				endCell = grid[end_y][end_x];

			}

		} while (!checkStartEndDistance(startCell, endCell));

		if (startCell.celltype == CellType.HARD) {
			startCell.convertTo(CellType.HARD_START);
		} else
			startCell.convertTo(CellType.UNBLOCKED_START);

		if (endCell.celltype == CellType.HARD) {
			endCell.convertTo(CellType.HARD_END);
		} else
			endCell.convertTo(CellType.UNBLOCKED_END);
	}

	private void newStartEndCells() {
		if (startCell == null && endCell == null) {
			return;
		}
		Cell start = grid[startCell.y][startCell.x];
		Cell end = grid[endCell.y][endCell.x];

		switch (start.celltype) {
		case HARD_START:
			start.convertTo(CellType.HARD);
			break;
		case UNBLOCKED_START:
			start.convertTo(CellType.UNBLOCKED);
			break;
		default:
			break;
		}
		switch (end.celltype) {
		case HARD_END:
			end.convertTo(CellType.HARD);
			break;
		case UNBLOCKED_END:
			end.convertTo(CellType.UNBLOCKED);
			break;
		default:
			break;
		}
	}

	private boolean checkStartEndDistance(Cell start_cell, Cell end_cell) {
		double distance = (Math.sqrt((start_cell.x - end_cell.x) * (start_cell.x - end_cell.x)
				+ (start_cell.y - end_cell.y) * (start_cell.y - end_cell.y)));

		if (distance >= 100)
			return true;
		else
			return false;
	}

	private Cell pave(int x, int y, Direction dir) {

		int new_x = x;
		int new_y = y;

		switch (dir) {
		case UP:
			new_y -= 1;
			break;
		case DOWN:
			new_y += 1;
			break;
		case LEFT:
			new_x -= 1;
			break;
		case RIGHT:
			new_x += 1;
			break;
		default:
			break;
		}

		if (inBounds(new_x, new_y))
			return new Cell(new_x, new_y);
		else
			return null;
	}

	private void removeHighways(LinkedList<Cell> highwayCells) {
		ListIterator<Cell> iter = highwayCells.listIterator();
		while (iter.hasNext()) {
			Cell cell = iter.next();
			int x = cell.x;
			int y = cell.y;

			if (cell.celltype == CellType.HARD_HIGHWAY) {
				grid[y][x].convertTo(CellType.HARD);
				cell.dir = Direction.NONE;
			} else if (cell.celltype == CellType.UNBLOCKED_HIGHWAY) {
				grid[y][x].convertTo(CellType.UNBLOCKED);
				cell.dir = Direction.NONE;
			}
		}
		highwayCells.clear();
	}

	private int countHighwayCells(LinkedList<Cell> highwayCells) {
		return highwayCells.size();
	}

	private boolean inBounds(int x, int y) {
		if (x < 0 || x >= 160 || y < 0 || y >= 120)
			return false;
		else
			return true;
	}

	private boolean isBoundaryCell(Cell cell) {
		if (cell.x == 0 || cell.x == 159 || cell.y == 0 || cell.y == 119)
			return true;
		else
			return false;
	}
	
	public Cell[] getNeighborCells(int x, int y) {
		ArrayList<Cell> neighborCells = new ArrayList<Cell>(8);
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (inBounds(x + i , y + j) && !( i == 0 && j == 0)) {
					neighborCells.add(this.getCell(x + i, y + j));
				}
			}
		}
		Cell[] neighborArray = new Cell[neighborCells.size()];
		return neighborCells.toArray(neighborArray);
	}

	public static int randominclusive(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}

	public Cell[][] getGrid() {
		return this.grid;
	}
	
	public ArrayList<Node> convertPathNodes(Node[] path) {
		oldPath = new ArrayList<Node> ();
		for (Node n : path) {
			if (n.celltype == CellType.UNBLOCKED_START ||
				n.celltype == CellType.HARD_START ||
				n.celltype == CellType.UNBLOCKED_END ||
				n.celltype == CellType.HARD_END)
				 continue;
			else {
				oldPath.add(n);
				grid[n.y][n.x].convertTo(CellType.AGENT);
			}
		}
		
		return oldPath;
	}
	
	public void revertPath() {
		for (Node n : oldPath) {
			grid[n.y][n.x].convertTo(n.celltype);
		}
	}

	public float getCost(Cell a, Cell b) {
		if (a == b)
			return 0;
		else if (a.x == b.x && Math.abs(b.y - a.y) == 1 || a.y == b.y && Math.abs(b.x - a.x) == 1) {
			if (a.celltype == CellType.UNBLOCKED) {
				if (b.celltype == CellType.UNBLOCKED)
					return 1;
				if (b.celltype == CellType.HARD)
					return 1.5f;
				if (b.celltype == CellType.BLOCKED)
					return -1;
				if (b.celltype == CellType.UNBLOCKED_HIGHWAY)
					return 0.625f;
				if (b.celltype == CellType.HARD_HIGHWAY)
					return 0.875f;
			} else if (a.celltype == CellType.HARD) {
				if (b.celltype == CellType.UNBLOCKED)
					return 1.5f;
				if (b.celltype == CellType.HARD)
					return 2;
				if (b.celltype == CellType.BLOCKED)
					return -1;
				if (b.celltype == CellType.UNBLOCKED_HIGHWAY)
					return 0.875f;
				if (b.celltype == CellType.HARD_HIGHWAY)
					return 1.25f;
			} else if (a.celltype == CellType.BLOCKED) {
					return -1;
			} else if (a.celltype == CellType.UNBLOCKED_HIGHWAY) {
				if (b.celltype == CellType.UNBLOCKED)
					return 0.625f;
				if (b.celltype == CellType.HARD)
					return 0.875f;
				if (b.celltype == CellType.BLOCKED)
					return -1;
				if (b.celltype == CellType.UNBLOCKED_HIGHWAY)
					return 0.25f;
				if (b.celltype == CellType.HARD_HIGHWAY)
					return 0.375f;
			} else if (a.celltype == CellType.HARD_HIGHWAY) {
				if (b.celltype == CellType.UNBLOCKED)
					return 0.875f;
				if (b.celltype == CellType.HARD)
					return 1.25f;
				if (b.celltype == CellType.BLOCKED)
					return -1;
				if (b.celltype == CellType.UNBLOCKED_HIGHWAY)
					return 0.375f;
				if (b.celltype == CellType.HARD_HIGHWAY)
					return 0.5f;
			}
		} else if (Math.abs(a.x - b.x) == 1 && Math.abs(b.y - a.y) == 1) {
			if (a.celltype == CellType.UNBLOCKED) {
				if (b.celltype == CellType.UNBLOCKED)
					return 1.414f;
				if (b.celltype == CellType.HARD)
					return 2.121f;
				if (b.celltype == CellType.BLOCKED)
					return -1;
				if (b.celltype == CellType.UNBLOCKED_HIGHWAY)
					return 0.884f;
				if (b.celltype == CellType.HARD_HIGHWAY)
					return 1.326f;
			} else if (a.celltype == CellType.HARD) {
				if (b.celltype == CellType.UNBLOCKED)
					return 2.121f;
				if (b.celltype == CellType.HARD)
					return 2.828f;
				if (b.celltype == CellType.BLOCKED)
					return -1;
				if (b.celltype == CellType.UNBLOCKED_HIGHWAY)
					return 1.326f;
				if (b.celltype == CellType.HARD_HIGHWAY)
					return 1.768f;
			} else if (a.celltype == CellType.BLOCKED) {
					return -1;
			} else if (a.celltype == CellType.UNBLOCKED_HIGHWAY) {
				if (b.celltype == CellType.UNBLOCKED)
					return 0.884f;
				if (b.celltype == CellType.HARD)
					return 1.326f;
				if (b.celltype == CellType.BLOCKED)
					return -1;
				if (b.celltype == CellType.UNBLOCKED_HIGHWAY)
					return 0.354f;
				if (b.celltype == CellType.HARD_HIGHWAY)
					return 0.53f;
			} else if (a.celltype == CellType.HARD_HIGHWAY) {
				if (b.celltype == CellType.UNBLOCKED)
					return 1.326f;
				if (b.celltype == CellType.HARD)
					return 1.768f;
				if (b.celltype == CellType.BLOCKED)
					return -1;
				if (b.celltype == CellType.UNBLOCKED_HIGHWAY)
					return 0.53f;
				if (b.celltype == CellType.HARD_HIGHWAY)
					return 0.707f;
			}
		}

		return -1;

	}
}