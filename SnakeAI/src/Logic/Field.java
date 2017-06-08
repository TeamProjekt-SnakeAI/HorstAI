/*
 * Stores the current state of the field
 * Author: Thomas Stüber
 * */

package Logic;
import java.util.Arrays;
import java.util.HashMap;


public class Field {
	public enum CellType {
		SNAKE,
		WALL,
		APPLE,
		SPACE
	}

	private CellType[][] cells;
	private int width;
	private int height;
	private HashMap<Point, Apple> apples;
	
	public Field(int width, int height) {
		cells = new CellType[width][height];
		this.width = width;
		this.height = height;
		apples = new HashMap<Point, Apple>();
	}
	
	public static Field defaultField(int width, int height) {
		Field f = new Field(width, height);
		for (int x = 0;x < width;x++) {
			for (int y = 0;y < height;y++) {
				if (x == 0 || x == width-1 || y == 0 || y == height-1) {
					f.cells[x][y] = CellType.WALL;
				} else {
					f.cells[x][y] = CellType.SPACE;
				}
			}
		}
		return f;
	}
	
	public static Field defaultFieldWithoutBorders(int width, int height) {
		Field f = new Field(width, height);
		for (int x = 0;x < width;x++) {
			for (int y = 0;y < height;y++) {
				f.cells[x][y] = CellType.SPACE;
			}
		}
		return f;
	}
	
	public void addApple(Apple apple, Point position) {
		apples.put(position,  apple);
		cells[position.x][position.y] = CellType.APPLE;
	}
	
	public Apple getApple(Point position) {
		return apples.get(position);
	}
	
	public HashMap<Point, Apple> getApples() {
		return apples;
	}

	public void setApples(HashMap<Point, Apple> apples) {
		this.apples = apples;
	}

	public void removeApple(Point position) {
		apples.remove(position);
		cells[position.x][position.y] = CellType.SPACE;
	}
	
	public void draw() {
		System.out.println(this);
	}
	
	public void setCell(CellType type, Point point) {
		cells[point.x][point.y] = type;
	}
	
	public CellType cell(Point point) {
		return cells[point.x][point.y];
	}
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}

	@Override
	public String toString() {
		String s = "";
		for (int y = 0;y < height;y++) {
			for (int x = 0;x < width;x++) {
				switch(cells[x][y]) {
				case APPLE:
					s += "*";
					break;
				case SNAKE:
					s += "#";
					break;
				case SPACE:
					s += " ";
					break;
				case WALL:
					s += "X";
					break;
				default:
					break;
				
				}
			}
			if (y < height-1) {
				s += "\n";
			}
		}
		return s;
	}
}
