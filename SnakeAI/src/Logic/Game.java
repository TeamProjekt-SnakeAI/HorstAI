/*
 * Stores the current state of the game, implements main logic main loop
 * Author: Thomas Stüber
 * */

package Logic;
import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;
import Brains.HorstAI;
import Brains.RandomBrain;


public class Game {
	private ArrayList<Snake> snakes;
	private Field field;
	private Random rand;
	private double appleProbability; //probability per move that an apple spawns 
	private int playersLeft; //is decreased every time a player dies
	private int currentSnake;
	
	//MYCODE
	public ArrayList<Snake> getSnakes() {
		return snakes;
	}

	public Field getField() {
		return field;
	}

	public Game(ArrayList<SnakeBrain> brains, ArrayList<Point> startPositions, ArrayList<Color> colors, Field field, double appleProbability) {
		this.field = field;
		currentSnake = 0;
		GameInfo gameInfo = new GameInfo(this);
		snakes = new ArrayList<Snake>();
		playersLeft = brains.size();
		
		//adding the snakes
		for (int i = 0;i < brains.size();i++) {
			addSnake(new Snake(startPositions.get(i),gameInfo, brains.get(i), colors.get(i)), startPositions.get(i));
		}
		
		rand = new Random();
		this.appleProbability = appleProbability;
		
	}
	
	//add a snake to the game
	public void addSnake(Snake snake, Point start) {
		snakes.add(snake);
		field.setCell(Field.CellType.SNAKE, start);
	}

	public static void main(String[] args) {
		Field field = Field.defaultField(30, 20);
		field.addApple(new Apple(50, 1, new Point(1,2)), new Point(1,2));
		field.addApple(new Apple(50, 1, new Point(3,2)), new Point(3,2));
		field.addApple(new Apple(50, 1, new Point(2,1)), new Point(2,1));
		field.addApple(new Apple(50, 1, new Point(2,3)), new Point(2,3));
		
		Point start1 = new Point(2, 2);
		Point start2 = new Point(27, 17);
		ArrayList<Point> startPositions = new ArrayList<Point>();
		startPositions.add(start1);
		startPositions.add(start2);
		ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
		brains.add(new RandomBrain());
		brains.add(new RandomBrain());
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(Color.YELLOWGREEN);
		colors.add(Color.AZURE);
		Game game = new Game(brains, startPositions, colors, field, 0.1);
		game.run();
	}
	
	//main loop
	public void run() {
		while (playersLeft > 1) {
			nextStep();
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				System.out.println("Das ist garnicht mal so gut...");
//				e.printStackTrace();
//			}
		}
	}


	public void nextStep() {
		if (playersLeft > 1) {

			//adding apples and stuff
			if (rand.nextDouble() <= appleProbability && field.getApples().size() == 0) {
				Point position = new Point(0,0);
				do {
					position.x = rand.nextInt(field.width());
					position.y = rand.nextInt(field.height());
				} while(field.cell(new Point(position.x, position.y)) != Field.CellType.SPACE);
				field.addApple(new Apple(50, 1, position), position);
			}

			//finding next snake which is alive
			Snake snake = snakes.get(currentSnake);
			while(snake.alive() == false) {
				currentSnake++;
			}

			//moving the current snake
			snake.move();

			//update the field
			Point headPosition = snake.headPosition();
			if (field.cell(headPosition) == Field.CellType.SPACE) {
				field.setCell(Field.CellType.SNAKE, headPosition);
			} else if (field.cell(headPosition) == Field.CellType.APPLE) { //apple is eaten
//				System.out.println(headPosition);
				Apple apple = field.getApple(headPosition);
				apple.apply(snake);
				field.removeApple(headPosition);
				field.setCell(Field.CellType.SNAKE, headPosition);
			} else { //snake hit itself or the wall
				field.setCell(Field.CellType.SNAKE, headPosition);
				snake.kill();
				playersLeft--;
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