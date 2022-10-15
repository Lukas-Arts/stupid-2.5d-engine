package Engine.Objects;

import Engine.ViewPort;
import Engine.Math.Util;
import Engine.Math.Vector;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Created by lynx on 31.05.17.
 */
public class Planet extends Object3D {
    protected BufferedImage img;
    protected ImageObserver io;
    {
        this.toggleShowXYPlainVector();
        this.showPointOnScreen=false;
    }
    public Planet(double[] nullVector) {
        this(new Vector(nullVector));
    }

    public Planet(Vector nullVector) {
        super(nullVector);
        this.moveMatrix= Util.getBaseMatrix(4);
        this.addMouseAdapter(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                hover(true);
            }
        });
    }

    public Planet(Vector nullVector, double radius) {
        this(nullVector);
        this.setBounds(radius);
    }

    public Planet(Vector nullVector, String name) {
        this(nullVector);
        this.name=name;
    }

    public Planet(Vector nullVector, double radius, String name) {
        this(nullVector, name);
        this.setBounds(radius);
    }
    public Planet(Vector nullVector, String name, BufferedImage img, ImageObserver io) {
        this(nullVector, name);
        this.setBounds(Math.max(img.getWidth()/2,img.getHeight()/2));
        this.img=img;
        this.io=io;
    }
    public Planet(Vector nullVector, double radius, String name, Color color) {
        this(nullVector,radius,name);
        this.setColor(color);
    }
    public void setBounds(double radius){
        if(radius<5)radius=5;
        this.setBounds(new Rectangle2D.Double(-radius,-radius,radius*2,radius*2));
    }
    @Override
    public void paint(Graphics2D g, int dx, int dy, ViewPort vp) {
        g.setColor(Color.red);
        Vector v=worldVectorMap.get(vp);
        if(img!=null){
            if(scale){
                AffineTransform af=new AffineTransform();
                //3&6 because of the parallel-projection width of 3
                double scaleFactor=getScaleFactor(v);
                BufferedImage after = new BufferedImage((int)(img.getWidth()*scaleFactor),(int)(img.getHeight()*scaleFactor), BufferedImage.TYPE_INT_ARGB);
                af.scale(scaleFactor,scaleFactor);
                AffineTransformOp scaleOp =
                        new AffineTransformOp(af, AffineTransformOp.TYPE_BILINEAR);
                after = scaleOp.filter(img, after);
                setAlpha(after,(byte)getColor().getAlpha());
                g.drawImage(after,(int)(v.get(0)-after.getWidth()/2+dx),(int)(v.get(1)-after.getHeight()/2+dy),io);
            }else g.drawImage(img,(int)(v.get(0)-img.getWidth()/2+dx),(int)(v.get(1)-img.getHeight()/2+dy),io);
        }
        super.paint(g,dx,dy,vp);
    }
    public static void setAlpha(BufferedImage modMe, byte alpha) {
        alpha=(byte)Math.max(alpha,50);
        for (int x = 0; x < modMe.getWidth(); x++) {
            for (int y = 0; y < modMe.getHeight(); y++) {
                //
                int argb = modMe.getRGB(x, y); //always returns TYPE_INT_ARGB
                int oldAlpha=(argb >> 24);
                if(oldAlpha>50){
                    System.out.println("before: "+oldAlpha);
                    argb &= 0x00ffffff; //remove old alpha info
                    argb |= (alpha << 24);  //add new alpha info
                    modMe.setRGB(x, y, argb);
                    int newAlpha=(argb >> 24);
                    System.out.println("after: "+newAlpha);
                }
            }
        }
    }
}