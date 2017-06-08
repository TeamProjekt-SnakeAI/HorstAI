package Util;

import java.util.LinkedList;

import Logic.Field;
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
	
	public Point move(Direction dir,Type[][] field) {
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
			newHead.x = field.length-1;
		}
		if (newHead.x == field.length) {
			newHead.x = 0;
		}
		if (newHead.y == -1) {
			newHead.y = field[0].length-1;
		}
		if (newHead.y == field[0].length) {
			newHead.y = 0;
		}
		
		segments.addLast(newHead);
		
		if (grow == 0) { //don't grow, delete tail
			Point rp = segments.removeFirst();
			lastPosition = rp;
			field[rp.x][rp.y] = Type.SPACE;
		} else { //tail isn't deleted, snake grew one field
			grow--;
		}
		
		return newHead;
	}
	public Point undoMove(Direction dir, Type[][] field) {
		segments.removeLast();
		segments.add(lastPosition);
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


}
