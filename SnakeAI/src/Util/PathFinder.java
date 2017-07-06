package Util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import Logic.Field;
import Logic.Point;
import Logic.Portals;
import Logic.Snake;
import Logic.Field.CellType;

public class PathFinder {
	private int[][] distanceMap;
	private int[][] blockingMap;
//	private PriorityQueue<Node> openList = new PriorityQueue<>(new Comparator<Node>(){
//		@Override
//		public int compare(Node o1, Node o2) {
//			if(o1.getFCost() > o2.getFCost())
//				return 1;
//			else if(o1.getFCost()< o2.getFCost())
//				return -1;
//			return 0;
//		}		
//	});
	private PriorityQueue<Node> openList = new PriorityQueue<>((Node e1, Node e2) -> (int)(e1.getFCost()-e2.getFCost()));
	private List<Node> closedList = new LinkedList<>();
	private Field actualField;
	private Point portal1;
	private Point portal2;
	
	private Point altPoint1=new Point(15,1);
	private Point altPoint2=new Point(15,18);
	
	//Heuristik values
	public static final int SPACE = 1;
	public static final int WALL = 100;
	
	public Node getMinPath(Snake snake, Point target,Field field, Portals portals) 
	{
		Point startPoint = snake.headPosition();
		Point snakeTail  = snake.segments().get(0);
		portal1 = portals.getPortal1();
		portal2 = portals.getPortal2();
		openList.clear();
		closedList.clear();
		actualField = field;
		Node start = new Node(null,startPoint,0,0);
		calcShortWayMap(target,actualField);
		if(UtilFunctions.getDistance(startPoint, snakeTail) > 1)
			blockingMap[snakeTail.x][snakeTail.y]=1;
		// Calculate A*
		openList.add(start);
//		if(target.equals(altPoint1) || target.equals(altPoint2))
//			System.out.println("openList-Start: " + Arrays.toString(openList.toArray()));
		//Wenn das Ziel in der ClosedList ist oder die OpenList leer ist, sind wir fertig!
		while (!isInList(false,target) && !openList.isEmpty()) {
			Node min = openList.remove();
			closedList.add(min);
			Point current = min.getActual();
//			if(target.equals(altPoint1) || target.equals(altPoint2))
//			{
//				System.out.println("openList: " + Arrays.toString(openList.toArray()));
//				System.out.println("closedList: " + Arrays.toString(closedList.toArray()));
//				System.out.println("current: " + current);
//			}
			if(field.cell(current).equals(CellType.PORTAL))
			{
				if(current.equals(portal1))
				{
					for (int i = -1; i <= 1; i += 2)
					{
						if (portal2.x + i < 29 && portal2.x + i >= 1)
						{
							Point next = new Point(portal2.x + i, portal2.y);
							if(blockingMap[portal2.x+i][portal2.y] == 1)
								if(next.equals(portal2) && portals.getTTL() >=  min.lengthToDest(startPoint))
									updateMin(next, min);
						}
						if (portal2.y + i < 19 && portal2.y + i >= 1)
						{
							Point next = new Point(portal2.x, portal2.y + i);
							if(blockingMap[portal2.x][portal2.y+i] == 1)
								if(next.equals(portal2) && portals.getTTL() >=  min.lengthToDest(startPoint))
									updateMin(next, min);
						}
					}
				}
				else
				{
					for (int i = -1; i <= 1; i += 2)
					{
						if (portal1.x + i < 29 && portal1.x + i >= 1)
						{
							Point next = new Point(portal1.x + i, portal1.y);
							if(blockingMap[portal1.x+i][portal1.y] == 1)
								if(next.equals(portal1) && portals.getTTL() >=  min.lengthToDest(startPoint))
									updateMin(next, min);
						}
						if (portal1.y + i < 19 && portal1.y + i >= 1)
						{
							Point next = new Point(portal1.x, portal1.y + i);
							if(blockingMap[portal1.x][portal1.y+i] == 1)
								if(next.equals(portal1) && portals.getTTL() >=  min.lengthToDest(startPoint))
									updateMin(next, min);
						}
					}
				}
			}
			for (int i = -1; i <= 1; i += 2)
			{
				if (current.x + i < 29 && current.x + i >= 1)
				{
					Point next = new Point(current.x + i, current.y);
					if(blockingMap[current.x+i][current.y] == 1)
						updateMin(next, min);
					if(field.cell(next).equals(CellType.PORTAL))
					{
						if(next.equals(portal1) && portals.getTTL() >=  min.lengthToDest(startPoint))
							updateMin(portal1, min);
						if(next.equals(portal2) && portals.getTTL() >=  min.lengthToDest(startPoint))
							updateMin(portal2, min);						
					}
				}
				if (current.y + i < 19 && current.y + i >= 1)
				{
					Point next = new Point(current.x, current.y + i);
					if(blockingMap[current.x][current.y+i] == 1)
						updateMin(next, min);
				}
			}
		}
//		if(target.equals(altPoint1) || target.equals(altPoint2))
//		{
//			System.out.println("openList: " + Arrays.toString(openList.toArray()));
//			System.out.println("closedList: " + Arrays.toString(closedList.toArray()));
//		}

		closedList.remove(0);
		return UtilFunctions.getMovePair(target,closedList);
	}
	private void updateMin(Point check, Node node) {
		//Ist der Neue Punkt bereits in der ClosedList?
		if(isInList(false,check))
			return;
		
		//Berechne die GCosts fuer den neuen Punkt
		int costs = node.getGCosts() + blockingMap[check.x][check.y];
		
		//Gibt es bereits einen besseren Weg zu dem neuen Punkt?
		if (isInList(true,check) && costs >= getElement(check).getGCosts())
			return;
		
		//Erstelle den neuen Node
		Node checkNode = new Node(node,check,distanceMap[check.x][check.y],costs);
		//Falls es einen schlechteren Node gab, loesche diesen
		if(isInList(true,check))
			openList.remove(getElement(check));

		//Fuege den neuen/besseren Node der OpenList hinzu
		openList.add(checkNode);
	}
	private boolean isInList(boolean open,Point target) {
		for (Node closed : (open?openList:closedList))
		{
			if (closed.getActual().equals(target))
				return true;
		}
		return false;
	}

	private Node getElement(Point p) {
		for (Node open : openList)
		{
			if (open.getActual().equals(p))
				return open;
		}
		return null;
	}

	private int getDistance(Point a, Point b) {
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}
	
	public void calcShortWayMap(Point target, Field actualField) {
		distanceMap = new int[actualField.width()][actualField.height()];
		blockingMap = new int[actualField.width()][actualField.height()];
		for (int i = 0; i < actualField.width(); i++)
			for (int j = 0; j < actualField.height(); j++)
			{
				distanceMap[i][j] = getDistance(new Point(i,j),target);
				switch(actualField.cell(new Point(i,j)))
				{
				case PORTAL:
				case APPLE:
				case SPACE:
				case CHANGESNAKE:
				case CHANGEHEADTAIL:
				case FEATUREWALL: blockingMap[i][j] = SPACE; break;
				case SNAKE:
				case WALL: blockingMap[i][j] = WALL; break;
				}
			}
	}
	public List<Node> getClosedList()
	{
		return closedList;
	}
}
