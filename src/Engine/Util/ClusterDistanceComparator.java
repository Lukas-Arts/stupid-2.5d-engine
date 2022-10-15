package Engine.Util;

import Engine.Objects.Connection3D;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by lynx on 04.06.17.
 */
public class ClusterDistanceComparator implements Comparator<Pair<ArrayList<Connection3D>,Double>> {
    @Override
    public int compare(Pair<ArrayList<Connection3D>, Double> o1, Pair<ArrayList<Connection3D>, Double> o2) {
        return o1.getValue().compareTo(o2.getValue());
    }
}
