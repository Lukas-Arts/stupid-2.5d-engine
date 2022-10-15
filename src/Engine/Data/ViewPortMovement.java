package Engine.Data;

import Engine.Math.Matrix2;
import Engine.Math.Util;

/**
 * Created by lynx on 05.12.17.
 */
public class ViewPortMovement extends Movement{
    private boolean moveTargetWithCamera;
    public ViewPortMovement(Matrix2 moveMatrix, int steps,boolean moveTargetWithCamera) {
        super(moveMatrix, steps);
        this.moveTargetWithCamera=moveTargetWithCamera;
    }
    public void setMoveTargetWithCamera(boolean moveTargetWithCamera){
        this.moveTargetWithCamera=moveTargetWithCamera;
    }
    public boolean isMoveTargetWithCamera(){
        return this.moveTargetWithCamera;
    }
    public static ViewPortMovement getTurnMovement(double xDeg,double yDeg,double zDeg,boolean passive){
        return new ViewPortMovement(Util.getTurnMatrix(xDeg, yDeg, zDeg, passive),0,false);
    }
    public static ViewPortMovement getTransMovement(double dx,double dy,double dz,double dwtf){
        return new ViewPortMovement(Util.getTransMatrix(dx,dy,dz,dwtf),0,false);
    }
    public static ViewPortMovement getTransTurnMovement(double xDeg,double yDeg,double zDeg,boolean passive,double dx,double dy,double dz,double dwtf){
        return new ViewPortMovement(Util.getTurnMatrix(xDeg, yDeg, zDeg, passive).mul(Util.getTransMatrix(dx,dy,dz,dwtf)),0,false);
    }
    public static ViewPortMovement getTurnMovement(double xDeg,double yDeg,double zDeg,boolean passive,int steps){
        return new ViewPortMovement(Util.getTurnMatrix(xDeg, yDeg, zDeg, passive),steps,false);
    }
    public static ViewPortMovement getTransMovement(double dx,double dy,double dz,double dwtf,int steps){
        return new ViewPortMovement(Util.getTransMatrix(dx,dy,dz,dwtf),steps,false);
    }
    public static ViewPortMovement getTransTurnMovement(double xDeg,double yDeg,double zDeg,boolean passive,double dx,double dy,double dz,double dwtf,int steps){
        return new ViewPortMovement(Util.getTurnMatrix(xDeg, yDeg, zDeg, passive).mul(Util.getTransMatrix(dx,dy,dz,dwtf)),steps,false);
    }
}
