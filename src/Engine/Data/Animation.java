package Engine.Data;

import java.awt.image.BufferedImage;

/**
 * Created by lynx on 03.12.17.
 */
public class Animation {
    private BufferedImage imgs[];
    private int frames, msPerFrame;
    public Animation(BufferedImage imgs[], int msPerFrame){
        this.frames=imgs.length;
        this.imgs=imgs;
        this.msPerFrame=msPerFrame;
    }

    public BufferedImage[] getImgs() {
        return imgs;
    }

    public int getFrames() {
        return frames;
    }

    public int getMsPerFrame() {
        return msPerFrame;
    }

}
