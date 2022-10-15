package Engine.Math;

/**
 * Created by lynx on 29.05.17.
 */
public class Util {
    //all stuff for 4d-matrices
    public static final int XAXIS=0,YAXIS=1,ZAXIS=2;
    public static Matrix2 getTurnMatrix(int axis, double alpha_deg,boolean passive){
        return getTM(axis,Math.toRadians(alpha_deg),passive);
    }
    private static Matrix2 getTM(int axis, double alpha,boolean passive){
        Matrix2 m;
        switch (axis){
            case XAXIS:{
                double[][] a={{1,          0,           0,0},
                        {0,Math.cos(alpha),Math.sin(alpha),0},
                        {0,-Math.sin(alpha), Math.cos(alpha),0},
                        {0,0,0,1}};
                m=new Matrix2(a);
                break;
            }
            case YAXIS:{
                double[][] a={{Math.cos(alpha),0,-Math.sin(alpha),0},
                        {0,1,0,0},
                        {Math.sin(alpha),0,Math.cos(alpha),0},
                        {0,0,0,1}};
                m=new Matrix2(a);
                break;
            }
            case ZAXIS:{
                double[][] a={{Math.cos(alpha),Math.sin(alpha),0,0},
                        {-Math.sin(alpha), Math.cos(alpha),0,0},
                        {0,           0,1,0},
                        {0,0,0,1}};
                m=new Matrix2(a);
                break;
            }
            default:{
                return null;
            }
        }
        if(passive)m=m.trans();
        return m;
    }
    public static Matrix2 getTurnMatrixAroundOrigin(Vector v,double alpha_deg){
        return getTurnMatrixAroundOrigin2(v,Math.toRadians(alpha_deg));
    }
    private static Matrix2 getTurnMatrixAroundOrigin2(Vector v,double alpha){
        double[][] a={
                {Math.pow(v.get(0),2)*(1-Math.cos(alpha))+Math.cos(alpha),    v.get(0)*v.get(1)*(1-Math.cos(alpha))-v.get(2)*Math.sin(alpha),     v.get(0)*v.get(2)*(1-Math.cos(alpha))+v.get(1)*Math.sin(alpha),0},
                {v.get(1)*v.get(0)*(1-Math.cos(alpha))+v.get(2)*Math.sin(alpha),    Math.pow(v.get(1),2)*(1-Math.cos(alpha))+Math.cos(alpha),     v.get(1)*v.get(2)*(1-Math.cos(alpha))-v.get(0)*Math.sin(alpha),0},
                {v.get(2)*v.get(0)*(1-Math.cos(alpha))-v.get(1)*Math.sin(alpha),    v.get(2)*v.get(1)*(1-Math.cos(alpha))+v.get(0)*Math.sin(alpha),     Math.pow(v.get(2),2)*(1-Math.cos(alpha))+Math.cos(alpha),0},
                {0,0,0,1}};
        Matrix2 m=new Matrix2(a);
        return m;
    }
    public static Matrix2 getTurnMatrix(double x_deg,double y_deg,double z_deg,boolean passive){
        Matrix2 x=getTurnMatrix(XAXIS,x_deg,passive);
        Matrix2 y=getTurnMatrix(YAXIS,y_deg,passive);
        Matrix2 z=getTurnMatrix(ZAXIS,z_deg,passive);
        return x.mul(y).mul(z);
    }
    public static Matrix2 getTransMatrix(double dx,double dy,double dz,double dwtf){

        double[][] a={
                { 1, 0, 0,   0},
                { 0, 1, 0,   0},
                { 0, 0, 1,   0},
                {dx,dy,dz,dwtf}};
        return new Matrix2(a);
    }
    public static Vector[] getCameraAxis(Vector cameraUpVector, Vector cameraPosition,Vector cameraTarget){
        Vector zaxis=((Vector)cameraPosition.sub(cameraTarget)).norm();
        Vector[] vs={cameraUpVector,zaxis};
        Vector xaxis= Vector.cross(vs).norm();
        Vector[] vs2={zaxis,xaxis};
        Vector yaxis= Vector.cross(vs2).norm();
        return new Vector[]{xaxis,yaxis,zaxis};
    }
    public static Matrix2 getCameraTransformation(Vector cameraPoisition, Vector cameraTarget, Vector cameraUpVector){
        if(cameraPoisition.dim()>3||cameraTarget.dim()>3||cameraUpVector.dim()>3)
            System.out.println("Attention: Vectors for camera calculation have to be of dim=3!");
        Vector[] vs=getCameraAxis(cameraUpVector,cameraPoisition,cameraTarget);
        Vector xaxis= vs[0];
        Vector yaxis= vs[1];
        Vector zaxis=vs[2];
        Matrix2 cameraPositionTrans=cameraPoisition.trans();
        double[][] dCamerTransformation=
                {{xaxis.get(0),yaxis.get(0),zaxis.get(0),0},
                 {xaxis.get(1),yaxis.get(1),zaxis.get(1),0},
                 {xaxis.get(2),yaxis.get(2),zaxis.get(2),0},
                 {-xaxis.mul(cameraPositionTrans).get(0,0),-yaxis.mul(cameraPositionTrans).get(0,0),-zaxis.mul(cameraPositionTrans).get(0,0),1}};
        return new Matrix2(dCamerTransformation);
    }
    public static Matrix2 getPerspective2(double fieldOfView,double aspectRatio,double far,double near){
        double h=1.0/Math.tan(fieldOfView/2.0);
        double w=h/aspectRatio;
        return getPerspective(w,h,far,near);
    }

    /**
     *
     * @param w h/aspectRatio                   (Seitenverhältnis Zielbild)
     * @param h 1.0/Engine.Math.tan(fieldOfView/2.0)   (Öffnungswinkel Kamera)
     * @param far   Weiteste Distanz, die sichtbar sein soll
     * @param near  Kleinste Distanz, die sichtbar sein soll
     * @return
     */
    public static Matrix2 getPerspective(double w,double h,double far,double near){
        double[][] d={  {w,0,0,0},
                        {0,h,0,0},
                        {0,0,far/(near-far),-1},
                        {0,0,(near*far)/(near-far),0}};
        Matrix2 m=new Matrix2(d);
        return m;
    }
    public static Matrix2 getParallel(double w,double h,double far,double near){
        double[][] d={  {2.0/w,0,0,0},
                {0,2.0/h,0,0},
                {0,0,1/(near-far),-1},
                {0,0,(near)/(near-far),0}};
        Matrix2 m=new Matrix2(d);
        return m;
    }
    //translate and rescale image for screen
    public static Vector getWindowViewPortTransformation(double vpX, double vpY, double vpMinZ, double vpMaxZ, Vector v,
                                                         double vpWidth, double vpHeight){
        double[] d={vpX+(1.0*v.get(0))*vpWidth/2.0,
                    vpY+(1.0*v.get(1))*vpHeight/2.0,
                    vpMinZ+v.get(2)*(vpMaxZ-vpMinZ)};
        return new Vector(d);
    }
    public static Matrix2 getBaseMatrix(int base){
        Matrix2 b=new Matrix2(base,base);
        for(int i=0;i<base;i++){
            for(int j=0;j<base;j++){
                if(i==j)b.set(i,j,1);
                    else b.set(i,j,0);
            }
        }
        return b;
    }
    public static int mod(int x, int y)
    {
        int result = x % y;
        if (result < 0)
        {
            result += y;
        }
        return result;
    }
}
