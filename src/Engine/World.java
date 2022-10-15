package Engine;

import Engine.Math.Vector;
import Engine.Objects.Connection3D;
import Engine.Objects.Object3D;
import Engine.Util.ZBufferComparator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.*;

/**
 * Created by lynx on 30.05.17.
 */
public class World implements Runnable{
    private Collection<Object3D> objs =new HashSet<>();
    private ArrayList<ViewPort> vps=new ArrayList<>();
    private int step=0;
    private Thread th;
    private boolean running=false;
    private boolean end=false;
    private boolean showTimeBetweenFrames=false;
    private HashSet<Object3D> objsToAdd=new HashSet<>();
    private HashSet<Object3D> objsToRemove=new HashSet<>();
    private Object lock=new Object();
    private ArrayList<Integer> lastFrameTimes=new ArrayList<>();
    private boolean showFPS=true;
    private int FPS=40;
    public World(Collection<Object3D> objs, ArrayList<ViewPort> vps){
        th=new Thread(this);
        this.objs = objs;
        this.vps=vps;
    }
    public int getFPS(){
        return FPS;
    }
    public void setFPS(int fps){
        FPS=fps;
    }
    public void toggleShowFPS(){
        this.showFPS= !showFPS;
    }
    public void toggleShowTimeBetweenFrames(){
        this.showTimeBetweenFrames= !showTimeBetweenFrames;
    }
    public void doStep(){
        step++;
        int fps=0;
        if(showFPS){
            int dFrameTime=0;
            for(int i:lastFrameTimes)
                dFrameTime+=i;
            if(lastFrameTimes.size()>0){
                dFrameTime=dFrameTime/lastFrameTimes.size();
                fps=1000/dFrameTime;
            }
        }
        //System.out.println("doing step.. "+(step));
        HashMap<ViewPort,ArrayList<Object3D>> vpZBuffer=new HashMap<>();
        for(ViewPort vp:vps){
            vp.doMoveStep();
            vpZBuffer.put(vp,new ArrayList<>());
        }
        for(Object3D oin:objs){
            //System.out.println(oin.getNullVector());
            oin.doMovementStep();
            for(ViewPort vp:vps){
                oin.doCameraStep(vp,vp.getViewPortTransformation());
                vpZBuffer.get(vp).addAll(getAllObjects(oin));
            }
            //System.out.println();
        }
        for(ViewPort vp:vps){
            BufferedImage img=new BufferedImage(vp.getWidth(),vp.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g=img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.black);
            g.fillRect(0,0,vp.getWidth(),vp.getHeight());
            ArrayList<Object3D> zBuffer=vpZBuffer.get(vp);
            zBuffer.sort(new ZBufferComparator(vp));
            for(Object3D p:zBuffer){
                Color c=p.getColor();
                int alpha=255-Math.min(Math.max(0,-200+(int)p.getWorldVector(vp).get(3)),230);
                p.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),alpha));
                p.paint(g,vp.getWidth()/2,vp.getHeight()/2,vp);
            }
            if(showFPS)g.drawString("FPS: "+fps,img.getWidth()-100,100);
            vp.setBufferedImage(img);
        }
    }
    private int getStep(){
        return step;
    }
    public static ArrayList<Object3D> getAllObjects(ArrayList<Object3D> objs){
        ArrayList<Object3D> out=new ArrayList<>();
        for(Object3D o:objs)out.addAll(getAllObjects(o));
        return out;
    }
    public ArrayList<ViewPort> getViewPorts(){
        return vps;
    }
    public void addObj(Object3D obj){
        objsToAdd.add(obj);
    }
    public void addObjs(Collection<Object3D> objs){
        objsToAdd.addAll(objs);
    }
    public void removeObj(Object3D obj){
        objsToRemove.add(obj);
    }
    public void removeObjs(Collection<Object3D> objs){
        objsToRemove.addAll(objs);
    }
    public Collection<Object3D> getObjs(){
        return objs;
    }
    public static ArrayList<Object3D> getAllObjects(Object3D obj){
        ArrayList<Object3D> objs=new ArrayList<>();
        objs.add(obj);
        for(Object3D o:obj.getObjects3D())
            objs.addAll(getAllObjects(o));
        return objs;
    }
    public static Collection<Object3D> getAllClickableObjects(Collection<Object3D> objs){
        Collection<Object3D> out=new HashSet<>();
        for(Object3D o:objs)out.addAll(getAllObjects(o));
        return out;
    }
    public static ArrayList<Object3D> getAllClickableObjects(Object3D obj){
        ArrayList<Object3D> objs=new ArrayList<>();
        if(obj.getMouseAdapters().size()>0)objs.add(obj);
        for(Object3D o:obj.getObjects3D())
            objs.addAll(getAllObjects(o));
        return objs;
    }

    public static ArrayList<Object3D> getAxisObjects(){
        ArrayList<Object3D> axisObjects=new ArrayList<>();
        double[] dCenter={0,0,0,1};
        Object3D center=new Object3D(dCenter,"Center");
        double[] dX={100,0,0,1};
        Object3D x=new Object3D(dX,"X");
        double[] dY={0,100,0,1};
        Object3D y=new Object3D(dY,"Y");
        double[] dZ={0,0,100,1};
        Object3D z=new Object3D(dZ,"Z");
        x.toggleShowPointOnScreen();
        y.toggleShowPointOnScreen();
        z.toggleShowPointOnScreen();
        center.toggleShowPointOnScreen();
        center.addObject3D(x);
        center.addObject3D(y);
        center.addObject3D(z);
        axisObjects.add(center);
        axisObjects.add(new Connection3D(center,x,Color.RED));
        axisObjects.add(new Connection3D(center,y,Color.green));
        axisObjects.add(new Connection3D(center,z,Color.blue));
        return axisObjects;
    }
    public Thread start(){
        running=true;
        if(!th.isAlive())th.start();
        return th;
    }
    public void stop(){
        synchronized (this) {
            running = false;
        }
    }

    /**
     * kills the world-thread
     */
    public void end(){
        stop();
        end=true;
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ended world: "+end+"/"+running);
    }
    @Override
    public void run() {
        while (!end){
            if (running) {
                Instant i = Instant.now();
                this.doStep();
                if(objsToAdd.size()!=0){
                    this.objs.addAll(objsToAdd);
                    this.objsToAdd.clear();
                }
                if(objsToRemove.size()!=0){
                    this.objs.removeAll(objsToRemove);
                    this.objsToRemove.clear();
                }
                int pauseMillis = (1000/FPS) - (int) (Instant.now().toEpochMilli() - i.toEpochMilli());
                if (showTimeBetweenFrames)
                    System.out.println(this.getStep() + ": " + pauseMillis + "ms");
                try {
                    Thread.sleep((pauseMillis > 0 ? pauseMillis : 0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(showFPS){
                    lastFrameTimes.add((int)(Instant.now().toEpochMilli()-i.toEpochMilli()));
                    if(lastFrameTimes.size()>5)lastFrameTimes.remove(0);
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("running: "+running+" end: "+end);
    }

    public static World createWorld(){
        double dCameraPosition[]={50,50,50};
        Vector cameraPoisition=new Vector(dCameraPosition);
        double dCameraTarget[]={0,0,30};
        Vector cameraTarget=new Vector(dCameraTarget);
        double dCameraUpVector[]={0,0,-1};
        Vector cameraUpVector=new Vector(dCameraUpVector);
        ViewPort vp=new ViewPort(cameraPoisition,cameraTarget,cameraUpVector,16.0/9.0,60,100,1,500,500);
        ArrayList<ViewPort> vps=new ArrayList<>();
        vps.add(vp);
        Collection<Object3D> objs=new HashSet<>();
        objs.addAll(World.getAxisObjects());
        World myWorld=new World(objs,vps);
        return myWorld;
    }
}
