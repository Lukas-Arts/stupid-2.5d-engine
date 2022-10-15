package Engine.Math;

/**
 * Created by lynx on 28.05.17.
 */
public class Matrix2{
    protected int rows,cols;
    protected double[][] a;
    protected Matrix2(){

    }
    public Matrix2(int rows,int cols){
        this.rows=rows;
        this.cols=cols;
        this.a =new double[rows][cols];
    }
    public Matrix2(double[][] a){
        this.a = a;
        this.rows= a.length;
        this.cols= a[0].length;
    }
    public int getRows(){
        return rows;
    }
    public int getCols(){
        return cols;
    }
    public double get(int row,int col){
        return a[row][col];
    }
    public void set(int row,int col, double val){
        this.a[row][col]=val;
    }
    public static Matrix2 getInstance(int rows,int cols){
        if(rows==1||cols==1)return new Vector(rows,cols);
            else return new Matrix2(rows,cols);
    }
    public double[][] getA(){
        return a;
    }
    public Matrix2 addRow(double[] d){
        if(d.length==cols)
        {
            Matrix2 m=getInstance(rows+1,cols);
            for(int i=0;i<rows;i++){
                for(int j=0;j<cols;j++){
                    m.set(i,j,get(i,j));
                }
            }
            for(int j=0;j<cols;j++){
                m.set(rows,j,d[j]);
            }
            return m;
        }else return null;
    }
    public Matrix2 addCol(double[] d){
        if(d.length==rows)
        {
            Matrix2 m=getInstance(rows,cols+1);
            for(int i=0;i<rows;i++){
                for(int j=0;j<cols;j++){
                    m.set(i,j,get(i,j));
                }
            }
            for(int j=0;j<rows;j++){
                m.set(j,cols,d[j]);
            }
            return m;
        }else return null;
    }
    public Matrix2 add(Matrix2 m){
        if(m.rows==rows&&m.cols==cols){
            Matrix2 m2=getInstance(rows,cols);
            for(int i=0;i<rows;i++)
                for(int j=0;j<cols;j++)
                    m2.set(i,j,this.a[i][j]+m.get(i,j));
            return m2;
        }
        return null;
    }

