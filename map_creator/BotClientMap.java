package map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import map.BotClientMap.Wall.WallType;

public class BotClientMap {
  public int gridSize;
  public Pose startPose;
  public ArrayList<Wall> walls;
  public ArrayList<Stack> stacks;
  public HomeBase base;

  public BotClientMap() {
    walls = new ArrayList<>();
    stacks = new ArrayList<>();
    base = new HomeBase();
  }

  public void load(String s) {
    String[] parts = s.split(":");
    int i = 0;
    // this.gridSize = parseGridSize(parts[i++]);
    this.startPose = parsePose(parts[i++]);

    for (; i < parts.length; i++) {
      walls.add(parseWall(parts[i]));
    }
  }

  // private int parseGridSize(String s) {
  //   return Integer.valueOf(s);
  // }

  private Pose parsePose(String s) {
    String[] parts = s.split(",");
    assert(parts[0] == "L");
    return new Pose(Integer.valueOf(parts[1].trim()), Integer.valueOf(parts[2].trim()));
  }

  private Wall parseWall(String s) {
    String[] parts = s.split(",");

    Point start = new Point(Integer.valueOf(parts[0].trim()),
                            Integer.valueOf(parts[1].trim()));
    Point end = new Point(Integer.valueOf(parts[2].trim()),
                          Integer.valueOf(parts[3].trim()));
    Wall.WallType type = Wall.WallType.values()[Wall.WallTypeShort.valueOf(
        parts[4]).ordinal()];

    return new Wall(start, end, type);
  }

  public static class Point {
    public final int x;
    public final int y;

    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String toString() {
      return String.format("(%d, %d)", x, y);
    }

    public String toBotClientString() {
      return String.format("%d,%d", x, y);
    }
  }

  public static class Pose extends Point {

    public Pose(int x, int y) {
      super(x, y);
    }

    @Override
    public String toString() {
      return String.format("(%d,%d)", x, y);
    }

    public String toBotClientString() {
      return String.format("L,%d,%d", x, y);
    }
  }

  public static class Wall {
    enum WallTypeShort {
      W, O, P
    };

    enum WallType {
      WALL, OPPONENT, PLATFORM
    };

    public final WallType type;
    public final Point start;
    public final Point end;

    public Wall(Point start, Point end, WallType type) {
      this.start = start;
      this.end = end;
      this.type = type;
    }

    @Override
    public String toString() {
      return String.format("Wall: %s\t[%s - %s]", type, start, end);
    }

    public String toBotClientString() {
      return String.format("%s,%s,%s",
                           WallTypeShort.values()[type.ordinal()],
                           start.toBotClientString(),
                           end.toBotClientString());
    }
  }

  public static class HomeBase {
    public static ArrayList<Wall> bounds;

    public HomeBase() {
      bounds = new ArrayList<Wall>();
    }

    @Override
    public String toString() {
      String out = String.format("Home base: (%d) ", bounds.size());
      Point last = null;
      for (Wall w : bounds) {
        if (last != null) {
          assert(w.start.x == last.x);
          assert(w.start.y == last.y);
        }
        out += w.start;
        out += ",";
        last = w.end;
      }
      assert(last.x == bounds.get(0).start.x);
      assert(last.y == bounds.get(0).start.y);
      return out;
    }

    public String toBotClientString() {
      String out = String.format("H,%d", bounds.size());
      Point last = null;
      for (Wall w : bounds) {
        if (last != null) {
          assert(w.start.x == last.x);
          assert(w.start.y == last.y);
        }
        out += "," + w.start.toBotClientString();
        last = w.end;
      }
      assert(last.x == bounds.get(0).start.x);
      assert(last.y == bounds.get(0).start.y);
      return out;
    }
  }

  public static class Stack {
    enum BlockTypeShort {
      R, G
    };

    enum BlockType {
      RED, GREEN
    };

    public final Point loc;
    public BlockType c1;
    public BlockType c2;
    public BlockType c3;

    public Stack(Point loc, BlockType c1, BlockType c2, BlockType c3) {
      this.loc = loc;
      this.c1 = c1;
      this.c2 = c2;
      this.c3 = c3;
    }

    @Override
    public String toString() {
      return String.format("Wall: [%s]\t%s,%s,%s", loc, c1, c2, c3);
    }

