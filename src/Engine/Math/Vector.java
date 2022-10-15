package Engine.Math;

/**
 * Created by lynx on 29.05.17.
 */
public class Vector extends Matrix2{
    public static final Vector nullVector=new Vector(new double[]{0,0,0,1});
    protected Vector(){

    }
    public Vector(int rows, int cols) {
        if((rows!=1&&cols!=1))System.err.println("No Vector! rows: "+rows+" cols: "+cols);
        this.rows=rows;
        this.cols=cols;
        this.a =new double[rows][cols];
    }
    public Vector(double[] a) {
        super();
        this.a=new double[1][a.length]; //colum-vector .trans for row-vector
        System.arraycopy(a, 0, this.a[0], 0, a.length);
        this.rows=1;
        this.cols=a.length;
    }
    public double distance(Vector v){
        int length=Math.max(rows,cols);
        double x=0;
        for(int i=0;i<length;i++){
            x+=Math.pow(get(i)-v.get(i),2);
        }
        return Math.sqrt(x);
    }
    public static double distance(Vector v1,Vector v2){
        return v1.distance(v2);
    }
    public double get(int i){
        if(rows>cols)return get(i,0);
            else return get(0,i);
    }
    public void set(int i,double val){
        if(rows>cols)set(i,0,val);
            else set(0,i,val);
    }
    //only for 4d
    public Vector norm(){
        final double[] centerV={0,0,0,1};
        final Vector center=new Vector(centerV);
        //System.out.println(distance(center));
        return (Vector)this.mul(1/distance(center));
    }
    public static Vector cross(Vector[] vs){
        int n=vs.length+1;
        Vector res=new Vector(1,n);
        Matrix2 temp=new Matrix2(n,n);
        for(int i=0;i<n;i++){
            for(int j=0;j<n;j++){
                if(j==0)temp.set(i,j,1);
                    else temp.set(i,j,vs[j-1].get(i));
            }
        }
        //System.out.println("M: "+temp);
        for(int i=0;i<n;i++){
            Matrix2 temp2=Matrix2.generateSubArray(temp,i);
            //System.out.println("M"+i+": "+temp2);
            res.set(i,Math.pow(-1,i)*temp2.det());
        }
        return res;
    }
    public int dim(){
        return Math.max(getRows(),getCols());
    }
}
