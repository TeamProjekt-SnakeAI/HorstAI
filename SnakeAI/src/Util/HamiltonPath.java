package Util;

import Logic.Field;
import Logic.Point;
import Logic.Snake;
import Logic.Field.CellType;
import Logic.Snake.Direction;

public class HamiltonPath {
	private Pathfinding finder;
	private Field actualField;
	private int[][] longWayMap;
	public static int SPACE = 1;
	public static int APPLE = 1;
	public static int SNAKE = 100;
	public static int WALL = 100;
	
	public HamiltonPath(Field field)
	{
		actualField = field;
		finder = new Pathfinding(field);
	}
	public Node getCompleteMaxPath(Field f)
	{
		actualField = Field.defaultField(f.width(), f.height());
		Node start = new Node(null,new Point(1,1),0,0);
		Point target = new Point(1,2);
		calcDistanceMap(target);
		
		Node way = new Node(new Node(null,start.getActual(),0,0),target,0,0);
		Node tempWay = way;
		while(tempWay != null)
		{
			longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
			tempWay = tempWay.getFrom();
		}
		tempWay = way;
//		System.out.println(way);
//		System.out.println("Way ready to optimze");
		int i=0;
		while(tempWay != null && tempWay.getFrom() != null && i <= 4)
		{
			Point from = tempWay.getFrom().getActual();
			Point to = tempWay.getActual();
			Direction dir = UtilFunctions.getDirection(from,to);
//			System.out.println("From: " + from);
//			System.out.println("To: " + to);
//			System.out.println("Dir: " + dir);
			boolean changed = false;
			switch(dir)
			{
			case UP:
			case DOWN:
				Point left = new Point(from.x-1,from.y);
				Point right = new Point(from.x+1,from.y);
				if(longWayMap[left.x][left.y] == 1 && !pathContains(left,way))
				{
					Point leftUp = new Point(to.x-1,to.y);
					if(longWayMap[leftUp.x][leftUp.y] == 1 && !pathContains(leftUp,way))
					{
						tempWay.setFrom(new Node(new Node(tempWay.getFrom(),left,0,0),leftUp,0,0));						
						changed=true;
					}
//					longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
//					longWayMap[left.x][left.y]=100;
//					longWayMap[tempWay.getFrom().getActual().x][tempWay.getFrom().getActual().y]=100;
				}
				else if(longWayMap[right.x][right.y] == 1 && !pathContains(right,way))
				{
					Point rightDown = new Point(to.x+1,to.y);
					if(longWayMap[rightDown.x][rightDown.y] == 1 && !pathContains(rightDown,way))
					{
						tempWay.setFrom(new Node(new Node(tempWay.getFrom(),right,0,0),rightDown,0,0));
						changed=true;						
					}
//					longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
//					longWayMap[right.x][right.y]=100;
//					longWayMap[tempWay.getFrom().getActual().x][tempWay.getFrom().getActual().y]=100;
				}
				break;
			case LEFT:
			case RIGHT:
				Point up = new Point(from.x,from.y-1);
				Point down = new Point(from.x,from.y+1);
				if(longWayMap[up.x][up.y] == 1 && !pathContains(up,way))
				{
					Point upLeft = new Point(to.x,to.y-1);
					if(longWayMap[upLeft.x][upLeft.y] == 1 && !pathContains(upLeft,way))
					{
						tempWay.setFrom(new Node(new Node(tempWay.getFrom(),up,0,0),upLeft,0,0));
						changed=true;						
					}
//					longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
//					longWayMap[up.x][up.y]=100;
//					longWayMap[tempWay.getFrom().getActual().x][tempWay.getFrom().getActual().y]=100;
				}
				else if(longWayMap[down.x][down.y] == 1 && !pathContains(down,way))
				{
					Point downRight = new Point(to.x,to.y+1);
					if(longWayMap[downRight.x][downRight.y] == 1 && !pathContains(downRight,way))
					{
						tempWay.setFrom(new Node(new Node(tempWay.getFrom(),down,0,0),downRight,0,0));
						changed=true;						
					}
//					longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
//					longWayMap[down.x][down.y]=100;
//					longWayMap[tempWay.getFrom().getActual().x][tempWay.getFrom().getActual().y]=100;
				}
				break;
			}
//			System.out.println("Temp: " +way.getPath());
			if(!changed)
			{
//				System.out.println("not changed!");
				longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
				tempWay = tempWay.getFrom();
			}
//			i++;
		}
		return way;
	}
	public Node getMaxPath(Point startPoint, Field field, Snake snake) {
		actualField = field;
		Node start = new Node(null,startPoint,0,0);
		Point target = snake.segments().get(0);
		Node way = null;
		for(Point p : snake.segments())
		{
			calcDistanceMap(p);
			Field tmpField = Field.defaultField(field.width(), field.height());
			for(int i=0;i<snake.segments().size();i++)
			{
				if(snake.segments().get(i).equals(snake.headPosition()) || snake.segments().get(i).equals(p))
					tmpField.setCell(CellType.SPACE, snake.segments().get(i));
			}
			if(finder == null)
				finder = new Pathfinding(tmpField);
			finder.getMinPath(startPoint, target, tmpField, snake.segments().get(0));
			way = UtilFunctions.getMovePair(target,finder.getClosedList());
			if(way != null)
				break;
		}	
		 
		Node tempWay = way;
		while(tempWay != null)
		{
			longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
			tempWay = tempWay.getFrom();
		}
		tempWay = way;
//		System.out.println(way);
//		System.out.println("Way ready to optimze");
		int i=0;
		while(tempWay != null && tempWay.getFrom() != null && i <= 4)
		{
			Point from = tempWay.getFrom().getActual();
			Point to = tempWay.getActual();
			Direction dir = UtilFunctions.getDirection(from,to);
//			System.out.println("From: " + from);
//			System.out.println("To: " + to);
//			System.out.println("Dir: " + dir);
			boolean changed = false;
			switch(dir)
			{
			case UP:
			case DOWN:
				Point left = new Point(from.x-1,from.y);
				Point right = new Point(from.x+1,from.y);
				if(longWayMap[left.x][left.y] == 1 && !pathContains(left,way))
				{
					Point leftUp = new Point(to.x-1,to.y);
					if(longWayMap[leftUp.x][leftUp.y] == 1 && !pathContains(leftUp,way))
					{
						tempWay.setFrom(new Node(new Node(tempWay.getFrom(),left,0,0),leftUp,0,0));						
						changed=true;
					}
//					longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
//					longWayMap[left.x][left.y]=100;
//					longWayMap[tempWay.getFrom().getActual().x][tempWay.getFrom().getActual().y]=100;
				}
				else if(longWayMap[right.x][right.y] == 1 && !pathContains(right,way))
				{
					Point rightDown = new Point(to.x+1,to.y);
					if(longWayMap[rightDown.x][rightDown.y] == 1 && !pathContains(rightDown,way))
					{
						tempWay.setFrom(new Node(new Node(tempWay.getFrom(),right,0,0),rightDown,0,0));
						changed=true;						
					}
//					longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
//					longWayMap[right.x][right.y]=100;
//					longWayMap[tempWay.getFrom().getActual().x][tempWay.getFrom().getActual().y]=100;
				}
				break;
			case LEFT:
			case RIGHT:
				Point up = new Point(from.x,from.y-1);
				Point down = new Point(from.x,from.y+1);
				if(longWayMap[up.x][up.y] == 1 && !pathContains(up,way))
				{
					Point upLeft = new Point(to.x,to.y-1);
					if(longWayMap[upLeft.x][upLeft.y] == 1 && !pathContains(upLeft,way))
					{
						tempWay.setFrom(new Node(new Node(tempWay.getFrom(),up,0,0),upLeft,0,0));
						changed=true;						
					}
//					longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
//					longWayMap[up.x][up.y]=100;
//					longWayMap[tempWay.getFrom().getActual().x][tempWay.getFrom().getActual().y]=100;
				}
				else if(longWayMap[down.x][down.y] == 1 && !pathContains(down,way))
				{
					Point downRight = new Point(to.x,to.y+1);
					if(longWayMap[downRight.x][downRight.y] == 1 && !pathContains(downRight,way))
					{
						tempWay.setFrom(new Node(new Node(tempWay.getFrom(),down,0,0),downRight,0,0));
						changed=true;						
					}
//					longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
//					longWayMap[down.x][down.y]=100;
//					longWayMap[tempWay.getFrom().getActual().x][tempWay.getFrom().getActual().y]=100;
				}
				break;
			}
//			System.out.println("Temp: " +way.getPath());
			if(!changed)
			{
//				System.out.println("not changed!");
				longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
				tempWay = tempWay.getFrom();
			}
//			i++;
		}
	
		return way;
	}
	private boolean pathContains(Point contains, Node way) {
		while(way != null)
		{
			if(way.getActual().equals(contains))
				return true;
			way = way.getFrom();
		}
		return false;
	}
	private void calcDistanceMap(Point target) {
		longWayMap = new int[actualField.width()][actualField.height()];
		for (int i = 0; i < actualField.width(); i++)
			for (int j = 0; j < actualField.height(); j++)
			{
				if(actualField.cell(new Point(i,j)).equals(CellType.APPLE))
				{
					longWayMap[i][j] = SPACE;
				}
				else if(actualField.cell(new Point(i,j)).equals(CellType.SPACE))
				{
					longWayMap[i][j] = SPACE;
				}
				else if(actualField.cell(new Point(i,j)).equals(CellType.WALL))
				{
					longWayMap[i][j] = WALL;
				}
				else if(actualField.cell(new Point(i,j)).equals(CellType.SNAKE))
				{
					longWayMap[i][j] = SPACE;
				}
			}
	}
}
