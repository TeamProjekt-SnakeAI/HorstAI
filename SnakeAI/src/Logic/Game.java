/*
 * Stores the current state of the game, implements main logic main loop
 * Author: Thomas Stüber
 * */

package Logic;
import Logic.Portals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import javafx.scene.paint.Color;
import Brains.HorstAI;
import Brains.RandomBrain;


public class Game {
	private ArrayList<Snake> snakes;
	private Field field;
	private Random rand;
		private boolean drawOutput;        
		private int gameSpeed;
		private int gameticks;
//	private double appleProbability; //probability per move that an apple spawns 
//	private double featureWallProbability; 
	//0 = apple , 1 = featureWall , 2 = changeSnake , 3 = changeHeadTail
	private double[] probabilitys;	//probability per move that this feature spawns
	private int playersLeft; //is decreased every time a player dies
	private int currentSnake;
	private Portals portal;
	
	public ArrayList<Snake> getSnakes() {
		return snakes;
	}
	public Portals getPortal(){
        return portal;
}
	public Field getField() {
		return field;
	}

        public void setOutput(boolean value){
            this.drawOutput=value;
        }
        public void setGameSpeed(int speed){
            this.gameSpeed=speed;
        }


	public Game(ArrayList<SnakeBrain> brains, ArrayList<Point> startPositions, ArrayList<Color> colors, Field field, double[] probabilitys) {
			this.drawOutput=true;
                this.gameSpeed=1000;
		this.field = field;
		currentSnake = 0;
			this.gameticks=0;
		GameInfo gameInfo = new GameInfo(this);
		snakes = new ArrayList<Snake>();
		playersLeft = brains.size();
		
		//adding the snakes
		for (int i = 0;i < brains.size();i++) {
			addSnake(new Snake(startPositions.get(i),gameInfo, brains.get(i), colors.get(i)), startPositions.get(i));
		}
		this.portal= new Portals();
		
		rand = new Random();
		this.probabilitys = probabilitys;		
	}
	
	//add a snake to the game
	public void addSnake(Snake snake, Point start) {
		snakes.add(snake);
		field.setCell(Field.CellType.SNAKE, start);
	}

