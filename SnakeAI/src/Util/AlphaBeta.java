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
	private int MAXDEPTH;				//Maximale Tiefe, die vorraus geschaut wird
	private TempSnake mySnake;			//Referenz zur eigenen Schlange
	private TempSnake enemySnake;		//Referenz zur gegnerischen Schlange
	private Point startingPoint;
	private Point saveApple;
	private int[][] evalField;
	
	//{ WIN LOOSE }
	private int[] evalSituation = {100000,-100000 };
	//{ DISTANCEAPPLE DISTANCEWALL DISTANCECHANGESNAKE DISTANCECHANGEHEATTAIL DISTANCEPORTAL DISTANCESPEEDUP DISTANCECUTTAIL }
	private int[] evalDistances = {100, 20,10, 10,5, 30, 40};
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
	public HashMap<Direction,Integer> directionScores = new HashMap<>();	//HashMap, durch die man sich für jede Direction den berechneten Score holen kann
	public int bestScore;													//Bester Score, der berechnet wurde
	public Direction bestMove;												//Bester Move der berechnet wurde

	//Konstruktor für genetischen Algorithmus um für evalSituation den besten Array zu ermitteln um Situationen zu bewerten
//	public AlphaBeta(int[] evalStuff) {
//		evalSituation = evalStuff;
//	}
	
