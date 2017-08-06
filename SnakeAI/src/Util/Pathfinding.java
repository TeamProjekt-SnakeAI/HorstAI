package Util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import com.sun.javafx.css.CalculatedValue;

import Logic.Field;
import Logic.Point;
import Logic.Field.CellType;

public class Pathfinding {
	private int[][] distanceMap;
	private int[][] shortWayMap;
	private PriorityQueue<Node> openList = new PriorityQueue<>(new Comparator<Node>(){

		@Override
		public int compare(Node o1, Node o2) {
			if(o1.getFCost() > o2.getFCost())
				return 1;
			else if(o1.getFCost()< o2.getFCost())
				return -1;
			return 0;
		}
		
	});
	private List<Node> closedList = new LinkedList<>();
	private Field actualField;
	
	//Heuristik values
	public static int SPACE = 1;
	public static int WALL = 9;
	public boolean STALLING = false;
	
	public Pathfinding()
	{
		
	}
	public Pathfinding(Field field)
	{
		actualField = field;
		distanceMap = new int[field.width()][field.height()];
	}
	public Node getMinPath(Point startPoint, Point target,Field field, Point snakeTail) 
	{
		openList.clear();
		closedList.clear();
		actualField = field;
		Node start = new Node(null,startPoint,0,0);
		if(shortWayMap == null)
			calcShortWayMap(target,actualField);
		
		if(UtilFunctions.getDistance(startPoint, snakeTail) > 1)
			shortWayMap[snakeTail.x][snakeTail.y]=1;
		// Calculate A*
		openList.add(start);
		
		//Wenn das Ziel in der ClosedList ist oder die OpenList leer ist, sind wir fertig!
		while (!isInList(false,target) && !openList.isEmpty()) {
			Node min = openList.remove();
			closedList.add(min);
			Point current = min.getActual();
			for (int i = -1; i <= 1; i += 2)
			{
				if (current.x + i < 29 && current.x + i >= 1)
					if(shortWayMap[current.x+i][current.y] == 1)
						updateMin(new Point(current.x + i, current.y), min);
				if (current.y + i < 19 && current.y + i >= 1)			
					if(shortWayMap[current.x][current.y+i] == 1)
						updateMin(new Point(current.x, current.y + i), min);
			}
		}
		
		closedList.remove(0);
		return UtilFunctions.getMovePair(target,closedList);
	}
	private void updateMin(Point check, Node node) {
		//Ist der Neue Punkt bereits in der ClosedList?
		if(isInList(false,check))
			return;
		
		//Berechne die GCosts fuer den neuen Punkt
		int costs = node.getGCosts() + shortWayMap[check.x][check.y];
		
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
	public int[][] calcShortWayMap(Point target, Field actualField) {
		distanceMap = new int[actualField.width()][actualField.height()];
		shortWayMap = new int[actualField.width()][actualField.height()];
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
				case SPEEDUP:
				case FEATUREWALL: shortWayMap[i][j] = SPACE; break;
				case SNAKE:
				case WALL: shortWayMap[i][j] = WALL; break;
				}
			}
		return shortWayMap;
	}
	public List<Node> getClosedList()
	{
		return closedList;
	}
}
