/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys,  Andrew Wang, Peng Zhang
 ** All rights reserved.
 **This work is licensed under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 **To view a copy of this license, visit http://www.gnu.org/licenses/
 *****************************************************************************/
package ab.demo;

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
import ab.vision.ABType;
import ab.vision.GameStateExtractor.GameState;
import ab.vision.Vision;
import ab.demo.ShootingPriority;
import ab.vision.VisionMBR;

public class NaiveAgent implements Runnable {

    private ActionRobot aRobot;
    private Random randomGenerator;
    public int currentLevel = 10 ;
    public static int time_limit = 12;
    private Map<Integer,Integer> scores = new LinkedHashMap<Integer,Integer>();
    TrajectoryPlanner tp;
    private boolean firstShot;
    private Point prevTarget;
    private Vision vision;
    private ABType type;

    private ShootingPriority shoot_priority;

    // a standalone implementation of the Naive Agent
    public NaiveAgent() {

        aRobot = new ActionRobot();
        tp = new TrajectoryPlanner();
        prevTarget = null;
        firstShot = true;
        randomGenerator = new Random();
        // --- go to the Poached Eggs episode level selection page ---
        ActionRobot.GoFromMainMenuToLevelSelection();

    }


    // run the client
    public void run() {

        aRobot.loadLevel(currentLevel);
        while (true) {
            GameState state = solve();
            if (state == GameState.WON) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int score = StateUtil.getScore(ActionRobot.proxy);
                if(!scores.containsKey(currentLevel))
                    scores.put(currentLevel, score);
                else
                {
                    if(scores.get(currentLevel) < score)
                        scores.put(currentLevel, score);
                }
                int totalScore = 0;
                for(Integer key: scores.keySet()){

                    totalScore += scores.get(key);
                    System.out.println(" Level " + key
                            + " Score: " + scores.get(key) + " ");
                }
                System.out.println("Total Score: " + totalScore);
                aRobot.loadLevel(++currentLevel);
                // make a new trajectory planner whenever a new level is entered
                tp = new TrajectoryPlanner();

                // first shot on this level, try high shot first
                firstShot = true;
            } else if (state == GameState.LOST) {
                System.out.println("Restart");
                aRobot.restartLevel();
            } else if (state == GameState.LEVEL_SELECTION) {
                System.out
                        .println("Unexpected level selection page, go to the last current level : "
                                + currentLevel);
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.MAIN_MENU) {
                System.out
                        .println("Unexpected main menu page, go to the last current level : "
                                + currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            } else if (state == GameState.EPISODE_MENU) {
                System.out
                        .println("Unexpected episode menu page, go to the last current level : "
                                + currentLevel);
                ActionRobot.GoFromMainMenuToLevelSelection();
                aRobot.loadLevel(currentLevel);
            }

        }

    }

