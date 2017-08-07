package PrototypKIs;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;

import Logic.Field;
import Logic.Field.CellType;
import Logic.GameInfo;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;
import Logic.SnakeBrain;
import Util.AlphaBeta;
import Util.HamiltonPath;
import Util.Node;
import Util.PathFinder;
import Util.TempSnake;
import Util.UtilFunctions;

public class BrainMaster implements SnakeBrain{
	
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
	
	//MinPathFinder Alg.: A*-Algorithm 
	private PathFinder minPathFinder;
	
	//MaxPathFinder Alg.: HamiltonPath
	private HamiltonPath maxPathFinder;
	private HashMap<Point, Direction> completeMaxPath;
	private Stack<Direction> maxPath = new Stack<>();
	
	//AlphaBeta
	private AlphaBeta alphaBeta;
	
	@Override
	public Direction nextDirection(GameInfo gameInfo, Snake snake) {
//		System.out.println("------new round-----");
		info = gameInfo;
		init(snake);
		
		
		//TODO: Loeschen wenn Threading da ist
		if(firstRound)
		{
			firstRound = false;
			return Direction.RIGHT;
		}
		minPathFinder.clearBadPositions();
		altTargets[0] = enemySnake.headPosition();
		//Verschiebe das alternativ Target, wenn nötig
		if(UtilFunctions.getDistance(snake.headPosition(), altTargets[currentAltTarget]) <= CHANGE_DISTANCE)
			changeAltTarget();
		
		alphaBeta.alphaBeta(gameInfo.field(), snake, enemySnake, 8, eatable);
		if(alphaBeta.getBestScore() > 9000)
		{
			System.out.println("we win?");
			return alphaBeta.getBestMove();
		}
		int countWorstScores = 0;
		for(Entry<Direction,Integer> entry : alphaBeta.getDirectionScores().entrySet())
		{
			Point head = new Point(snake.headPosition().x,snake.headPosition().y);
			switch(entry.getKey())
			{
			case UP: head.y--; break;
			case DOWN:head.y++; break;
			case LEFT:head.x--; break;
			case RIGHT:head.x++; break;
			}
			if(entry.getValue() < -9000)
			{
//				System.out.println("entryVal: " + entry.getValue());
				countWorstScores++;
				minPathFinder.addBadPosition(head);
			}	
		}
		if(countWorstScores > 2)
		{
			System.out.println("We loose? "+ alphaBeta.getBestScore());
			return alphaBeta.getBestMove();
		}
		
		//TODO umbenennen!
		wallDetection();
		//Ist der Schlangenkoerper gerade in einem Portal?
//		System.out.println("check Portals");
		if(gameInfo.getPortals().isActive())
		{
			if(gameInfo.field().cell(snake.headPosition()).equals(CellType.PORTAL))
				passedPortal = true;
			if(gameInfo.field().cell(snake.segments().get(0)).equals(CellType.PORTAL))
				passedPortal = false;
		}
		else
			passedPortal = false;
		
//		System.out.println("place Walls for a trap");
		if(wallPlacedAtApple())
			return moveDirection;
		
//		System.out.println("PortalHelpful");
		if(isPortalHelpfulForSnake())
			return moveDirection;
		
//		System.out.println("AppleReach");
		if(isAppleReachable())
			return moveDirection;
				
		//Wenn wir bis jetzt noch keinen Weg gefunden haben, sollten wir auf Zeit spielen:
//		System.out.println("WallItemReach");
		if(isWallItemReachable())
			return moveDirection;
		
//		System.out.println("AltTargetReach");
		if(isAlternativeTargetReachable())
			return moveDirection;
		
		//Wahrscheinlich haben wir uns eingeschlossen! Berechne den kuerzesten Weg zum Schwanz
//		System.out.println("SnakeTrapped");
		if(isSnakeTrapped())
			return moveDirection;
		
//		System.out.println("CompleteMaxPath or random");
		if((moveDirection = completeMaxPath.get(snake.headPosition())) != null && isMoveValid(moveDirection))
			return moveDirection;
		
		return randomMove();
	}
	/**
	 * depending on wallDetection() this function places a wall for a trap if the apple is already next to a wall.
	 * After placing the wall the snake moves to a 'close point'.
	 * @return true if the snake placed a wall and made a trap.
	 */
	private boolean placeWallIfPossible()
	{
		//TODO: Wall Feature hier einfuegen
			//   8         4         2         1
			//[wallUp][wallRight][wallDown][wallLeft]
		if(mySnake.getCanSetWall())
		{
			int walls = wallDetection();
			if(walls > 0 && !isSnakeCloserToTarget(eatable[Items.APPLE.getIndex()]))
			{
				Point apple = eatable[Items.APPLE.getIndex()];
				boolean upDown = false;
				Point closePoint = apple;
				boolean foundClosePoint = false;
				switch(walls)
				{
				case 1:
					//
					//| x  
					//			
					for(int i=-3;i<=3;i++)
					{
						closePoint = new Point(apple.x,apple.y+i);
						if(i >= 0)
							upDown = true;
						if(isSnakeCloserToTarget(closePoint,apple))
						{
							foundClosePoint = true;
							break;
						}
					}
					if(foundClosePoint)
						mySnake.setWall(new Point(closePoint.x+1,(upDown?closePoint.y-2:closePoint.y+2)), Direction.DOWN);
					break;
				case 2:
					//
					// x
					// _
					for(int i=-3;i<=3;i++)
					{
						closePoint = new Point(apple.x+i,apple.y);
						if(i >= 0)
							upDown = true;
						if(isSnakeCloserToTarget(closePoint,apple))
						{
							foundClosePoint = true;
							break;
						}
					}
					if(foundClosePoint)
						mySnake.setWall(new Point((upDown?closePoint.x-2:closePoint.x+2),closePoint.y-1), Direction.LEFT);
					break;
				case 3:
					//
					//| x
					//  _
				case 4:
					//
					// x |
					// 
					for(int i=-3;i<=3;i++)
					{
						closePoint = new Point(apple.x,apple.y+i);
						if(i >= 0)
							upDown = true;
						if(isSnakeCloserToTarget(closePoint,apple))
						{
							foundClosePoint = true;
							break;
						}
					}
					if(foundClosePoint)
						mySnake.setWall(new Point(closePoint.x-1,(upDown?closePoint.y-2:closePoint.y+2)), Direction.UP);
					break;
				case 5:
					//
					//| x |
					// 
				case 6:
					//
					// x |
					// _
				case 7:
					//
					//| x |
					//  _
				case 8:
					// _
					// x
					// 
					for(int i=-3;i<=3;i++)
					{
						closePoint = new Point(apple.x+i,apple.y);
						if(i >= 0)
							upDown = true;
						if(isSnakeCloserToTarget(closePoint,apple))
						{
							foundClosePoint = true;
							break;
						}
					}
					if(foundClosePoint)
						mySnake.setWall(new Point((upDown?closePoint.x-2:closePoint.x+2),closePoint.y+1), Direction.LEFT);
					break;
				case 9:
					//  _
					//| x
					// _
				case 10:
					// _
					// x
					// _
				case 11:
					//  _
					//| x 
					//  _
				case 12:
					//
					// x |
					// _
				case 13:
					//  _
					//| x |
					// 
				case 14:
					// _
					// x |
					// _
				case 15:
					//  _
					//| x |
					//  _
				default:
				}
				if(foundClosePoint)
				{
					wallPlacedTarget = closePoint;
					if(getNextDirection(closePoint))
						return true;
				}
			}
			//Place Wall at Random Point
//			Random r = new Random();
//			Point wall = null;
//			do
//			{
//				wall = new Point(r.nextInt(info.field().width()),r.nextInt(info.field().height()));
//				mySnake.setWall(wall, Direction.LEFT);
//			}
//			while(mySnake.getCanSetWall());
		}
		wallPlacedTarget = null;
		return false;
	}
	/**
	 * if the snake made a trap, then the snake should continue to move to the 'close Point'
	 * @return true if it is possible to move to the 'close Point'
	 */
	private boolean wallPlacedAtApple()
	{	
		if(wallPlacedTarget != null)
		{
			if(getNextDirection(wallPlacedTarget))
				return true;
		}
		else
			return placeWallIfPossible();
		wallPlacedTarget = null;
		return false;
	}
	/**
	 * 
	 * @return
	 */
	private boolean isPortalHelpfulForSnake()
	{
		if(mySnake.segments().size() > MIN_CUT_LENGTH && !passedPortal && info.getPortals().isActive())
		{
			Point[] portals = {info.getPortals().getPortal1(),info.getPortals().getPortal2()};
			for(int i=0;i<portals.length;i++)
			{
				Node path = minPathFinder.getMinPath(new TempSnake(mySnake), portals[i],info.field(),info.getPortal());
				int dist = (path!= null?path.lengthToDest(mySnake.headPosition()):0);
				double TTL = info.getPortals().getTTL();
				if(path != null &&  TTL == dist+DESIRED_SNAKE_LENGTH)
				{	
					//Wir haben einen Pfad
					while(path.getFrom() != null && !path.getFrom().getActual().equals(mySnake.headPosition()))
						path = path.getFrom();	
					maxPath.clear();
					moveDirection = UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());
					return true;
				}			
			}
		}
		return false;
	}
	private boolean isAppleReachable()
	{
		if(eatable[Items.APPLE.getIndex()] != null && isSnakeCloserToTarget(eatable[Items.APPLE.getIndex()]))
		{
			//Wir sind naeher am Apfel!
			if(getNextDirection(eatable[Items.APPLE.getIndex()]))
				return true;
		}
		else
		{
			//Mist! Der Gegner ist naeher am Apfel. Koennen wir die Schlangen tauschen? bevor er beim Apfel ist?
			if(eatable[Items.CHANGESNAKE.getIndex()] != null && eatable[Items.APPLE.getIndex()] != null)
			{
				if(isSnakeCloserToTarget(eatable[Items.CHANGESNAKE.getIndex()], eatable[Items.APPLE.getIndex()]))
				{
					//Jap! Dann lass uns da hin gehen
					if(getNextDirection(eatable[Items.CHANGESNAKE.getIndex()]))
						return true;
				}
			}
		}
		return false;
	}
	private boolean isWallItemReachable()
	{
		//Holen wir uns ein WallItem, falls wir noch keine setzen koennen
		if(eatable[Items.WALLITEM.getIndex()] != null && !mySnake.getCanSetWall())
		{
			if(getNextDirection(eatable[Items.WALLITEM.getIndex()]))
				return true;
		}
		return false;
	}
	private boolean isAlternativeTargetReachable()
	{
		//Koennen wir zu unserem AlternativZiel gehen?
		for(int i=0;i<altTargets.length;i++)
		{
			if(!info.field().cell(altTargets[currentAltTarget]).equals(CellType.SNAKE) && 
					!info.field().cell(altTargets[currentAltTarget]).equals(CellType.WALL))
			{
				Node altWay = minPathFinder.getMinPath(new TempSnake(mySnake), altTargets[currentAltTarget],info.field(),info.getPortal());
				
				//Gibt es keinen Pfad dorthin?
				if(altWay != null)
				{	
					int currentAltTarget2 = ((currentAltTarget+1)%4);
					if(!info.field().cell(altTargets[currentAltTarget2]).equals(CellType.SNAKE) && 
							!info.field().cell(altTargets[currentAltTarget2]).equals(CellType.WALL))
					{
						Node altWay2 = minPathFinder.getMinPath(new TempSnake(mySnake), altTargets[currentAltTarget2],info.field(),info.getPortal());
						if(altWay2 != null)
						{
							//Wir haben einen Pfad
							while(altWay.getFrom() != null && !altWay.getFrom().getActual().equals(mySnake.headPosition()))
								altWay = altWay.getFrom();	
							maxPath.clear();
							moveDirection = UtilFunctions.getDirection(altWay.getFrom().getActual(),altWay.getActual());
							if(moveDirection == null)
							{
								int x = altWay.getFrom().getActual().x - altWay.getActual().x; 
								moveDirection = (x > 0?Direction.LEFT:(x == 0?null:Direction.RIGHT));
								int y = altWay.getFrom().getActual().y - altWay.getActual().y;
								if(moveDirection == null)
									moveDirection = (y > 0?Direction.UP:Direction.DOWN);
							}
							return true;
						}
					}
				}
				else
					break;
			}
			changeAltTarget();
		}
		return false;
	}
	private boolean isSnakeTrapped()
	{
		//TODO: Pruefe ob Portal erreichbar ist oder Item um Kopf und Schwanz zu tauschen
		
		//Wahrscheinlich haben wir uns eingeschlossen! Berechne den kuerzesten Weg zum Schwanz
		if(maxPath.isEmpty())
		{
			maxPathFinder = new HamiltonPath();
			Node way = maxPathFinder.getMaxPath(new TempSnake(mySnake).headPosition(), info.field(), new TempSnake(mySnake), new TempSnake(enemySnake),info.getPortal());
			while(way != null && way.getFrom() != null && !way.getActual().equals(mySnake.headPosition()))
			{
				maxPath.add(UtilFunctions.getDirection(way.getFrom().getActual(),way.getActual()));
				way = way.getFrom();
			}
			if(!maxPath.isEmpty())
			{
				moveDirection = maxPath.pop();
				return true;
			}
			else
				return false;
		}
		else 
		{
			moveDirection = maxPath.pop();
			return true;
		}
	}
	private boolean isSnakeCloserToTarget(Point target)
	{
		return UtilFunctions.getDistance(mySnake.headPosition(),target) <= UtilFunctions.getDistance(enemySnake.headPosition(),target);
	}
	private boolean isSnakeCloserToTarget(Point myTarget, Point enemyTarget)
	{
		return UtilFunctions.getDistance(mySnake.headPosition(),myTarget) < UtilFunctions.getDistance(enemySnake.headPosition(),enemyTarget);
	}
	private void changeAltTarget()
	{
		currentAltTarget = ((++currentAltTarget)%altTargets.length);
	}
	private int wallDetection()
	{
		Point apple = eatable[Items.APPLE.value];
		if(apple== null)
			return 0;
		//   8         4         2         1
		//[wallUp][wallRight][wallDown][wallLeft]
		//Bsp.: 1101 => Wall oben,rechts und links
		int wall = 0;
		for (int i = -1; i <= 1; i += 2)
		{
			if (apple.x + i <= 29 && apple.x + i >= 0)
			{
				Point next = new Point(apple.x +i,apple.y);
				if(info.field().cell(next) == CellType.WALL)
				{
					wall |= (i > 0?4:1);
				}
			}
			if (apple.y + i <= 19 && apple.y + i >= 0)		
			{
				Point next = new Point(apple.x,apple.y+i);
				if(info.field().cell(next) == CellType.WALL)
				{
					wall |= (i > 0?2:8);
				}
			}
		}
		return wall;
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
		if(minPathFinder == null)
			minPathFinder = new PathFinder();
		if(maxPathFinder == null)
			maxPathFinder = new HamiltonPath();
		if(alphaBeta == null)
			alphaBeta = new AlphaBeta();
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
	private boolean getNextDirection(Point target)
	{
		Node path = minPathFinder.getMinPath(new TempSnake(mySnake), target,info.field(),info.getPortal());
		//Gibt es keinen Pfad dorthin?
		if(path != null)
		{	
			//Wir haben einen Pfad
			while(path.getFrom() != null && !path.getFrom().getActual().equals(mySnake.headPosition()))
				path = path.getFrom();	
			maxPath.clear();
			moveDirection = UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());
			if(moveDirection == null)
			{
				int x = path.getFrom().getActual().x - path.getActual().x; 
				moveDirection = (x > 0?Direction.LEFT:(x == 0?null:Direction.RIGHT));
				int y = path.getFrom().getActual().y - path.getActual().y;
				if(moveDirection == null)
					moveDirection = (y > 0?Direction.UP:Direction.DOWN);
			}
			return true;
		}
		return false;
	}
	
	//Calculate Valid Moves
	private boolean isMoveValid(Direction d) {
		Point newHead = new Point(mySnake.headPosition().x, mySnake.headPosition().y);
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
			newHead.x = info.field().width()-1;
		}
		if (newHead.x == info.field().width()) {
			newHead.x = 0;
		}
		if (newHead.y == -1) {
			newHead.y = info.field().height()-1;
		}
		if (newHead.y == info.field().height()) {
			newHead.y = 0;
		}
		
		return info.field().cell(newHead) != CellType.SNAKE && info.field().cell(newHead) != CellType.WALL;
	}
	
	private boolean isValidMovePossible() {
		return isMoveValid(Direction.DOWN) || isMoveValid(Direction.UP) || isMoveValid(Direction.LEFT) || isMoveValid(Direction.RIGHT);
	}
	private Direction randomMove() {
		Random rand = new Random();
		Direction d;
		if (rand.nextDouble() < 0.95 && last != null && isMoveValid(last)) {
			d = last;
		} else {
			do {
				d = Direction.values()[rand.nextInt(4)];
			} while(!isMoveValid(d) && isValidMovePossible());
		}
		
		last = d;
		
		return d;
	}
}
