package ure.math;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ure.areas.UArea;
import ure.actors.UActor;
import ure.things.UThing;
import ure.ui.UCamera;
import ure.ui.ULight;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * UPath implements A* pathfinding.  To use, create an instance of UPath and call .nextStep() to find
 * the next step toward a destination from a source.
 *
 * TODO: bugs and inefficiencies
 *
 */
public class UPath {

    private static Log log = LogFactory.getLog(UPath.class);

    static class Nodelist extends TreeSet<Node> {
        public Nodelist() {
            super(new Node(0,0));
        }
    }

    static class Node implements Comparator<Node> {
        int x, y;
        Node parent;
        double g, h, f;

        public Node(int thex, int they) {
            x = thex;
            y = they;
        }

        public Node(int thex, int they, Node theparent) {
            x = thex;
            y = they;
            parent = theparent;
        }
        public void recalc(int goalx, int goaly) {
            if (parent != null)
                g = parent.g + 1;
            h = Math.abs(x - goalx) + Math.abs(y - goaly);
            f = g + h;
        }
        public double getval() {
            return f;
        }
        public int compare(Node n1, Node n2) {
            if (n1.getval() > n2.getval())
                return 1;
            return -1;
        }
    }

    public static Node NodeIfOpen(UArea area, int x, int y, Node parent, UActor actor) {
        if (x < 0 || y < 0 || x >= area.xsize || y >= area.ysize)
            return null;
        if (area.willAcceptThing((UThing)actor, x, y))
            return new Node(x,y,parent);
        return null;
    }

    /**
     * Utility method: Calculate the manhattan distance (4-direction travel distance) between two points.
     */
    public static int mdist(int x1, int y1, int x2, int y2) {
        return Math.abs(x2-x1) + Math.abs(y2-y1);
    }

    public static int mdistToRect(int px, int py, int rx, int ry, int rwidth, int rheight) {
        int cx = Math.max(Math.min(px,rx+rwidth),rx);
        int cy = Math.max(Math.min(py,ry+rheight),ry);
        return mdist(px,py,cx,cy);
    }

    public static int distsqr(int x1, int y1, int x2, int y2) {
        return (int)Math.pow(x2-x1,2) + (int)Math.pow(y2-y1,2);
    }
    public static int dist(int x1, int y1, int x2, int y2) {
        return (int)Math.sqrt(Math.pow(x2-x1,2) + (int)Math.pow(y2-y1,2));
    }

