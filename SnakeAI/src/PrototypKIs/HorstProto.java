package PrototypKIs;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Random;

import Logic.Field;
import Logic.Field.CellType;
import Logic.GameInfo;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;
import Util.AlphaBeta;
import Util.HamiltonPath;
import Util.Node;
import Util.Pathfinding;
import Util.TempSnake;
import Util.UtilFunctions;
import Logic.SnakeBrain;

//HorstAI
//Created by: Julia Hofmann, Marco Piechotta

public class HorstProto implements SnakeBrain {
	
	//Wird vielleicht noch gebraucht um Features einheitlich abzugreifen (Jedes Feature implementiert ein Interface und wir k�nnen Objekte dieses Interfaces verwenden f�r die
	// "eatable Objects"
//	private PriorityQueue<ApplePos> apples = new PriorityQueue<>(new Comparator<ApplePos>(){
//
//		@Override
//		public int compare(ApplePos o1, ApplePos o2) {
//			if(o1.getDistance() > o2.getDistance())
//				return 1;
//			else if(o1.getDistance()< o2.getDistance())
//				return -1;
//			return 0;
//		}
//		
//	});
	private Node target;
	private Snake mySnake;
	private Snake enemySnake;
	private Direction last = null;
	private boolean playSave= false;
	private Field tempField;
	
	//Eatable Stuff
	private Point apple;
	private Point wallItem;
	
	//A* 
	private Pathfinding finder;
	
	//HamiltonPath
	private HamiltonPath hFinder;
	private HashMap<Point, Direction> hPath;
	private HashMap<Point, Integer> hPointToIndex;
	
	//AlphaBeta
	private AlphaBeta alphaBeta;
	
