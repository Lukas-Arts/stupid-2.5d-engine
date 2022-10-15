package Engine.Util;

import Engine.Objects.Connection3D;
import Engine.Objects.Line3D;
import Engine.Objects.Object3D;
import Engine.ViewPort;

import java.util.Comparator;

/**
 * Created by lynx on 31.05.17.
 */
public class ZBufferComparator implements Comparator<Object3D> {
    private ViewPort vp;
    public ZBufferComparator(ViewPort vp){
        super();
        this.vp=vp;
    }
    private double getCompareValue(Object3D o){
        if(o instanceof Line3D){
            return Math.max(o.getWorldVector(vp).get(3),o.getObject3D(0).getWorldVector(vp).get(3));
        }else if(o instanceof Connection3D){
            return Math.max(((Connection3D) o).getObject1().getWorldVector(vp).get(3),((Connection3D) o).getObject2().getWorldVector(vp).get(3));
        }else return o.getWorldVector(vp).get(3);
    }
    @Override
    public int compare(Object3D o1, Object3D o2) {
        return Double.compare(getCompareValue(o2),getCompareValue(o1));
    }
}
