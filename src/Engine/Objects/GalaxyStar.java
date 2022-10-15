package Engine.Objects;

import Engine.ViewPort;
import Engine.Math.Vector;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Created by lynx on 01.06.17.
 */
public class GalaxyStar extends Object3D {
    BufferedImage img;
    ImageObserver io;
    {
        this.showPointOnScreen=false;
    }
    public GalaxyStar(double[] nullVector, String name, BufferedImage img, ImageObserver io) {
        this(new Vector(nullVector),name,img,io);
    }

    public GalaxyStar(Vector nullVector, String name, BufferedImage img, ImageObserver io) {
        super(nullVector,name);
        this.img=img;
        this.io=io;
    }
    public void paint(Graphics2D g, int dx, int dy, ViewPort vp){
        g.setColor(Color.black);
        Vector v=worldVectorMap.get(vp);
        g.drawImage(img,(int)(v.get(0)-(int)img.getWidth()/2+dx),(int)(v.get(1)-(int)img.getHeight()/2+dy),io);
        super.paint(g,dx,dy, vp);
    }
}
