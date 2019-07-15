package de.kjosu.neatdebug.util;

public class Point {

	public double x;
	public double y;

	public Point() {

	}

	public Point(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public double distance(final Point p) {
		return distance(p.x, p.y);
	}

	public double distance(final double x, final double y) {
		return Point.distance(this.x, this.y, x, y);
	}

	public static double distance(final double x1, final double y1, final double x2, final double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	@Override
	public String toString() {
		return String.format("Point { %.2f, %.2f }", x, y);
	}
}
