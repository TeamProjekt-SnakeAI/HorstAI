package Util;

import Logic.Point;

public class Node{
	private Node from;
	private Point actual;
	private int gCosts;
	private int hCosts;
	
	public Node (Node from, Point actual, int heuristik, int moveCost)
	{
		this.from = from;
		this.actual = actual;
		hCosts = heuristik;
		gCosts = moveCost;
	}
	public int getFCost()
	{
		return gCosts + hCosts;
	}
	public int getGCosts()
	{
		return gCosts;
	}
	public int getHCosts()
	{
		return hCosts;
	}
	public Node getFrom() {
		return from;
	}
	public void setFrom(Node from) {
		this.from = from;
	}
	public Point getActual() {
		return actual;
	}
	public void setActual(Point actual) {
		this.actual = actual;
	}
	public void updateNode(Node from, int newGCost)
	{
		this.from = from;
		gCosts = newGCost;
	}
	@Override
	public String toString()
	{
		String ret = (from != null? "("+from.actual.x +":"+from.actual.y+")":"");
		return "["+ret+" -> ("+actual.x+":"+actual.y+"): G: " +gCosts+" F: " + getFCost()+"]";
	}
	public String getPath()
	{
		Node t = this;
		String ret = "";
		while(t != null)
		{
			ret+= t.getActual()+" <- ";
			t = t.getFrom();
		}
		return ret.substring(0, ret.length()-4);
	}
}