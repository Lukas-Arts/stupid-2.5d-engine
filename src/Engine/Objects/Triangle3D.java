package Engine.Objects;

import Engine.Math.Vector;

/**
 * Created by lynx on 01.06.17.
 */
public class Triangle3D extends Object3D {
    Line3D l1;
    Line3D l2;
    Line3D l3;
    public Triangle3D(Vector nullVector, Vector v1_rel_null, Vector v2_rel_v1, Vector v3_rel_v2) {
        super(nullVector);
        System.err.println(v1_rel_null.toString()+" | "+v2_rel_v1.toString()+" | "+v3_rel_v2.toString());
        Vector sum=(Vector)v1_rel_null.add(v2_rel_v1).add(v3_rel_v2);
        if(!(sum.get(0)==0&&sum.get(1)==0&&sum.get(2)==0)){
            System.err.println("ERRO: Triangle Lines must join in null: "+sum.toString());
        }
        double d[]={0,0,0,1};
        Vector v=new Vector(d);
        l1=new Line3D(v,v1_rel_null,1);
        l2=new Line3D(v,v2_rel_v1,1);
        l3=new Line3D(v,v3_rel_v2,1);
        l1.addLine3D(l2);
        l2.addLine3D(l3);
        this.addObject3D(l1);
    }

}
