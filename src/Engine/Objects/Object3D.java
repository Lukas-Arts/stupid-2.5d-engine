package Engine.Objects;

import Engine.CameraPanel;
import Engine.Util.Inverse;
import Engine.Util.Object3DUtil;
import Engine.ViewPort;
import Engine.Math.Matrix2;
import Engine.Math.Util;
import Engine.Math.Vector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lynx on 30.05.17.
 */
public class Object3D {
    private ArrayList<Object3D> objs=new ArrayList<>();
    protected Vector nullVector;
    protected Matrix2 moveMatrix;
    protected String name="";
    private static int base=4;
    protected Color color=Color.white;
    protected Color hoverColor=Color.RED;
    protected boolean showPointOnScreen=true;
    protected boolean showNameOnScreen=true;
    protected boolean showCoordinatesOnScreen =false;
    protected HashMap<ViewPort, Vector> worldVectorMap=new HashMap<>();
    protected Object3D parent=null;
    protected Line3D plainLine;
    protected boolean showXYPlainVector=false;
    protected int moveSteps=0;
    protected AbstractAction finishedMoveAction=null;
    protected ArrayList<MouseAdapter> mas=new ArrayList<>();
    protected Shape bounds=new Rectangle(-3,-3,6,6);
    protected boolean hover=false;
    protected boolean scale =false;
    protected boolean visible=true;
    protected static boolean showNamesOnlyOnHover=false;
    public Object3D(){}
    public Object3D(double[] nullVector){
        this(new Vector(nullVector));
    }
    public Object3D(double[] nullVector,String name){
        this(nullVector);
        this.name=name;
    }
    public Object3D(Vector nullVector){
        this.nullVector=nullVector;
        this.moveMatrix= Util.getBaseMatrix(base);
    }
    public Object3D(Vector nullVector, String name){
        this(nullVector);
        this.name=name;
    }

