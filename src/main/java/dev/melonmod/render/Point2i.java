package dev.melonmod.render;

public class Point2i {
    public int x;
    public int y;

    public Point2i(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Point2i subtract(int i, int j) {
        return new Point2i(x-i, y-j);
    }
    public Point2i subtract(Point2i p) {
        return new Point2i(x-p.x, y-p.y);
    }

    public Point2i add(int i, int j) {
        return new Point2i(x+i, y+j);
    }
    public Point2i add(Point2i p) {
        return new Point2i(x+p.x, y+p.y);
    }

    public Point2i multiply(Point2d p) {
        return new Point2i((int) (x*p.x), (int) (y*p.y));
    }
    public Point2i multiply(double dx, double dy) {
        return new Point2i((int) (x*dx), (int) (y*dy));
    }
}