	//Konstruktor f�r genetischen Algorithmus um f�r evalSituation den besten Array zu ermitteln um Situationen zu bewerten
//	public HorstAI(int[] evalStuff)
//	{
//		alphaBeta = new AlphaBeta(evalStuff);
//	}
	@Override
	public Direction nextDirection(GameInfo gameInfo, Snake snake) {
		
		//Initialisiere alle n�tigen Variablen, falls diese noch nicht initialisiert wurden
		init(gameInfo,snake);
		
		Direction move = null;
		
		//Berechne AlphaBeta-Pruning f�r die aktuelle Position
		alphaBeta.alphaBeta(gameInfo.field(), mySnake, enemySnake,4);

		//K�nnen wir direkt gewinnen? -> folge diesem "Gewinnpfad"
		if(alphaBeta.directionScores.get(alphaBeta.bestMove) != null && alphaBeta.bestScore > 1000)
		{
			return alphaBeta.bestMove;
		}	
		
		boolean castle = true;	//Castle = wir haben uns eingeschlossen!
		playSave = true;		//PlaySave = wir haben zur Zeit kein Ziel
		
		//Zunaechst schaue ob der Apfel erreichbar ist
		if(apple != null)
		{
			target = new Node(null,apple,0,0);
			
			//Bin ich naeher am Apfel -> gehe dort hin
			if(UtilFunctions.getDistance(mySnake.headPosition(),apple) <= 
					UtilFunctions.getDistance(enemySnake.headPosition(),apple))
			{		
				
				int[][] shortWayMap = finder.calcShortWayMap(target.getActual(),gameInfo.field());
				Point snakeHead = snake.headPosition();
				Point snakeTail = snake.segments().get(0);
				for (int i = -1; i <= 1; i += 2) {
					if (snakeHead.x + i < 29 && snakeHead.x + i >= 1)
					{
						Point nextPos = new Point(snakeHead.x + i, snakeHead.y);
						int headIndex = hPointToIndex.get(snakeHead);
						int nextIndex = hPointToIndex.get(nextPos);
						int tailIndex = hPointToIndex.get(snakeTail);
						if(tailIndex < nextIndex && nextIndex < headIndex && tailIndex > headIndex)
							shortWayMap[nextPos.x][nextPos.y]=100;
						Direction dir = UtilFunctions.getDirection(snakeHead, nextPos);
						if(alphaBeta.directionScores.containsKey(dir))
							if(alphaBeta.directionScores.get(dir)  < 0)
							{
								shortWayMap[nextPos.x][nextPos.y]=100;
							}
					}
					if (snakeHead.y + i < 19 && snakeHead.y + i >= 1)
					{					
						Point nextPos = new Point(snakeHead.x, snakeHead.y + i);
						int headIndex = hPointToIndex.get(snakeHead);
						int nextIndex = hPointToIndex.get(nextPos);
						int tailIndex = hPointToIndex.get(snakeTail);
						if(tailIndex < nextIndex && nextIndex < headIndex && tailIndex > headIndex)
							shortWayMap[nextPos.x][nextPos.y]=100;
						Direction dir = UtilFunctions.getDirection(snakeHead, nextPos);
						if(alphaBeta.directionScores.containsKey(dir))
							if(alphaBeta.directionScores.get(dir)  < 0)
							{
								shortWayMap[nextPos.x][nextPos.y]=100;
							}
					}
				}
				//Berechne kuerzesten Weg zum Ziel
				Node path = finder.getMinPath(snake.headPosition(), target.getActual(),gameInfo.field(),snake.segments().get(0));
				
				//Gibt es keinen Pfad dorthin?
				if(path != null)
				{	
					//Wir haben einen Pfad
					while(path.getFrom() != null && !path.getFrom().getActual().equals(snake.headPosition()))
						path = path.getFrom();	
					
					move = UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());
					
					//Ist der gew�hlte Pfad schlecht?
//					if(alphaBeta.directionScores.get(move) != null && alphaBeta.directionScores.get(move) <= -9000)
//					{
////					System.out.println("Death!!");
//						playSave = true;
//						move = null;
//					}
					
					playSave = false;
					castle = false;
				}
				else
				{
					boolean appleInside = false;
//					for(Point p : snake.segments())
//						if(p.equals(apple))
//						{
//							appleInside = true;
//							break;
//						}
//					System.out.println("###continue because: No Path");
					if(!appleInside)
					{
//						System.out.println("-> Castle");
						castle = true;
					}
					else
					{
//						System.out.println("-> Play Save");
						castle = false;
						playSave = true;
					}
				}
			}
			else
			{
//				//Gegner ist näher am Apfel
//				if(!snake.getCanSetWall())
//				{
//					System.out.println("Cant Set Wall");
//					target = new Node(null,wallItem,0,0);
//					//Berechne kuerzesten Weg zum Ziel
//					Node path = finder.getMinPath(snake.headPosition(), target.getActual(),gameInfo.field(),snake.segments().get(0));
//					
//					//Gibt es keinen Pfad dorthin, suche einen neues Ziel
//					if(path != null)
//					{
//						//Wir haben einen Pfad
//						while(path.getFrom() != null && !path.getFrom().getActual().equals(snake.headPosition()))
//							path = path.getFrom();	
//						
//						move = UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());
//						playSave = false;
//						castle = false;
//					}
//				}
				//Berechne kuerzesten Weg zum Ziel
				Node path = finder.getMinPath(snake.headPosition(), target.getActual(),gameInfo.field(),snake.segments().get(0));
				
				//Gibt es keinen Pfad dorthin, suche einen neues Ziel
				if(path != null)
				{
					playSave=true;
					castle = false;
				}
				else
				{
					playSave=false;
					castle = true;
				}
			}	
		}
//		else if(snake.getCanSetWall())
//		{
//			System.out.println("Can Set Wall");
//			snake.setWall(new Point(15,10), Direction.LEFT);
//		}
		else
		{
		//Gibt es keine Aepfel mehr, dann spiele auf Zeit
			playSave=true;
			castle = false;
		}	
		if(castle)
		{
			Field tempField = Field.defaultField(gameInfo.field().width(), gameInfo.field().height());
			for(Point snakePoint : mySnake.segments())
			{
				if(!snakePoint.equals(mySnake.headPosition()) && !snakePoint.equals(mySnake.segments().get(0)))
					tempField.setCell(CellType.WALL, snakePoint);
			}
			for(Point snakePoint : enemySnake.segments())
			{
				tempField.setCell(CellType.WALL, snakePoint);
			}
			Node path = hFinder.getMaxPath(snake.headPosition(), tempField, new TempSnake(snake), new TempSnake(enemySnake));
			if(path != null)
			{
				while(path != null && path.getFrom() != null && !path.getFrom().getActual().equals(snake.headPosition()))
					path = path.getFrom();
				move = UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());
			}
		}
		if(playSave)
		{
			Point snakeHead = snake.headPosition();
			Point snakeTail = snake.segments().get(0);
			List<Direction> dirs = new LinkedList<>();
			for (int i = -1; i <= 1; i += 2) {
				boolean addX = true;
				boolean addY = true;
				Point nextPosX = null;
				Point nextPosY = null;
				if (snakeHead.x + i < 29 && snakeHead.x + i >= 1)
				{
					nextPosX = new Point(snakeHead.x + i, snakeHead.y);
					if(isMoveValid(UtilFunctions.getDirection(snakeHead, nextPosX), snake, gameInfo))
					{
						int headIndex = hPointToIndex.get(snakeHead);
						int nextIndex = hPointToIndex.get(nextPosX);
						int tailIndex = hPointToIndex.get(snakeTail);
//						System.out.println("noPath: "+tailIndex + "---" +nextIndex + "---"+headIndex);
						if(tailIndex > nextIndex && nextIndex > headIndex)
							addX = false;
					}
					else
					{
						addX = false;
					}
				}
				else
					addX = false;
				if (snakeHead.y + i < 19 && snakeHead.y + i >= 1)
				{		
					nextPosY = new Point(snakeHead.x, snakeHead.y + i);
					if(isMoveValid(UtilFunctions.getDirection(snakeHead, nextPosY), snake, gameInfo))
					{
						int headIndex = hPointToIndex.get(snakeHead);
						int nextIndex = hPointToIndex.get(nextPosY);
						int tailIndex = hPointToIndex.get(snakeTail);
						if(tailIndex > nextIndex && nextIndex > headIndex)
							addY = false;
					}
					else
					{
						addY = false;
					}
				}
				else
					addY = false;
				
				if(addX && nextPosX != null)
					dirs.add(UtilFunctions.getDirection(snakeHead, nextPosX));
				if(addY && nextPosY != null)
					dirs.add(UtilFunctions.getDirection(snakeHead, nextPosY));
			}
			if(!dirs.isEmpty())
				move = dirs.get(0);
//			int best = Integer.MIN_VALUE;
//			for(Entry<Direction,Integer> entry : alphaBeta.directionScores.entrySet())
//			{
//				System.out.println(entry.getKey() + " -> " + entry.getValue());
//				if(entry.getValue() > best && isMoveValid(entry.getKey(), snake, gameInfo))
//				{
//					best = entry.getValue();
//					move = entry.getKey();
//				}
//			}
//			System.out.println("Best: " + move+"\n--------------------------");
//			for(int i=0;i<snake.segments().size();i++)
//			{
//				hamiltonPath = hFinder.getMaxPath(snake.headPosition(), mySnake.segments().get(i),gameInfo.field(),snake);
//				if(hamiltonPath != null)
//					break;
//			}			
//			if(hamiltonPath != null)
//			{
//				System.out.println("SnakeHead: " + snake.headPosition());
//				System.out.println(hamiltonPath.getActual());
//				move = UtilFunctions.getDirection(snake.headPosition(),hamiltonPath.getActual());
//				System.out.println("Move: " + move);
//				hamiltonPath = hamiltonPath.getFrom();
//				while(hamiltonPath.getFrom() != null && !hamiltonPath.getFrom().getActual().equals(snake.headPosition()))
//					hamiltonPath = hamiltonPath.getFrom();				
//			}
//			stallPoint = mySnake.segments().get(0);
//			//Laufe im Kreis!
//			System.out.println("StallPoint: " + stallPoint);
//			Node path = finder.getMaxPath(start, stallPoint);
//			System.out.println(path);
//			while(path.getFrom() != null && !path.getFrom().getActual().equals(start.getActual()))
//			{
//				System.out.println(path.getFrom().getActual()+ " -> "+path.getActual() + ": Costs" + path.getFCost());
//				path = path.getFrom();
//			}
//			move = getDirection(snake.headPosition(),path.getActual());
		}
		if(move == null)
		{
			return randomMove(gameInfo, snake);
		}
		return move;
	}
	private void init(GameInfo info, Snake snake)
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
		if(finder == null)
			finder = new Pathfinding(info.field());
		if(hFinder == null)
			hFinder = new HamiltonPath(info.field());
		if(alphaBeta == null)
			alphaBeta = new AlphaBeta();
				
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
	
		//Find all eatable Stuff
		getItems(info.field(),snake.headPosition());
	}
	private void getItems(Field f, Point snakeHead) {
		for(int x=0;x<f.width();x++)
			for(int y=0;y<f.height();y++)
			{
				Point p = new Point(x,y);
				if(f.cell(p).equals(CellType.APPLE))
					apple = p;
				if(f.cell(p).equals(CellType.FEATUREWALL))
					wallItem = p;
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

