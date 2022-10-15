package Engine;

import Engine.Objects.Object3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by lynx on 02.06.17.
 */
public class CameraPanel extends JPanel {
    private ViewPort vp;
    private int lastMX=0,lastMY=0;
    private Collection<Object3D> objs;
    private CameraPanelMouseAdapter cpma;
    public CameraPanel(ViewPort vp, Collection<Object3D> objs){
        super();
        this.vp=vp;
        //this.setSize(vp.width,vp.height);
        this.setBackground(Color.green);
        this.setVisible(true);
        this.objs=objs;
        cpma=new CameraPanelMouseAdapter(vp,objs);
        this.addMouseMotionListener(cpma);
        this.addMouseListener(cpma);
        this.addMouseWheelListener(cpma);
    }
    public void setNotFiredMouseAdapter(MouseAdapter ma){
        cpma.setNotFiredMouseAdapter(ma);
    }
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x,y,width,height);
        vp.setSize(width,height);
    }
    @Override
    public void paint(Graphics g){
        g.drawImage(vp.getBufferedImage(),0,0,vp.getWidth(),vp.getHeight(),this);
    }

}
