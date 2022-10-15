package Engine.Objects;

import Engine.ViewPort;
import Engine.Math.Vector;

import java.awt.*;

/**
 * Created by lynx on 31.05.17.
 */
public class Line3D extends Object3D {
    public Line3D(Vector nullV1, Vector nullV2){
        this(nullV1,(Vector)nullV2.sub2(nullV1,3),1.0);
    }
    public Line3D(Vector nullVector, Vector direction, double length) {
        super(nullVector);
        Vector v=(Vector)direction.mul(length);
        v.set(3,1);
        this.addObject3D(new Object3D(v));
    }
    public Line3D(Vector nullVector, Vector direction, int length, Color c) {
        this(nullVector,direction,length);
        this.color=c;
    }
    public void paint(Graphics2D g, int dx, int dy, ViewPort vp){
        if(this.hover)g.setColor(getHoverColor());
            else g.setColor(getColor());
        Vector v1=worldVectorMap.get(vp);
        Vector v2=this.getObject3D(0).getWorldVector(vp);
        g.drawLine((int) v1.get(0)+dx,(int) v1.get(1)+dy,(int) v2.get(0)+dx,(int) v2.get(1)+dy);
        super.paint(g,dx,dy, vp);
    }
}
