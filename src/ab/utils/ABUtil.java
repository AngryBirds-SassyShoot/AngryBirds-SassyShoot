package ab.utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.Vision;

public class ABUtil {
	
	public static int gap = 5; //vision tolerance.
    public static int wood1;
    public static int wood2;
    public static int ice1;
    public static int ice2;
    public static int stone1;
    public static int stone2;
    public static int wood_difference;
    public static int ice_difference;
    public static int stone_difference;
    public static int total_difference;

    private static TrajectoryPlanner tp = new TrajectoryPlanner();

	// If o1 supports o2, return true
	public static boolean isSupport(ABObject o2, ABObject o1)
	{
		if(o2.x == o1.x && o2.y == o1.y && o2.width == o1.width && o2.height == o1.height)
				return false;
		
		int ex_o1 = o1.x + o1.width;
		int ex_o2 = o2.x + o2.width;
		
		int ey_o2 = o2.y + o2.height;
		if(
			(Math.abs(ey_o2 - o1.y) < gap)
			&&  
 			!( o2.x - ex_o1  > gap || o1.x - ex_o2 > gap )
		  )
	        return true;	
		
		return false;
	}
	//Return a link list of ABObjects that support o1 (test by isSupport function ). 
	//objs refers to a list of potential supporters.
	//Empty list will be returned if no such supporters. 
	public static List<ABObject> getSupporters(ABObject o2, List<ABObject> objs)
			{
				List<ABObject> result = new LinkedList<ABObject>();
				//Loop through the potential supporters
		        for(ABObject o1: objs)
		        {
		        	if(isSupport(o2,o1))
		        		result.add(o1);
		        }
		        return result;
			}

	//Return true if the target can be hit by releasing the bird at the specified release point
	public static boolean isReachable(Vision vision, Point target, Shot shot)
	{ 
		//test whether the trajectory can pass the target without considering obstructions
		Point releasePoint = new Point(shot.getX() + shot.getDx(), shot.getY() + shot.getDy()); 
		int traY = tp.getYCoordinate(vision.findSlingshotMBR(), releasePoint, target.x);
		boolean result = true;
		List<Point> points = tp.predictTrajectory(vision.findSlingshotMBR(), releasePoint);		
		for(Point point: points)
		{
		  if(point.x < 840 && point.y < 480 && point.y > 100 && point.x > 400)
			for(ABObject ab: vision.findBlocksMBR())
			{
               // if(ab.type == ABType.Ice || ab.type == ABType.Hill || ab.type == ABType.Stone)
                     {
                    if (
                            ((ab.contains(point) && !ab.contains(target)) || Math.abs(vision.getMBRVision()._scene[point.y][point.x] - 72) < 10)
                                    && point.x < target.x
                            ) {
                 //    System.out.println("jj");
                        return false;

                    }
                     }
			}
		  
		}
		return result;
	}

    public static Point find_first_obstacle(Vision vision, Point target, Shot shot)
    {
        //test whether the trajectory can pass the target without considering obstructions
        Point releasePoint = new Point(shot.getX() + shot.getDx(), shot.getY() + shot.getDy());
        int traY = tp.getYCoordinate(vision.findSlingshotMBR(), releasePoint, target.x);
        boolean result = true;
        List<Point> points = tp.predictTrajectory(vision.findSlingshotMBR(), releasePoint);
        int count = 0;
        Point first_point = new Point();
        first_point.x = 100000;
        first_point.y = 100000;


        for(Point point: points)
        {
            if(point.x < 840 && point.y < 480 && point.y > 100 && point.x > 400)
                for(ABObject ab: vision.findBlocksMBR())
                {
                    if(ab.type == ABType.Stone || ab.type == ABType.Wood || ab.type == ABType.Ice || ab.type == ABType.Pig)
                    {
                        if (
                                ((ab.contains(point) && !ab.contains(target)) || Math.abs(vision.getMBRVision()._scene[point.y][point.x] - 72) < 10)
                                        && point.x < target.x
                                )
                        {
                            if (ab.getCenter().x < first_point.x)
                                first_point = ab.getCenter();
                        }

                    }
                }

        }
        return first_point;
    }


    public static int find_ice_in_trajectory(Vision vision, Point target, Shot shot)
    {
        //test whether the trajectory can pass the target without considering obstructions
        Point releasePoint = new Point(shot.getX() + shot.getDx(), shot.getY() + shot.getDy());
        int traY = tp.getYCoordinate(vision.findSlingshotMBR(), releasePoint, target.x);
        boolean result = true;
        List<Point> points = tp.predictTrajectory(vision.findSlingshotMBR(), releasePoint);
        int count = 0;
        for(Point point: points)
        {
            if(point.x < 840 && point.y < 480 && point.y > 100 && point.x > 400)
                for(ABObject ab: vision.findBlocksMBR())
                {
                    if(ab.type == ABType.Ice)
                    {
                        if (
                                ((ab.contains(point) && !ab.contains(target)) || Math.abs(vision.getMBRVision()._scene[point.y][point.x] - 72) < 10)
                                        && point.x < target.x
                                )
                         count = count +2 ;
                    }
                }

        }
        return count;
    }

