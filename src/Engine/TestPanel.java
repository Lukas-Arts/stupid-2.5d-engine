package Engine;

import Engine.Math.Util;
import Engine.Math.Vector;
import Engine.Objects.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lynx on 30.05.17.
 */
public class TestPanel extends JPanel{
    ViewPort vp;
    ViewPort vp2;
    World myWorld;
    public TestPanel(){
        super();
        double dCameraPosition[]={50,50,50};
        Vector cameraPoisition=new Vector(dCameraPosition);
        double dCameraTarget[]={0,0,30};
        Vector cameraTarget=new Vector(dCameraTarget);
        double dCameraUpVector[]={0,0,-1};
        Vector cameraUpVector=new Vector(dCameraUpVector);
        vp=new ViewPort(cameraPoisition,cameraTarget,cameraUpVector,16.0/9.0,60,100,1,500,500);

        double dCameraPosition2[]={50,-50,70};
        Vector cameraPoisition2=new Vector(dCameraPosition2);
        vp2=new ViewPort(cameraPoisition2,cameraTarget,cameraUpVector,16.0/9.0,60,100,1,500,500);
        this.setSize(vp.getWidth(),vp.getHeight());
        this.setBackground(Color.blue);
        this.setVisible(true);

        double[] dP1={150,100,50,1};
        Object3D p1= null;
        try {
            p1 = new Planet(new Vector(dP1),"P1", ImageIO.read(new File("./assets/planets/planet1.png")),this);
            //p1.setBounds(new Ellipse2D.Double(-25,-25,50,50));
        } catch (IOException e) {
            e.printStackTrace();
        }
        double[] dP2={80,0,0,1};
        Planet p2= null;
        try {
            p2 = new Planet(new Vector(dP2),"P2", ImageIO.read(new File("./assets/planets/charon.png")),this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        p1.addMovement(Util.getTurnMatrix(0,0,1,false),Integer.MAX_VALUE);
        p2.addMovement(Util.getTurnMatrix(0,0,2,false),Integer.MAX_VALUE);
        p1.toggleShowCoordinatesOnScreen();
        p2.toggleShowCoordinatesOnScreen();
        p1.addObject3D(p2);
        ArrayList<Object3D> objs=new ArrayList<>();

        double[] dP3={150,0,50,1};
        Planet p3= null;
        double[] dP4={0,-150,50,1};
        Planet p4= null;
        double[] dP5={75,-75,50,1};
        Planet p5= null;
        double[] dP6={-75,-225,50,1};
        Planet p6= null;
        double[] dP7={-150,-300,50,1};
        Planet p7= null;
        try {
            p3 = new Planet(new Vector(dP3),"P3", ImageIO.read(new File("./assets/planets/charon.png")),this);
            p4 = new Planet(new Vector(dP4),"P4", ImageIO.read(new File("./assets/planets/charon.png")),this);
            p5 = new Planet(new Vector(dP5),"P5", ImageIO.read(new File("./assets/planets/charon.png")),this);
            p6 = new Planet(new Vector(dP6),"P6", ImageIO.read(new File("./assets/planets/charon.png")),this);
            p7 = new Planet(new Vector(dP7),"P7", ImageIO.read(new File("./assets/planets/charon.png")),this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        p3.toggleShowCoordinatesOnScreen();
        p4.toggleShowCoordinatesOnScreen();
        p5.toggleShowCoordinatesOnScreen();
        p6.toggleShowCoordinatesOnScreen();
        p7.toggleScale();
        objs.add(p3);
        objs.add(p4);
        objs.add(p5);
        objs.add(p6);
        objs.add(p7);
        objs.addAll(World.getAxisObjects());
        Line3D l=null;
        Line3D lastLine=null;
        Random r=new Random();
        double d1[]={0,0,0,1};
        Vector nv=new Vector(d1);
        for(int i=0;i<10;i++){
            double d[]={(r.nextInt()%50)+r.nextDouble(),(r.nextInt()%50)+r.nextDouble(),(r.nextInt()%50)+r.nextDouble(),1};
            Vector nextVector=new Vector(d);
            Line3D nextLine=new Line3D(nv,nextVector);
            if(i==0){
                l=nextLine;
            }else {
                lastLine.addLine3D(nextLine);
            }
            lastLine=nextLine;
        }
        objs.add(l);
        objs.add(p1);
        ArrayList<Connection3D> cons=getRoundBaseGrid();
        for(Connection3D c:cons){
            objs.add(c.getObject1());
            objs.add(c.getObject2());
        }
        objs.addAll(cons);
        double d[]={0,0,50,1};
        try {
            Planet gs=new Planet(new Vector(d),"Sun1", ImageIO.read(new File("./assets/stars/star1_big.png")),this);
            gs.toggleShowCoordinatesOnScreen();
            objs.add(gs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        double d2[]={-50,50,100,1};
        Vector v1=new Vector(d2);
        double d3[]={-10,0,10,1};
        Vector v2=new Vector(d3);
        double d4[]={0,10,0,1};
        Vector v3=new Vector(d4);
        double d5[]={10,-10,-10,1};
        Vector v4=new Vector(d5);
        Triangle3D t=new Triangle3D(v1,v2,v3,v4);
        objs.add(t);
        objs.add(new Connection3D(p1,p2));

        System.out.println("objs: ");
        for(Object3D o:objs)System.out.println(o.getNullVector());
        ArrayList<ViewPort> vps=new ArrayList<>();
        vps.add(vp);
        //vps.add(vp2);
        myWorld=new World(objs,vps);
        this.setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
        this.add(vp.getCameraPanel(World.getAllClickableObjects(objs)));
        //this.add(vp2.getCameraPanel());
        myWorld.start();
    }
    public ArrayList<Object3D> vectorsToObjects(ArrayList<Vector> vectors){
        ArrayList<Object3D> objs=new ArrayList<>();
        for(Vector v:vectors){
            objs.add(new Object3D(v));
        }
        return objs;
    }
    public ArrayList<Connection3D> getRoundBaseGrid(){
        double[] d={300,0,0,1};
        Vector v=new Vector(d);
        ArrayList<Object3D> vectors=new ArrayList<>();
        ArrayList<Connection3D> lines=new ArrayList<>();
        int amount=36;
        for(int i=0;i<amount+1;i++){
            Object3D obj=new Object3D((Vector)v.mul(Util.getTurnMatrix(0,0,i*(360/amount),false)));
            obj.toggleShowPointOnScreen();
            vectors.add(obj);
            if(i>0){
                Connection3D l=new Connection3D(vectors.get(i-1),vectors.get(i));
                l.toggleShowPointOnScreen();
                lines.add(l);

            }
        }
        for(int i=0;i<amount+1;i++){
            int i2=amount/2-i;
            if(i2>amount)i2-=amount;
            if(i2<0)i2+=amount;
            Connection3D l=new Connection3D(vectors.get(i),vectors.get(i2));
            l.toggleShowPointOnScreen();
            int i4=i2+amount/2;
            System.out.println(i4);
            if(i4>amount)i4-=amount;
            if(i4<0)i4+=amount;
            Connection3D l2=new Connection3D(vectors.get(i),vectors.get(i4));
            l2.toggleShowPointOnScreen();
            lines.add(l);
            lines.add(l2);
        }
        return lines;
    }
}
