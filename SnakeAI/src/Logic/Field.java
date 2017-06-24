/*
 * Stores the current state of the field
 * Author: Thomas Stüber
 * */

package Logic;

import java.util.HashMap;


import Logic.Snake.Direction;


public class Field {
	public enum CellType {
		SNAKE,
		WALL,
		APPLE,
		SPACE,
		FEATUREWALL,
		PORTAL,
		CHANGESNAKE,
		CHANGEHEADTAIL
	}

	private CellType[][] cells;
	private int width;
	private int height;
	private HashMap<Point, Apple> apples;
	
	//0 = featureWall , 1 = changeSnake , 2 = changeHeadTail , 3 = isPortalActive
	private boolean[] activeFeatures = { false , false , false , false };
//	private boolean hasFeatureWall;
//	private boolean hasChangeSnake;
//	private boolean hasChangeHeadTail;
//	private boolean isPortalActive;
	
	public Field(int width, int height) {
		cells = new CellType[width][height];
		this.width = width;
		this.height = height;
		apples = new HashMap<Point, Apple>();
//		hasFeatureWall = false;
//		isPortalActive=false;
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
	public void addPortal(Portals portal) {
		cells[portal.getPortal1().x][portal.getPortal1().y] = CellType.PORTAL;
		cells[portal.getPortal2().x][portal.getPortal2().y] = CellType.PORTAL;
	}
	public void removePortal(Portals portal) {
		cells[portal.getPortal1().x][portal.getPortal1().y] = CellType.SPACE;
		cells[portal.getPortal2().x][portal.getPortal2().y] = CellType.SPACE;
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
					break;
				case PORTAL:
					s += "O";
					break;
				case CHANGESNAKE:
					s += "%";
					break;
				case CHANGEHEADTAIL:
					s += "=";
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
	public HashMap getApples(){
		return apples;
	}
	public void setFeatureWall(Point position){
		cells[position.x][position.y] = CellType.FEATUREWALL;
		activeFeatures[0] = true;
	}
	public void setChangeSnake(Point position){
		cells[position.x][position.y] = CellType.CHANGESNAKE;
		activeFeatures[1] = true;
	}
	public void setChangeHeadTail(Point position){
		cells[position.x][position.y] = CellType.CHANGEHEADTAIL;
		activeFeatures[2] = true;
	}
	public void removeFeatureWall(Point position){
		if(cells[position.x][position.y] == CellType.FEATUREWALL){
			cells[position.x][position.y] = CellType.SPACE;
			activeFeatures[0] = false;
		}
	}
	public void removeChangeSnake(Point position){
		if(cells[position.x][position.y] == CellType.CHANGESNAKE){
			cells[position.x][position.y] = CellType.SPACE;
			activeFeatures[1] = false;
		}
	}
	public void removeChangeHeadTail(Point position){
		if(cells[position.x][position.y] == CellType.CHANGEHEADTAIL){
			cells[position.x][position.y] = CellType.SPACE;
			activeFeatures[2] = false;
		}
	}
	public boolean hasFeatureWall(){
		return activeFeatures[0];
	}
	public boolean hasChangeSnake()
	{
		return activeFeatures[1];
	}
	public boolean hasChangeHeadTail()
	{
		return activeFeatures[2];
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