    public void removeAllMouseAdapters(){
        this.mas.clear();
    }
    public void removeMouseAdapter(int i){
        this.mas.remove(i);
    }
    public void removeMouseAdapter(MouseAdapter ma){
        this.mas.remove(ma);
    }
    public void addMouseAdapter(MouseAdapter ma){
        this.mas.add(ma);
    }
    public ArrayList<MouseAdapter> getMouseAdapters(){
        return mas;
    }
    public static void setShowNamesOnlyOnHover(boolean b){
        showNamesOnlyOnHover= b;
    }
    public static void toggleShowNamesOnlyOnHover(){
        showNamesOnlyOnHover= !showNamesOnlyOnHover;
    }
    public void toggleShowPointOnScreen(){
        this.showPointOnScreen= !showPointOnScreen;
    }
    public void toggleShowNameOnScreen(){
        this.showNameOnScreen= !showNameOnScreen;
    }
    public void toggleShowCoordinatesOnScreen(){
        this.showCoordinatesOnScreen = !showCoordinatesOnScreen;
    }
    public void toggleShowXYPlainVector(){
        if(showXYPlainVector){
            showXYPlainVector=false;
            this.objs.remove(plainLine);
        }else{
            showXYPlainVector=true;
            if(plainLine==null){
                plainLine=getPlainLine();
            }
            this.addObject3D(plainLine);
        }
    }
    public void updatePlainLine(){
        this.objs.remove(plainLine);
        this.plainLine=getPlainLine();
        this.addObject3D(plainLine);
    }
    public void toggleVisible(){
        this.visible= !visible;
    }
    public void toggleScale(){
        this.scale= !scale;
    }
    public Shape getBounds(){
        return bounds;
    }
    public void setBounds(Shape s){
        this.bounds=s;
    }
    public void hover(boolean hover){
        this.hover= hover;
    }
    private Line3D getPlainLine(){
        double d[]={0,0,0,1};
        double d2[]={0,0,-getRealNullVector().get(2),1};
        Vector v=new Vector(d2);
        //System.out.println("plain: "+v.toString());
        return new Line3D(new Vector(d),v,1,Color.green);
    }
    public void setColor(Color c){
        this.color=c;
    }
    public Color getColor(){
        return color;
    }
    public void setHoverColor(Color c){
        this.hoverColor=c;
    }
    public Color getHoverColor(){
        return hoverColor;
    }
    public ArrayList<Object3D> getObjects3D(){
        return objs;
    }
    public Object3D getObject3D(int i){
        return objs.get(i);
    }
    public void addObject3D(Object3D obj){
        this.objs.add(obj);
        obj.setParent(this);
    }
    public void addLine3D(Line3D l){
        this.objs.get(0).addObject3D(l);
        l.setParent(this.objs.get(0));
    }
    public void setObject3Ds(ArrayList<Object3D> objs){
        this.objs=objs;
    }
    public Vector getNullVector(){
        return nullVector;
    }
    public Matrix2 getMoveMatrix(){
        synchronized (this){
            Matrix2 m=moveMatrix;
            if(moveSteps<=0){
                moveMatrix=Util.getBaseMatrix(base);
                if(finishedMoveAction!=null){
                    finishedMoveAction.actionPerformed(new ActionEvent(this,0,"finished"));
                    finishedMoveAction=null;
                }
            }
            moveSteps--;
            return m;
        }
    }
    public void addMovement(double xDegTrans,double yDegTrans,double zDegTrans,int steps){
        this.moveSteps=steps;
        addMovement(Util.getTurnMatrix(xDegTrans,yDegTrans,zDegTrans,false),steps);
    }
    public void addMovement(double xDegTrans,double yDegTrans,double zDegTrans,double xTrans,double yTrans,double zTrans,int steps){
        addMovement(Util.getTurnMatrix(xDegTrans,yDegTrans,zDegTrans,false)
                .mul(Util.getTransMatrix(xTrans,yTrans,zTrans,1)),steps);
    }
    public void addMovement(double xDegTrans,double yDegTrans,double zDegTrans,double xTrans,double yTrans,double zTrans){

        addMovement(Util.getTurnMatrix(xDegTrans,yDegTrans,zDegTrans,false)
                .mul(Util.getTransMatrix(xTrans,yTrans,zTrans,1)));
    }
    public void addMovement(double xDegTrans,double yDegTrans,double zDegTrans){
        addMovement(Util.getTurnMatrix(xDegTrans,yDegTrans,zDegTrans,false));
    }
    public void addMovement(Matrix2 m,int steps){
        this.moveSteps=steps;
        this.addMovement(m);
    }
    public void moveTo(Object3D o){
        Vector v= (Vector) o.getNullVector().sub(this.getNullVector());
        addMovement(0,0,0,v.get(0),v.get(1),v.get(2));
    }
    public void moveTo(Object3D o,double speed){
        if(speed>0){
            Vector dir= (Vector) o.getNullVector().sub(this.getNullVector());
            Vector dirNorm= (Vector) dir.norm();
            Vector movementInOneStep=(Vector)dirNorm.mul(speed);
            double d1=dir.distance(Vector.nullVector);
            double d2=movementInOneStep.distance(Vector.nullVector);
            double steps=(int)(d1/d2)+1;
            Vector movement=(Vector)dir.mul(1.0/steps);
            addMovement(0,0,0,movement.get(0),movement.get(1),movement.get(2),(int)steps-1);
        }
    }
    public void moveTo(Vector v,int steps,AbstractAction a){
        Vector dir= (Vector) v.sub(this.getNullVector());
        Vector movement=(Vector)dir.mul(1.0/steps);
        this.finishedMoveAction=a;
        addMovement(0,0,0,movement.get(0),movement.get(1),movement.get(2),(int)steps-1);
    }
    //speed= speedPerSecond/FPS
    public void moveTo(Vector v,double speed,AbstractAction a){
        if(speed>0){
            Vector dir= (Vector) v.sub(this.getNullVector());
            Vector dirNorm= (Vector) dir.norm();
            Vector movementInOneStep=(Vector)dirNorm.mul(speed);
            double d1=dir.distance(Vector.nullVector);
            double d2=movementInOneStep.distance(Vector.nullVector);
            double steps=(int)(d1/d2)+1;
            Vector movement=(Vector)dir.mul(1.0/steps);
            this.finishedMoveAction=a;
            addMovement(0,0,0,movement.get(0),movement.get(1),movement.get(2),(int)steps-1);
        }
    }
    public void addMovement(Matrix2 m){
        synchronized (this){
            this.moveMatrix=m;
        }
    }
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return name;
    }
    public Object3D getParent(){
        return parent;
    }
    public void setParent(Object3D parent){
        this.parent=parent;
        if(showXYPlainVector)this.setPlainLine(getPlainLine());
    }
    private void setPlainLine(Line3D l){
        this.objs.remove(plainLine);
        this.plainLine=l;
        this.addObject3D(plainLine);
    }
    public Vector getRealNullVector(){
        //System.out.println(name+" has no parent :(");
        if(getParent()!=null){
            Vector v=(Vector)this.nullVector.add(getParent().getRealNullVector());
            v.set(3,1);
            //System.out.println(name+" has a parent: "+getParent().name+" "+v.toString());
            return v;
        }else return this.nullVector;
    }
    public void setWorldVector(ViewPort vp, Vector v){
        worldVectorMap.put(vp,v);
    }
    public Vector getWorldVector(ViewPort vp){
        return worldVectorMap.get(vp);
    }
    public void doMovementStep(){
        //these are the new coordinates, those must be saved for movement
        //mul should return as vector if it is one..
        Matrix2 m=getMoveMatrix();
        if(!Object3DUtil.isBaseMatrix(m)){
            nullVector=(Vector)nullVector.mul(m);
            if(showXYPlainVector)updatePlainLine();
        }
        for(Object3D o:objs){
            o.doMovementStep();
        }
    }
    //viewPortMatrix is seperate, so it can be adjusted to translate childs
    private HashMap<ViewPort,Matrix2> lastViewPortMatrix=new HashMap<>();
    private Vector lastNullVector=null;
    public void doCameraStep(ViewPort vp,Matrix2 viewPortMatrix){
        if(!viewPortMatrix.equals(lastViewPortMatrix.get(vp)) || lastNullVector==null || lastNullVector!=nullVector){
            lastViewPortMatrix.put(vp,viewPortMatrix);
            if(lastNullVector!=nullVector)lastNullVector=nullVector;

            Vector worldVector=(Vector)nullVector.mul(viewPortMatrix);
            //System.out.println("after camera: "+worldVector);
            worldVectorMap.put(vp,worldVector);
            for(Object3D o:objs)
                o.doCameraStep(vp,
                    Util.getTransMatrix(nullVector.get(0),nullVector.get(1),nullVector.get(2),nullVector.get(3))
                        .mul(viewPortMatrix));
        }
    }
    public double getScaleFactor(Vector worldVector){
        //3&6 because of the parallel-projection width of 3
        return 0.5+(3-worldVector.get(2))/6;
    }
    public void paint(Graphics2D g,int dx,int dy,ViewPort vp){
        if(visible){
            g.setColor(getColor());
            Vector v1=worldVectorMap.get(vp);
            if(showPointOnScreen || hover){
                if(hover)g.setColor(hoverColor);
                AffineTransform af=new AffineTransform();
                af.translate(v1.get(0)+dx,v1.get(1)+dy);
                if(scale){
                    double scaleFactor=getScaleFactor(v1);
                    af.scale(scaleFactor,scaleFactor);
                }
                Shape drawShape=af.createTransformedShape(getBounds());
                g.draw(drawShape);
                //g.drawLine((int)v1.get(0)+dx,(int)v1.get(1)+dy,(int)v1.get(0)+dx,(int)v1.get(1)+dy);
                g.setColor(getColor());
            }
            if(showNameOnScreen&&(!showNamesOnlyOnHover || hover)){
                g.drawString(name,(int)(v1.get(0)+dx+getBounds().getBounds().getWidth()/2),(int)(v1.get(1)+dy));
            }
            if(showCoordinatesOnScreen){
                g.setColor(Color.white);
                g.drawString("("+d(v1.get(0))+"/"+d(v1.get(1))+")",(int)(v1.get(0)+dx+getBounds().getBounds().getWidth()/2),(int)v1.get(1)+dy+25);
                g.drawString("("+d(getRealNullVector().get(0))+"/"+d(getRealNullVector().get(1))+"/"+d(getRealNullVector().get(2))+"/"+d(getRealNullVector().get(3))+")",(int)(v1.get(0)+dx+getBounds().getBounds().getWidth()/2),(int)v1.get(1)+dy+50);
            }
        }
    }
    private String d(double d){
        String s=(d+"");
        return s.substring(0,Math.min(s.length(),s.indexOf(".")+2));
    }
    public String toString(){
        return getName()+" ("+nullVector.toString()+")";
    }
}