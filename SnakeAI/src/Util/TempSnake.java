package Util;

import java.util.LinkedList;

import Logic.Field;
import Logic.Field.CellType;
import Logic.GameInfo;
import Logic.Point;
import Logic.Snake;
import Logic.SnakeBrain;
import Logic.Snake.Direction;
import javafx.scene.paint.Color;

public class TempSnake 
{
	private LinkedList<Point> segments; //snake segments, snake head is last element
	private int grow; //tail of the snake isn't deletet while moving as long as grow is > 0
	private Point lastPosition;
	private boolean alive;
	private String name;
	
	public TempSnake(Point[] points)
	{
		this.segments = new LinkedList<Point>();
		this.grow = 0;
		for(Point p : points)
		{
			segments.add(p);
		}
		this.name = "Test Snake";
		this.alive = true;
	}
	
	public TempSnake(Snake snake, String name) {
		this.segments = new LinkedList<Point>();
		this.grow = 0;
		for(Point p : snake.segments())
		{
			Point temp = new Point(p.x,p.y);
			segments.add(temp);
		}
		this.name = name;
		this.alive = true;
	}
	public TempSnake(TempSnake snake) {
		this.segments = new LinkedList<Point>();
		this.grow = 0;
		for(Point p : snake.segments())
		{
			Point temp = new Point(p.x,p.y);
			segments.add(temp);
		}
		this.name = snake.name;
		this.alive = true;
	}
	
	public TempSnake(Snake snake) {
		this.segments = new LinkedList<Point>();
		this.grow = 0;
		for(Point p : snake.segments())
		{
			Point temp = new Point(p.x,p.y);
			segments.add(temp);
		}
		this.alive = true;
	}
	public void grow(int n) {
		grow += n;
	}
	
	public void move(Direction dir) {
//		System.out.println("Snake: " + name);
//		System.out.println("Segments: " +Arrays.toString(segments.toArray()));
		Point head = segments.getLast();
		
		//calculate new head position
		Point newHead = new Point(head.x, head.y);
		switch(dir) {
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
		
		segments.addLast(newHead);
		
		if (grow == 0) { //don't grow, delete tail
			Point rp = segments.removeFirst();
		} else { //tail isn't deleted, snake grew one field
			grow--;
		}
	}
	public Point move(Direction dir, Field field) {
		Point head = segments.getLast();
		
		//calculate new head position
		Point newHead = new Point(head.x, head.y);
		switch(dir) {
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
			newHead.x = field.width()-1;
		}
		if (newHead.x == field.width()) {
			newHead.x = 0;
		}
		if (newHead.y == -1) {
			newHead.y = field.height()-1;
		}
		if (newHead.y == field.height()) {
			newHead.y = 0;
		}
		
		segments.addLast(newHead);
		
		if (grow == 0) { //don't grow, delete tail
			Point rp = segments.removeFirst();
			field.setCell(CellType.SPACE, rp);
		} else { //tail isn't deleted, snake grew one field
			grow--;
		}
		
		return newHead;
	}
	public Point undoMove(Direction dir, Type[][] field) {
//		System.out.println("RemoveLast: " + Arrays.toString(segments.toArray()));
		segments.removeLast();
		segments.add(lastPosition);
//		System.out.println("Removed: " + Arrays.toString(segments.toArray()));
		return lastPosition;
	}
	
	public Point headPosition() {
		return segments.getLast();
	}
	
	public LinkedList<Point> segments() {
		return segments;
	}
	
	public boolean alive() {
		return alive;
	}
	
	public void kill() {
		alive = false;
	}

	public String getName() {
		return name;
	}
}
