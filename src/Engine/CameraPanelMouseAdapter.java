package Engine;

import Engine.Data.ViewPortMovement;
import Engine.Math.Matrix2;
import Engine.Math.Util;
import Engine.Math.Vector;
import Engine.Objects.Object3D;
import Engine.Util.ZBufferComparator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Created by lynx on 05.06.17.
 */
public class CameraPanelMouseAdapter extends MouseAdapter {
    protected ViewPort vp;
    protected int lastMX=0,lastMY=0;
    protected Collection<Object3D> objs;
    protected MouseAdapter notFiredMouseAdapter=null;
    public CameraPanelMouseAdapter(ViewPort vp,Collection<Object3D> objs){
        this.vp=vp;
        this.objs=objs;
    }
    public void setNotFiredMouseAdapter(MouseAdapter ma){
        this.notFiredMouseAdapter=ma;
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        Vector v1=this.vp.getCameraTarget();
        Vector v2=this.vp.getCameraPosition();
        Vector v3=this.vp.getCameraUpVector();
        double dx=(e.getX()-lastMX)*0.75;
        double dy=(e.getY()-lastMY)*0.75;
        dx=Math.min(5,Math.max(-5,dx));
        dy=Math.min(5,Math.max(-5,dy));
        System.out.println("Mouse dragged: "+dx+"/"+dy+" Button: "+e.getButton()+" Button: "+e.getModifiers()+" "+e.getModifiersEx());

        Vector v4=Vector.cross(new Vector[]{v3,(Vector)v1.sub(v2)}).norm();
        //Matrix2 m1=Util.getTransMatrix(v1.get(0),v1.get(1),v1.get(2),1);
        Matrix2 m2=Util.getTurnMatrixAroundOrigin(v3,dx);   //turn around camera-system-z(/vertical)-vector for horizontal movement
        Matrix2 m3=Util.getTurnMatrixAroundOrigin(v4,dy);   //turn around camera-system-x(/horizontal)-vector for vertical movement
        //Matrix2 m4=Util.getTransMatrix(-v1.get(0),-v1.get(1),-v1.get(2),1);
        vp.addMovement(new ViewPortMovement(m3.mul(m2),0,false));

        lastMX=e.getX();
        lastMY=e.getY();
    }
    private ArrayList<Object3D> getAffectedObjects(MouseEvent e){
        ArrayList<Object3D> affected=new ArrayList<>();
        for(Object3D o:objs){
            o.hover(false);
            Vector v=o.getWorldVector(vp);
            if(o.getMouseAdapters().size()>0&&v!=null){
                if(o.getBounds().contains(e.getX()-v.get(0)-vp.getWidth()/2,e.getY()-v.get(1)-vp.getHeight()/2))
                {
                    affected.add(o);
                }
            }
        }
        return affected;
    }
    public void notifyAdapter(ArrayList<Object3D> affected,MouseEvent e, BiConsumer<MouseAdapter,MouseEvent> c){
        boolean fired=false;
        if(affected.size()>0){
            affected.sort(new ZBufferComparator(vp));
            Object3D o=affected.get(affected.size()-1);
            for(MouseAdapter ma:o.getMouseAdapters()){
                c.accept(ma,e);
                fired=true;
            }
        }
        if(!fired&&notFiredMouseAdapter!=null){
            c.accept(notFiredMouseAdapter,e);
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        notifyAdapter(getAffectedObjects(e),e, MouseAdapter::mouseMoved);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse Clicked: "+System.currentTimeMillis()+"ms");
        notifyAdapter(getAffectedObjects(e),e, MouseAdapter::mouseClicked);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        notifyAdapter(getAffectedObjects(e),e, MouseAdapter::mousePressed);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        notifyAdapter(getAffectedObjects(e),e, MouseAdapter::mouseReleased);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        notifyAdapter(getAffectedObjects(e),e, MouseAdapter::mouseEntered);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        notifyAdapter(getAffectedObjects(e),e, MouseAdapter::mouseExited);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e){

        double d=e.getScrollAmount()*e.getWheelRotation();
        Vector v1=this.vp.getCameraTarget();
        Vector v2=this.vp.getCameraPosition();
        Vector v3=(Vector)((Vector)v1.sub(v2)).norm().mul(d);
        this.vp.addMovement(new ViewPortMovement(Util.getTransMatrix(v3.get(0),v3.get(1),v3.get(2),1),0,true));
        System.out.println(" v2: "+v2.toString());
        System.out.println(" v1: "+v1.toString());
        System.out.println(d+" "+e.getWheelRotation()+" v: "+v3.toString()+" "+e.getScrollAmount()+" "+e.getScrollType()+" "+e.getPreciseWheelRotation());

    }
}
