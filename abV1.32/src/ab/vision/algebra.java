package ab.vision;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class algebra {
    public boolean isB(ABObject x,ABObject y){
        if((x.x+x.width)<y.x )
            return true;
        else
            return false;
    }
    public boolean isA(ABObject x,ABObject y){
        if((x.x)>(y.x+y.width) )
            return true;
        else
            return false;
    }
    public boolean isM(ABObject x,ABObject y){
        if((y.x+y.width)>(x.x+x.width)&&(x.x+x.width)>y.x )
            return true;
        else
            return false;
    }

    public boolean isMi(ABObject x,ABObject y){
        if((x.x+x.width)>(y.x+y.width)&&(y.x+y.width)>x.x )
            return true;
        else
            return false;
    }
    public boolean isMom(ABObject x,ABObject y){
        if((x.x+x.width)>(y.x+(y.width/2))&&(y.x)<(x.x+(x.width/2))&&(y.x>x.x)&&(x.x+x.width)<(y.x+(y.width)) )
            return true;
        else
            return false;
    }
    public boolean isMomi(ABObject x,ABObject y) {
        if ((y.x + y.width) > (x.x + (x.width / 2)) && (x.x) < (y.x + (y.width / 2))&&(x.x>y.x)&&(y.x+y.width)<(x.x+(x.width)))
            return true;
        else
            return false;
    }
    public boolean isLol(ABObject x,ABObject y) {
        if((x.x+x.width)<(y.x+(y.width/2))&&(y.x)>(x.x+(x.width/2))&&(y.x<(x.x+x.width))&&(x.x+x.width)>(y.x) )
            return true;
        else
            return false;
    }
    public boolean isLoli(ABObject x,ABObject y) {
        if((y.x+y.width)<(x.x+(x.width/2))&&(x.x)>(y.x+(y.width/2))&&(x.x<(y.x+y.width))&&(y.x+y.width)>(x.x) )
            return true;
        else
            return false;
    }
    public boolean isMol(ABObject x,ABObject y) {
        if((x.x+x.width)<(y.x+(y.width/2))&&y.x<(x.x+x.width)&&x.x<y.x&&y.x<(x.x+(x.width/2)) )
            return true;
        else
            return false;
    }
    public boolean isMoli(ABObject x,ABObject y) {
        if((y.x+y.width)<(x.x+(x.width/2))&&x.x<(y.x+y.width)&&y.x<x.x&&x.x<(y.x+(y.width/2)) )
            return true;
        else
            return false;
    }
    public boolean isLom(ABObject x,ABObject y) {
        if((x.x+x.width)<(y.x+y.width)&&(y.x+(y.width/2))<(x.x+x.width)&&y.x<(x.x+x.width)&&(x.x+(x.width/2))<y.x )
            return true;
        else
            return false;
    }
    public boolean isLomi(ABObject x,ABObject y) {
        if((y.x+y.width)<(x.x+x.width)&&(x.x+(x.width/2))<(y.x+y.width)&&x.x<(y.x+y.width)&&(y.x+(y.width/2))<x.x )
            return true;
        else
            return false;
    }
    public boolean isMs(ABObject x,ABObject y) {
        if(x.x==y.x&&(x.x+x.width)>(y.x+(y.width/2))&&(x.x+x.width)<(y.x+y.width))
            return true;
        else
            return false;
    }
    public boolean isMsi(ABObject x,ABObject y) {
        if(y.x==x.x&&(y.x+y.width)>(x.x+(x.width/2))&&(y.x+y.width)<(x.x+x.width))
            return true;
        else
            return false;
    }
    public boolean isLs(ABObject x,ABObject y) {
        if(x.x==y.x&&(x.x+x.width)<(y.x+(y.width/2)))
            return true;
        else
            return false;
    }
    public boolean isLsi(ABObject x,ABObject y) {
        if(y.x==x.x&&(y.x+y.width)<(x.x+(x.width/2)))
            return true;
        else
            return false;
    }
    public boolean isLd(ABObject x,ABObject y) {
        if(x.x>y.x&&(x.x+x.width)<(y.x+(y.width/2)))
            return true;
        else
            return false;
    }
    public boolean isLdi(ABObject x,ABObject y) {
        if(y.x>x.x&&(y.x+y.width)<(x.x+(x.width/2)))
            return true;
        else
            return false;
    }
    public boolean isRd(ABObject x,ABObject y) {
        if(x.x>(y.x+(y.width/2))&&(x.x+x.width)<(y.x+(y.width)))
            return true;
        else
            return false;
    }
    public boolean isRdi(ABObject x,ABObject y) {
        if(y.x>(x.x+(x.width/2))&&(y.x+y.width)<(x.x+(x.width)))
            return true;
        else
            return false;
    }
    public boolean isCd(ABObject x,ABObject y) {
        if((x.x+(x.width/2))==(y.x+(y.width/2))&&x.x>y.x)
            return true;
        else
            return false;
    }
    public boolean isCdi(ABObject x,ABObject y) {
        if((y.x+(y.width/2))==(x.x+(x.width/2))&&y.x>x.x)
            return true;
        else
            return false;
    }
    public boolean isMf(ABObject x,ABObject y) {
        if((x.x+x.width)==(y.x+y.width)&&x.x>y.x&&x.x<(y.x+(y.width/2)))
            return true;
        else
            return false;
    }
    public boolean isMfi(ABObject x,ABObject y) {
        if((y.x+y.width)==(x.x+x.width)&&y.x>x.x&&y.x<(x.x+(x.width/2)))
            return true;
        else
            return false;
    }
    public boolean isLf(ABObject x,ABObject y) {
        if((x.x+x.width)==(y.x+y.width)&&x.x>(y.x+(y.width/2)))
            return true;
        else
            return false;
    }
    public boolean isLfi(ABObject x,ABObject y) {
        if((y.x+y.width)==(x.x+x.width)&&y.x>(x.x+(x.width/2)))
            return true;
        else
            return false;
    }
    public boolean isEq(ABObject x,ABObject y) {
        if((x.x+x.width)==(y.x+y.width)&&x.x==y.x)
            return true;
        else
            return false;
    }



}