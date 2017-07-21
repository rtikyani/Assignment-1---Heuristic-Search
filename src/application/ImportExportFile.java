package application;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;

import model.Cell;
import model.Grid;
import model.Cell.CellType;

public class ImportExportFile {
	private static int linesRead = 0;
	
	public static Grid importFile(String pathName, int rows, int columns) {	
		Grid grid = new Grid(columns, rows);
		
		// number coordinates to read before making grid
		int numCoordsToRead = grid.hardCenters.length + 2; 
		
		try(Stream<String> stream = Files.lines(Paths.get(pathName))) {
			
			stream.forEach(s -> {
				if(++linesRead <= numCoordsToRead) {
					String[] stringArr = s.split(",");
					int a = Integer.parseInt(stringArr[0]), b = Integer.parseInt(stringArr[1]);
					
					if(linesRead == 1) { // start cell
						grid.startCell = grid.getCell(a+1,b+1);
					} else if(linesRead == 2) { // end cell
						grid.endCell = grid.getCell(a+1, b+1);
					} else { // hard cells centers
						grid.hardCenters[linesRead - 3][0] = a;
						grid.hardCenters[linesRead - 3][1] = b;
					}
				} else {
					char[] charArr = s.toCharArray();
					for(int i = 1; i <= charArr.length; i++) {						
						int col = linesRead - numCoordsToRead;
						Cell c = grid.getCell(i, col);
						switch(charArr[i - 1]) {
						case '0':
							c.convertTo(CellType.BLOCKED); break;
						case '1':
							c.convertTo(CellType.UNBLOCKED); break;
						case '2':
							c.convertTo(CellType.HARD); break;
						case 'a':
							c.convertTo(CellType.UNBLOCKED_HIGHWAY); break;
						case 'b':
							c.convertTo(CellType.HARD_HIGHWAY); break;
						default: break;
						}
					}
				}
			});
			linesRead = 0;
			
			
			if (grid.startCell.x == 0 || grid.startCell.x == 160)
				grid.startCell.x++;
			if (grid.endCell.x == 0 || grid.endCell.x == 160)
				grid.endCell.x++;
			
			if (grid.startCell.y == 0 || grid.startCell.y == 120)
				grid.startCell.y++;
			if (grid.endCell.y == 0 || grid.endCell.y == 160)
				grid.endCell.y++;
			
			Cell startCell = grid.getCell(grid.startCell.x, grid.startCell.y);		
			Cell endCell = grid.getCell(grid.endCell.x, grid.endCell.y);
			
			if(startCell.celltype == CellType.UNBLOCKED) {
				startCell.convertTo(CellType.UNBLOCKED_START);
			} else {
				startCell.convertTo(CellType.HARD_START);
			}
			
			if(endCell.celltype == CellType.UNBLOCKED) {
				endCell.convertTo(CellType.UNBLOCKED_END);
			} else {
				endCell.convertTo(CellType.HARD_END);
			}
		} catch(IOException e) {
			linesRead = 0;
			e.printStackTrace();
		}
		
		return grid;
	}
	
	public static void exportFile(Grid grid, File file) {
		try{
		    PrintWriter writer = new PrintWriter(file, "UTF-8");
		    writer.println(grid.startCell.x + "," + grid.startCell.y);
		    writer.println(grid.endCell.x + "," + grid.endCell.y);
		    
		    for (int[] hardCenter : grid.hardCenters) {
		    	writer.println(hardCenter[0] + "," + hardCenter[1]);
		    }
		    
		    for(Cell[] cArr : grid.getGrid()) {
		    	for(Cell c : cArr) {
		    		switch(c.celltype) {
					case BLOCKED:
						writer.print("0"); break;
					case UNBLOCKED:
						writer.print("1"); break;	
					case HARD:
						writer.print("2"); break;
					case UNBLOCKED_HIGHWAY:
						writer.print("a"); break;	
					case HARD_HIGHWAY:
						writer.print("b"); break;				
					default: break;
		    		}	
		    	}
		    	writer.println();
		    }
		    
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
