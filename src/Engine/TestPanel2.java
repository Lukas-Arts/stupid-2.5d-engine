package Engine;

import Engine.Math.Util;
import Engine.Math.Vector;
import Engine.Objects.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by lynx on 30.05.17.
 */
public class TestPanel2 extends JPanel{
    ViewPort vp;
    ViewPort vp2;
    World myWorld;
    public TestPanel2(){
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

        ArrayList<Object3D> objs=new ArrayList<>();
        objs.addAll(World.getAxisObjects());

        double r=0;
        double i=0;
        while(i<360){
            double x=Math.sin(Math.toRadians(i))*r;
            double y=Math.cos(Math.toRadians(i))*r;

            double r2=r/300;    //max 2
            double d=Math.pow(Math.E,-Math.pow(r2,2));  //0-1
            System.out.println("prob: "+d);
            for(int j=0;j<d*15;j++){
                objs.add(getRandomStar(d,x,y,50,30,30,30));
                objs.add(getRandomStar(d,-x,-y,50,30,30,30));
                objs.add(getRandomStar(d,-y,x,50,30,30,30));
                objs.add(getRandomStar(d,y,-x,50,30,30,30));
            }
            r+=5;
            i+=5;
        }

        System.out.println("objs: "+objs.size());
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
    Random rand=new Random();
    private Object3D getRandomStar(double prob, double x,double y,double z,double maxDx,double maxDy,double maxDz){
        double x2=x+((int)(prob*maxDx)==0?0:rand.nextInt()%(int)(prob*maxDx));
        double y2=y+((int)(prob*maxDy)==0?0:rand.nextInt()%(int)(prob*maxDy));
        double z2=z+((int)(prob*maxDz)==0?0:rand.nextInt()%(int)(prob*maxDz));
        Object3D o=new Object3D(new double[]{x2,y2,z2,1.0});
        o.addMovement(Util.getTurnMatrix(0,0,1,false),Integer.MAX_VALUE);
        o.setBounds(new Rectangle(0,0,1,1));
        return o;
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