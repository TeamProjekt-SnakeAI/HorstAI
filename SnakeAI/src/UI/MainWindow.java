package UI;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import Brains.RandomBrain;
import Logic.Apple;
import Logic.Field;
import Logic.Field.CellType;
import Logic.Game;
import Logic.Point;
import Logic.Snake;
import Logic.SnakeBrain;
import PrototypKIs.BrainMaster;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
 
public class MainWindow extends Application {
	private int width;
	private int height;
	private int cellWidth;
	private Canvas canvas;
	private Game game;
	private Random rnd = new Random();
	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
    	//TODO: read this from command line arguments
    	width = 30;
    	height = 20;
    	cellWidth = 30;
    	
    	Field field = Field.defaultField(30, 20);
		field.addApple(new Apple(50, 1, new Point(1,2)), new Point(1,2));
		
		Point start1 = new Point(2, 2);
		Point start2 = new Point(27, 17);
		ArrayList<Point> startPositions = new ArrayList<Point>();
		startPositions.add(start1);
		startPositions.add(start2);
		ArrayList<SnakeBrain> brains = new ArrayList<SnakeBrain>();
		brains.add(new BrainMaster());
		brains.add(new RandomBrain());
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(Color.YELLOWGREEN);
		colors.add(Color.BLUEVIOLET);
		double[] probabilitys = {1, 0.005, 1, 1};
		game = new Game(brains, startPositions, colors, field, probabilitys);
		//game.run();Apple
		