    public String toBotClientString() {
      return String.format("S,%s,%s,%s,%s",
                           loc.toBotClientString(),
                           BlockTypeShort.values()[c1.ordinal()],
                           BlockTypeShort.values()[c2.ordinal()],
                           BlockTypeShort.values()[c3.ordinal()]);
    }
  }

  @Override
  public String toString() {
    // String mapString = String.format("Grid Size: %.2f\n", gridSize);
    String mapString = "";
    mapString += "Pose: " + this.startPose.toString();
    for (Wall w : walls)
      mapString += "\n" + w.toString();
    for (Stack s : stacks)
      mapString += "\n" + s.toString();
    return mapString;
  }

  public String toBotClientString() {
    // String mapString = String.format("%.2f:", gridSize);
    String mapString = "";
    mapString += startPose.toBotClientString() + "\n";
    mapString += base.toBotClientString() + "\n";
    for (Wall w : walls)
      mapString += w.toBotClientString() + "\n";
    for (Stack s : stacks) 
      mapString += s.toBotClientString() + "\n";
    return mapString;
  }

  public static BotClientMap getDefaultMap() {
    // String mapString = "22.00:4.00,6.00,2.36:1.00,3.00,1.00,4.00,N:1.00,4.00,0.00,5.00,N:0.00,5.00,0.00,6.00,N:0.00,6.00,1.00,6.00,N:1.00,6.00,1.00,7.00,N:1.00,7.00,1.00,8.00,N:1.00,8.00,2.00,8.00,R:2.00,8.00,4.00,8.00,S:4.00,8.00,5.00,7.00,N:5.00,7.00,6.00,6.00,N:6.00,6.00,5.00,5.00,N:5.00,5.00,6.00,4.00,N:6.00,4.00,5.00,3.00,R:5.00,3.00,4.00,3.00,N:4.00,3.00,4.00,4.00,N:4.00,4.00,4.00,5.00,N:4.00,5.00,3.00,4.00,N:3.00,4.00,3.00,3.00,N:3.00,3.00,2.00,3.00,N:2.00,3.00,1.00,3.00,R:";
    String mapString = "L,5,5\n";

    BotClientMap m = new BotClientMap();
    m.load(mapString);
    return m;
  }

  public void drawMap() {
    JFrame jf = new JFrame();
    jf.setContentPane(new MapPainter());
    jf.setSize(800, 800);
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jf.setBackground(Color.white);
    jf.setVisible(true);
  }

  private class MapPainter extends JComponent {
    private Point start = null;
    private int size = 50;
    private int xOff = 1 * size;
    private int yOff = 15 * size;

