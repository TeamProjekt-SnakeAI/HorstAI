package PrototypKIs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import Logic.Field;
import Logic.GameInfo;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;
import Util.AlphaBeta;
import Util.HamiltonPath;
import Util.Node;
import Util.PathFinder;
import Util.Pathfinding;
import Util.UtilFunctions;
import Logic.SnakeBrain;
import Logic.Field.CellType;

public class BrainMaster implements SnakeBrain{

	private Snake mySnake;
	private Snake enemySnake;
	private Direction last = null;
	private Field tempField;
	private boolean firstRound=true;
	
	//Eatable Stuff
	//0 = apple , 1 = wallItem , 2 = changeSnake , 3 = changeHeadTail , 4 = Portal
	private Point[] eatable = new Point[5];
	
	//A* 
	private PathFinder finder;
	
	//HamiltonPath
	private HamiltonPath hFinder;
	private HashMap<Point, Direction> hPath;
	private HashMap<Point, Integer> hPointToIndex;
	
	//AlphaBeta
	private AlphaBeta alphaBeta;
	
	@Override
	public Direction nextDirection(GameInfo gameInfo, Snake snake) {
//		System.out.println("init!!!");
		//Initialisiere alle nötigen Variablen, falls diese noch nicht initialisiert wurden
		init(gameInfo,snake);
		if(firstRound)
		{
			firstRound = false;
			return Direction.RIGHT;
		}
//		System.out.println("init done");
		//Gibt es das Schlangentausch Feature und unsere Schlange ist min. 9 lang?
		if(eatable[2] != null && snake.segments().size() >= 9)
		{
			//Auf zum Sieg! Einrollen um den Schlangentausch
			System.out.println("SIEG!");
			if(UtilFunctions.getDistance(eatable[2], snake.headPosition()) == 1)
			{
				//Okay einkreisen von hier!
				Direction dir = UtilFunctions.getDirection(snake.headPosition(), eatable[2]);
				System.out.println("Distance Low: "+ dir);
				switch(dir)
				{
				case UP:
				case DOWN:
					if(isMoveValid(Direction.LEFT, snake, gameInfo))
						return Direction.LEFT;
					if(isMoveValid(Direction.RIGHT, snake, gameInfo))
						return Direction.RIGHT;
					return dir;
				case LEFT:
				case RIGHT:
					if(isMoveValid(Direction.UP, snake, gameInfo))
						return Direction.UP;
					if(isMoveValid(Direction.DOWN, snake, gameInfo))
						return Direction.DOWN;
					return dir;
				}
			}
			if(UtilFunctions.getDistance(eatable[2], snake.headPosition()) == 2)
			{
				//Okay einkreisen von hier!
				Direction dir = null;
				for (int i = -1; i <= 1; i += 2)
				{
					Point toCheck = new Point(snake.headPosition().x+i,snake.headPosition().y);
					if(UtilFunctions.getDistance(toCheck, eatable[2])==1)
					{
						dir = UtilFunctions.getDirection(snake.headPosition(), toCheck);
						if(isMoveValid(dir, snake, gameInfo))
							return dir;
					}
					toCheck = new Point(snake.headPosition().x,snake.headPosition().y+i);
					if(UtilFunctions.getDistance(toCheck, eatable[2])==1)
					{
						dir = UtilFunctions.getDirection(snake.headPosition(), toCheck);
						if(isMoveValid(dir, snake, gameInfo))
							return dir;
					}
				}
				
				System.out.println("Distance Low: "+ dir);
				switch(dir)
				{
				case UP:
				case DOWN:
					if(isMoveValid(Direction.LEFT, snake, gameInfo))
						return Direction.LEFT;
					if(isMoveValid(Direction.RIGHT, snake, gameInfo))
						return Direction.RIGHT;
					return dir;
				case LEFT:
				case RIGHT:
					if(isMoveValid(Direction.UP, snake, gameInfo))
						return Direction.UP;
					if(isMoveValid(Direction.DOWN, snake, gameInfo))
						return Direction.DOWN;
					return dir;
				}
			}
			//Berechne kuerzesten Weg zum Ziel
			Point targetPoint = null;
			foundTarget:for(int i=-1;i<2;i+=2)
			{
				for(int j=-1;j<2;j+=2)
				{
					targetPoint = new Point(eatable[2].x+i,eatable[2].y+i);
					if(gameInfo.field().cell(targetPoint).equals(CellType.SPACE))
						break foundTarget;
				}
			}
			if(targetPoint == null)
				targetPoint = eatable[2];
			Node path = finder.getMinPath(snake, targetPoint,gameInfo.field());
			//Gibt es keinen Pfad dorthin?
			if(path != null)
			{	
				//Wir haben einen Pfad
				while(path.getFrom() != null && !path.getFrom().getActual().equals(snake.headPosition()))
					path = path.getFrom();	
				
				return UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());				
			}
			
		}
		System.out.println("hier1");
		if(UtilFunctions.getDistance(mySnake.headPosition(),eatable[0]) <= 
				UtilFunctions.getDistance(enemySnake.headPosition(),eatable[0]))
		{
			//Wir sind näher am Apfel!
			//Berechne kuerzesten Weg zum Ziel
			Node path = finder.getMinPath(snake, eatable[0],gameInfo.field());
			System.out.println(path.getPath());
			System.out.println("hie2r"+ snake.headPosition());
			//Gibt es keinen Pfad dorthin?
			if(path != null)
			{	
				//Wir haben einen Pfad
				while(path.getFrom() != null && !path.getFrom().getActual().equals(snake.headPosition()))
					path = path.getFrom();	
				
				System.out.println("Direction: "+UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual()));
				return UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());				
			}
		}
		else
		{
			//Mist! Der Gegner ist näher am Apfel. Können wir die Schlangen tauschen? bevor er beim Apfel ist?
			if(eatable[2] != null)
			{
				if(UtilFunctions.getDistance(enemySnake.headPosition(),eatable[0]) > UtilFunctions.getDistance(mySnake.headPosition(),eatable[2]))
				{
					//Jap! Dann lass uns da hin gehen
					//Berechne kuerzesten Weg zum Ziel
					Node path = finder.getMinPath(snake, eatable[2],gameInfo.field());
					System.out.println("hier3");
					//Gibt es keinen Pfad dorthin?
					if(path != null)
					{	
						System.out.println(path.getPath());
						//Wir haben einen Pfad
						while(path.getFrom() != null && !path.getFrom().getActual().equals(snake.headPosition()))
							path = path.getFrom();	
						
						return UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());				
					}
				}
			}
		}
		//Wenn wir bis jetzt noch keinen Weg gefunden haben, sollten wir auf Zeit spielen
		if(eatable[1] != null)
		{
			Node path = finder.getMinPath(snake, eatable[2],gameInfo.field());
			System.out.println("hier4");
			//Gibt es keinen Pfad dorthin?
			if(path != null)
			{	
				//Wir haben einen Pfad
				while(path.getFrom() != null && !path.getFrom().getActual().equals(snake.headPosition()))
					path = path.getFrom();	
				
				return UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());				
			}
		}
		System.out.println("dann hier");
		Direction move = hPath.get(snake.headPosition());
		if(move!= null && isMoveValid(move, snake, gameInfo))
			return move;
		System.out.println("hier");
		return randomMove(gameInfo, snake);
	}
	private void init(GameInfo info, Snake snake)
	{
//		System.out.println("init1");
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
		if(finder == null)
			finder = new PathFinder();
		if(hFinder == null)
			hFinder = new HamiltonPath(info.field());
		if(alphaBeta == null)
			alphaBeta = new AlphaBeta();
				
//		System.out.println("init2");
		
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
		if(hPointToIndex == null || hPath == null)
		{
			Node hamiltonPath = hFinder.getCompleteMaxPath(Field.defaultField(tempField.width(), tempField.height()));
//			System.out.println(hamiltonPath.getPath());
			if(hamiltonPath != null)
			{
				hPointToIndex = new HashMap<>();
				hPath = new HashMap<>();
				int index = 0;
				Point first = hamiltonPath.getActual();
				Point last = null;
				while(hamiltonPath != null && hamiltonPath.getFrom() != null)
				{
					hPointToIndex.put(hamiltonPath.getActual(), index);
					hPath.put(hamiltonPath.getActual(), UtilFunctions.getDirection(hamiltonPath.getFrom().getActual(), hamiltonPath.getActual()));
					hamiltonPath = hamiltonPath.getFrom();
					if(hamiltonPath.getFrom() == null)
						last = hamiltonPath.getActual();
					index++;
				}
				if(last != null)
				{
					hPointToIndex.put(last, index);
					hPath.put(last, UtilFunctions.getDirection(first, last));
				}
			}
		}
//		System.out.println("init3");
	
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
					eatable[0] = p;
				if(f.cell(p).equals(CellType.FEATUREWALL))
					eatable[1] = p;
				if(f.cell(p).equals(CellType.CHANGESNAKE))
					eatable[2] = p;
				if(f.cell(p).equals(CellType.CHANGEHEADTAIL))
					eatable[3] = p;
				if(f.cell(p).equals(CellType.PORTAL))
					eatable[4] = p;
			}

	}

	//Calculate Valid Moves
	public static boolean isMoveValid(Direction d, Snake snake, GameInfo gameInfo) {
		Point newHead = new Point(snake.headPosition().x, snake.headPosition().y);
		switch(d) {
		case DOWN:
			newHead.y++;
			break;
		case LEFT:
			newHead.x--;
			break;
		case RIGHT:
			newHead.x++;
			break;
		case UP:
			newHead.y--;
			break;
		default:
			break;
		}
		if (newHead.x == -1) {
			newHead.x = gameInfo.field().width()-1;
		}
		if (newHead.x == gameInfo.field().width()) {
			newHead.x = 0;
		}
		if (newHead.y == -1) {
			newHead.y = gameInfo.field().height()-1;
		}
		if (newHead.y == gameInfo.field().height()) {
			newHead.y = 0;
		}
		
		return gameInfo.field().cell(newHead) == CellType.SPACE || gameInfo.field().cell(newHead) == CellType.APPLE;
	}
	
	public static boolean isValidMovePossible(Snake snake, GameInfo gameInfo) {
		return isMoveValid(Direction.DOWN, snake, gameInfo) || isMoveValid(Direction.UP, snake, gameInfo) || isMoveValid(Direction.LEFT, snake, gameInfo) || isMoveValid(Direction.RIGHT, snake, gameInfo);
	}
	public Direction randomMove(GameInfo gameInfo, Snake snake) {
		Random rand = new Random();
		Direction d;
		if (rand.nextDouble() < 0.95 && last != null && isMoveValid(last, snake, gameInfo)) {
			d = last;
		} else {
			do {
				d = Direction.values()[rand.nextInt(4)];
			} while(!isMoveValid(d, snake, gameInfo) && isValidMovePossible(snake, gameInfo));
		}
		
		last = d;
		
		return d;
	}
}
