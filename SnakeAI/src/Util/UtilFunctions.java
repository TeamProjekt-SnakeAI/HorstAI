package Util;

import java.util.List;

import Logic.Field;
import Logic.Point;
import Logic.Snake.Direction;

public final class UtilFunctions {
	public static Direction getDirection(Point a, Point b) {
		if (a.x + 1 == b.x && a.y == b.y)
			return Direction.RIGHT;
		if (a.x - 1 == b.x && a.y == b.y)
			return Direction.LEFT;
		if (a.x == b.x && a.y + 1 == b.y)
			return Direction.DOWN;
		if (a.x == b.x && a.y - 1 == b.y)
			return Direction.UP;
		return null;
	}
	public static Node getNodeFromClosedList(Point p, List<Node> closedList) {
		for (Node n : closedList)
			if (n.getActual().equals(p))
				return n;

		return null;
	}
	public static int getDistance(Point a, Point b) {
		return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
	}
	public static Field getFieldCopy(Field f)
	{
		Field copy = Field.defaultField(f.width(), f.height());
		for(int x=0;x<f.width();x++)
			for(int y=0;y<f.height();y++)
			{
				Point p = new Point(x,y);
				copy.setCell(f.cell(p), p);
			}
		return copy;
	}
}
