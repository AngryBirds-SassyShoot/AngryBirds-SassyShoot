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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    public int currentLevel = 13;
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

    public GameState solve()
    {

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

        shoot_priority = new ShootingPriority(blocks,pigs);

        GameState state = aRobot.getState();

        // if there is a sling, then play, otherwise just skip.
        if (sling != null) {

            if (!pigs.isEmpty()) {

                Point releasePoint1 = null;
                Point releasePoint2 = null;
                Point releasePoint = null;
                Shot shot1 = new Shot();
                Shot shot2 = new Shot();
                Shot shot = new Shot();
                int dx1,dy1,dx2,dy2,dx,dy;
                dx = 0;
                // random pick up a pig
                //ABObject pig = pigs.get(randomGenerator.nextInt(pigs.size()));
                ABUtil utility = new ABUtil();

                ArrayList<Point> pt = new ArrayList<Point>();
                pt  = shoot_priority.priority();

                if(pt.size() == 1)
                {
                    Point _tpt = pt.get(0);
                    ArrayList<Point> pts = tp.estimateLaunchPoint(sling, _tpt);
                    if (pts.size() > 1) {
                        releasePoint = pts.get(0);
                        if(aRobot.getBirdTypeOnSling()== ABType.BlueBird)
                            releasePoint = pts.get(1);
                    } else if (pts.size() == 1)
                        releasePoint = pts.get(0);
                    else if (pts.isEmpty()) {
                        System.out.println("No release point found for the target");
                        System.out.println("Try a shot with 45 degree");
                        releasePoint = tp.findReleasePoint(sling, Math.PI / 4);
                    }
                    if(aRobot.getBirdTypeOnSling().equals(ABType.BlueBird))
                        releasePoint = pt.get(1);

                    Point refPoint = tp.getReferencePoint(sling);
                    double releaseAngle = tp.getReleaseAngle(sling,
                            releasePoint);
                    int tapInterval = 0;
                    switch (aRobot.getBirdTypeOnSling()) {

                        case RedBird:
                            tapInterval = 0;
                            break;               // start of trajectory
                        case YellowBird:
                            tapInterval = shoot_priority.is_YellowBird();
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

                    int tapTime = tp.getTapTime(sling, releasePoint, _tpt, tapInterval);
                    dx = (int) releasePoint.getX() - refPoint.x;
                    dy = (int) releasePoint.getY() - refPoint.y;
                    shot = new Shot(refPoint.x, refPoint.y, dx, dy, 0, tapTime);

                }
                else {
                    Point _tpt1 = new Point();
                    Point _tpt2 = new Point();
                    while(pt.size()!=1)
                    {
                        _tpt1 = pt.get(0);
                        _tpt2 = pt.get(1);
                        System.out.println("size" + pt.size());

                        // estimate the trajectory
                        ArrayList<Point> pts1 = tp.estimateLaunchPoint(sling, _tpt1);
                        ArrayList<Point> pts2 = tp.estimateLaunchPoint(sling, _tpt2);

                        // do a high shot when entering a level to find an accurate velocity
                        if (pts1.size() > 1) {
                            releasePoint1 = pts1.get(0);
                            if(aRobot.getBirdTypeOnSling()== ABType.BlueBird)
                                releasePoint1 = pts1.get(1);
                        } else if (pts1.size() == 1)
                            releasePoint1 = pts1.get(0);
                        else if (pts1.isEmpty()) {
                            System.out.println("No release point found for the target");
                            System.out.println("Try a shot with 45 degree");
                            releasePoint1 = tp.findReleasePoint(sling, Math.PI / 4);
                        }

                        if (pts2.size() > 1) {
                            releasePoint2 = pts2.get(0);
                            if(aRobot.getBirdTypeOnSling() == ABType.BlueBird)
                                releasePoint2 = pts2.get(1);
                        } else if (pts2.size() == 1)
                            releasePoint2 = pts2.get(0);
                        else if (pts2.isEmpty()) {
                            System.out.println("No release point found for the target");
                            System.out.println("Try a shot with 45 degree");
                            releasePoint2 = tp.findReleasePoint(sling, Math.PI / 4);
                        }

                        // Get the reference point
                        Point refPoint = tp.getReferencePoint(sling);


                        //Calculate the tapping time according the bird type
                        if (releasePoint1 != null && releasePoint2 != null) {
                            double releaseAngle1 = tp.getReleaseAngle(sling,
                                    releasePoint1);
                            double releaseAngle2 = tp.getReleaseAngle(sling,
                                    releasePoint2);
                            int tapInterval = 0;
                            switch (aRobot.getBirdTypeOnSling()) {

                                case RedBird:
                                    tapInterval = 0;
                                    break;               // start of trajectory
                                case YellowBird:
                                    tapInterval = 65 + randomGenerator.nextInt(25);
                                    break; // 65-90% of the way
                                case WhiteBird:
                                    tapInterval = 70 + randomGenerator.nextInt(20);
                                    break; // 70-90% of the way
                                case BlackBird:
                                    tapInterval = 70 + randomGenerator.nextInt(20);
                                    break; // 70-90% of the way
                                case BlueBird:
                                    int  tap = shoot_priority.is_BlueBird();
                                    tapInterval = tap;
                                    //  tapInterval = 65 + randomGenerator.nextInt(20);
                                    break; // 65-85% of the way
                                default:
                                    tapInterval = 60;
                            }

                            int tapTime1 = tp.getTapTime(sling, releasePoint1, _tpt1, tapInterval);
                            int tapTime2 = tp.getTapTime(sling, releasePoint2, _tpt2, tapInterval);
                            dx1 = (int) releasePoint1.getX() - refPoint.x;
                            dy1 = (int) releasePoint1.getY() - refPoint.y;
                            dx2 = (int) releasePoint2.getX() - refPoint.x;
                            dy2 = (int) releasePoint2.getY() - refPoint.y;
                            shot1 = new Shot(refPoint.x, refPoint.y, dx1, dy1, 0, tapTime1);
                            shot2 = new Shot(refPoint.x, refPoint.y, dx2, dy2, 0, tapTime2);

                            if (utility.is_better_point1(vision, _tpt1, _tpt2, shot1, shot2)) {
                                dx = dx1;
                                dy = dy1;
                                shot = shot1;
                                releasePoint = releasePoint1;
                                pt.remove(1);

                            } else {
                                dx = dx2;
                                dy = dy2;
                                releasePoint = releasePoint2;
                                shot = shot2;
                                pt.remove(0);
                            }

                        } else {
                            System.err.println("No Release Point Found");
                            return state;
                        }
                    }

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
                            if(dx < 0)
                            {
                                aRobot.cshoot(shot);
                                state = aRobot.getState();
                                if ( state == GameState.PLAYING )
                                {
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
