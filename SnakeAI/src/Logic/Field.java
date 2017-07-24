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
		CHANGEHEADTAIL,
		SPEEDUP
	}
	
	private CellType[][] cells;
	private int width;
	private int height;
	private HashMap<Point, Apple> apples;
	// 0 = Apple, 1 = featureWall, 2 = changeSnake, 3 = changeHeadTail, 4 = speedUp, 5 = isPortalActive
	private boolean[] activeFeatures = {false, false , false , false , false, false};
	private Point speedUpPos; // the position of the speedUp feature, null if field hasn't it
//	private boolean hasFeatureWall;
//	private boolean isPortalActive;
	
	public Field(int width, int height) {
		cells = new CellType[width][height];
		this.width = width;
		this.height = height;
		apples = new HashMap<Point, Apple>();
		speedUpPos = null;
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
	
	public int width() {
		return width;
	}
	
	public int height() {
		return height;
	}
	
	public void setCell(CellType type, Point point) {
		cells[point.x][point.y] = type;
	}
	
	public CellType cell(Point point) {
		return cells[point.x][point.y];
	}
	
	public boolean isFeatureActive(int i) {
		return activeFeatures[i];
	}
	
	public void setFeature(int i, Point p) {
		switch (i) {
		case 0: addApple(new Apple(50, 1, p), p); break;
		case 1: setFeatureWall(p); break;
		case 2: setChangeSnake(p); break;
		case 3: setChangeHeadTail(p); break;
		case 4: setSpeedUp(p); break;
		}
	}
	
	// apple stuff
	public Apple getApple(Point position) {
		return apples.get(position);
	}
	
	public HashMap getApples() {
		return apples;
	}
	
	public void addApple(Apple apple, Point position) {
		apples.put(position,  apple);
		cells[position.x][position.y] = CellType.APPLE;
		activeFeatures[0] = true;
	}
	
	public void removeApple(Point position) {
		apples.remove(position);
		cells[position.x][position.y] = CellType.SPACE;
		activeFeatures[0] = false;
	}
	
	// featurewall stuff
	public boolean hasFeatureWall() {
		return activeFeatures[1];
	}
	
	public void setFeatureWall(Point position) {
		cells[position.x][position.y] = CellType.FEATUREWALL;
		activeFeatures[1] = true;
	}
	
	public void removeFeatureWall(Point position) {
		if(cells[position.x][position.y] == CellType.FEATUREWALL) {
			cells[position.x][position.y] = CellType.SPACE;
			activeFeatures[1] = false;
		}
	}
	
	// portal stuff
	public void addPortal(Portals portal) {
		cells[portal.getPortal1().x][portal.getPortal1().y] = CellType.PORTAL;
		cells[portal.getPortal2().x][portal.getPortal2().y] = CellType.PORTAL;
	}
	
	public void removePortal(Portals portal) {
		cells[portal.getPortal1().x][portal.getPortal1().y] = CellType.SPACE;
		cells[portal.getPortal2().x][portal.getPortal2().y] = CellType.SPACE;
	}
	
	// changeSnake stuff
	public boolean hasChangeSnake() {
		return activeFeatures[2];
	}
	
	public void setChangeSnake(Point position) {
		cells[position.x][position.y] = CellType.CHANGESNAKE;
		activeFeatures[2] = true;
	}
	
	public void removeChangeSnake(Point position) {
		if(cells[position.x][position.y] == CellType.CHANGESNAKE) {
			cells[position.x][position.y] = CellType.SPACE;
			activeFeatures[2] = false;
		}
	}
	
	// changeHeadTail stuff
	public boolean hasChangeHeadTail() {
		return activeFeatures[3];
	}
	
	public void setChangeHeadTail(Point position) {
		cells[position.x][position.y] = CellType.CHANGEHEADTAIL;
		activeFeatures[3] = true;
	}
	
	public void removeChangeHeadTail(Point position) {
		if(cells[position.x][position.y] == CellType.CHANGEHEADTAIL) {
			cells[position.x][position.y] = CellType.SPACE;
			activeFeatures[3] = false;
		}
	}
	
	// speedUp stuff
	public boolean hasSpeedUp() {
		return activeFeatures[4];
	}
	
	public void setSpeedUp(Point position) {
		speedUpPos = position;
		cells[position.x][position.y] = CellType.SPEEDUP;
		activeFeatures[4] = true;
	}
	
	public Point getSpeedUpPos() {
		return speedUpPos;
	}
	
	public void removeSpeedUp(Point position) {
		if(cells[position.x][position.y] == CellType.SPEEDUP) {
			speedUpPos = null;
			cells[position.x][position.y] = CellType.SPACE;
			activeFeatures[4] = false;
		}
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
	
	public void draw() {
		System.out.println(this);
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
				case SPEEDUP:
					s += ">";
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
