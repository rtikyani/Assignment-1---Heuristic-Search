package controller;

import java.io.File;

import algorithms.AStar;
import algorithms.UniformCostSearch;
import algorithms.WeightedAStar;
import application.ImportExportFile;
import heuristics.ChebyshevDistance;
import heuristics.EuclideanDistance;
import heuristics.EuclideanSquaredDistance;
import heuristics.ManhattanDistance;
import heuristics.OctileDistance;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import model.Cell;
import model.Cell.CellType;
import model.Grid;
import model.Node;

public class LayoutController {

	final int CELL_SIZE = 6;
	final int ROWS = 120;
	final int COLUMNS = 160;

	@FXML
	private GridPane gridPane;
	private Grid grid;
	private Stage stage;
	private FileChooser fileChoose = new FileChooser();
	private long runTime;

	@FXML
	private Label coordsLabel;
	@FXML
	private Label cellTypeLabel;
	@FXML
	private Label startCellLabel;
	@FXML
	private Label endCellLabel;
	@FXML
	private Label calculateCostLabel;
	@FXML
	private Label runTimeLabel;
	@FXML
	private Label memoryUsageLabel;
	@FXML
	private ComboBox<String> AStarHeuristics;
	@FXML
	private ComboBox<String> WeightedAStarHeuristics;

	private String[] heuristic = { "Euclidean", "EuclideanSquared", "Chebyshev", "Manhattan", "Octile" };

	public LayoutController() {
		this.grid = new Grid(COLUMNS, ROWS);
	}

	@FXML
	private void initialize() {
		initGridGui(gridPane);
		setData();
		setDataSelectors();
	}

	private void setData() {

		AStarHeuristics.getItems().addAll(heuristic);
		WeightedAStarHeuristics.getItems().addAll(heuristic);

		AStarHeuristics.getSelectionModel();
		WeightedAStarHeuristics.getSelectionModel();

	}

	private void setDataSelectors() {
		AStarHeuristics.setOnAction(e -> {
			runAStar(AStarHeuristics.getSelectionModel().getSelectedItem());
		});

		WeightedAStarHeuristics.setOnAction(e -> {
			runWeightedAStar(WeightedAStarHeuristics.getSelectionModel().getSelectedItem());
		});
	}
	
	private void initGridGui(GridPane gridPane) {
		gridPane.setPadding(new Insets(2));
		gridPane.setHgap(2);
		gridPane.setVgap(2);

		grid.setHardCells();
		grid.makeHighways();
		grid.makeBlockedCells();
		grid.makeStartEndCells();
		paintGrid();
	}