    public static int find_wood_in_trajectory(Vision vision, Point target, Shot shot)
    {
        //test whether the trajectory can pass the target without considering obstructions
        Point releasePoint = new Point(shot.getX() + shot.getDx(), shot.getY() + shot.getDy());
        int traY = tp.getYCoordinate(vision.findSlingshotMBR(), releasePoint, target.x);
        boolean result = true;
        List<Point> points = tp.predictTrajectory(vision.findSlingshotMBR(), releasePoint);
        int count = 0;
        for(Point point: points)
        {
            if(point.x < 840 && point.y < 480 && point.y > 100 && point.x > 400)
                for(ABObject ab: vision.findBlocksMBR())
                {
                    if(ab.type == ABType.Wood)
                    {
                        if (
                                ((ab.contains(point) && !ab.contains(target)) || Math.abs(vision.getMBRVision()._scene[point.y][point.x] - 72) < 10)
                                        && point.x < target.x
                                )
                            count ++;
                    }
                }

        }
        return count;
    }

    public static int find_stone_in_trajectory(Vision vision, Point target, Shot shot)
    {
        //test whether the trajectory can pass the target without considering obstructions
        Point releasePoint = new Point(shot.getX() + shot.getDx(), shot.getY() + shot.getDy());
        int traY = tp.getYCoordinate(vision.findSlingshotMBR(), releasePoint, target.x);
        boolean result = true;
        List<Point> points = tp.predictTrajectory(vision.findSlingshotMBR(), releasePoint);
        int count = 0;
        for(Point point: points)
        {
            if(point.x < 840 && point.y < 480 && point.y > 100 && point.x > 400)
                for(ABObject ab: vision.findBlocksMBR())
                {
                    if(ab.type == ABType.Stone)
                    {
                        if (
                                ((ab.contains(point) && !ab.contains(target)) || Math.abs(vision.getMBRVision()._scene[point.y][point.x] - 72) < 10)
                                        && point.x < target.x
                                )
                            count = count +3 ;
                    }
                }

        }
        return count;
    }

    public static int find_hill_in_trajectory(Vision vision, Point target, Shot shot)
    {
        //test whether the trajectory can pass the target without considering obstructions
        Point releasePoint = new Point(shot.getX() + shot.getDx(), shot.getY() + shot.getDy());
        int traY = tp.getYCoordinate(vision.findSlingshotMBR(), releasePoint, target.x);
        boolean result = true;
        List<Point> points = tp.predictTrajectory(vision.findSlingshotMBR(), releasePoint);
        int count = 0;
        for(Point point: points)
        {
            ArrayList<ABObject> obj = new ArrayList<ABObject>();
            obj.addAll(vision.findHills());
           // obj.addAll(vision.findBlocksRealShape());
            if(point.x < 840 && point.y < 480 && point.y > 100 && point.x > 400)
                for(ABObject ab: obj)
                {
                    //if(ab.type == ABType.Hill ||ab.type == ABType.Ground || ab.type == ABType.Unknown)
                    {
                        if (
                                ((ab.contains(point) && !ab.contains(target)) || Math.abs(vision.getMBRVision()._scene[point.y][point.x] - 72) < 10)
                                        && point.x < target.x
                                ) {
                               return 1;
                           // else
                             //   return 0;
                        }
                        //    count = count +30 ;
                    }
                }

        }
        return 0;
    }


    public void obstacles_in(Vision vision, Point target1, Point target2, Shot shot1,Shot shot2)
    {
         wood1 = find_wood_in_trajectory(vision,target1,shot1);
         wood2 = find_wood_in_trajectory(vision,target2,shot2);
         ice1 = find_ice_in_trajectory(vision,target1,shot1);
         ice2 = find_ice_in_trajectory(vision,target2,shot2);
         stone1 = find_stone_in_trajectory(vision,target1,shot1);
         stone2 = find_stone_in_trajectory(vision,target2,shot2);
         wood_difference = wood1-wood2;
         ice_difference = ice1-ice2;
         stone_difference = stone1-stone2;
         total_difference = wood_difference + ice_difference + stone_difference;

    }
    public static boolean is_better_point1(Vision vision, Point target1, Point target2, Shot shot1,Shot shot2,ABType type)
    {
       if(total_difference>0)
        {
           // if(stone_difference>0)
              //  return false;
            /*else if(ice_difference>0 && (ice_difference>(-1*stone_difference)))
                return false;
            else*/
                return false;
        }
        else if(total_difference < 0)
        {
            //if(stone_difference>0)
              //  return false;
           /* else*/
                return true;
        }
        else
        {
            /*if(target1.y>target2.y)
                return true;
            else
                return false;*/
            if(stone_difference + ice_difference >0)
                return false;
            else return true;
        }
    }


}
