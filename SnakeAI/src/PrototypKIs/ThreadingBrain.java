package PrototypKIs;

import java.util.HashMap;
import java.util.Stack;

import Logic.Field;
import Logic.GameInfo;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;
import Util.AlphaBeta;
import Util.HamiltonPath;
import Util.Node;
import Util.PathFinder;
import Util.UtilFunctions;
import Logic.SnakeBrain;
import Logic.Field.CellType;

public class ThreadingBrain implements SnakeBrain {
	//Konstanten
		private final int CHANGE_DISTANCE = 1;
		private final int MIN_CUT_LENGTH = 15;
		private final int DESIRED_SNAKE_LENGTH = 9;

		private Snake mySnake;
		private Snake enemySnake;
		private Direction last = null;				//letzter Ausweg: RandomBrain-Move
		private Direction moveDirection = null;
		private Field tempField;
		private boolean firstRound=true;
		private boolean passedPortal = false;
		
		//Eatable Stuff
		//0 = apple , 1 = wallItem , 2 = changeSnake , 3 = changeHeadTail , 4 = Portal
		private enum Items {
			APPLE(0), WALLITEM(1), CHANGESNAKE(2), CHANGEHEADTAIL(3), PORTAL(4);
			private final int value;		
			private Items(int value) {
				this.value = value;
			}
			public int getIndex()
			{
				return value;
			}
		}
		private Point[] eatable = new Point[5];
		private Point[] altTargets;
		private int currentAltTarget;
		private GameInfo info;
		private Point wallPlacedTarget = null;
		
		//MaxPathFinder Alg.: HamiltonPath
		private HamiltonPath maxPathFinder;
		private HashMap<Point, Direction> completeMaxPath;
		private Stack<Direction> maxPath = new Stack<>();
		

	@Override
	public Direction nextDirection(GameInfo gameInfo, Snake snake) {
		
		
		return null;
	}
	private void init(Snake snake)
	{
		tempField = new Field(info.field().width(),info.field().height());
		for(int x=0;x<tempField.width();x++)
		{
			for(int y=0;y<tempField.height();y++)
			{
				Point p = new Point(x,y);
				tempField.setCell(info.field().cell(p), p);
			}
		}
		//A* berechnen
		//snake.move(direction)
		for(Point p : snake.segments())
			tempField.setCell(CellType.SNAKE, p);
		
		//initialisieren
		if(maxPathFinder == null)
			maxPathFinder = new HamiltonPath();
		if(altTargets == null)
		{
			altTargets = new Point[4];
			altTargets[0] = new Point(info.field().width()/2,1);
			altTargets[1] = new Point(info.field().width()/2,info.field().height()-2);
			altTargets[2] = new Point(1,info.field().height()/2);
			altTargets[3] = new Point(info.field().width()-2,info.field().height()/2);
			currentAltTarget = 0;
		}
					
		if(firstRound)
			return;
		//Initialize mySnake and EnemySnake
		if(mySnake == null || enemySnake == null)
		{
			mySnake = snake;
			for(Snake s : info.snakes())
			{
				if(!s.equals(snake))
				{
					enemySnake = s;
					break;
				}
			}
		}
		
		//Berechne HamiltonPath ueber das gesamte Feld
		if(completeMaxPath == null)
		{
			Node hamiltonPath = maxPathFinder.getCompleteMaxPath(Field.defaultField(tempField.width(), tempField.height()));
			if(hamiltonPath != null)
			{
				completeMaxPath = new HashMap<>();
				Point first = hamiltonPath.getActual();
				Point last = null;
				while(hamiltonPath != null && hamiltonPath.getFrom() != null)
				{
					completeMaxPath.put(hamiltonPath.getActual(), UtilFunctions.getDirection(hamiltonPath.getFrom().getActual(), hamiltonPath.getActual()));
					hamiltonPath = hamiltonPath.getFrom();
					if(hamiltonPath.getFrom() == null)
						last = hamiltonPath.getActual();
				}
				if(last != null)
				{
					completeMaxPath.put(last, UtilFunctions.getDirection(first, last));
				}
			}
		}
	
		//Find all eatable Stuff
		getItems(info.field(),snake.headPosition());
	}
	private void getItems(Field f, Point snakeHead) {
		eatable = new Point[5];
		for(int x=0;x<f.width();x++)
			for(int y=0;y<f.height();y++)
			{
				Point p = new Point(x,y);
				if(f.cell(p).equals(CellType.APPLE))
					eatable[Items.APPLE.getIndex()] = p;
				if(f.cell(p).equals(CellType.FEATUREWALL))
					eatable[Items.WALLITEM.getIndex()] = p;
				if(f.cell(p).equals(CellType.CHANGESNAKE))
					eatable[Items.CHANGESNAKE.getIndex()] = p;
				if(f.cell(p).equals(CellType.CHANGEHEADTAIL))
					eatable[Items.CHANGEHEADTAIL.getIndex()] = p;
				if(f.cell(p).equals(CellType.PORTAL))
					eatable[Items.PORTAL.getIndex()] = p;
			}

	}
}