    private double distance(Point p1, Point p2) {
        return Math
                .sqrt((double) ((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
                        * (p1.y - p2.y)));
    }

    public GameState solve() {

        // capture Image
        BufferedImage screenshot = ActionRobot.doScreenShot();

        // process image
        Vision vision = new Vision(screenshot);

        // find the slingshot
        Rectangle sling = vision.findSlingshotMBR();

        // confirm the slingshot
        while (sling == null && aRobot.getState() == GameState.PLAYING) {
            System.out
                    .println("No slingshot detected. Please remove pop up or zoom out");
            ActionRobot.fullyZoomOut();
            screenshot = ActionRobot.doScreenShot();
            vision = new Vision(screenshot);
            sling = vision.findSlingshotMBR();
        }
        // get all the pigs
        List<ABObject> pigs = vision.findPigsMBR();
        List<ABObject> blocks = vision.findBlocksRealShape();

        shoot_priority = new ShootingPriority(blocks, pigs);

        GameState state = aRobot.getState();

        // if there is a sling, then play, otherwise just skip.
        if (sling != null) {

            if (!pigs.isEmpty()) {
                Point releasePoint1 = null;
                Point releasePoint = null;
                Shot shot1 = new Shot();
                Shot shot = new Shot();
                int dx1, dy1, dx2, dy2, dx, dy;
                dx = 0;

                // random pick up a pig
                //ABObject pig = pigs.get(randomGenerator.nextInt(pigs.size()));

                ABUtil utility = new ABUtil();

                ArrayList<Point> pt = new ArrayList<Point>();

                pt = shoot_priority.priority();     // Getting all the points where the bird can hit.


                    ArrayList<Integer> wood_between = new ArrayList<Integer>();   //for wood in between the trajectory
                    ArrayList<Integer> stone_between = new ArrayList<Integer>();  //for stone in between the trajectory
                    ArrayList<Integer> ice_between = new ArrayList<Integer>();    //for ice in between the trajectory
                    ArrayList<Integer> total_between = new ArrayList<Integer>();  //total obstacles
                    ArrayList<Integer> hill_between = new ArrayList<Integer>();    //hills in between the trajectory
                    ArrayList<Shot> shots = new ArrayList<Shot>(); //for the shots for each candidate point
                    ArrayList<Point> rp = new ArrayList<Point>(); //
                     List<ABObject> blocks_ice = new LinkedList<ABObject>();
                if(aRobot.getBirdTypeOnSling().equals(ABType.BlueBird))
                { int tap_blue=0;
                    Point _tpt = new Point();
                    int y = 100000;
                    int temp;
                    int up = 0;

                    for(ABObject piggy: pigs)
                    {
                        if(piggy.getCenter().y < y)
                        {
                            _tpt = piggy.getCenter();
                            System.out.print("piggy  " + _tpt);
                            y = _tpt.y;
                            System.out.println("y  "+ y);
                        }
                    }

                    for(ABObject blck : blocks)
                    {
                        if(blck.type.equals(ABType.Ice))
                        {
                            blocks_ice.add(blck);
                            if(blck.getCenter().y > _tpt.y)
                                up = blck.getCenter().y;


                        }


                    }

                    ArrayList<Point> pts = tp.estimateLaunchPoint(sling,_tpt);
                    Point refPoint = tp.getReferencePoint(sling);


                    tap_blue = 80;


                    if (pts.size() >= 1)
                        releasePoint = pts.get(1);
                    else
                        releasePoint = pts.get(0);

                    //    Point obstacle = utility.find_first_obstacle(vision,_tpt,shot);

                    refPoint = tp.getReferencePoint(sling);

                    tap_blue = 80;
                    int tapTime1 = tp.getTapTime(sling,releasePoint,_tpt,tap_blue);

                    dx1 = (int) releasePoint.getX() - refPoint.x;
                    dy1 = (int) releasePoint.getY() - refPoint.y;
                    shot = new Shot(refPoint.x, refPoint.y, dx1, dy1, 0, tapTime1);
                    temp = utility.find_ice_in_trajectory(vision,_tpt,shot);



                    int temp1 = utility.find_stone_in_trajectory(vision,_tpt,shot);

                    if(temp > 0)
                    {
                        if (pts.size() >= 1) {
                            releasePoint = pts.get(1);

                        }
                        else if(temp1 > 0)
                            releasePoint = pts.get(0);
                        else
                            releasePoint = tp.findReleasePoint(sling, Math.PI/4);
                        if(up > 0)
                            releasePoint = pts.get(0);


                    }




                    if(releasePoint == pts.get(0))
                        tap_blue = 82;
                    else if(releasePoint == pts.get(1))
                    {
                        tap_blue = 75;
                    }


                    tapTime1 = tp.getTapTime(sling,releasePoint,_tpt,tap_blue);
                    shot = new Shot(refPoint.x, refPoint.y, dx1, dy1, 0, tapTime1);



                } else if(aRobot.getBirdTypeOnSling().equals(ABType.RedBird)) {


                         for (Point _tpt1 : pt) {

                             // estimate the trajectory
                             ArrayList<Point> pts1 = tp.estimateLaunchPoint(sling, _tpt1);

                             // do a high shot when entering a level to find an accurate velocity
                             if (pts1.size() > 1) {
                                 releasePoint1 = pts1.get(0);
                             } else if (pts1.size() == 1)
                                 releasePoint1 = pts1.get(0);
                             else if (pts1.isEmpty()) {
                                 System.out.println("No release point found for the target");
                                 System.out.println("Try a shot with 45 degree");
                                 releasePoint1 = tp.findReleasePoint(sling, Math.PI / 4);
                             }

                             // Get the reference point
                             Point refPoint = tp.getReferencePoint(sling);


                             //Calculate the tapping time according the bird type
                             if (releasePoint1 != null) {
                                 double releaseAngle1 = tp.getReleaseAngle(sling,
                                         releasePoint1);
                                 int tapInterval = 0;
                                 switch (aRobot.getBirdTypeOnSling()) {

                                     case RedBird:
                                         tapInterval = 0;
                                         break;               // start of trajectory
                                     case YellowBird:
                                         tapInterval = 70; //shoot_priority.is_YellowBird();
                                         //tapInterval = 65 + randomGenerator.nextInt(25);
                                         break; // 65-90% of the way
                                     case WhiteBird:
                                         tapInterval = 70 + randomGenerator.nextInt(20);
                                         break; // 70-90% of the way
                                     case BlackBird:
                                         tapInterval = 70 + randomGenerator.nextInt(20);
                                         break; // 70-90% of the way
                                     case BlueBird:
                                         tapInterval = shoot_priority.is_BlueBird();
                                         //  tapInterval = 65 + randomGenerator.nextInt(20);
                                         break; // 65-85% of the way
                                     default:
                                         tapInterval = 60;
                                 }

                                 int tapTime1 = tp.getTapTime(sling, releasePoint1, _tpt1, tapInterval);
                                 dx1 = (int) releasePoint1.getX() - refPoint.x;
                                 dy1 = (int) releasePoint1.getY() - refPoint.y;
                                 shot1 = new Shot(refPoint.x, refPoint.y, dx1, dy1, 0, tapTime1);


                                 int wood, stone, ice, hill;
                                 wood = utility.find_wood_in_trajectory(vision, _tpt1, shot1);
                                 ice = utility.find_ice_in_trajectory(vision, _tpt1, shot1);
                                 stone = utility.find_stone_in_trajectory(vision, _tpt1, shot1);
                                 hill = utility.find_hill_in_trajectory(vision, _tpt1, shot1);

                                 //hill_between.add(hill);
                                 wood_between.add(wood);
                                 stone_between.add(stone);
                                 ice_between.add(ice);

                                 if (hill == 1)   //if there is hill present in trajectory follow the trajectory with angle greater than 45 degree.
                                 {
                                     System.out.println("yo");
                                     if (pts1.size() > 1) {
                                         releasePoint1 = pts1.get(1);
                                         tapTime1 = tp.getTapTime(sling, releasePoint1, _tpt1, tapInterval);
                                         dx1 = (int) releasePoint1.getX() - refPoint.x;
                                         dy1 = (int) releasePoint1.getY() - refPoint.y;
                                         shot1 = new Shot(refPoint.x, refPoint.y, dx1, dy1, 0, tapTime1);
                                     }

                                 }

                                 total_between.add(wood + ice + stone + hill);
                                 rp.add(releasePoint1);

                                 shots.add(shot1);

                             } else {
                                 System.err.println("No Release Point Found");
                                 return state;
                             }

                         }
                         int i = 0;
                         int p=0;
                         int min = 10000;
                         for (Integer k : total_between) {
                             if (aRobot.getBirdTypeOnSling().equals(ABType.RedBird)) {
                                 System.out.println(shots.size());
                                 if(k <min)
                                 {
                                     p=i;
                                     min=k;
                                     shot = shots.get(i);
                                     releasePoint=rp.get(i);
                                 }
                                 else if(k==min)
                                 {
                                     if(wood_between.get(i)<wood_between.get(p))
                                     {
                                         p=i;
                                         min=k;
                                         shot = shots.get(i);
                                         System.out.println(shot.getX());System.out.println(shot.getY());
                                         releasePoint=rp.get(i);
                                     }
                                     else if(stone_between.get(i)<stone_between.get(p))
                                     {
                                         p=i;
                                         min=k;
                                         shot = shots.get(i);
                                         System.out.println(shot.getX());System.out.println(shot.getY());
                                         releasePoint=rp.get(i);

                                     }
                                     else if(ice_between.get(i)<ice_between.get(p))
                                     {
                                         p=i;
                                         min=k;
                                         shot = shots.get(i);
                                         System.out.println(shot.getX());System.out.println(shot.getY());
                                         releasePoint=rp.get(i);

                                     }

                                 }
                                 i++;
                             }
                         }
                         if ((prevTarget != null && distance(prevTarget, pt.get(p)) < 10) || !utility.isReachable(vision,pt.get(p),shot)) {
                             double _angle = randomGenerator.nextDouble() * Math.PI * 2;
                             pt.get(p).x = pt.get(p).x + (int) (Math.cos(_angle) * 10);
                             pt.get(p).y = pt.get(p).y + (int) (Math.sin(_angle) * 10);
                             System.out.println("Randomly changing to " + pt.get(p));
                             ArrayList<Point> pts1 = tp.estimateLaunchPoint(sling, pt.get(p));
                             releasePoint1 = pts1.get(0);
                             int tapTime1 = tp.getTapTime(sling, releasePoint1, pt.get(p), 0);
                             shot.setDx((int) releasePoint1.getX() - shot.getX());
                             shot.setDy((int) releasePoint1.getY() - shot.getY());
                             shot.setT_tap(tapTime1);
                         }

                         prevTarget = new Point(pt.get(p).x, pt.get(p).y);

                     }
                else if(aRobot.getBirdTypeOnSling().equals(ABType.YellowBird))
                {
                    LinkedList<ABObject> blocks_wood= new LinkedList<ABObject>();
                    blocks_wood = new LinkedList<ABObject>();

                    for (ABObject block : blocks) {
                        if (block.equals(ABType.Wood)) {
                            blocks_wood.add(block);
                        }
                    }
                    Point _trpt = null;
                    double min = 100000;
                    ABObject sel_pig = null;
                    for (ABObject pig: pigs)
                    {
                        if(pig.getCenter().getX()<=min)
                        {
                            if(pig.getCenter().x < min) {
                                min = pig.getCenter().x;
                                sel_pig = pig;
                                System.out.println("Checking x coordinates for pig"+ sel_pig.getCenter().getX());
                            }
                            else
                            {
                                if(pig.getCenter().y < sel_pig.getCenter().y)
                                {
                                    sel_pig = pig;
                                }
                            }
                        }
                    }
                    for(ABObject wbood : blocks_wood) {

                        if(shoot_priority.find_left_block(sel_pig).equals(wbood))
                        {
                            _trpt = wbood.getCenter();
                            break;
                        }

                    }
                      /*  for(ABObject wbood : blocks_wood) {

                            if(shoot_priority.find_lower_block(sel_pig).equals(wbood) && _trpt == null)
                            {
                                _trpt = wbood.getCenter();
                            }

                        }
                        if(_trpt == null && shoot_priority.find_left_block(sel_pig)!=null)
                        {
                            _trpt = shoot_priority.find_left_block(sel_pig).getCenter();
                        }
                        else if(shoot_priority.find_left_block(sel_pig)==null)
                        {
                            _trpt = sel_pig.getCenter();
                        }*/

                    _trpt = sel_pig.getCenter();

                    System.out.print(_trpt);
                    ArrayList<Point> pts = tp.estimateLaunchPoint(sling,_trpt);
                    Point refPoint = tp.getReferencePoint(sling);


                    if (pts.size() >= 1 )
                        releasePoint = pts.get(0);
                    else
                        releasePoint = tp.findReleasePoint(sling, Math.PI / 4);

                    int tapTime = tp.getTapTime(sling, releasePoint, _trpt, 75);
                    dx1 = (int) releasePoint.getX() - refPoint.x;
                    dy1 = (int) releasePoint.getY() - refPoint.y;
                    shot = new Shot(refPoint.x, refPoint.y, dx1, dy1, 0, tapTime);
                    if(utility.find_stone_in_trajectory(vision,_trpt,shot)>0)
                    {
                        if(pts.size()>1) {
                            releasePoint = pts.get(1);
                            tapTime = tp.getTapTime(sling,releasePoint,_trpt,85);
                            dx1 = (int) releasePoint.getX() - refPoint.x;
                            dy1 = (int) releasePoint.getY() - refPoint.y;
                            shot = new Shot(refPoint.x,refPoint.y,dx1,dy1,85);
                        }
                    }
                    //shot = new Shot(refPoint.x, refPoint.y, dx1, dy1, 0, 75);
                }






            // check whether the slingshot is changed. the change of the slingshot indicates a change in the scale.
                {
                    ActionRobot.fullyZoomOut();
                    screenshot = ActionRobot.doScreenShot();
                    vision = new Vision(screenshot);
                    Rectangle _sling = vision.findSlingshotMBR();
                    if(_sling != null)
                    {
                        double scale_diff = Math.pow((sling.width - _sling.width),2) +  Math.pow((sling.height - _sling.height),2);
                        if(scale_diff < 25)
                        {
                            if(shot.getDx() < 0)
                            {
                                aRobot.cshoot(shot);
                                state = aRobot.getState();
                                System.out.println("tap" + shot.getT_tap() + "    ");
                                if ( state == GameState.PLAYING )
                                {
                                    System.out.println("hey");
                                    screenshot = ActionRobot.doScreenShot();
                                    vision = new Vision(screenshot);
                                    List<Point> traj = vision.findTrajPoints();
                                    tp.adjustTrajectory(traj, sling, releasePoint);
                                    firstShot = false;
                                }
                            }
                        }
                        else
                            System.out.println("Scale is changed, can not execute the shot, will re-segement the image");
                    }
                    else
                        System.out.println("no sling detected, can not execute the shot, will re-segement the image");
                }

            }

        }
            return state;
        }


    public static void main(String args[]) {

        NaiveAgent na = new NaiveAgent();
        if (args.length > 0)
            na.currentLevel = Integer.parseInt(args[0]);
        na.run();

    }
}
