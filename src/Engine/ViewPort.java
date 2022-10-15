package Engine;

import Engine.Data.Movement;
import Engine.Data.ViewPortMovement;
import Engine.Math.Matrix2;
import Engine.Math.Util;
import Engine.Math.Vector;
import Engine.Objects.Object3D;
import Engine.Util.Inverse;
import Engine.Util.Object3DUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by lynx on 30.05.17.
 */
public class ViewPort {
    private boolean isZselected=false;
    private Object3D mouseCursor3D;
    private Vector cameraPosition;
    private Vector cameraTarget;
    private Vector cameraUpVector;
    private double aspectRatio;     //seitenverhältnis des kameraausschnitts
    private double fieldOfView_deg; //öffnungswinkel der kamera
    private double far,near;
    private Matrix2 cameraTransformation;
    private Matrix2 perspectiveTransformation;
    private Matrix2 viewPortTransformation;
    private ViewPortMovement movement;
    private BufferedImage image_current;
    private BufferedImage image_next;
    private int height,width;
    private final Object transformationLock=new Object();
    private ArrayList<CameraPanel> cps=new ArrayList<>();
    private boolean moveTargetWithCamera=false;
    private double mouseX2D=0;
    private double mouseY2D=0;
    private double mouseZ3D=0;
    private double z3D_diff=0;
    private double tempMouseX2D=0;
    private double tempMouseY2D=0;
    private double tempMouseZ3D=0;
    public ViewPort(Vector cameraPosition, Vector cameraTarget, Vector cameraUpVector,
                    double aspectRatio, double fieldOfView_deg, double far, double near, int width, int height){
        this.cameraPosition = cameraPosition;
        this.cameraTarget=cameraTarget;
        this.cameraUpVector=cameraUpVector;
        this.aspectRatio=aspectRatio;
        this.fieldOfView_deg=fieldOfView_deg;
        this.far=far;
        this.near=near;

        this.height=height; //height&width of the canvas the picture will be painted on to translate the center
        this.width=width;

        this.cameraTransformation= Util.getCameraTransformation(cameraPosition,cameraTarget,cameraUpVector);
        this.perspectiveTransformation=Util.getParallel(3,aspectRatio,far,near);//Util.getPerspective2(Math.toRadians(fieldOfView_deg),aspectRatio,far,near);
        this.viewPortTransformation=this.cameraTransformation.mul(this.perspectiveTransformation);

        this.movement=new ViewPortMovement(Util.getBaseMatrix(4),0,false);
    }
    public void toggleMoveTargetWithCamera(){
        this.moveTargetWithCamera= !this.moveTargetWithCamera;
    }
    public void setSize(int width,int height){
        System.out.println("ViewPort size changed: "+width+"/"+height);
        this.width=width;
        this.height=height;
    }
    public int getWidth(){
        return this.width;
    }
    public int getHeight(){
        return this.height;
    }
    public ViewPortMovement getMovement(){
        synchronized (this){
            ViewPortMovement m=movement;
            if(m.getSteps()<=0 && (!Object3DUtil.isBaseMatrix(m.getMoveMatrix()))){
                movement=new ViewPortMovement(Util.getBaseMatrix(4),0,false);
            } else{
                movement.step();
            }
            return m;
        }
    }
    public void addMovement(ViewPortMovement m){
        synchronized (this){
            this.movement=m;
        }
    }
    public void doMoveStep(){
        double d[]={1};
        ViewPortMovement m=getMovement();
        if(!Object3DUtil.isBaseMatrix(m.getMoveMatrix())){
            Vector v=(Vector) cameraPosition.addCol(d).mul(m.getMoveMatrix());
            //this.cameraPosition=new Vector(1,v.dim()-1);
            for(int i=0;i<v.dim()-1;i++){
                this.cameraPosition.set(i,v.get(i));
            }
            if(m.isMoveTargetWithCamera()) {
                Vector v2 = (Vector) cameraTarget.addCol(d).mul(m.getMoveMatrix());
                //this.cameraTarget = new Vector(1, v2.dim() - 1);
                for(int i=0;i<v2.dim()-1;i++){
                    this.cameraTarget.set(i,v2.get(i));
                }
            }
            this.cameraTransformation= Util.getCameraTransformation(cameraPosition,cameraTarget,cameraUpVector);
            synchronized (transformationLock){
                this.viewPortTransformation=this.cameraTransformation.mul(this.perspectiveTransformation);
            }
        }

        if(mouseCursor3D !=null&&((!Object3DUtil.isBaseMatrix(m.getMoveMatrix())) || (tempMouseX2D!=mouseX2D||tempMouseY2D!=mouseY2D||tempMouseZ3D!=mouseZ3D||z3D_diff!=0))){
            if(!isZselected){
                Vector[] vs= Util.getCameraAxis(cameraUpVector, cameraPosition, cameraTarget);  //normalized camera-dir-vectors
                Matrix2 m2=new Matrix2(Inverse.invert(perspectiveTransformation.getA()));   //dont know why excatly but it helps
                Vector vxy=new Vector(new double[]{(mouseX2D-(double)getWidth()/2.0),(mouseY2D-(double)getHeight()/2.0),1,1});  //y*2.x helps
                vxy=(Vector)vxy.mul(m2);
                //dx and dy to the mouse x/y
                Vector d2=(Vector)(vs[0].mul(vxy.get(0)).add(vs[1].mul(vxy.get(1))).addCol(new double[]{1}));
                //camera+dx/dy
                Vector position= (Vector) cameraPosition.addCol(new double[]{1}).add(d2);
                //target+dx/dy
                Vector target=(Vector)cameraTarget.addCol(new double[]{1}).add(d2);
                //norm vector showing to the target
                Vector dir=((Vector)target.sub(position)).norm();
                dir.set(3,1);
                //solve mouseZ3D=cameraZ+t*dirZ to t
                double z=position.get(2);
                double z2=dir.get(2);
                double z3=mouseZ3D;
                double t=(z3-z)/z2;
                //System.out.println(z3+"="+z+"+"+t+"*"+z2);
                //x=camera+t*dir is the mouse position
                Vector v3=(Vector)position.add(dir.mul(t));
                v3.set(3,1);

                /*System.out.println("mouse in 3d: ");
                for(int i=0;i<v3.getRows();i++){
                    for(int j=0;j<v3.getCols();j++){
                        double x=v3.get(i,j);
                        System.out.print(x+" ");
                    }
                    System.out.println("");
                }*/
                //move 3d-cursor to new position
                Vector m5= (Vector) v3.sub(mouseCursor3D.getNullVector());
                mouseCursor3D.addMovement(0,0,0,m5.get(0,0),m5.get(0,1),m5.get(0,2));
            }else{
                mouseCursor3D.addMovement(0,0,0,0,0,z3D_diff);
                mouseZ3D=tempMouseZ3D+z3D_diff;
                z3D_diff=0;
            }
            tempMouseX2D=mouseX2D;
            tempMouseY2D=mouseY2D;
            tempMouseZ3D=mouseZ3D;
        }
    }
    public void setZselected(boolean b){
        this.isZselected=b;
    }
    public boolean isZselected(){
        return isZselected;
    }
    public void setMouseCursor3D(Object3D o){
        this.mouseCursor3D =o;
        if(o!=null){
            this.mouseZ3D=this.mouseCursor3D.getNullVector().get(2);
        }
    }
    public Object3D getMouseCursor3D(){
        return mouseCursor3D;
    }
    public void setMouseParams(double z3d_diff){
        this.z3D_diff=z3d_diff*2.5;
    }
    public void setMouseParams(double x2d,double y2d){
        this.mouseX2D=x2d;
        this.mouseY2D=y2d;
    }
    public Vector getCameraPosition(){
        return this.cameraPosition;
    }
    public Vector getCameraUpVector() {
        return cameraUpVector;
    }
    public Vector getCameraTarget(){
        return cameraTarget;
    }
    public void setCameraPosition(Vector cameraPosition){
        this.cameraPosition =cameraPosition;
    }
    public void setCameraUpVector(Vector cameraUpVector){
        this.cameraUpVector=cameraUpVector;
    }
    public void setCameraTarget(Vector cameraTarget){
        this.cameraTarget=cameraTarget;
    }

    public Matrix2 getCameraTransformation(){
        return cameraTransformation;
    }
    public Matrix2 getPerspectiveTransformation(){
        return perspectiveTransformation;
    }
    public Matrix2 getViewPortTransformation(){
        synchronized (transformationLock){
            return viewPortTransformation;
        }
    }
    public BufferedImage getBufferedImage(){
        return image_current;
    }
    public void setBufferedImage(BufferedImage bi){
        this.image_current =bi;
        for(CameraPanel cp:cps)cp.repaint();
    }
    public CameraPanel getCameraPanel(Collection<Object3D> oms){
        CameraPanel cp=new CameraPanel(this,oms);
        this.cps.add(cp);
        return cp;
    }
}