	private void paintGrid() {
		gridPane.getChildren().clear();

		Rectangle rect;
		Cell cell;
		Color color;

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLUMNS; c++) {
				{
					cell = grid.getCell(c, r);

					switch (cell.celltype) {

					case UNBLOCKED:
						color = Color.LIGHTBLUE;
						break;
					case HARD:
						color = Color.ORANGE;
						break;
					case UNBLOCKED_HIGHWAY:
						color = Color.BLUE;
						break;
					case HARD_HIGHWAY:
						color = Color.DARKBLUE;
						break;
					case BLOCKED:
						color = Color.BLACK;
						break;
					case UNBLOCKED_START:
					case HARD_START:
						showStartEndCellsInfo(cell);
						color = Color.MEDIUMSPRINGGREEN;
						break;
					case UNBLOCKED_END:
					case HARD_END:
						showStartEndCellsInfo(cell);
						color = Color.RED;
						break;
					case AGENT:
						color = Color.GOLD;
						break;
					default:
						color = Color.LIGHTBLUE;
						break;
					}

					rect = new Rectangle(CELL_SIZE, CELL_SIZE, color);
					showInfoOnClick(rect, c, r);
					try {
						gridPane.add(rect, c, r);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void runUCS() {
		grid.revertPath();

		Node start = new Node(grid.startCell);
		Node end = new Node(grid.endCell);

		UniformCostSearch ucs = new UniformCostSearch(start, end, grid);
		
		long startTime = System.currentTimeMillis();
		Node[] path = ucs.run();
		long endTime = System.currentTimeMillis();
		
		runTime = endTime - startTime;

		grid.convertPathNodes(path);
		paintGrid();
		System.out.println("Cost: " + ucs.calculateCosts());
		System.out.println("Runtime: " + runTime + " ms");
		System.out.println("Memory Usage: " + ucs.totalMemory);
		
		memoryUsageLabel.setText("" + ucs.totalMemory);
		calculateCostLabel.setText("" + ucs.calculateCosts());
		runTimeLabel.setText(runTime + " ms");

	}
	
	public void runAStar(String heuristic) {
		grid.revertPath();
		
	

		Node start = new Node(grid.startCell);
		Node end = new Node(grid.endCell);
		AStar AStar = null;
		
		switch (heuristic) {
		case "Euclidean":
			EuclideanDistance ed = new EuclideanDistance(grid);
			AStar = new AStar(start, end, grid, ed);
			break;
		case "EuclideanSquared":
			EuclideanSquaredDistance esd = new EuclideanSquaredDistance(grid);
			AStar = new AStar(start, end, grid, esd);
			break;
		case "Chebyshev":
			ChebyshevDistance cd = new ChebyshevDistance(grid);
			AStar = new AStar(start, end, grid, cd);;
			break;
		case "Manhattan":
			ManhattanDistance md = new ManhattanDistance(grid);
			AStar = new AStar(start, end, grid, md);
			break;
		case "Octile":
			OctileDistance od = new OctileDistance(grid);
			AStar = new AStar(start, end, grid, od);
			break;
		default:
			break;
		}
		
		long startTime = System.currentTimeMillis();
		Node[] path = AStar.run();
		long endTime = System.currentTimeMillis();
		
		runTime = endTime - startTime;

		grid.convertPathNodes(path);
		paintGrid();
		System.out.println("Cost: " + AStar.calculateCosts());
		System.out.println("Runtime: " + runTime + " ms");
		System.out.println("Memory Usage: " + AStar.totalMemory);
		
		memoryUsageLabel.setText("" + AStar.totalMemory);
		calculateCostLabel.setText("" + AStar.calculateCosts());
		runTimeLabel.setText(runTime + " ms");
	}
	
	public void runWeightedAStar(String heuristic) {
		grid.revertPath();

		Node start = new Node(grid.startCell);
		Node end = new Node(grid.endCell);
		WeightedAStar WeightedAStar = null;
		
		switch (heuristic) {
		case "Euclidean":
			EuclideanDistance ed = new EuclideanDistance(grid);
			WeightedAStar = new WeightedAStar(start, end, grid, ed, 2);
			break;
		case "EuclideanSquared":
			EuclideanSquaredDistance esd = new EuclideanSquaredDistance(grid);
			WeightedAStar = new WeightedAStar(start, end, grid, esd, 2);
			break;
		case "Chebyshev":
			ChebyshevDistance cd = new ChebyshevDistance(grid);
			WeightedAStar = new WeightedAStar(start, end, grid, cd, 2);
			break;
		case "Manhattan":
			ManhattanDistance md = new ManhattanDistance(grid);
			WeightedAStar = new WeightedAStar(start, end, grid, md, 2);
			break;
		case "Octile":
			OctileDistance od = new OctileDistance(grid);
			WeightedAStar = new WeightedAStar(start, end, grid, od ,2);
			break;
		default:
			break;
		}

		long startTime = System.currentTimeMillis();
		Node[] path = WeightedAStar.run();
		long endTime = System.currentTimeMillis();
		
		runTime = endTime - startTime;
		
		grid.convertPathNodes(path);
		paintGrid();
		System.out.println("Cost: " + WeightedAStar.calculateCosts());
		System.out.println("Runtime: " + runTime + " ms");
		System.out.println("Memory Usage: " + WeightedAStar.totalMemory);
		
		memoryUsageLabel.setText("" + WeightedAStar.totalMemory);
		calculateCostLabel.setText("" + WeightedAStar.calculateCosts());
		runTimeLabel.setText(runTime + " ms");
	}

	private void showInfoOnClick(Rectangle rect, int c, int r) {

		rect.setOnMouseClicked(e -> {
			Cell cell = grid.getCell(c, r);
			coordsLabel.setText(cell.x + ", " + cell.y);
			cellTypeLabel.setText("" + cell.celltype);
		});
	}

	private void showStartEndCellsInfo(Cell cell) {
		if (cell.celltype == CellType.UNBLOCKED_START || cell.celltype == CellType.HARD_START) {
			startCellLabel.setText(cell.x + ", " + cell.y);
		} else
			endCellLabel.setText(cell.x + ", " + cell.y);
	}

	private void changeGrid(Grid grid) {
		this.grid = grid;
		paintGrid();
	}

	public Grid makeNewGrid(boolean changeGoalOnly) {
		if (!changeGoalOnly) {
			this.grid = new Grid(COLUMNS, ROWS);
			this.grid.setHardCells();
			this.grid.makeHighways();
			this.grid.makeBlockedCells();
		}
		this.grid.makeStartEndCells();
		paintGrid();
		return this.grid;
	}

	@FXML
	private void handleImport() {
		File file = fileChoose.showOpenDialog(stage);
		if (file != null) {
			grid = ImportExportFile.importFile(file.getAbsolutePath(), ROWS, COLUMNS);
			changeGrid(grid);
		}
	}

	@FXML
	private void handleExport() {
		File file = fileChoose.showOpenDialog(stage);
		if (file != null) {
			ImportExportFile.exportFile(grid, file);
		}
	}

	@FXML
	private void handleShowNewGrid() {
		makeNewGrid(false);
	}

	@FXML
	private void handleChangeStartEnd() {
		grid.revertPath();
		makeNewGrid(true);
	}

}