    /**
     * Utility method: Can actor see from point 1 to point 2 in area?
     *
     * TODO: export this to a bresenham util func w lambdas
     */
    public static boolean canSee(int x0, int y0, int x1, int y1, UArea area, UActor actor) {
        int dx = Math.abs(x1-x0); int dy = Math.abs(y1-y0);
        int sx = x0<x1 ? 1 : -1;
        int sy = y0<y1 ? 1 : -1;
        int err = dx-dy;
        int e2;
        int x = x0;
        int y = y0;
        while (true) {
            if (x==x1 && y==y1) break;
            if (!actor.canSeeThrough(area.cellAt(x,y))) return false;
            e2 = 2*err;
            if (e2 > -1 * dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        return true;
    }

    public static ArrayList<int[]> line(int x0, int y0, int x1, int y1) {
        ArrayList<int[]> points = new ArrayList<>();
        int dx = Math.abs(x1-x0); int dy = Math.abs(y1-y0);
        int sx = x0<x1 ? 1 : -1;
        int sy = y0<y1 ? 1 : -1;
        int err = dx-dy;
        int e2;
        int x = x0;
        int y = y0;
        while (true) {
            points.add(new int[]{x,y});
            if (x==x1 && y==y1) break;
            e2 = 2*err;
            if (e2 > -1 * dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        return points;
    }

    /**
     * Does a line from point 1 to point 2 intersect the given rect?
     * Rect at point 2 doesn't count.
     */
    public static boolean intersectsRect(int x0, int y0, int x1, int y1, int rx, int ry, int rw, int rh) {
        int dx = Math.abs(x1-x0); int dy = Math.abs(y1-y0);
        int sx = x0<x1 ? 1 : -1;
        int sy = y0<y1 ? 1 : -1;
        int err = dx-dy;
        int e2;
        int x = x0;
        int y = y0;
        while (true) {
            if (x==x1 && y==y1) break;
            if (x >= rx && x < rx+rw && y >= ry && y < ry+rh) return true;
            e2 = 2*err;
            if (e2 > -1 * dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        return false;
    }

    /**
     * Perform A* pathfinding between the two given points and return the next step from the first point
     * to reach the second.
     * @param actor The actor to use for deciding passability.  Can be null.
     * @param range No steps further than this from the start will be considered.
     * @return (x,y) walk delta of next step
     *
     */
    public static int[] nextStep(UArea area, int x1, int y1, int x2, int y2, UActor actor, int range) {
        if (mdist(x1,y1,x2,y2) > range)
            return null;
        Nodelist openlist = new Nodelist();
        Nodelist closedlist = new Nodelist();
        Node start = new Node(x1,y1);
        openlist.add(start);
        int stepcount = 0;
        while (!openlist.isEmpty() && stepcount < 2000) {
            stepcount++;
            Node q = openlist.pollFirst();
            openlist.remove(q);
            Node[] steps = new Node[8];
            steps[0] = NodeIfOpen(area, q.x-1, q.y, q, actor);
            steps[1] = NodeIfOpen(area, q.x+1, q.y, q, actor);
            steps[2] = NodeIfOpen(area, q.x, q.y-1, q, actor);
            steps[3] = NodeIfOpen(area, q.x, q.y+1, q, actor);
            steps[4] = NodeIfOpen(area, q.x-1,q.y-1, q, actor);
            steps[5] = NodeIfOpen(area, q.x+1,q.y+1, q, actor);
            steps[6] = NodeIfOpen(area, q.x-1,q.y+1,q,actor);
            steps[7] = NodeIfOpen(area, q.x+1,q.y-1,q,actor);
            for (int i=0;i<8;i++) {
                Node step = steps[i];
                if (step != null) {
                    if (step.x == x2 && step.y == y2) { // FOUND IT
                        while (step.parent != start) {
                            step = step.parent;
                        }
                        log.debug("found path in " + Integer.toString(stepcount) + " steps");
                        log.debug("walk " + Integer.toString(step.x - x1) + "," + Integer.toString(step.y - y1));

                        return new int[]{step.x, step.y};
                    }
                    step.recalc(x2,y2);
                    boolean skipstep = false;
                    for (Node o : openlist) {
                        if ((o.x == step.x) && (o.y == step.y) && (o.f < step.f)) {
                            skipstep = true;
                        }
                    }
                    for (Node c : closedlist) {
                        if ((c.x == step.x) && (c.y == step.y) && (c.f < step.f)) {
                            skipstep = true;
                        }
                    }
                    if (mdist(x1,y1,step.x,step.y) > range)
                        skipstep = true;
                    if (!skipstep) {
                        openlist.add(step);
                    }
                }
            }
            closedlist.add(q);
        }
        log.debug("failed path in max stepcount");
        return null;
    }

    public static int[][] steps(UArea area, int x1, int y1, int x2, int y2, UActor actor, int range) {
        if (mdist(x1,y1,x2,y2) > range)
            return null;
        Nodelist openlist = new Nodelist();
        Nodelist closedlist = new Nodelist();
        Node start = new Node(x1,y1);
        openlist.add(start);
        int stepcount = 0;
        while (!openlist.isEmpty() && stepcount < 2000) {
            stepcount++;
            Node q = openlist.pollFirst();
            openlist.remove(q);
            Node[] steps = new Node[8];
            steps[0] = NodeIfOpen(area, q.x-1, q.y, q, actor);
            steps[1] = NodeIfOpen(area, q.x+1, q.y, q, actor);
            steps[2] = NodeIfOpen(area, q.x, q.y-1, q, actor);
            steps[3] = NodeIfOpen(area, q.x, q.y+1, q, actor);
            steps[4] = NodeIfOpen(area, q.x-1,q.y-1, q, actor);
            steps[5] = NodeIfOpen(area, q.x+1,q.y+1, q, actor);
            steps[6] = NodeIfOpen(area, q.x-1,q.y+1,q,actor);
            steps[7] = NodeIfOpen(area, q.x+1,q.y-1,q,actor);
            for (int i=0;i<8;i++) {
                Node step = steps[i];
                if (step != null) {
                    if (step.x == x2 && step.y == y2) { // FOUND IT
                        Node countFrom = step;
                        int pathLength = 0;
                        while (countFrom.parent != start) {
                            countFrom = countFrom.parent;
                            pathLength++;
                        }
                        int[][] path = new int[pathLength][2];
                        pathLength--;
                        while (step.parent != start) {
                            step = step.parent;
                            path[pathLength][0] = step.x;
                            path[pathLength][1] = step.y;
                            pathLength--;
                        }
                        return path;
                    }
                    step.recalc(x2,y2);
                    boolean skipstep = false;
                    for (Node o : openlist) {
                        if ((o.x == step.x) && (o.y == step.y) && (o.f < step.f)) {
                            skipstep = true;
                        }
                    }
                    for (Node c : closedlist) {
                        if ((c.x == step.x) && (c.y == step.y) && (c.f < step.f)) {
                            skipstep = true;
                        }
                    }
                    if (mdist(x1,y1,step.x,step.y) > range)
                        skipstep = true;
                    if (!skipstep) {
                        openlist.add(step);
                    }
                }
            }
            closedlist.add(q);
        }
        log.debug("failed path in max stepcount");
        return null;
    }
}
