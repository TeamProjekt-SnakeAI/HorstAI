package UnitTests;

import static org.junit.Assert.*;

import org.junit.Test;

import Logic.Field;
import Logic.Field.CellType;
import Logic.Point;
import Util.Node;
import Util.Pathfinding;
public class PathfindingTest {

	@Test
	public void testOneMove() {
		Field f = Field.defaultField(30, 20);
		Pathfinding find = new Pathfinding(f);
		Node node = find.getMinPath(new Point(2,2), new Point(3,2), f,new Point(2,2));
		assertEquals("Point [x=3, y=2] <- Point [x=2, y=2]", node.getPath());
	}
	@Test
	public void testLeftMoves() {
		Field f = Field.defaultField(30, 20);
		Pathfinding find = new Pathfinding(f);
		Node node = find.getMinPath(new Point(10,10), new Point(2,10), f,new Point(10,10));
		assertEquals("Point [x=2, y=10] <- Point [x=3, y=10] <- Point [x=4, y=10] <- Point [x=5, y=10] <- Point [x=6, y=10] <- Point [x=7, y=10] <- Point [x=8, y=10] <- Point [x=9, y=10] <- Point [x=10, y=10]", node.getPath());
	}
	@Test
	public void testRightMoves() {
		Field f = Field.defaultField(30, 20);
		Pathfinding find = new Pathfinding(f);
		Node node = find.getMinPath(new Point(2,10), new Point(10,10), f,new Point(2,10));
		assertEquals("Point [x=10, y=10] <- Point [x=9, y=10] <- Point [x=8, y=10] <- Point [x=7, y=10] <- Point [x=6, y=10] <- Point [x=5, y=10] <- Point [x=4, y=10] <- Point [x=3, y=10] <- Point [x=2, y=10]", node.getPath());
	}
	@Test
	public void testDownMoves() {
		Field f = Field.defaultField(30, 20);
		Pathfinding find = new Pathfinding(f);
		Node node = find.getMinPath(new Point(10,2), new Point(10,10), f,new Point(10,2));
		assertEquals("Point [x=10, y=10] <- Point [x=10, y=9] <- Point [x=10, y=8] <- Point [x=10, y=7] <- Point [x=10, y=6] <- Point [x=10, y=5] <- Point [x=10, y=4] <- Point [x=10, y=3] <- Point [x=10, y=2]", node.getPath());
	}
	@Test
	public void testUpMoves() {
		Field f = Field.defaultField(30, 20);
		Pathfinding find = new Pathfinding(f);
		Node node = find.getMinPath(new Point(10,10), new Point(10,2), f,new Point(10,10));
		assertEquals("Point [x=10, y=2] <- Point [x=10, y=3] <- Point [x=10, y=4] <- Point [x=10, y=5] <- Point [x=10, y=6] <- Point [x=10, y=7] <- Point [x=10, y=8] <- Point [x=10, y=9] <- Point [x=10, y=10]", node.getPath());
	}
	@Test
	public void testNoPath(){
		Field f = Field.defaultField(30, 20);
		for(int x=5;x<13;x++)
		{
			for(int y=5;y<13;y++)
			{
				if(x==5 || x==12)
				{
					f.setCell(CellType.WALL, new Point(x,y));
				}
				if(y==5 || y==12)
				{
					f.setCell(CellType.WALL, new Point(x,y));
				}
			}
		}
		Pathfinding find = new Pathfinding(f);
		Node node = find.getMinPath(new Point(10,10), new Point(10,2), f,new Point(10,10));
		assertEquals(null,node);
	}

}