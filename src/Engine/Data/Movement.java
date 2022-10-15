package Engine.Data;

import Engine.Math.Matrix2;

/**
 * Created by lynx on 05.12.17.
 */
public class Movement {
    private Matrix2 moveMatrix;
    private int steps;
    public Movement(Matrix2 moveMatrix,int steps){
        this.moveMatrix=moveMatrix;
        this.steps=steps;
    }
    public Matrix2 getMoveMatrix() {
        return moveMatrix;
    }

    public void setMoveMatrix(Matrix2 moveMatrix) {
        this.moveMatrix = moveMatrix;
    }
    public void step(){
        steps--;
    }
    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
