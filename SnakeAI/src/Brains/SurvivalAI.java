package Brains;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import Logic.Apple;
import Logic.Field;
import Logic.Field.CellType;
import Logic.GameInfo;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;
import Util.Node;
import Util.Pathfinding;
import Util.UtilFunctions;
import Logic.SnakeBrain;

//SurvivalAI
//Created by: Julia Hofmann, Marco Piechotta

public class SurvivalAI implements SnakeBrain {
	private HashMap<Point, Integer> apples = new HashMap<>();
	private Direction last = null;
	
	//A* 
	Pathfinding finder;

	@Override
	public Direction nextDirection(GameInfo gameInfo, Snake snake) {
		Direction move = null;
		if(finder == null)
			finder = new Pathfinding(gameInfo.field());
		Point target = null;
		for(Entry<Point,Apple> entry : gameInfo.field().getApples().entrySet())
		{
			target = entry.getKey();
			break;
		}
		Node path = null;
		if(target != null)
			path = finder.getMinPath(snake.headPosition(),target , gameInfo.field(), snake.segments().get(0));
		
		if(path != null)
		{
			//Wir haben einen Pfad
			while(path.getFrom() != null && !path.getFrom().getActual().equals(snake.headPosition()))
				path = path.getFrom();	
			
			move = UtilFunctions.getDirection(path.getFrom().getActual(),path.getActual());
		}
		if(move == null)
			return randomMove(gameInfo, snake);
		else
			return move;
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