package Engine.Util;

import Engine.Math.Matrix2;
import Engine.Objects.Connection3D;
import Engine.Objects.Object3D;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by lynx on 05.06.17.
 */
public class Object3DUtil {

    public static double minRangeToNextObject(Collection<Object3D> objs, Object3D obj){
        double r=Integer.MAX_VALUE;
        for(Object3D obj2:objs){
            double d=obj.getNullVector().distance(obj2.getNullVector());
            r=Math.min(r,d);
        }
        return r;
    }

    public static ArrayList<Connection3D> getConnections(ArrayList<Connection3D> objs, Object3D obj){
        ArrayList<Connection3D> cons=new ArrayList<>();
        for(Connection3D con:objs)
            if(con.connects(obj))cons.add(con);
        return cons;
    }
    public static boolean areConnected(ArrayList<Connection3D> cons,Object3D o1,Object3D o2){
        for(Connection3D con:cons){
            if(con.connects(o1)&&con.connects(o2))return true;
        }
        return false;
    }
    public static int getConnectionCount(Object3D obj,ArrayList<Connection3D> cons){
        int connections=0;
        for(Connection3D con:cons){
            if(con.connects(obj)){
                connections++;
            }
        }
        return connections;
    }
    public static boolean isBaseMatrix(Matrix2 m){
        for(int i=0;i<m.getRows();i++){
            for (int j=0;j<m.getCols();j++){
                if(i==j){
                    if(m.get(i,j)!=1)return false;
                }else {
                    if(m.get(i,j)!=0)return false;
                }
            }
        }
        return true;
    }
}
