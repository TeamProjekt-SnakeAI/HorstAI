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
	private int[] evalSituation = {100000,-100000};
	
	public HashMap<Direction,Integer> directionScores = new HashMap<>();
	public int bestScore;
	public Direction bestMove;

	
//	public AlphaBeta(int[] evalStuff) {
//		evalSituation = evalStuff;
//	}
	/**
	 * Berechnet AlphaBetaPruning für das aktuelle Spielfeld
	 * @param field 	aktuelles Spielfeld
	 * @param mySnake	eigene Schlange(MAX-Spieler)
	 * @param enemySnake	gegnerische Schlange(MIN-Spieler)
	 * @param searchDepth	maxTiefe
	 */
	public void alphaBeta(Field field, Snake mySnake, Snake enemySnake, int searchDepth)
	{
		//Init AlphaBeta Klassenvariablen
		this.MAXDEPTH = searchDepth;
		this.mySnake = new TempSnake(mySnake,"MYSNAKE");
		this.enemySnake = new TempSnake(enemySnake, "ENEMYSNAKE");
		
		//Init GameField
		Type[][] gameField = new Type[field.width()][field.height()];
		fillGameField(gameField,field);
		
		//letzte Berechnung löschen
		directionScores.clear();
		
		//AlphaBeta berechnen. Nächster Spieler ist MAX-Spieler
		bestScore = max(MAXDEPTH,Integer.MIN_VALUE,Integer.MAX_VALUE,this.mySnake,this.enemySnake,gameField);
	}
	private int max(int depth, int alpha, int beta, TempSnake mySnake, TempSnake enemySnake,Type[][] gameField)
	{
//		System.out.println("--MAX--"+depth);
//		System.out.println(gameFieldString(gameField));
		List<Direction> possibleMoves = getPossibleMoves(mySnake.headPosition(),gameField, mySnake);
		if(depth==0 || possibleMoves.isEmpty() || gameEnd(gameField))
			return eval(gameField,mySnake.headPosition(),enemySnake.headPosition());
		int maxValue = alpha;
		for(Direction dir : possibleMoves)
		{
			TempSnake saveSnake = new TempSnake(mySnake);
			Type undo = makeMove(dir,gameField,saveSnake,true);
			int value = min(depth-1,maxValue,beta,saveSnake,enemySnake,gameField);
			undoMove(gameField,mySnake,saveSnake.headPosition(),true,undo);
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
//		System.out.println("--MIN--"+depth);
//		System.out.println(gameFieldString(gameField));
//		System.out.println("EnemyMove: " + enemySnake.headPosition());
		List<Direction> possibleMoves = getPossibleMoves(enemySnake.headPosition(),gameField, enemySnake);
		if(depth==0 || possibleMoves.isEmpty() || gameEnd(gameField))
			return eval(gameField,mySnake.headPosition(),enemySnake.headPosition());
		int minValue = beta;
		for(Direction dir : possibleMoves)
		{
			TempSnake saveSnake = new TempSnake(enemySnake);
			Type undo = makeMove(dir,gameField,saveSnake,false);
			int value = max(depth-1,alpha,minValue,mySnake,saveSnake,gameField);
			undoMove(gameField,enemySnake,saveSnake.headPosition(),false,undo);
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
	private Type makeMove(Direction dir, Type[][] gameField, TempSnake snake, boolean mySnake) {
		snake.move(dir);
		Point newHead = snake.headPosition();
		Type returnType = gameField[newHead.x][newHead.y];
		if(gameField[newHead.x][newHead.y] == Type.APPLE)
			snake.grow(1);
		if(mySnake)
			switch(returnType)
			{
			case SPACE: gameField[newHead.x][newHead.y] = Type.MYSNAKE;break;
			case APPLE: gameField[newHead.x][newHead.y] = Type.MYSNAKE;break;
			case ENEMYSNAKE: gameField[newHead.x][newHead.y] = Type.MYSNAKEINSNAKE;break;
			case MYSNAKE: gameField[newHead.x][newHead.y] = Type.MYSNAKEINSNAKE;break;
			case WALL: gameField[newHead.x][newHead.y] = Type.MYSNAKEINWALL;break;
			default:
			}
			
		else
			switch(gameField[newHead.x][newHead.y])
			{
			case SPACE: gameField[newHead.x][newHead.y] = Type.ENEMYSNAKE;break;
			case APPLE: gameField[newHead.x][newHead.y] = Type.ENEMYSNAKE;break;
			case ENEMYSNAKE: gameField[newHead.x][newHead.y] = Type.ENEMYSNAKEINSNAKE;break;
			case MYSNAKE: gameField[newHead.x][newHead.y] = Type.ENEMYSNAKEINSNAKE;break;
			case WALL: gameField[newHead.x][newHead.y] = Type.ENEMYINWALL;break;
			default:
			}
		return returnType;
	}
	private void undoMove(Type[][] gameField, TempSnake snake,Point movedTo, boolean mySnake, Type changed) {
		gameField[movedTo.x][movedTo.y] = changed;
		Point newTail = snake.segments().get(0);
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
				Point newPos = new Point(sH.x+i,sH.y);
//				if(gameField[sH.x+i][sH.y].equals(Type.APPLE) || gameField[sH.x+i][sH.y].equals(Type.SPACE) || newPos.equals(snake.segments().get(0)))
					possibleMoves.add(UtilFunctions.getDirection(sH,newPos ));
			}
			if (sH.y + i < 19 && sH.y + i >= 1)
			{					
				Point newPos =  new Point(sH.x,sH.y+i);
//				if(gameField[sH.x][sH.y+i].equals(Type.APPLE) || gameField[sH.x][sH.y+i].equals(Type.SPACE) || newPos.equals(snake.segments().get(0)))
					possibleMoves.add(UtilFunctions.getDirection(sH,newPos));
			}
		}
//		System.out.println("Possible Moves: "+Arrays.toString(possibleMoves.toArray()));
		return possibleMoves;
	}
	private int eval(Type[][] gameField, Point myHead, Point enemyHead)
	{
//		System.out.println(gameFieldString(gameField));
		int value= 0;
		//WIN
//		Point enemyHead = enemySnake.headPosition();
		switch(gameField[enemyHead.x][enemyHead.y])
		{
		case ENEMYSNAKEINSNAKE: value+= 1*evalSituation[0];break;
		case ENEMYINWALL: value+= 1*evalSituation[0];break;
		default:
		}
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
		switch(gameField[myHead.x][myHead.y])
		{
		case MYSNAKEINSNAKE: value+=1*evalSituation[1];break;
		case MYSNAKEINWALL: value+=1*evalSituation[1];break;
		default:
		}
		if(myHead.x == 0 || myHead.y == 0)
			value += 1*evalSituation[1];
		if(myHead.x == gameField.length-1 || myHead.y == gameField[0].length-1)
			value += 1*evalSituation[1];

		
		if(pointInSnake(enemySnake,mySnake.headPosition()))
			value+= 1*evalSituation[1];
		if(pointInSnake(mySnake,mySnake.headPosition()))
			value+= 1*evalSituation[1];
//		System.out.println("Selfbite: " + value);
		return value;
	}
	private boolean gameEnd(Type[][] gameField)
	{
		for(int x=0;x<gameField.length;x++)
			for(int y=0;y<gameField[x].length;y++)
				switch(gameField[x][y])
				{
				case MYSNAKEINSNAKE: 
				case MYSNAKEINWALL:
				case ENEMYSNAKEINSNAKE: 
				case ENEMYINWALL:
					return true;
				default:
				}
		return false;
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
	public String gameFieldString(Type[][] field) {
		String s = "";
		for (int x = 0;x < field.length;x++) {
			for (int y = 0;y < field[x].length;y++) {
				switch(field[x][y]) {
				case APPLE:
					s += "*";
					break;
				case MYSNAKE:
					s += "#";
					break;
				case ENEMYSNAKE:
					s += "°";
					break;
				case SPACE:
					s += " ";
					break;
				case WALL:
					s += "X";
					break;
				default:
					s+= "/";
					break;
				
				}
			}
			if (x < field.length-1) {
				s += "\n";
			}
		}
		return s;
	}
	
}