	public static void main(String[] args) {
		Field field = Field.defaultField(30, 20);
		
		
//		
//		int winsA = 0, winsB = 0;
//		for (int i = 0;i < 100;i++) {
//			Point start1 = new Point(2, 2);
//			Point start2 = new Point(27, 17);
//			ArrayList<Point> startPositions = new ArrayList<Point>();
//			startPositions.add(start1);
//			startPositions.add(start2);
//			ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
//			brains.add(new SuperBrain());
//			brains.add(new AwesomeBrain());
//			ArrayList<Color> colors = new ArrayList<Color>();
//			colors.add(Color.YELLOWGREEN);
//			colors.add(Color.AZURE);
//			double[] probabilitys = {0.1, 0.005, 0.003, 0.003};
//			Game game = new Game(brains, startPositions, colors, field, probabilitys);
//			game.run();
//			
//			if (game.getSnakes().get(0).segments().size() > game.getSnakes().get(1).segments().size()) {
//				winsA++;
//			} else {
//				winsB++;
//			}
//		}
//		System.out.println("SuperBrain vs AwesomeBrain");
//		System.out.println(winsA + " SuperBrain");
//		System.out.println(winsB + " AwesomeBrain");
//		winsA = 0; winsB = 0;
//		
//		for (int i = 0;i < 100;i++) {
//			Point start1 = new Point(2, 2);
//			Point start2 = new Point(27, 17);
//			ArrayList<Point> startPositions = new ArrayList<Point>();
//			startPositions.add(start1);
//			startPositions.add(start2);
//			ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
//			brains.add(new SuperBrain());
//			brains.add(new NCageBrain());
//			ArrayList<Color> colors = new ArrayList<Color>();
//			colors.add(Color.YELLOWGREEN);
//			colors.add(Color.AZURE);
//			double[] probabilitys = {0.1, 0.005, 0.003, 0.003};
//			Game game = new Game(brains, startPositions, colors, field, probabilitys);
//			game.run();
//			
//			if (game.getSnakes().get(0).segments().size() > game.getSnakes().get(1).segments().size()) {
//				winsA++;
//			} else {
//				winsB++;
//			}
//		}
//		System.out.println("SuperBrain vs NCageBrain");
//		System.out.println(winsA + " SuperBrain");
//		System.out.println(winsB + " NCageBrain");
//		winsA = 0; winsB = 0;
//		
//		for (int i = 0;i < 100;i++) {
//			Point start1 = new Point(2, 2);
//			Point start2 = new Point(27, 17);
//			ArrayList<Point> startPositions = new ArrayList<Point>();
//			startPositions.add(start1);
//			startPositions.add(start2);
//			ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
//			brains.add(new SuperBrain());
//			brains.add(new NotSoRandomBrain1());
//			ArrayList<Color> colors = new ArrayList<Color>();
//			colors.add(Color.YELLOWGREEN);
//			colors.add(Color.AZURE);
//			double[] probabilitys = {0.1, 0.005, 0.003, 0.003};
//			Game game = new Game(brains, startPositions, colors, field, probabilitys);
//			game.run();
//			
//			if (game.getSnakes().get(0).segments().size() > game.getSnakes().get(1).segments().size()) {
//				winsA++;
//			} else {
//				winsB++;
//			}
//		}
//		System.out.println("SuperBrain vs NotSoRandomBrain1");
//		System.out.println(winsA + " SuperBrain");
//		System.out.println(winsB + " NotSoRandomBrain1");
//		winsA = 0; winsB = 0;
//		
//		for (int i = 0;i < 100;i++) {
//			Point start1 = new Point(2, 2);
//			Point start2 = new Point(27, 17);
//			ArrayList<Point> startPositions = new ArrayList<Point>();
//			startPositions.add(start1);
//			startPositions.add(start2);
//			ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
//			brains.add(new SuperBrain());
//			brains.add(new HorstAI());
//			ArrayList<Color> colors = new ArrayList<Color>();
//			colors.add(Color.YELLOWGREEN);
//			colors.add(Color.AZURE);
//			double[] probabilitys = {0.1, 0.005, 0.003, 0.003};
//			Game game = new Game(brains, startPositions, colors, field, probabilitys);
//			game.run();
//			
//			if (game.getSnakes().get(0).segments().size() > game.getSnakes().get(1).segments().size()) {
//				winsA++;
//			} else {
//				winsB++;
//			}
//		}
//		System.out.println("SuperBrain vs HorstAI");
//		System.out.println(winsA + " SuperBrain");
//		System.out.println(winsB + " HorstAI");
//		winsA = 0; winsB = 0;
//		
//		
//		for (int i = 0;i < 100;i++) {
//			Point start1 = new Point(2, 2);
//			Point start2 = new Point(27, 17);
//			ArrayList<Point> startPositions = new ArrayList<Point>();
//			startPositions.add(start1);
//			startPositions.add(start2);
//			ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
//			brains.add(new AwesomeBrain());
//			brains.add(new NCageBrain());
//			ArrayList<Color> colors = new ArrayList<Color>();
//			colors.add(Color.YELLOWGREEN);
//			colors.add(Color.AZURE);
//			double[] probabilitys = {0.1, 0.005, 0.003, 0.003};
//			Game game = new Game(brains, startPositions, colors, field, probabilitys);
//			game.run();
//			
//			if (game.getSnakes().get(0).segments().size() > game.getSnakes().get(1).segments().size()) {
//				winsA++;
//			} else {
//				winsB++;
//			}
//		}
//		System.out.println("Awesome vs NCageBrain");
//		System.out.println(winsA + " Awesome");
//		System.out.println(winsB + " NCageBrain");
//		winsA = 0; winsB = 0;
//		
//		for (int i = 0;i < 100;i++) {
//			Point start1 = new Point(2, 2);
//			Point start2 = new Point(27, 17);
//			ArrayList<Point> startPositions = new ArrayList<Point>();
//			startPositions.add(start1);
//			startPositions.add(start2);
//			ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
//			brains.add(new AwesomeBrain());
//			brains.add(new NotSoRandomBrain1());
//			ArrayList<Color> colors = new ArrayList<Color>();
//			colors.add(Color.YELLOWGREEN);
//			colors.add(Color.AZURE);
//			double[] probabilitys = {0.1, 0.005, 0.003, 0.003};
//			Game game = new Game(brains, startPositions, colors, field, probabilitys);
//			game.run();
//			
//			if (game.getSnakes().get(0).segments().size() > game.getSnakes().get(1).segments().size()) {
//				winsA++;
//			} else {
//				winsB++;
//			}
//		}
//		System.out.println("Awesome vs NotSoRandomBrain1");
//		System.out.println(winsA + " Awesome");
//		System.out.println(winsB + " NotSoRandomBrain1");
//		winsA = 0; winsB = 0;
//		
//		for (int i = 0;i < 100;i++) {
//			Point start1 = new Point(2, 2);
//			Point start2 = new Point(27, 17);
//			ArrayList<Point> startPositions = new ArrayList<Point>();
//			startPositions.add(start1);
//			startPositions.add(start2);
//			ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
//			brains.add(new AwesomeBrain());
//			brains.add(new HorstAI());
//			ArrayList<Color> colors = new ArrayList<Color>();
//			colors.add(Color.YELLOWGREEN);
//			colors.add(Color.AZURE);
//			double[] probabilitys = {0.1, 0.005, 0.003, 0.003};
//			Game game = new Game(brains, startPositions, colors, field, probabilitys);
//			game.run();
//			
//			if (game.getSnakes().get(0).segments().size() > game.getSnakes().get(1).segments().size()) {
//				winsA++;
//			} else {
//				winsB++;
//			}
//		}
//		System.out.println("AwesomeBrain vs HorstAI");
//		System.out.println(winsA + " AwesomeBrain");
//		System.out.println(winsB + " HorstAI");
//		winsA = 0; winsB = 0;
//		
//		for (int i = 0;i < 100;i++) {
//			Point start1 = new Point(2, 2);
//			Point start2 = new Point(27, 17);
//			ArrayList<Point> startPositions = new ArrayList<Point>();
//			startPositions.add(start1);
//			startPositions.add(start2);
//			ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
//			brains.add(new NCageBrain());
//			brains.add(new NotSoRandomBrain1());
//			ArrayList<Color> colors = new ArrayList<Color>();
//			colors.add(Color.YELLOWGREEN);
//			colors.add(Color.AZURE);
//			double[] probabilitys = {0.1, 0.005, 0.003, 0.003};
//			Game game = new Game(brains, startPositions, colors, field, probabilitys);
//			game.run();
//			
//			if (game.getSnakes().get(0).segments().size() > game.getSnakes().get(1).segments().size()) {
//				winsA++;
//			} else {
//				winsB++;
//			}
//		}
//		System.out.println("NCage vs NotSoRandomBrain1");
//		System.out.println(winsA + " NCage");
//		System.out.println(winsB + " NotSoRandomBrain1");
//		winsA = 0; winsB = 0;
//		
//		for (int i = 0;i < 100;i++) {
//			Point start1 = new Point(2, 2);
//			Point start2 = new Point(27, 17);
//			ArrayList<Point> startPositions = new ArrayList<Point>();
//			startPositions.add(start1);
//			startPositions.add(start2);
//			ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
//			brains.add(new NCageBrain());
//			brains.add(new HorstAI());
//			ArrayList<Color> colors = new ArrayList<Color>();
//			colors.add(Color.YELLOWGREEN);
//			colors.add(Color.AZURE);
//			double[] probabilitys = {0.1, 0.005, 0.003, 0.003};
//			Game game = new Game(brains, startPositions, colors, field, probabilitys);
//			game.run();
//			
//			if (game.getSnakes().get(0).segments().size() > game.getSnakes().get(1).segments().size()) {
//				winsA++;
//			} else {
//				winsB++;
//			}
//		}
//		System.out.println("NCage vs HorstAI");
//		System.out.println(winsA + " NCage");
//		System.out.println(winsB + " HorstAI");
//		winsA = 0; winsB = 0;
//		
		
		
	}
	
	
	//main loop
	public void run() {
		while (playersLeft > 1) {
			//System.out.println(portal.getTTL());
			nextStep();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				System.out.println("Das ist garnicht mal so gut...");
				e.printStackTrace();
			}
		}
	}


	public void nextStep() {
		if (playersLeft > 1) {
			
			//adding apples and stuff
			if (rand.nextDouble() <= probabilitys[0] && field.getApples().size() == 0) {
				Point position = new Point(0,0);
				do {
					position.x = rand.nextInt(field.width());
					position.y = rand.nextInt(field.height());
				} while(field.cell(new Point(position.x, position.y)) != Field.CellType.SPACE);
				field.addApple(new Apple(50, 1, position), position);
			}
			//adding FeatureWall and stuff
			if(rand.nextDouble() <= probabilitys[1] && !field.hasFeatureWall()){
				Point position = new Point(0,0);
				do {
					position.x = rand.nextInt(field.width());
					position.y = rand.nextInt(field.height());
				} while(field.cell(new Point(position.x,position.y)) != Field.CellType.SPACE);
				field.setFeatureWall(position);
			}
			//adding ChangeSnake
			if(rand.nextDouble() <= probabilitys[2] && !field.hasChangeSnake()){
				Point position = new Point(0,0);
				do {
					position.x = rand.nextInt(field.width());
					position.y = rand.nextInt(field.height());
				} while(field.cell(new Point(position.x,position.y)) != Field.CellType.SPACE);
				field.setChangeSnake(position);
			}
			//adding ChangeHeadTail
			if(rand.nextDouble() <= probabilitys[3] && !field.hasChangeHeadTail()){
				Point position = new Point(0,0);
				do {
					position.x = rand.nextInt(field.width());
					position.y = rand.nextInt(field.height());
				} while(field.cell(new Point(position.x,position.y)) != Field.CellType.SPACE);
				field.setChangeHeadTail(position);
			}
			
                        
			//finding next snake which is alive
			Snake snake = snakes.get(currentSnake);
			while(snake.alive() == false) {
				currentSnake++;
			} 
                        
			//portal: checks if portals appear or disapear
                        portal.portalAppeareance(field, snake);
                        
			//moving the current snake
			snake.move();
			
			
			
			//update the field
			Point headPosition = snake.headPosition();
			switch(field.cell(headPosition))
			{
			case SPACE: field.setCell(Field.CellType.SNAKE, headPosition); break;
			case APPLE:
				Apple apple = field.getApple(headPosition);
				apple.apply(snake);
				field.removeApple(headPosition);
				field.setCell(Field.CellType.SNAKE, headPosition);
				break;
			case FEATUREWALL:
				snake.setCanSetWall(true);
				field.removeFeatureWall(headPosition);
				field.setCell(Field.CellType.SNAKE, headPosition);
				break;
			case CHANGESNAKE:
				Snake otherSnake = null;
				for(Snake s : snakes)
					if(s != snake)
						otherSnake = s;
				
				LinkedList<Point> snake1Segments = otherSnake.segments();
				otherSnake.setSegments(snake.segments());
				snake.setSegments(snake1Segments);
				field.removeChangeSnake(headPosition);
				field.setCell(Field.CellType.SNAKE, headPosition);
				break;
			case CHANGEHEADTAIL:
				snake.switchHeadTail();
				field.removeChangeHeadTail(headPosition);
				field.setCell(Field.CellType.SNAKE, headPosition);
				break;
			case PORTAL:
				if(portal.isActive())
					portal.teleportHead(field,snake);
				break;
			default:
				field.setCell(Field.CellType.SNAKE, headPosition);
				snake.kill();
				System.out.println("Schlange gestorben");
				playersLeft--;
			}
                        
                        //portal: prevent portals from beeing eaten
			if(portal.isActive()){
				field.setCell(Field.CellType.PORTAL, portal.getPortal1());
				field.setCell(Field.CellType.PORTAL, portal.getPortal2());
			}

			//drawing of the field and everything
//			field.draw();
			

			//next player
			currentSnake++;
			if (currentSnake == snakes.size()) {
				currentSnake = 0;
			}
		}
	}

}
