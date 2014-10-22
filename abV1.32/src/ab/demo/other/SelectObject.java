package ab.demo.other;

import java.awt.Rectangle;
import ab.vision.ABObject;
import ab.vision.algebra;
import ab.vision.Vision;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class SelectObject {
    public List<ABObject> getReachableObjects()
    {
        BufferedImage screenshot = ActionRobot.doScreenShot();
        Vision vision = new Vision(screenshot);
        algebra algebra = new algebra();
        List<ABObject> blocks = vision.findBlocksMBR();
        System.out.println("No of blocks:" + blocks.size());
        List<ABObject> reachableObjects=new LinkedList<ABObject>();

        //Formula
        for (int i = 0; i < blocks.size(); i++) {
            int x=0;
            for (int j = 0; j < blocks.size(); j++) {
                if(
                        (i!=j)&&((algebra.isB(blocks.get(i), blocks.get(j)) || algebra.isLdi(blocks.get(i), blocks.get(j)) || algebra.isCdi(blocks.get(i), blocks.get(j)) || algebra.isRdi(blocks.get(i), blocks.get(j)) || algebra.isMom(blocks.get(i), blocks.get(j)) || algebra.isLom(blocks.get(i), blocks.get(j)) || algebra.isLol(blocks.get(i), blocks.get(j)) || algebra.isMoli(blocks.get(i), blocks.get(j)) || algebra.isMomi(blocks.get(i), blocks.get(j)) || algebra.isM(blocks.get(i), blocks.get(j)) || algebra.isMs(blocks.get(i), blocks.get(j)) || algebra.isLs(blocks.get(i), blocks.get(j)) || algebra.isMsi(blocks.get(i), blocks.get(j)) || algebra.isLsi(blocks.get(i), blocks.get(j)) || algebra.isMfi(blocks.get(i), blocks.get(j)) || algebra.isLfi(blocks.get(i), blocks.get(j)) || algebra.isEq(blocks.get(i), blocks.get(j)))&&( algebra.isB(blocks.get(i), blocks.get(j)) || algebra.isA(blocks.get(i), blocks.get(j)) || algebra.isM(blocks.get(i), blocks.get(j)) || algebra.isMi(blocks.get(i), blocks.get(j)) || algebra.isMom(blocks.get(i), blocks.get(j)) || algebra.isMomi(blocks.get(i), blocks.get(j)) || algebra.isLol(blocks.get(i), blocks.get(j)) || algebra.isLoli(blocks.get(i), blocks.get(j)) || algebra.isMol(blocks.get(i), blocks.get(j)) || algebra.isMoli(blocks.get(i), blocks.get(j)) || algebra.isLom(blocks.get(i), blocks.get(j)) || algebra.isLomi(blocks.get(i), blocks.get(j)) || algebra.isMs(blocks.get(i), blocks.get(j)) || algebra.isMsi(blocks.get(i), blocks.get(j)) || algebra.isLs(blocks.get(i), blocks.get(j)) || algebra.isLsi(blocks.get(i), blocks.get(j)) || algebra.isLd(blocks.get(i), blocks.get(j)) || algebra.isLdi(blocks.get(i), blocks.get(j)) || algebra.isRd(blocks.get(i), blocks.get(j)) || algebra.isRdi(blocks.get(i), blocks.get(j)) || algebra.isCd(blocks.get(i), blocks.get(j)) || algebra.isCdi(blocks.get(i), blocks.get(j)) || algebra.isMf(blocks.get(i), blocks.get(j)) || algebra.isMfi(blocks.get(i), blocks.get(j)) || algebra.isLf(blocks.get(i), blocks.get(j)) || algebra.isLfi(blocks.get(i), blocks.get(j)) || algebra.isEq(blocks.get(i), blocks.get(j)))) ||
                                ((algebra.isA(blocks.get(i), blocks.get(j))||algebra.isLd(blocks.get(i), blocks.get(j))||algebra.isCd(blocks.get(i), blocks.get(j))||algebra.isRd(blocks.get(i), blocks.get(j))||algebra.isLomi(blocks.get(i), blocks.get(j))||algebra.isLoli(blocks.get(i), blocks.get(j))||algebra.isMi(blocks.get(i), blocks.get(j))||algebra.isMf(blocks.get(i), blocks.get(j))||algebra.isLf(blocks.get(i), blocks.get(j))) &&(algebra.isA(blocks.get(i), blocks.get(j))||algebra.isB(blocks.get(i), blocks.get(j))||algebra.isM(blocks.get(i), blocks.get(j))||algebra.isMi(blocks.get(i), blocks.get(j))||algebra.isMom(blocks.get(i), blocks.get(j))||algebra.isMol(blocks.get(i), blocks.get(j))||algebra.isLom(blocks.get(i), blocks.get(j))||algebra.isLol(blocks.get(i), blocks.get(j))||algebra.isMomi(blocks.get(i), blocks.get(j))||algebra.isMoli(blocks.get(i), blocks.get(j))||algebra.isLomi(blocks.get(i), blocks.get(j))||algebra.isLoli(blocks.get(i), blocks.get(j))||algebra.isLdi(blocks.get(i), blocks.get(j))||algebra.isCdi(blocks.get(i), blocks.get(j))||algebra.isRdi(blocks.get(i), blocks.get(j))||algebra.isMsi(blocks.get(i), blocks.get(j))||algebra.isLsi(blocks.get(i), blocks.get(j))||algebra.isMfi(blocks.get(i), blocks.get(j))||algebra.isLfi(blocks.get(i), blocks.get(j))))

                        )
                    x++;




            }
            if(x==blocks.size()-1)
                reachableObjects.add(blocks.get(i));

        }
        System.out.println("No of blocks reachable:" + reachableObjects.size());
        return reachableObjects;
    }

    public ABObject getTarget()
    {
        SelectObject obj = new SelectObject();
        List<ABObject> reachableObjectList = obj.getReachableObjects();

        return reachableObjectList.get(0);
    }
}