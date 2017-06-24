package Util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import com.sun.javafx.css.CalculatedValue;

import Logic.Field;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;
import Logic.Field.CellType;

public class Pathfinder {
	private int[][] distanceMap;		//Gives the distance from every Position on the field to the selected Target
	private int[][] blockingMap;		//Maps the field to a Map, which only shows what is blocking and what not.
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
	private Stack<Direction> way = new Stack<>();
	
	//Heuristik values
	public static final int SPACE = 0;
	public static final int WALL = 1;
	
	public Pathfinder(Field field)
	{
		actualField = field;
		distanceMap = new int[field.width()][field.height()];
	}
	
	public void calculateMinPath(Snake snake, Point target,Field field) 
	{
		//Clear old calculations
		openList.clear();
		closedList.clear();
		way.clear();
		
		//init
		actualField = field;
		Point snakeTail = snake.segments().get(0);
		calcShortWayMap(target,actualField);
		if(UtilFunctions.getDistance(snake.headPosition(), snakeTail) > 1)
			blockingMap[snakeTail.x][snakeTail.y]=1;
		
		// Calculate A*
		Node start = new Node(null,snake.headPosition(),0,0);
		openList.add(start);
		System.out.println("Start: " + snake.headPosition());
		System.out.println("Target: " + target);
		for(int i=0;i<blockingMap.length;i++)
			System.out.println(Arrays.toString(blockingMap[i]));
		
		//Wenn das Ziel in der ClosedList ist oder die OpenList leer ist, sind wir fertig!
		while (!isInList(false,target) && !openList.isEmpty()) {
			Node min = openList.remove();
			closedList.add(min);
			Point current = min.getActual();
			for (int i = -1; i <= 1; i += 2)
			{
				if (current.x + i < 29 && current.x + i >= 1)
					if(blockingMap[current.x+i][current.y] == 1)
						updateMin(new Point(current.x + i, current.y), min);
				if (current.y + i < 19 && current.y + i >= 1)			
					if(blockingMap[current.x][current.y+i] == 1)
						updateMin(new Point(current.x, current.y + i), min);
			}
		}
		closedList.remove(0);
		Node moveNode = UtilFunctions.getNodeFromClosedList(target,closedList);
		while(moveNode != null && !moveNode.getActual().equals(snake.headPosition()))
		{
			System.out.println("from: " + moveNode.getFrom().getActual() + "  to: " + moveNode.getActual());
			System.out.println(UtilFunctions.getDirection(moveNode.getFrom().getActual(),moveNode.getActual()));
			way.push(UtilFunctions.getDirection(moveNode.getFrom().getActual(),moveNode.getActual()));
			moveNode = moveNode.getFrom();
		}
		if(moveNode!= null && moveNode.getFrom() != null)
			way.push(UtilFunctions.getDirection(moveNode.getFrom().getActual(),moveNode.getActual()));
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
	public int[][] calcShortWayMap(Point target, Field actualField) {
		distanceMap = new int[actualField.width()][actualField.height()];
		blockingMap = new int[actualField.width()][actualField.height()];
		for (int i = 0; i < actualField.width(); i++)
			for (int j = 0; j < actualField.height(); j++)
			{
				distanceMap[i][j] = getDistance(new Point(i,j),target);
				switch(actualField.cell(new Point(i,j)))
				{
				case APPLE:
				case SPACE:
				case PORTAL:
				case FEATUREWALL: blockingMap[i][j] = SPACE; break;
				case SNAKE:
				case WALL: blockingMap[i][j] = WALL; break;
				}
			}
		return blockingMap;
	}
	public List<Node> getClosedList()
	{
		return closedList;
	}
	public Direction getNextDirection()
	{
		return (!way.isEmpty()?way.pop():null);		
	}
	public boolean hasNextDirection()
	{
		return !way.isEmpty();
	}
}
