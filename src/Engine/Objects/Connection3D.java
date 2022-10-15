package Engine.Objects;

import Engine.ViewPort;
import Engine.Math.Matrix2;
import Engine.Math.Util;
import Engine.Math.Vector;

import java.awt.*;

/**
 * Created by lynx on 01.06.17.
 *
 * IMPORTANT: Initial Objects have to exist in worlds ObjectList before the starlane!
 */
public class Connection3D extends Object3D {
    Object3D o1;
    Object3D o2;
    public Connection3D(Object3D o1, Object3D o2){
        super();
        this.o1=o1;
        this.o2=o2;
    }
    public Connection3D(Object3D o1, Object3D o2, Color c){
        this(o1,o2);
        setColor(c);
    }
    public Object3D getObject1(){
        return this.o1;
    }
    public Object3D getObject2(){
        return this.o2;
    }
    public Object3D getOther(Object3D o){
        return (o==o1?o2:(o==o2?o1:null));
    }
    public boolean connects(Object3D o){
        return o==this.o1 || o==this.o2;
    }
    public boolean connects(String name){
        return this.o1.getName().equalsIgnoreCase(name) || this.o2.getName().equalsIgnoreCase(name);
    }
    public Vector getNullVector(){
        return o1.getNullVector();
    }

    @Deprecated
    public Matrix2 getMoveMatrix(){
        return Util.getBaseMatrix(4);
    }

    //Connection3D Connects Objects, that move for them self, so no movement here..
    @Deprecated
    public void addMovement(double xDegTrans,double yDegTrans,double zDegTrans,int steps){
    }
    @Deprecated
    public void addMovement(double xDegTrans,double yDegTrans,double zDegTrans,double xTrans,double yTrans,double zTrans,int steps){
    }
    @Deprecated
    public void addMovement(double xDegTrans,double yDegTrans,double zDegTrans,double xTrans,double yTrans,double zTrans){
    }
    @Deprecated
    public void addMovement(double xDegTrans,double yDegTrans,double zDegTrans){
    }
    @Deprecated
    public void addMovement(Matrix2 m,int steps){
    }
    @Deprecated
    public void addMovement(Matrix2 m){
    }

    public Object3D getParent(){
        return o1.getParent();
    }
    @Deprecated
    public void setParent(Object3D parent){
    }
    public Vector getRealNullVector(){
        System.out.println(name+" has no parent :(");
        if(getParent()!=null){
            Vector v=(Vector)this.o1.getNullVector().add(getParent().getRealNullVector());
            v.set(3,1);
            System.out.println(name+" has a parent: "+getParent().name+" "+v.toString());
            return v;
        }else return this.o1.getNullVector();
    }
    public Vector getWorldVector(ViewPort vp){
        if(o2.getWorldVector(vp)==null||o1.getWorldVector(vp).get(3)>o2.getWorldVector(vp).get(3)){
            return o1.getWorldVector(vp);
        }else return o2.getWorldVector(vp);
    }
    public void doMovementStep(){
        //these are the new coordinates, those must be saved for movement
        //mul should return as vector if it is one..
        for(Object3D o:getObjects3D()){
            o.doMovementStep();
        }
    }
    //viewPortMatrix is seperate, so it can be adjusted to translate childs
    public void doCameraStep(ViewPort vp,Matrix2 viewPortMatrix){
        for(Object3D o:getObjects3D())
            o.doCameraStep(vp,
                    Util.getTransMatrix(o1.getNullVector().get(0),o1.getNullVector().get(1),o1.getNullVector().get(2),o1.getNullVector().get(3))
                            .mul(viewPortMatrix));
    }
    public void paint(Graphics2D g, int dx, int dy, ViewPort vp){
        if(visible){
            if(this.hover)g.setColor(getHoverColor());
                else g.setColor(getColor());
            Color c=g.getColor();
            Vector v1=this.o1.getWorldVector(vp);
            Vector v2=this.o2.getWorldVector(vp);
            int alpha= -50+(int)(v1.get(3)+v2.get(3))/2;
            alpha=255-Math.min(Math.max(0,alpha),230);
            g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),alpha));
            g.drawLine((int)v1.get(0)+dx,(int)v1.get(1)+dy,(int)v2.get(0)+dx,(int)v2.get(1)+dy);
        }
    }
}
