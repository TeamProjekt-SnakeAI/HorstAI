/*
 * Stores the current state of the field
 * Author: Thomas Stüber
 * */

package Logic;
import java.util.Arrays;
import java.util.HashMap;

import Logic.Snake.Direction;


public class Field {
	public enum CellType {
		SNAKE,
		WALL,
		APPLE,
		SPACE,
		FEATUREWALL
	}

	private CellType[][] cells;
	private int width;
	private int height;
	private HashMap<Point, Apple> apples;
	private boolean hasFeatureWall;
	
	public Field(int width, int height) {
		cells = new CellType[width][height];
		this.width = width;
		this.height = height;
		apples = new HashMap<Point, Apple>();
		hasFeatureWall = false;
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
				case FEATUREWALL:
					s += "+";
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
	public HashMap<Point,Apple> getApples(){
		return apples;
	}
	public void setFeatureWall(Point position){
		cells[position.x][position.y] = CellType.FEATUREWALL;
		hasFeatureWall = true;
	}
	public void removeFeatureWall(Point position){
		if(cells[position.x][position.y] == CellType.FEATUREWALL){
			cells[position.x][position.y] = CellType.SPACE;
			hasFeatureWall = false;
		}
	}
	public boolean hasFeatureWall(){
		return hasFeatureWall;
	}
	//Sets a wall at a given point in a given direction, length 3
	public void setWall(Point centerPoint, Direction direction){
		if(cell(centerPoint) == CellType.SPACE){
			if(direction == Direction.UP || direction == Direction.DOWN ){
				if(cell(new Point(centerPoint.x,centerPoint.y +1)) == CellType.SPACE && cell(new Point(centerPoint.x, centerPoint.y-1)) == CellType.SPACE){
					for(int i = -1;i<2;i++){
						cells[centerPoint.x][centerPoint.y+i] = CellType.WALL;
					}
				}
			}
			else{
				if(cell(new Point(centerPoint.x+1,centerPoint.y)) == CellType.SPACE && cell(new Point(centerPoint.x-1,centerPoint.y)) == CellType.SPACE){
					for(int i = -1;i<2;i++){
						cells[centerPoint.x+i][centerPoint.y] = CellType.WALL;
					}
				}
			}
		}
	}
}
