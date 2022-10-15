package Engine.Objects;

import Engine.ViewPort;
import Engine.Math.Matrix2;
import Engine.Math.Util;
import Engine.Math.Vector;

import java.util.HashMap;

/**
 * Created by lynx on 30.05.17.
 */
public class Point3D{
    protected Vector nullVector;
    protected Matrix2 moveMatrix;
    private static int base=4;
    protected HashMap<ViewPort, Vector> worldVectorMap=new HashMap<>();
    protected int moveSteps=0;
    public Point3D(double[] nullVector){
        this(new Vector(nullVector));
    }
    public Point3D(Vector nullVector){
        this.nullVector=nullVector;
        this.moveMatrix= Util.getBaseMatrix(base);
    }
    public Vector getNullVector(){
        return nullVector;
    }
    public void setNullVector(Vector nullVector){
        this.nullVector=nullVector;
    }
    public Matrix2 getMoveMatrix(){
        synchronized (this){
            Matrix2 m=moveMatrix;
            if(moveSteps<0)moveMatrix=Util.getBaseMatrix(base);
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
    public void addMovement(Matrix2 m){
        synchronized (this){
            this.moveMatrix=this.moveMatrix.mul(m);
        }
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
        nullVector=(Vector)nullVector.mul(getMoveMatrix());
    }
    //viewPortMatrix is seperate, so it can be adjusted to translate childs
    public void doCameraStep(ViewPort vp,Matrix2 viewPortMatrix){
        Vector worldVector=(Vector)nullVector.mul(viewPortMatrix);
        //System.out.println("after camera: "+worldVector);
        worldVectorMap.put(vp,worldVector);
    }
    public String toString(){
        return "("+nullVector.toString()+")";
    }
}