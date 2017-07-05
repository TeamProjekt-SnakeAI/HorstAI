package UnitTests;

import static org.junit.Assert.*;

import org.junit.Test;

import Logic.Field;
import Logic.Point;
import Logic.Field.CellType;
import Util.AlphaBeta;
import Util.HamiltonPath;
import Util.Node;
import Util.TempSnake;

public class HamiltonPathTest {

	@Test
	public void testTailRightOverHead() {
		Field f = Field.defaultField(30, 20);
		Point[] snakePos = new Point[13];
		snakePos[0] = new Point(4,4);
		snakePos[1] = new Point(5,4);
		snakePos[2] = new Point(5,5);
		snakePos[3] = new Point(5,6);
		snakePos[4] = new Point(5,7);
		snakePos[5] = new Point(4,7);
		snakePos[6] = new Point(3,7);
		snakePos[7] = new Point(2,7);
		snakePos[8] = new Point(2,6);
		snakePos[9] = new Point(2,5);
		snakePos[10] = new Point(2,4);
		snakePos[11] = new Point(3,4);
		snakePos[12] = new Point(3,5);
		TempSnake snake = new TempSnake(snakePos);
		for(Point p : snake.segments())
			f.setCell(CellType.SNAKE, p);
		HamiltonPath hpath = new HamiltonPath(f);
		Node path = hpath.getMaxPath(snake.headPosition(), f, snake);
		assertEquals("Point [x=4, y=4] <- Point [x=4, y=5] <- Point [x=4, y=6] <- Point [x=3, y=6] <- Point [x=3, y=5]", path.getPath());
	}
	@Test
	public void testTailOverHead() {
		Field f = Field.defaultField(30, 20);
		Point[] snakePos = new Point[19];
		snakePos[0] = new Point(8,6);
		snakePos[1] = new Point(9,6);
		snakePos[2] = new Point(9,7);
		snakePos[3] = new Point(10,7);
		snakePos[4] = new Point(10,8);
		snakePos[5] = new Point(10,9);
		snakePos[6] = new Point(10,10);
		snakePos[7] = new Point(10,11);
		snakePos[8] = new Point(9,11);
		snakePos[9] = new Point(8,11);
		snakePos[10] = new Point(7,11);
		snakePos[11] = new Point(7,10);
		snakePos[12] = new Point(6,10);
		snakePos[13] = new Point(6,9);
		snakePos[14] = new Point(6,8);
		snakePos[15] = new Point(6,7);
		snakePos[16] = new Point(7,7);
		snakePos[17] = new Point(8,7);
		snakePos[18] = new Point(8,8);
		TempSnake snake = new TempSnake(snakePos);
		for(Point p : snake.segments())
			f.setCell(CellType.SNAKE, p);
		HamiltonPath hpath = new HamiltonPath(f);
		Node path = hpath.getMaxPath(snake.headPosition(), f, snake);
		assertEquals("Point [x=9, y=7] <- Point [x=9, y=8] <- Point [x=9, y=9] <- Point [x=9, y=10] <- Point [x=8, y=10] <- Point [x=8, y=9] <- Point [x=7, y=9] <- Point [x=7, y=8] <- Point [x=8, y=8]", path.getPath());
	}
	@Test
	public void testTailNextToHead() {
		Field f = Field.defaultField(30, 20);
		Point[] snakePos = new Point[16];
		snakePos[0] = new Point(8,9);
		snakePos[1] = new Point(9,9);
		snakePos[2] = new Point(9,10);
		snakePos[3] = new Point(8,10);
		snakePos[4] = new Point(7,10);
		snakePos[5] = new Point(6,10);
		snakePos[6] = new Point(6,9);
		snakePos[7] = new Point(6,8);
		snakePos[8] = new Point(6,7);
		snakePos[9] = new Point(7,7);
		snakePos[10] = new Point(8,7);
		snakePos[11] = new Point(9,7);
		snakePos[12] = new Point(9,8);
		snakePos[13] = new Point(6,9);
		snakePos[14] = new Point(6,8);
		snakePos[15] = new Point(8,8);
		TempSnake snake = new TempSnake(snakePos);
		for(Point p : snake.segments())
			f.setCell(CellType.SNAKE, p);
		HamiltonPath hpath = new HamiltonPath(f);
		Node path = hpath.getMaxPath(snake.headPosition(), f, snake);
		assertEquals("Point [x=9, y=7] <- Point [x=9, y=8] <- Point [x=9, y=9] <- Point [x=9, y=10] <- Point [x=8, y=10] <- Point [x=8, y=9] <- Point [x=7, y=9] <- Point [x=7, y=8] <- Point [x=8, y=8]", path.getPath());
	}
}
