package Util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Logic.Field;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;

public class AlphaBeta {		
	private int MAXDEPTH;
	private TempSnake mySnake;
	private TempSnake enemySnake;
	
	//{ WIN LOOSE }
	private int[] evalSituation={100000,-100000};
	
	public HashMap<Direction,Integer> directionScores = new HashMap<>();
	public int bestScore;
	public Direction bestMove;

	
//	public AlphaBeta(int[] evalStuff) {
//		evalSituation = evalStuff;
//	}
	public void alphaBeta(Field field, Snake mySnake, Snake enemySnake, int searchDepth)
	{
		directionScores.clear();
		//Init GameField
		Type[][] gameField = new Type[field.width()][field.height()];
		this.MAXDEPTH = searchDepth;
		this.mySnake = new TempSnake(mySnake);
		this.enemySnake = new TempSnake(enemySnake);
//		System.out.println("EnemyStart: " + enemySnake.headPosition());
		fillGameField(gameField,field);	
			
		bestScore = max(MAXDEPTH,Integer.MIN_VALUE,Integer.MAX_VALUE,this.mySnake,this.enemySnake,gameField);
//		while(true)
//			bestScore = 0;
	}
	private int max(int depth, int alpha, int beta, TempSnake mySnake, TempSnake enemySnake,Type[][] gameField)
	{
//		System.out.println("MyMove: "+ mySnake.headPosition());
		List<Direction> possibleMoves = getPossibleMoves(mySnake.headPosition(),gameField, mySnake);
		if(depth==0 || possibleMoves.isEmpty())
			return eval(gameField);
		int maxValue = alpha;
		for(Direction dir : possibleMoves)
		{
			updateSnakePos(mySnake,gameField,true);
			Type undo = makeMove(dir,gameField,mySnake,true);
			int value = min(depth-1,maxValue,beta,mySnake,enemySnake,gameField);
			undoMove(dir,gameField,mySnake,true,undo);
			if(depth == MAXDEPTH)
			{
				directionScores.put(dir, value);
//				System.out.println(dir+" -> " + value);
			}
			if(value > maxValue)
			{
				maxValue = value;
				if(depth == MAXDEPTH)
				{
					bestMove = dir;
				}
				if(maxValue >= beta)
					break;
			}
		}
//		System.out.println("--MyMove-----");
		return maxValue;
	}
	private int min(int depth, int alpha, int beta, TempSnake mySnake, TempSnake enemySnake,Type[][] gameField)
	{
//		System.out.println("EnemyMove: " + enemySnake.headPosition());
		List<Direction> possibleMoves = getPossibleMoves(enemySnake.headPosition(),gameField, enemySnake);
		if(depth==0 || possibleMoves.isEmpty())
			return eval(gameField);
		int minValue = beta;
		for(Direction dir : possibleMoves)
		{
			updateSnakePos(enemySnake,gameField,false);
			Type undo = makeMove(dir,gameField,enemySnake,false);
			int value = max(depth-1,alpha,minValue,mySnake,enemySnake,gameField);
			if(enemySnake != null)
			{
				for(int i=0;i<gameField.length;i++)
				{
					System.out.println(Arrays.toString(gameField[i]));
				}
				while(true)
					enemySnake = null;
			}
			undoMove(dir,gameField,enemySnake,false,undo);
			if(value < minValue)
			{
				minValue = value;
				if(minValue <= alpha)
					break;
			}
		}
//		System.out.println("--enemyMove-----");
		return minValue;
	}
	private void updateSnakePos(TempSnake snake, Type[][] gameField, boolean mySnake) {
		for(Point p : snake.segments())
			gameField[p.x][p.y] = (mySnake?Type.MYSNAKE:Type.ENEMYSNAKE);
		
	}
	private Type makeMove(Direction dir, Type[][] gameField, TempSnake snake, boolean mySnake) {
		Point newHead = snake.move(dir, gameField);
		Type returnType = gameField[newHead.x][newHead.y];
		if(mySnake)
			gameField[newHead.x][newHead.y] = Type.MYSNAKE;
		else
			gameField[newHead.x][newHead.y] = Type.ENEMYSNAKE;
		return returnType;
	}
	private void undoMove(Direction dir, Type[][] gameField, TempSnake snake, boolean mySnake, Type changed) {
		gameField[snake.headPosition().x][snake.headPosition().y] = changed;
		Point newTail = snake.undoMove(dir, gameField);
		if(mySnake)
			gameField[newTail.x][newTail.y] = Type.MYSNAKE;
		else
			gameField[newTail.x][newTail.y] = Type.ENEMYSNAKE;
	}
	private List<Direction> getPossibleMoves(Point sH, Type[][] gameField, TempSnake snake) {
		List<Direction> possibleMoves = new LinkedList<>();
		for (int i = -1; i <= 1; i += 2) {
			if (sH.x + i < 29 && sH.x + i >= 1)
			{
				if(gameField[sH.x+i][sH.y].equals(Type.APPLE) || gameField[sH.x+i][sH.y].equals(Type.SPACE) || sH.equals(snake.segments().get(0)))
					possibleMoves.add(UtilFunctions.getDirection(sH, new Point(sH.x+i,sH.y)));
			}
			if (sH.y + i < 19 && sH.y + i >= 1)
			{					
				if(gameField[sH.x][sH.y+i].equals(Type.APPLE) || gameField[sH.x][sH.y+i].equals(Type.SPACE) || sH.equals(snake.segments().get(0)))
					possibleMoves.add(UtilFunctions.getDirection(sH, new Point(sH.x,sH.y+i)));
			}
		}
		return possibleMoves;
	}
	private int eval(Type[][] gameField)
	{
		int value= 0;

		//WIN
		Point enemyHead = enemySnake.headPosition();
//		System.out.println("EnemyHead: "+enemyHead);
		if(enemyHead.x == 0 || enemyHead.y == 0)
			value += 1*evalSituation[0];
		if(enemyHead.x == gameField.length-1 || enemyHead.y == gameField[0].length-1)
			value += 1*evalSituation[0];
		
//		System.out.println("EnemyHead in Wall: " + value);
		if(pointInSnake(mySnake,enemySnake.headPosition()))
			value+= 1*evalSituation[0];
		if(pointInSnake(enemySnake,enemySnake.headPosition()))
			value+= 1*evalSituation[0];
//		System.out.println("EnemyHead bite: " + value);
		
		//LOOSE
//		Point myHead = mySnake.headPosition();
//		if(myHead.x == 0 || myHead.y == 0)
//			value += 1*evalSituation[1];
//		if(myHead.x == gameField.length-1 || myHead.y == gameField[0].length-1)
//			value += 1*evalSituation[1];
//		System.out.println("Self in Wall: " + value);
		
//		if(pointInSnake(enemySnake,mySnake.headPosition()))
//			value+= 1*evalSituation[1];
//		if(pointInSnake(mySnake,mySnake.headPosition()))
//			value+= 1*evalSituation[1];
//		System.out.println("Selfbite: " + value);
		return value;
	}
	private void fillGameField(Type[][] gameField,Field field)
	{
		for(int x=0;x<gameField.length;x++)
			for(int y=0;y<gameField[x].length;y++)
			{
				Point point = new Point(x,y);
				switch(field.cell(point))
				{
				case SNAKE:
					if(mySnake.segments().contains(point))
						gameField[x][y] = Type.MYSNAKE;
					else
						gameField[x][y] = Type.ENEMYSNAKE;
					break;
				case WALL: gameField[x][y] = Type.WALL; break;
				case APPLE: gameField[x][y] = Type.APPLE; break;
				case SPACE: gameField[x][y] = Type.SPACE; break;
				}
			}
	}
	private boolean pointInSnake(TempSnake snake, Point head)
	{
		for(int i=0;i<snake.segments().size()-1;i++)
			if(head.equals(snake.segments().get(i)))
				return true;
		return false;
	}
}
