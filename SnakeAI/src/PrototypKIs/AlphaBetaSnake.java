package PrototypKIs;

import java.util.Random;

import Logic.Field;
import Logic.GameInfo;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;
import Logic.SnakeBrain;
import Logic.Field.CellType;
import Util.AlphaBeta;

public class AlphaBetaSnake implements SnakeBrain {
	private AlphaBeta alphaBeta = new AlphaBeta();
	private Snake mySnake;
	private Direction last = null;	
	private GameInfo info;
	private Snake enemySnake;
	//Eatable Stuff
	//0 = apple , 1 = wallItem , 2 = changeSnake , 3 = changeHeadTail , 4 = Portal
	private enum Items {
		APPLE(0), WALLITEM(1), CHANGESNAKE(2), CHANGEHEADTAIL(3), PORTAL(4), SPEEDUP(5), CUTTAIL(6);
		private final int value;		
		private Items(int value) {
			this.value = value;
		}
		public int getIndex()
		{
			return value;
		}
	}
	private Point[] eatable = new Point[7];
	@Override
	public Direction nextDirection(GameInfo gameInfo, Snake snake) {
		info = gameInfo;
		//Initialize mySnake and EnemySnake
		if(mySnake == null || enemySnake == null)
		{
			mySnake = snake;
			for(Snake s : gameInfo.snakes())
			{
				if(!s.equals(snake))
				{
					enemySnake = s;
					break;
				}
			}
		}
		getItems(gameInfo.field());
		alphaBeta.alphaBeta(gameInfo.field(), snake, enemySnake, 8, eatable);
		if(alphaBeta.getBestMove() != null)
			return alphaBeta.getBestMove();
		
		return randomMove();
	}
	private void getItems(Field f) {
		eatable = new Point[7];
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
				if(f.cell(p).equals(CellType.SPEEDUP))
					eatable[Items.SPEEDUP.getIndex()] = p;
				if(f.cell(p).equals(CellType.CUTTAIL))
					eatable[Items.CUTTAIL.getIndex()] = p;
			}

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
