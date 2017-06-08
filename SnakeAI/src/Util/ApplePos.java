package Util;

import java.util.Comparator;

import Logic.Point;

public class ApplePos implements Comparable<ApplePos>{
	private int distance;
	private Point pos;
	
	public ApplePos(Point pos,int distance) {
		this.distance = distance;
		this.pos = pos;
	}
	public int getDistance() {
		return distance;
	}

	public Point getPos() {
		return pos;
	}
	@Override
	public int compareTo(ApplePos o) {
		// TODO Auto-generated method stub
		if(this.getDistance() > o.getDistance())
			return 1;
		else if(this.getDistance() < o.getDistance())
			return -1;
		return 0;
	}
	@Override
	public String toString() {
		return "[" + distance + "]";
	}
	
}