//FEHLER: TODO	
//	Exception in thread "Thread-6" java.lang.NullPointerException
//	at Util.AlphaBeta.gameEnd(AlphaBeta.java:262)
//	at Util.AlphaBeta.max(AlphaBeta.java:66)
//	at Util.AlphaBeta.alphaBeta(AlphaBeta.java:50)
//	at Brains.HorstAI.nextDirection(HorstAI.java:77)
//	at Logic.BrainThread.run(BrainThread.java:20)
	
	/**
	 * Berechnet AlphaBetaPruning für das aktuelle Spielfeld
	 * @param field 	aktuelles Spielfeld
	 * @param mySnake	eigene Schlange(MAX-Spieler)
	 * @param enemySnake	gegnerische Schlange(MIN-Spieler)
	 * @param searchDepth	maxTiefe
	 */
	public void alphaBeta(Field field, Snake mySnake, Snake enemySnake, int searchDepth,Point[] eatable)
	{
		//Init AlphaBeta Klassenvariablen
		this.MAXDEPTH = searchDepth;
		this.mySnake = new TempSnake(mySnake,"MYSNAKE");
		this.enemySnake = new TempSnake(enemySnake, "ENEMYSNAKE");
		this.eatable = eatable;
		startingPoint = new Point(mySnake.headPosition().x,mySnake.headPosition().y);
		evalField = new int[field.width()][field.height()];
		
		//Init GameField
		Type[][] gameField = new Type[field.width()][field.height()];
		fillGameField(gameField,field);
		
		//letzte Berechnung löschen
		directionScores.clear();
		
		//AlphaBeta berechnen. Nächster Spieler ist MAX-Spieler
		bestScore = max(MAXDEPTH,Integer.MIN_VALUE,Integer.MAX_VALUE,this.mySnake,this.enemySnake,gameField);
//		if(eatable[0]!= null)
//		{
//			System.out.println("ApplePos: " + eatable[0]);
//			System.out.println("StartPos: " + startingPoint);
//		}
//		for(int i=0;i<evalField.length;i++)
//		{
//			System.out.println(Arrays.toString(evalField[i]));
//		}
//		if(eatable[0]== null)
//			while(true)
//				bestScore = 0;
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
		
		//Check if we reached our desired search depth or if there is no possible move left or if the game is over (snake hit something)
		if(depth==0 || possibleMoves.isEmpty() || gameEnd(gameField,depth) || (eatable[0] != null?mySnake.headPosition().equals(eatable[0]):false))
			return eval(gameField,mySnake.headPosition(),enemySnake.headPosition(),depth);
		
		int maxValue = alpha;
			
		for(Direction dir : possibleMoves)
		{
			TempSnake saveSnake = new TempSnake(mySnake);
			if(gameField[saveSnake.headPosition().x][saveSnake.headPosition().y] == Type.APPLE)
			{
				if(eatable[0] != null)
					saveApple = eatable[0];
				eatable[0] = null;
			}
			Type undo = makeMove(dir,gameField,saveSnake,true);
//			makeMove(dir,gameField,saveSnake,true);
			int value = min(depth-1,maxValue,beta,saveSnake,enemySnake,gameField);
			undoMove(gameField,mySnake,saveSnake.headPosition(),true,undo);
			if(saveApple != null)
				eatable[0] = saveApple;
			if(depth == MAXDEPTH)
			{
				System.out.println(dir + " : " + value);
				evalField[saveSnake.headPosition().x][saveSnake.headPosition().y] = value;
				directionScores.put(dir, value);
			}
			if(value > maxValue)
			{
				maxValue = value;
				evalField[saveSnake.headPosition().x][saveSnake.headPosition().y] = value;
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
		if(depth==0 || possibleMoves.isEmpty() || gameEnd(gameField,depth))
			return eval(gameField,mySnake.headPosition(),enemySnake.headPosition(),depth);
		int minValue = beta;
		for(Direction dir : possibleMoves)
		{
			TempSnake saveSnake = new TempSnake(enemySnake);
			if(gameField[saveSnake.headPosition().x][saveSnake.headPosition().y] == Type.APPLE)
			{
				if(eatable[0] != null)
					saveApple = eatable[0];
				eatable[0] = null;
			}
			Type undo = makeMove(dir,gameField,saveSnake,false);
//			makeMove(dir,gameField,saveSnake,false);
			int value = max(depth-1,alpha,minValue,mySnake,saveSnake,gameField);
			undoMove(gameField,enemySnake,saveSnake.headPosition(),false,undo);
			if(saveApple != null)
				eatable[0] = saveApple;
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
		
		Point oldTail = snake.segments().get(0);
		snake.move(dir);
		Point newHead = snake.headPosition();
		Type returnType = gameField[newHead.x][newHead.y];
		gameField[oldTail.x][oldTail.y] = Type.SPACE;
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
	private int eval(Type[][] gameField, Point myHead, Point enemyHead, int depth)
	{
//		System.out.println("myHead: " + myHead);
//		System.out.println("enemyHead: " + enemyHead);
		int value= 0;
		//POINTS:
		value += mySnake.segments().size();
		value += (mySnake.getScore() - enemySnake.getScore());
		
//		value += depth * 100;
		//DISTANCE
//		for(int i=0;i<eatable.length;i++)
//		{
//			if(eatable[i] != null)
//				value += 1000000 - UtilFunctions.getDistance(myHead, eatable[i])*evalDistances[i];			
//		}
		if(eatable[0] == null)
			System.out.println("no eatable");
		if(eatable[0] != null && myHead.equals(eatable[0]))
			value += 100;
		if(eatable[0] != null)
			value += 200 - 2*UtilFunctions.getDistance(myHead, eatable[0]) - UtilFunctions.getDistance(startingPoint, eatable[0]) ;
		value += 200 - 2*UtilFunctions.getDistance(startingPoint, enemyHead) - UtilFunctions.getDistance(myHead, enemyHead);
		
//		//WIN
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
	private boolean gameEnd(Type[][] gameField, int depth)
	{
		for(int x=0;x<gameField.length;x++)
			for(int y=0;y<gameField[x].length;y++)
				switch(gameField[x][y])
				{
				case MYSNAKEINSNAKE: 
				case MYSNAKEINWALL:
				case ENEMYSNAKEINSNAKE: 
				case ENEMYINWALL:
//					System.out.println("TRUE!" + depth);
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
				case CHANGEHEADTAIL:
					gameField[x][y] = Type.CHANGEHEADTAIL;
					break;
				case CHANGESNAKE:
					gameField[x][y] = Type.CHANGESNAKE;
					break;
				case CUTTAIL:
					gameField[x][y] = Type.CUTTAIL;
					break;
				case OPENFIELD:
					gameField[x][y] = Type.OPENFIELD;
					break;
				case OPENFIELDPICTURE:
					gameField[x][y] = Type.OPENFIELDPICTURE;
					break;
				case PORTAL:
					gameField[x][y] = Type.PORTAL;
					break;
				case SPEEDUP:
					gameField[x][y] = Type.SPEEDUP;
					break;
				default:
					break;
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