    public MapPainter() {
      this.setFocusable(true);
      this.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            System.out.println(e.getPoint());
            Point p = toPoint(e.getPoint());
            if (start == null) {
              start = p;
            } else {
              Wall w = new Wall(start, p, WallType.WALL);
              walls.add(w);
              start = null;
            }

            repaint();
          }
        });

      this.addKeyListener(new KeyAdapter() {
          @Override
          public void keyPressed(KeyEvent e) {
            super.keyPressed(e);

            WallType t = null;
            Stack s = null;
            switch (e.getKeyChar()) {
              case 'w':
                t = WallType.WALL;
                break;
              case 'o':
                t = WallType.OPPONENT;
                break;
              case 'l':
                t = WallType.PLATFORM;
                break;
              case 'h':
                if (walls.size() > 0) {
                  Wall b = walls.remove(walls.size() - 1);
                  base.bounds.add(b);
                }
                // case 's':
                // 	t = WallType.SILO;
                // 	break;
                // case 'r':
                // 	t = WallType.REACTOR;
                // 	break;
              case 'd':
                if (walls.size() > 0)
                  walls.remove(walls.size() - 1);
                break;
              case 'x':
                if (stacks.size() > 0)
                  stacks.remove(stacks.size() - 1);
                break;
              case 'C':
                walls.clear();
                break;
              case 'p':
                System.out.println(toBotClientString());
                break;
              case 's':
                // Wall w = walls.remove(walls.size() - 1);
                s = new Stack(start, Stack.BlockType.GREEN,
                              Stack.BlockType.GREEN, Stack.BlockType.GREEN);
                stacks.add(s);
                start = null;
                break;
              case '1':
                if (stacks.size() > 0) {
                  Stack last = stacks.get(stacks.size() - 1);
                  if (last.c1 == Stack.BlockType.RED)
                    last.c1 = Stack.BlockType.GREEN;
                  else
                    last.c1 = Stack.BlockType.RED;
                }
                break;
              case '2':
                if (stacks.size() > 0) {
                  Stack last = stacks.get(stacks.size() - 1);
                  if (last.c2 == Stack.BlockType.RED)
                    last.c2 = Stack.BlockType.GREEN;
                  else
                    last.c2 = Stack.BlockType.RED;
                }
                break;
              case '3':
                if (stacks.size() > 0) {
                  Stack last = stacks.get(stacks.size() - 1);
                  if (last.c3 == Stack.BlockType.RED)
                    last.c3 = Stack.BlockType.GREEN;
                  else
                    last.c3 = Stack.BlockType.RED;
                }
                break;
              case 'i':
                Wall w1 = walls.remove(walls.size() - 1);
                // int theta = Math.atan2(w.end.y - w.start.y, w.end.x - w.start.x);
                startPose = new Pose(w1.start.x, w1.start.y);

            }

            if (t != null) {
              Wall w = walls.remove(walls.size() - 1);
              walls.add(new Wall(w.start, w.end, t));
            }

            repaint();
          }
        });
    }

    @Override
    public void paint(Graphics g) {
      super.paint(g);
      ((Graphics2D) g).setStroke(new BasicStroke(3));

      g.setColor(Color.gray);
      for (int x = 0; x < 20; x++) {
        for (int y = 0; y < 20; y++) {
          g.fillOval(toPixelX(x) - 1, toPixelY(y) - 1, 3, 3);
        }
      }

      if (start != null) {
        g.setColor(Color.magenta);
        g.fillOval(toPixelX(start.x) - 3, toPixelY(start.y) - 3, 7, 7);
      }

      g.setColor(Color.magenta);
      for (Wall b : base.bounds) {
        g.drawLine(toPixelX(b.start.x), toPixelY(b.start.y),
                   toPixelX(b.end.x), toPixelY(b.end.y));
      }

      Color[] wallColors = new Color[] { Color.black, Color.yellow };
      for (Wall w : walls) {
        g.setColor(wallColors[w.type.ordinal()]);
        g.drawLine(toPixelX(w.start.x), toPixelY(w.start.y),
                   toPixelX(w.end.x), toPixelY(w.end.y));
      }

      Color[] stackColors = new Color[] { Color.red, Color.green };
      for (Stack s : stacks) {
        g.setColor(stackColors[s.c3.ordinal()]);
        g.fillOval(toPixelX(s.loc.x)+3, toPixelY(s.loc.y)-3, 7, 7);
        g.setColor(stackColors[s.c2.ordinal()]);
        g.fillOval(toPixelX(s.loc.x), toPixelY(s.loc.y)-3, 7, 7);
        g.setColor(stackColors[s.c1.ordinal()]);
        g.fillOval(toPixelX(s.loc.x)-3, toPixelY(s.loc.y)-3, 7, 7);
      }

      g.setColor(Color.black);
      g.fillOval(toPixelX((double)startPose.x - .25),
                 toPixelY((double)startPose.y + .25), size / 2, size / 2);

      // int DX = Math.cos(startPose.theta) * size / 2.0;
      // int DY = Math.sin(startPose.theta) * size / 2.0;
      // g.drawLine(toPixelX(startPose.x), toPixelY(startPose.y),
      // 		(int) (toPixelX(startPose.x) + DX),
      // 		(int) (toPixelY(startPose.y) - DY));
    }

    public int toPixelX(double x) {
      return (int) (x * size) + xOff;
    }

    public int toPixelY(double y) {
      return (int) (-y * size) + yOff;
    }

    public Point toPoint(java.awt.Point mousePoint) {
      double x = (mousePoint.x - xOff) / (double) size;
      double y = -(mousePoint.y - yOff) / (double) size;
			
      return new Point((int)Math.round(x), (int)Math.round(y));
    }
  }

  public static void main(String[] args) {
    BotClientMap map = getDefaultMap();
    map.drawMap();
  }
}
