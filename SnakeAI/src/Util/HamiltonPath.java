package Util;


import Logic.Field;
import Logic.Point;
import Logic.Portals;
import Logic.Snake.Direction;

public class HamiltonPath {
	private PathFinder finder;
	private Field actualField;
	private int[][] longWayMap;
	public static int SPACE = 1;
	public static int WALL = 100;
	
	public Node getCompleteMaxPath(Field f)
	{
		finder = new PathFinder();
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
		int i=0;
		while(tempWay != null && tempWay.getFrom() != null && i <= 4)
		{
			Point from = tempWay.getFrom().getActual();
			Point to = tempWay.getActual();
			Direction dir = UtilFunctions.getDirection(from,to);
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
			if(!changed)
			{
				longWayMap[tempWay.getActual().x][tempWay.getActual().y]=100;
				tempWay = tempWay.getFrom();
			}
//			i++;
		}
		return way;
	}
	public Node getMaxPath(Point startPoint, Field field, TempSnake snake, TempSnake enemySnake, Portals portals) {
		actualField = field;
		Node way = null;
		Field tmpField = UtilFunctions.getFieldCopy(field);
		for(Point p : snake.segments())
		{
			calcDistanceMap(p);
			for(Point p2 : enemySnake.segments())
			{	
				longWayMap[p2.x][p2.y] = 100;
			}
			for(Point p2 : snake.segments())
			{	
				longWayMap[p2.x][p2.y] = 100;
			}

			if(finder == null)
				finder = new PathFinder();
			finder.ignorePortals = true;
			finder.getMinPathWithTail(snake, p, tmpField, portals, p);
//			finder.getMinPath(snake, p, tmpField, portals);
			way = UtilFunctions.getMovePair(p,finder.getClosedList());
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
		int i=0;
		while(tempWay != null && tempWay.getFrom() != null && i <= 4)
		{
			Point from = tempWay.getFrom().getActual();
			Point to = tempWay.getActual();
			Direction dir = UtilFunctions.getDirection(from,to);
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
			if(!changed)
			{
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
	public void calcDistanceMap(Point target) {
		longWayMap = new int[actualField.width()][actualField.height()];
		for (int i = 0; i < actualField.width(); i++)
			for (int j = 0; j < actualField.height(); j++)
			{
				switch(actualField.cell(new Point(i,j)))
				{
				case PORTAL:
				case APPLE:
				case SPACE:
				case CHANGESNAKE:
				case CHANGEHEADTAIL:
				case SPEEDUP:
				case SNAKE:
				case FEATUREWALL: longWayMap[i][j] = SPACE; break;
				case WALL: longWayMap[i][j] = WALL; break;
				}
			}
	}
}
