package PrototypKIs;

import java.util.HashMap;
import java.util.Random;

import Logic.Field;
import Logic.GameInfo;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;
import Util.AlphaBeta;
import Util.HamiltonPath;
import Util.Node;
import Util.Pathfinder;
import Util.UtilFunctions;
import Logic.SnakeBrain;
import Logic.Field.CellType;

public class BrainMaster implements SnakeBrain{
	private Pathfinder pathfinder;
	private Snake mySnake;
	private Snake enemySnake;
	private Point selectedTarget;
	private Point apple;
	private Point wallItem;
	private Direction last;
	@Override
	public Direction nextDirection(GameInfo gameInfo, Snake snake) {
		init(gameInfo,snake);
		System.out.println("1");
		boolean newTarget = false;
		if(selectedTarget == null)
			newTarget = true;
		if(!newTarget && snake.headPosition().equals(selectedTarget))
			newTarget = true;
		if(!newTarget && gameInfo.field().cell(selectedTarget).equals(CellType.SPACE))
			newTarget = true;
		if(!newTarget && !pathfinder.hasNextDirection())
			newTarget = true;
		if(!newTarget)
		{
			Direction next = pathfinder.getNextDirection();
			if(isMoveValid(next, snake, gameInfo))
				return next;
			else newTarget = true;
		}
		System.out.println("2");
		if(newTarget)
		{
			pathfinder.calculateMinPath(mySnake, apple, gameInfo.field());
			selectedTarget = apple;
			if(pathfinder.hasNextDirection())
				return pathfinder.getNextDirection();
			else
			{
				System.out.println("3");
				pathfinder.calculateMinPath(mySnake, wallItem, gameInfo.field());
				selectedTarget = wallItem;
				if(pathfinder.hasNextDirection())
					return pathfinder.getNextDirection();
			}
		}
		
		
		return null;
	}
	private void init(GameInfo info, Snake snake)
	{	
		//initialisieren
		if(pathfinder == null)
			pathfinder = new Pathfinder(info.field());
				
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
