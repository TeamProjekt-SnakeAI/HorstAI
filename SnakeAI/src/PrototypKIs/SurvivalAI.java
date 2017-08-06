package PrototypKIs;

import java.util.Random;

import Logic.Field.CellType;
import Logic.GameInfo;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;
import Util.Node;
import Util.PathFinder;
import Util.Pathfinding;
import Util.TempSnake;
import Util.UtilFunctions;
import Logic.SnakeBrain;

//SurvivalAI
//Created by: Julia Hofmann, Marco Piechotta

public class SurvivalAI implements SnakeBrain {

	private final int DISTANCE_TO_ENEMYSNAKE = 2;
	private final int MIN_CUT_LENGTH = 2;
	private final int DESIRED_SNAKE_LENGTH = 1;
	
	private Snake mySnake;
	private Snake enemySnake;
	private Direction last = null;				//letzter Ausweg: RandomBrain-Move
	private Direction moveDirection = null;
	private GameInfo info;
	private boolean passedPortal = false;
		
	//MinPathFinder Alg.: A*-Algorithm 
	private Pathfinding minPathFinder;

	@Override
	public Direction nextDirection(GameInfo gameInfo, Snake snake) {
		info = gameInfo;
		init(snake);
//		System.out.println("MySnake: " + mySnake.headPosition());
//		System.out.println("EnemySnake: " + enemySnake.headPosition());
//		System.out.println("EnemySnake-tail: " + enemySnake.segments().get(0));
//		System.out.println("Portal?");
		if(isPortalHelpfulForSnake())
			return moveDirection;
//		System.out.println("To the Enemy Snake!");
		if(UtilFunctions.getDistance(mySnake.headPosition(),enemySnake.segments().get(0)) > DISTANCE_TO_ENEMYSNAKE && getNextDirection(enemySnake.segments().get(0)))
			return moveDirection;
//		System.out.println("Random");
		return randomMove(gameInfo, snake);	
	}
	private boolean isPortalHelpfulForSnake()
	{
		if(mySnake.segments().size() > MIN_CUT_LENGTH && !passedPortal && info.getPortals().isActive())
		{
			Point[] portals = {info.getPortals().getPortal1(),info.getPortals().getPortal2()};
			for(int i=0;i<portals.length;i++)
			{
				Node path = minPathFinder.getMinPath(mySnake.headPosition(), portals[i],info.field(),mySnake.segments().get(0));
				int dist = (path!= null?path.lengthToDest(mySnake.headPosition()):0);
				double TTL = info.getPortals().getTTL();
				if(path != null &&  TTL == dist+DESIRED_SNAKE_LENGTH)
				{	
					//Wir haben einen Pfad
					while(path.getFrom() != null && !path.getFrom().getActual().equals(mySnake.headPosition()))
						path = path.getFrom();	
					moveDirection = UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());
					return true;
				}			
			}
		}
		return false;
	}
	private boolean getNextDirection(Point target)
	{
		Node path = minPathFinder.getMinPath(mySnake.headPosition(), target,info.field(),mySnake.segments().get(0));
		
		//Gibt es keinen Pfad dorthin?
		if(path != null)
		{	
			//Wir haben einen Pfad
			while(path.getFrom() != null && !path.getFrom().getActual().equals(mySnake.headPosition()))
				path = path.getFrom();	
			moveDirection = UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());
			if(!isMoveValid(moveDirection, mySnake, info))
				return false;
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
	private void init(Snake snake)
	{
		
		//initialisieren
		if(minPathFinder == null)
			minPathFinder = new Pathfinding();
		
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