		//move intervall of the snakes
		//TODO: add mode with "every snake gets as much time as needed"
		Timeline timeline = new Timeline(new KeyFrame(
		        Duration.millis(10), new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent arg0) {
						game.nextStep();
						gameUpdate();
					}
		        	
		        }));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
    	
        primaryStage.setTitle("Super Ultra Deluxe Snake 3000");
        
        canvas = new Canvas(cellWidth*width, cellWidth*height);
        gameUpdate();
        ScrollPane root = new ScrollPane();
        root.setMaxWidth(width*cellWidth);
        root.setMaxHeight(height*cellWidth);
        
        root.setContent(canvas);
        primaryStage.setScene(new Scene(root, 800, 800));
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);
        primaryStage.show();
    }
    
    public void gameUpdate() {
    	GraphicsContext gc = canvas.getGraphicsContext2D();
    	gc.setFill(Color.GREEN);
    	gc.setLineWidth(5);
        gc.fillRect(0, 0, width*cellWidth, height*cellWidth);
        
        Field f = game.getField();
        for (int x = 0;x < f.width();x++) {
        	for (int y = 0;y < f.height();y++) {
            	CellType cell = f.cell(new Point(x,y));
            	switch(cell) {
				case APPLE:
					gc.setFill(Color.GREEN);
					gc.fillRect(x*cellWidth, y*cellWidth, (x+1)*cellWidth, (y+1)*cellWidth);
					gc.setFill(Color.RED);
					gc.fillRoundRect(x*cellWidth+6, y*cellWidth+6, cellWidth-12, cellWidth-12,10,10);
					break;
				case PORTAL:
					gc.setStroke(Color.CYAN);
					gc.strokeOval(x*cellWidth, y*cellWidth, 10, 20);
					break;
				case CHANGESNAKE:
					int xPos = x*cellWidth;
					int yPos = y*cellWidth;
					double[] xPoints = {xPos+7,xPos+7,xPos+15,xPos+23,xPos+23,xPos+15};
					double[] yPoints = {yPos+9,yPos+17,yPos+25,yPos+17,yPos+9,yPos+1};
					gc.setFill(Color.YELLOW);
					gc.fillPolygon(xPoints, yPoints, 6);
					gc.setStroke(Color.ORANGE);
					gc.setLineWidth(2);
					gc.strokePolygon(xPoints, yPoints, 6);
					break;
				case CHANGEHEADTAIL:
					xPos = x*cellWidth;
					yPos = y*cellWidth;
					double[] xPoints2 = {xPos+7,xPos+7,xPos+15,xPos+23,xPos+23,xPos+15};
					double[] yPoints2 = {yPos+9,yPos+17,yPos+25,yPos+17,yPos+9,yPos+1};
					gc.setFill(Color.DARKBLUE);
					gc.fillPolygon(xPoints2, yPoints2, 6);
					gc.setStroke(Color.BLUE);
					gc.strokePolygon(xPoints2, yPoints2, 6);
					gc.setLineWidth(5);
					break;
				case SNAKE:
					gc.setFill(Color.GREEN);
					gc.fillRect(x*cellWidth, y*cellWidth, (x+1)*cellWidth, (y+1)*cellWidth);
					gc.setFill(Color.BLACK);
					gc.fillRoundRect(x*cellWidth+1, y*cellWidth+1, cellWidth-2, cellWidth-2,10,10);
					break;
				case SPACE:
					gc.setFill(Color.GREEN);
					gc.fillRect(x*cellWidth, y*cellWidth, (x+1)*cellWidth, (y+1)*cellWidth);
					break;
				case WALL:
					gc.setFill(Color.DARKGREEN);
					gc.fillRect(x*cellWidth, y*cellWidth, (x+1)*cellWidth, (y+1)*cellWidth);
					break;
				case FEATUREWALL: //paints the feature "Wall" on the canvas as a wall-pixel-art
					gc.setFill(Color.CORAL);
					gc.fillRect(x*cellWidth, y*cellWidth, (x+1)*cellWidth,(y+1)* cellWidth);
					gc.setFill(Color.LIGHTGREY);
					gc.fillRect(x*cellWidth, y*cellWidth+2, cellWidth, 2);
					gc.fillRect(x*cellWidth, y*cellWidth+8, cellWidth, 2);
					gc.fillRect(x*cellWidth, y*cellWidth+14, cellWidth, 2);
					gc.fillRect(x*cellWidth, y*cellWidth+20, cellWidth, 2);
					gc.fillRect(x*cellWidth, y*cellWidth+26, cellWidth, 2);
					gc.setFill(Color.SILVER);
					gc.fillRect(x*cellWidth+5, y*cellWidth, 1, 3);
					gc.fillRect(x*cellWidth+17, y*cellWidth, 1, 3);
					gc.fillRect(x*cellWidth+29, y*cellWidth, 1, 3);
					gc.fillRect(x*cellWidth+11, y*cellWidth+3, 1, 6);
					gc.fillRect(x*cellWidth+23, y*cellWidth+3, 1, 6);
					gc.fillRect(x*cellWidth+5, y*cellWidth+9, 1, 6);
					gc.fillRect(x*cellWidth+17, y*cellWidth+9, 1, 6);
					gc.fillRect(x*cellWidth+29, y*cellWidth+9, 1, 6);
					gc.fillRect(x*cellWidth+11, y*cellWidth+15, 1, 6);
					gc.fillRect(x*cellWidth+23, y*cellWidth+15, 1, 6);
					gc.fillRect(x*cellWidth+5, y*cellWidth+21, 1, 6);
					gc.fillRect(x*cellWidth+17, y*cellWidth+21, 1, 6);
					gc.fillRect(x*cellWidth+29, y*cellWidth+21, 1, 6);
					gc.fillRect(x*cellWidth+11, y*cellWidth+27, 1, 3);
					gc.fillRect(x*cellWidth+23, y*cellWidth+27, 1, 3);
					break;
				default:
					break;
            	
            	}
            }
        }
        gc.setLineWidth(3);
        
        ArrayList<Snake> snakes = game.getSnakes();
        for (Snake snake : snakes) {
        	gc.setStroke(snake.color());
        	LinkedList<Point> segments = snake.segments();
        	if (segments.size() == 1) {
        		gc.strokeOval(segments.get(0).x * cellWidth+7, segments.get(0).y * cellWidth+7, cellWidth-14, cellWidth-14);
        	} else {
        		for (int i = 0;i < segments.size()-1;i++) {
        			Point currentSegment = segments.get(i);
        			Point nextSegment = segments.get(i+1);
        			Snake.Direction currentToNext = relativ(currentSegment, nextSegment);
        			if (currentToNext != null) {
        				switch(currentToNext) {
						case DOWN:
							gc.strokeLine(currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+(cellWidth/2), currentSegment.x*cellWidth+(cellWidth/2), (currentSegment.y+1)*cellWidth-5);
							
							break;
						case LEFT:
							gc.strokeLine(currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+(cellWidth/2), currentSegment.x*cellWidth+5, currentSegment.y*cellWidth+(cellWidth/2));
							break;
						case RIGHT:
							gc.strokeLine(currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+(cellWidth/2), (currentSegment.x+1)*cellWidth-5, currentSegment.y*cellWidth+(cellWidth/2));
							
							break;
						case UP:
							gc.strokeLine(currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+(cellWidth/2), currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+5);
							
							break;
						default:
							break;
        				}
        			}
        			
        			if (i > 0) {
        				Point predSegment = segments.get(i-1);
            			Snake.Direction predToCurrent = relativ(currentSegment, predSegment);
            			if (predToCurrent != null) {
            				switch(predToCurrent) {
    						case DOWN:
    							gc.strokeLine(currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+(cellWidth/2), currentSegment.x*cellWidth+(cellWidth/2), (currentSegment.y+1)*cellWidth-5);
    							
    							break;
    						case LEFT:
    							gc.strokeLine(currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+(cellWidth/2), currentSegment.x*cellWidth+5, currentSegment.y*cellWidth+(cellWidth/2));
    							break;
    						case RIGHT:
    							gc.strokeLine(currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+(cellWidth/2), (currentSegment.x+1)*cellWidth-5, currentSegment.y*cellWidth+(cellWidth/2));
    							
    							break;
    						case UP:
    							gc.strokeLine(currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+(cellWidth/2), currentSegment.x*cellWidth+(cellWidth/2), currentSegment.y*cellWidth+5);
    							
    							break;
    						default:
    							break;
            				}
            			}
        			}
        		}
        		gc.strokeOval(segments.getLast().x * cellWidth+7, segments.getLast().y * cellWidth+7, cellWidth-14, cellWidth-14);
        	}
        }
    }
    
    
    //TODO: add cases where snake leaves field at one side and enters at another side
    private Snake.Direction relativ(Point p1, Point p2) {
    	if (p1.x == p2.x && p1.y == p2.y-1) {
    		return Snake.Direction.DOWN;
    	} else if (p1.x == p2.x && p1.y == p2.y+1) {
    		return Snake.Direction.UP;
    	} else if (p1.x == p2.x-1 && p1.y == p2.y) {
    		return Snake.Direction.RIGHT;
    	} else if (p1.x == p2.x+1 && p1.y == p2.y) {
    		return Snake.Direction.LEFT;
    	} 
    	return null;
    }
}