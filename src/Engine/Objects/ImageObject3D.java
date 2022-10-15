package Engine.Objects;

import Engine.ViewPort;
import Engine.Math.Vector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

/**
 * Created by lynx on 31.05.17.
 */
public class ImageObject3D extends Object3D {
    private String imgPath;
    protected BufferedImage img;
    protected ImageObserver io;
    public ImageObject3D(){}
    public ImageObject3D(double[] nullVector, String imgPath, ImageObserver io) {
        this(new Vector(nullVector),imgPath,io);
    }
    public ImageObject3D(Vector nullVector, String imgPath, ImageObserver io) {
        super(nullVector);
        this.imgPath=imgPath;
        try {
            this.img= ImageIO.read(new File(imgPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setBounds(Math.max(img.getWidth()/2,img.getHeight()/2));
        this.io=io;
        this.toggleShowXYPlainVector();
        this.showPointOnScreen=false;
    }
    public ImageObject3D(double[] nullVector, String name, String imgPath, ImageObserver io) {
        this(new Vector(nullVector),name,imgPath,io);
    }
    public ImageObject3D(Vector nullVector, String name, String imgPath, ImageObserver io) {
        super(nullVector, name);
        this.imgPath=imgPath;
        try {
            this.img= ImageIO.read(new File(imgPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setBounds(Math.max(img.getWidth()/2,img.getHeight()/2));
        this.io=io;
        this.toggleShowXYPlainVector();
        this.showPointOnScreen=false;
    }
    public void setBounds(double radius){
        if(radius<5)radius=5;
        this.setBounds(new Rectangle2D.Double(-radius,-radius,radius*2,radius*2));
    }
    public String getImgPath(){
        return imgPath;
    }
    public void paint(Graphics2D g, int dx, int dy, ViewPort vp){
        Vector v=worldVectorMap.get(vp);
        //distance to camera target (to check if 'in sight')
        double d=Math.sqrt(Math.pow(v.get(0)-vp.getCameraTarget().get(0),2)+Math.pow(v.get(1)-vp.getCameraTarget().get(1),2));
        if(img!=null&&d<Math.max(vp.getHeight(),vp.getWidth())){
            g.drawImage(img,(int)(v.get(0)-img.getWidth()/2+dx),(int)(v.get(1)-img.getHeight()/2+dy),io);
            AffineTransform af=new AffineTransform();
            af.translate(v.get(0)+dx,v.get(1)+dy);
            Shape drawShape=af.createTransformedShape(getBounds());
            g.setColor(new Color(0,0,0,255-getColor().getAlpha()));
            g.fill(drawShape);
        }
        super.paint(g,dx,dy, vp);
    }
}
