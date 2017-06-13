package Util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Logic.Field;
import Logic.Point;
import Logic.Snake;
import Logic.Snake.Direction;

public class AlphaBeta {		
	private int MAXDEPTH;				//Maximale Tiefe, die vorraus geschaut wird
	private TempSnake mySnake;			//Referenz zur eigenen Schlange
	private TempSnake enemySnake;		//Referenz zur gegnerischen Schlange
	
	//{ WIN LOOSE }
	private int[] evalSituation = {100000,-100000};
	
	public HashMap<Direction,Integer> directionScores = new HashMap<>();	//HashMap, durch die man sich für jede Direction den berechneten Score holen kann
	public int bestScore;													//Bester Score, der berechnet wurde
	public Direction bestMove;												//Bester Move der berechnet wurde

	//Konstruktor für genetischen Algorithmus um für evalSituation den besten Array zu ermitteln um Situationen zu bewerten
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
	
	/**
	 * max bestimmt für den Max-Spieler(die eigene Schlange) den nächsten Zug und testet diesen
	 * @param depth		gibt die derzeitige Suchtiefe an und dient als Abbruchkriterium
	 * @param alpha		um unwahrscheinliche Möglichkeiten auszuschließen
	 * @param beta		um unwahrscheinliche Möglichkeiten auszuschließen
	 * @param mySnake	derzeitige eigene Schlange
	 * @param enemySnake	derzeitige gegnerische Schlange
	 * @param gameField	derzeitiges Spielfeld
	 * @return gibt die Bewertung für den gewählten Zug zurück sobald evaluiert wurde
	 */
	private int max(int depth, int alpha, int beta, TempSnake mySnake, TempSnake enemySnake,Type[][] gameField)
	{
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
		return maxValue;
	}
	/**
	 * min bestimmt für den Min-Spieler(die gegnerische Schlange) den nächsten Zug und testet diesen
	 * @param depth		gibt die derzeitige Suchtiefe an und dient als Abbruchkriterium
	 * @param alpha		um unwahrscheinliche Möglichkeiten auszuschließen
	 * @param beta		um unwahrscheinliche Möglichkeiten auszuschließen
	 * @param mySnake	derzeitige eigene Schlange
	 * @param enemySnake	derzeitige gegnerische Schlange
	 * @param gameField	derzeitiges Spielfeld
	 * @return gibt die Bewertung für den gewählten Zug zurück sobald evaluiert wurde
	 */
	private int min(int depth, int alpha, int beta, TempSnake mySnake, TempSnake enemySnake,Type[][] gameField)
	{
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
		return minValue;
	}
	/**
	 * wird verwendet um einen Zug zu simulieren
	 * @param dir	in welche Richtung soll sich die Schlange bewegen
	 * @param gameField	aktuelles Spielfeld
	 * @param snake	zu bewegende Schlange
	 * @param mySnake	handelt es sich dabei um die eigene Schlange?
	 * @return	gibt den Feldtyp zurück, auf dem nun die Schlange ist. (Um den Zug später rückgängig machen zu können)
	 */
	private Type makeMove(Direction dir, Type[][] gameField, TempSnake snake, boolean mySnake) {
		snake.move(dir);
		Point newHead = snake.headPosition();
		Type returnType = gameField[newHead.x][newHead.y];
		if(gameField[newHead.x][newHead.y] == Type.APPLE)
			snake.grow(1);
		if(mySnake)
			switch(returnType)
			{
			case SPACE:
			case WALLFEATURE:
			case APPLE: gameField[newHead.x][newHead.y] = Type.MYSNAKE;break;
			case ENEMYSNAKE: gameField[newHead.x][newHead.y] = Type.MYSNAKEINSNAKE;break;
			case MYSNAKE: gameField[newHead.x][newHead.y] = Type.MYSNAKEINSNAKE;break;
			case WALL: gameField[newHead.x][newHead.y] = Type.MYSNAKEINWALL;break;
			default: 
			}
			
		else
			switch(gameField[newHead.x][newHead.y])
			{
			case SPACE:
			case WALLFEATURE:
			case APPLE: gameField[newHead.x][newHead.y] = Type.ENEMYSNAKE;break;
			case ENEMYSNAKE: gameField[newHead.x][newHead.y] = Type.ENEMYSNAKEINSNAKE;break;
			case MYSNAKE: gameField[newHead.x][newHead.y] = Type.ENEMYSNAKEINSNAKE;break;
			case WALL: gameField[newHead.x][newHead.y] = Type.ENEMYINWALL;break;
			default:
			}
		return returnType;
	}
	/**
	 * undoMove macht einen gewählten Zug für eine Schlange rückgängig
	 * @param gameField aktuelles Spielfeld
	 * @param snake	Schlange die bewegt werden muss
	 * @param movedTo Schlange hatte sich hier hin bewegt
	 * @param mySnake ist das meine eigene Schlange?
	 * @param changed Feldtyp der durch die move-Bewegung ersetzt wurde
	 */
	private void undoMove(Type[][] gameField, TempSnake snake,Point movedTo, boolean mySnake, Type changed) {
		gameField[movedTo.x][movedTo.y] = changed;
		Point newTail = snake.segments().get(0);
		if(mySnake)
			gameField[newTail.x][newTail.y] = Type.MYSNAKE;
		else
			gameField[newTail.x][newTail.y] = Type.ENEMYSNAKE;
	}
	/**
	 * Gibt alle gültigen Directions zurück
	 * @param sH Position des Schlangenkopfs
	 * @param gameField aktuelles Spielfeld
	 * @param snake welche Schlange
	 * @return Liste mit allen gültigen Directions
	 */
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
	/**
	 * eval bewertet das aktuelle Spielfeld und gibt zurück wie gut dieses für die eigene Schlange ist
	 * @param gameField aktuelles Spielfeld
	 * @param myHead Kopf der eigenen Schlange
	 * @param enemyHead Kopf der gegnerischen Schlange
	 * @return Bewertung des Spielfelds 
	 */
	private int eval(Type[][] gameField, Point myHead, Point enemyHead)
	{
		int value= 0;
		
		//WIN
		switch(gameField[enemyHead.x][enemyHead.y])
		{
		case ENEMYSNAKEINSNAKE: value+= 1*evalSituation[0];break;
		case ENEMYINWALL: value+= 1*evalSituation[0];break;
		default:
		}
		
		if(enemyHead.x == 0 || enemyHead.y == 0)
			value += 1*evalSituation[0];
		if(enemyHead.x == gameField.length-1 || enemyHead.y == gameField[0].length-1)
			value += 1*evalSituation[0];
		
		if(pointInSnake(mySnake,enemySnake.headPosition()))
			value+= 1*evalSituation[0];
		if(pointInSnake(enemySnake,enemySnake.headPosition()))
			value+= 1*evalSituation[0];
		
		
		//LOOSE
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

		return value;
	}
	/**
	 * gameEnd bestimmt anhand des Spielfelds ob das Spiel vorbei ist.
	 * @param gameField aktuelles Spielfeld
	 * @return
	 */
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
	/**
	 * dient zur initialisierung des Spielfelds, mit dem simuliert wird
	 * @param gameField aktuelles Spielfeld(leer)
	 * @param field Spielfeld des tatsächlichen Spiels
	 */
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
				case FEATUREWALL: gameField[x][y] = Type.WALLFEATURE; break;
				}
			}
	}
	/**
	 * gibt Zurück ob die Headposition in der anderen Schlange liegt
	 * @param snake Schlange
	 * @param head	Kopf der anderen Schlange
	 * @return
	 */
	private boolean pointInSnake(TempSnake snake, Point head)
	{
		for(int i=0;i<snake.segments().size()-1;i++)
			if(head.equals(snake.segments().get(i)))
				return true;
		return false;
	}
	/**
	 * Zur Ausgae des Spielfelds. Für Debugging Zwecke
	 * @param field
	 * @return
	 */
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
