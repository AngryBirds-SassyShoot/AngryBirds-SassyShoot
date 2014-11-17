package ab.demo;

/**
 * Created by amisha on 10/12/2014.
 */


import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.*;

import ab.demo.other.ActionRobot;
import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.utils.ABUtil;
import ab.utils.StateUtil;
import ab.vision.ABObject;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import ab.vision.ABShape;
import ab.vision.ABType;


public class ShootingPriority {
    public List<ABObject> blocks;
    public List<ABObject> pigs;
    public List<ABObject> blocks_glass = new LinkedList<ABObject>();
    public List<ABObject> blocks_wood = new LinkedList<ABObject>();
    public ABUtil utility;
    public static ArrayList<ABObject> undesirableObj;

    public ShootingPriority(List<ABObject> _blocks, List<ABObject> _pigs)
    {
        blocks = _blocks;
        pigs = _pigs;
    }
    public int is_BlueBird()
    {
        int min = 100000;
        ABObject left_pig = new ABObject();

        for (ABObject block : blocks)
        {
            if(block.type.equals(ABType.Ice))
            {
                blocks_glass.add(block);
            }
        }

        for (ABObject piggy : pigs)
        {
            Point _tptPig = piggy.getCenter();
            if(_tptPig.x < min)
            {
                min = _tptPig.x;
                left_pig = piggy;
            }

        }
        for (ABObject block : blocks_glass)
        {
            Point _tpt = block.getCenter();
            if(_tpt.x <= left_pig.x && _tpt.y >= left_pig.y )
            {
                return 85;
            }
            else
                continue;
        }

        return 70;
    }
    public int is_YellowBird() {
        for (ABObject block : blocks) {
            if (block.equals(ABType.Wood)) {
                blocks_wood.add(block);
            }
        }
        Point _trpt = null;
        double min = 720;
        ABObject sel_pig = null;
        for (ABObject pig: pigs)
        {
            if(pig.getCenter().getX()<min)
            {
                min = pig.getCenter().getX();
                _trpt = pig.getCenter();
                sel_pig = pig;
            }
        }
        for(ABObject wbood : blocks_wood) {

            if(find_left_block(sel_pig).equals(wbood))
                _trpt = wbood.getCenter();
            return 75;
        }
        for(ABObject wbood : blocks_wood) {

            if(find_lower_block(sel_pig).equals(wbood))
                _trpt = wbood.getCenter();
            return 85;
        }

        return 85;
    }


    public ABObject find_target_pig()
    {
        int y_coordinate= 100000;
        int x_coordinate=100000;
        ABObject temp;
        temp = new ABObject();

        for(ABObject pig: pigs){
            Point _tpt = pig.getCenter();
            if(y_coordinate == _tpt.y && x_coordinate>_tpt.x)
            {
                y_coordinate = _tpt.y;
                x_coordinate = _tpt.x;
                temp = pig;
            }

            if(y_coordinate > _tpt.y)
            {
                y_coordinate = _tpt.y;
                x_coordinate = _tpt.x;
                temp=pig;
            }
        }
        return temp;

    }

    public ABObject find_left_block (ABObject pig)
    {
        double x_distance = 10000;
        ABObject temp = new ABObject ();
        temp = null;
        for(ABObject block: blocks)
        {
            if(pig.getCenter().getX()-block.getCenter().getX() == x_distance)
            {
                if(pig.getCenter().getY()-block.getCenter().getY()<pig.getCenter().getY()-temp.getCenter().getY())
                {
                    x_distance = pig.getCenter().getX()-block.getCenter().getX();
                    temp = block;
                }
            }
            if(pig.getCenter().getX()-block.getCenter().getX()<x_distance)
            {
                x_distance = pig.getCenter().getX()-block.getCenter().getX();
                temp = block;
            }
        }
        return temp;
    }
    public ABObject find_upper_block (ABObject left_block)
    {
        ABObject temp = new ABObject ();
        temp = null;
        for(ABObject block :blocks)
        {
            if(utility.isSupport(block,left_block))
            {
                if(block.getCenter().getX()-left_block.getCenter().getX()>0)
                {
                    temp = block;
                }
            }
        }
        return temp;
    }

    public ABObject find_lower_block(ABObject pig)
    {
        List<ABObject> lower_blocks = new LinkedList<ABObject>();
        lower_blocks = utility.getSupporters(pig,blocks);
        ABObject temp = new ABObject ();
        temp = null;
        double x_coordinate = 100000;
        for(ABObject lower_block:lower_blocks)
        {
            if(lower_block.getCenter().getX()<x_coordinate)
            {
                x_coordinate = lower_block.getCenterX();
                temp = lower_block;
            }
        }
        return temp;
    }

    public ArrayList<Point> find_target_point()
    {
        ArrayList<Point> points = new ArrayList<Point>();
        ABObject temp = new ABObject();
        for(ABObject pig:pigs)
        {
            if(find_lower_block(pig) != null)
            {
                if (find_lower_block(pig).type == ABType.Wood) {
                    Point pt = new Point();
                    pt.setLocation(find_lower_block(pig).getX(), find_lower_block(pig).getY());
                    points.add(pt);
                }
                else
                    points.add(pig.getCenter());
            }
            else
                points.add(pig.getCenter());
        }
        return points;
        // Point pt = new Point();
        // ABObject target_pig = find_target_pig();
     /*   ABObject left_block = find_left_block(target_pig);
        ABObject upper_block = null;
        if(left_block != null)
        {
            upper_block = find_upper_block(left_block);
        }
        ABObject lower_block = find_lower_block(target_pig);
        if(left_block!=null && upper_block!=null)
        {
            pt = upper_block.getLocation();
        }
        else if(left_block!=null && lower_block!=null)
        {
            pt = lower_block.getLocation();
        }
        else if(lower_block!= null)
        {
            pt = lower_block.getLocation();
        }
        else if(left_block!=null)
        {
            pt = left_block.getCenter();
        }
        else
        {*/
        //   pt = target_pig.getCenter();
        //}
        //return pt;
    }
/*  public void setup()
  {
      BufferedImage screenshot = ActionRobot.doScreenShot();
      Vision vision = new Vision(screenshot);
      Rectangle sling = vision.findSlingshotMBR();
      tp = new TrajectoryPlanner();
      Point _tpt = new Point();
      _tpt = priority();
      Shot shot = new Shot();
      int dx,dy;
      Point refPoint = tp.getReferencePoint(sling);
      dx = (int)releasePoint.getX() - refPoint.x;
      dy = (int)releasePoint.getY() - refPoint.y;
      shot = new Shot(refPoint.x, refPoint.y, dx, dy, 0);
  }*/

    public ArrayList<Point> priority()
    {
        /*for(ABObject block: blocks)
        {
            if(block.type.equals(ABType.TNT))
            {
                return block.getCenter();
            }
        }
        for(ABObject block: blocks)
        {
            if( block.shape.equals(ABShape.Poly) && !(undesirableObj.contains(block)))
            {
                undesirableObj.add(block);
                return block.getCenter();
            }
        }*/
        return find_target_point();
    }
}

