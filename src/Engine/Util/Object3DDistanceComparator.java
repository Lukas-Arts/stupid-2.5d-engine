package Engine.Util;

import Engine.Objects.Object3D;
import java.util.Comparator;

/**
 * Created by lynx on 04.06.17.
 */
public class Object3DDistanceComparator implements Comparator<Pair<Object3D,Double>> {
    public Object3DDistanceComparator(){
        super();
    }
    @Override
    public int compare(Pair<Object3D,Double> o1, Pair<Object3D,Double> o2) {
        return (int) (o1.getValue()-o2.getValue());
    }
}