    public Matrix2 sub2(Matrix2 m,int ignore)
    {
        if(m.rows==rows&&m.cols==cols){
            Matrix2 m2=getInstance(rows,cols);
            for(int i=0;i<rows;i++)
            {
                for(int j=0;j<cols;j++){
                    if(j==ignore||i==ignore){
                        m2.set(i,j,1);
                    }else m2.set(i,j,this.a[i][j]-m.get(i,j));
                }
            }
            return m2;
        }
        return null;
    }
    public Matrix2 sub(Matrix2 m)
    {
        return sub2(m,-1);
    }
    public Matrix2 neg(){
        Matrix2 m2=getInstance(rows,cols);
        for(int i=0;i<rows;i++)
            for(int j=0;j<cols;j++)
                m2.set(i,j,this.a[i][j]*(-1));
        return m2;
    }
    public Matrix2 mul(double scalar){
        Matrix2 m2=getInstance(rows,cols);
        for(int i=0;i<rows;i++)
            for(int j=0;j<cols;j++)
                m2.set(i,j, a[i][j]*scalar);
        return m2;
    }
    public Matrix2 mul(Matrix2 a){
        if(cols==a.rows){
            Matrix2 m2=getInstance(rows,a.cols);
            for(int i=0;i<rows;i++){
                for(int j=0;j<a.cols;j++){
                    double cij=0;
                    for(int k=0;k<cols;k++){
                        cij+=this.a[i][k]*a.get(k,j);
                    }
                    m2.set(i,j,cij);
                }
            }
            return m2;
        }
        return null;
    }
    //transponierte matrix
    public Matrix2 trans(){
        Matrix2 m2=getInstance(cols,rows);
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                m2.set(j,i,a[i][j]);
            }
        }
        return m2;
    }
    public double det(){
        return det(this);
    }
    public static double det(Matrix2 A){
        if(A.rows==A.cols) {
            double res;
            // Trivial 1x1 matrix
            if (A.rows == 1)
                res = A.get(0, 0);
                // Trivial 2x2 matrix
            else if (A.cols == 2)
            {
                //System.out.println(A.toString());
                //System.out.println(A.get(0, 0) + "*" + A.get(1, 1) + "-" + A.get(1, 0) + "*" + A.get(0, 1));
                res = A.get(0, 0) * A.get(1, 1) - A.get(1, 0) * A.get(0, 1);
                // NxN matrix
            }else{
                res=0;
                for (int j1=0; j1<A.rows; j1++)
                {
                    res += Math.pow(-1.0, 1.0+j1+1.0) * A.get(0,j1) * det(generateSubArray (A, j1));
                }
            }
            return res;
        }
        System.err.println("Matrix must be quadratic");
        return 0;
    }
    public static Matrix2 generateSubArray (Matrix2 A, int j1){
        Matrix2 m=new Matrix2(A.rows-1,A.cols-1);
        //System.out.println(A.toString()+"\n"+j1);
        int i2=0;
        for (int i=0; i<A.rows; i++)
        {
            if(!(i == j1))
            {
                for (int j=1; j<A.cols; j++)
                {
                    m.set(i2,j-1,A.get(i,j));
                }
                i2++;
            }
        }
        return m;
    }
    public boolean equals(Matrix2 m){
        if(m!=null&&m.getRows()==getRows()&&m.getCols()==getCols()){
            for(int i=0;i<getRows();i++){
                for(int j=0;j<getCols();j++){
                    if(m.get(i,j)!=get(i,j))return false;
                }
            }
            return true;
        }
        return false;
    }
    public String toString(){
        StringBuilder s= new StringBuilder();
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                s.append(a[i][j]).append(" ");
            }
            s.append("\n");
        }
        return s.substring(0,s.length()-2);
    }
    public static void main(String[] args){
        //a.mul(b)=a.trans().mul(b.trans()).trans()

        double[][] a1 ={ {1,2,3},
                        {4,5,6},
                        {7,8,9}};
        double[][] a2 ={{1,2},{3,4},{5,6}};
        Matrix2 ma1=new Matrix2(a1);
        Matrix2 ma2=new Matrix2(a2);
        System.out.println(ma1.toString());
        System.out.println(ma2.toString());
        System.out.println(ma1.mul(ma2).toString());
        System.out.println(ma2.trans().mul(ma1.trans()).trans().toString());

        double[][] a3 ={ {1,0,0,0},
                        {0,1,0,0},
                        {0,0,1,0},
                        {5,5,5,1}};
        double[][] a4 ={ {0},    //relative coordinates
                        {0},
                        {0},
                        {1}};

        Matrix2 ma3=new Matrix2(a3);
        Matrix2 ma4=new Matrix2(a4);
        System.out.println(ma3.toString());
        System.out.println(ma4.toString());
        System.out.println(ma4.trans().toString());
        Matrix2 p1=ma4.trans().mul(ma3).trans();
        System.out.println(p1.toString());    //point defined relative to (5/5/5)

        double[][] a5 ={ {1,0,0,0},
                {0,1,0,0},
                {0,0,1,0},
                {p1.get(0,0),p1.get(1,0),p1.get(2,0),p1.get(3,0)}};
        double[][] a6 ={ {1},    //relative coordinates
                {1},
                {1},
                {1}};
        Matrix2 ma5=new Matrix2(a5);
        Matrix2 ma6=new Matrix2(a6);
        System.out.println(ma6.trans().mul(ma5).trans());   //point at (1/1/1) relative to the other point
        System.out.println(p1.mul(p1.trans()));
        System.out.println(p1.mul(p1));
        System.out.println(p1.getClass());

        double[][] a7 ={ {0,1,2},
                {3,2,1},
                {1,1,0}};
        Matrix2 ma7=new Matrix2(a7);
        System.out.println(ma7.det() +" "+ma7.trans().det());

        double[] a8 ={1,2,3};
        double[] a9 ={-7,8,9};
        Vector v1=new Vector(a8);
        Vector v2=new Vector(a9);
        Vector[] vs ={v1,v2};
        System.out.println("x: "+ Vector.cross(vs).toString());
        System.out.println(ma6.trans().toString());
        double[] a10 ={1,2,3,4};
        System.out.println(ma6.mul(1.5).addCol(a10).addCol(a10).trans().mul(Util.getBaseMatrix(4)).mul(Util.getBaseMatrix(4)).toString());

    }
}