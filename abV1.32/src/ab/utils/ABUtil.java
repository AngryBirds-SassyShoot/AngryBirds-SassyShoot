package ab.utils;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import ab.demo.other.Shot;
import ab.planner.TrajectoryPlanner;
import ab.vision.ABObject;
import ab.vision.ABType;
import ab.vision.Vision;

public class ABUtil {
	
	public static int gap = 5; //vision tolerance.
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
		if (Math.abs(traY - target.y) > 100)
		{	
			//System.out.println(Math.abs(traY - target.y));
			return false;
		}
		boolean result = true;
		List<Point> points = tp.predictTrajectory(vision.findSlingshotMBR(), releasePoint);		
		for(Point point: points)
		{
		  if(point.x < 840 && point.y < 480 && point.y > 100 && point.x > 400)
			for(ABObject ab: vision.findBlocksMBR())
			{
                if(ab.type == ABType.Ice || ab.type == ABType.Hill)
                     {
                    if (
                            ((ab.contains(point) && !ab.contains(target)) || Math.abs(vision.getMBRVision()._scene[point.y][point.x] - 72) < 10)
                                    && point.x < target.x
                            )
                        return false;
                     }
			}
		  
		}
		return result;
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
                         count ++;
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
                            count ++;
                    }
                }

        }
        return count;
    }
    public static boolean is_better_point1(Vision vision, Point target1, Point target2, Shot shot1,Shot shot2)
    {
    int wood1 = find_wood_in_trajectory(vision,target1,shot1);
    int wood2 = find_wood_in_trajectory(vision,target2,shot2);
    int ice1 = find_ice_in_trajectory(vision,target1,shot1);
    int ice2 = find_ice_in_trajectory(vision,target2,shot2);
    int stone1 = find_stone_in_trajectory(vision,target1,shot1);
    int stone2 = find_stone_in_trajectory(vision,target2,shot2);
    int wood_difference = wood1-wood2;
    int ice_difference = ice1-ice2;
    int stone_difference = stone1-stone2;
    int total_difference = wood_difference + ice_difference + stone_difference;
        if(total_difference>0)
        {
            if(stone_difference>0)
                return false;
            else if(ice_difference>0 && (ice_difference>(-1*stone_difference)))
                return false;
            else
                return true;
        }
        else if(total_difference < 0)
        {
            if(stone_difference>0)
                return false;
            else
                return true;
        }
        else
        {
            if(target1.y>target2.y)
                return true;
            else
                return false;
        }
    }


}
