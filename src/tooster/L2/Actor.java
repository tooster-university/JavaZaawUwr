package tooster.L2;

import java.beans.*;

import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import javafx.concurrent.Task;
import sun.plugin2.util.SystemUtil;

import java.util.Random;

public class Actor extends Task<Void> {


    private static Random random = new Random();
    private int actorId;

    private VetoableChangeSupport vcs = new VetoableChangeSupport(this);
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    // returns probability for move in given direction where dR dC are delta of zero position minus self position
    // probabilities sum up to 100. Returned array in form [up, left], down and right is implicit 50 - < up | left >
    protected int[] probabilityCalculator() {
        Controller.Pos zeroPos = Controller.getInstance().getPos(0);
        Controller.Pos pos = Controller.getInstance().getPos(actorId);
        int deltaRow = zeroPos.getRow() - pos.getRow(), deltaCol = zeroPos.getCol() - pos.getCol();
        double factor = (-20.0) / 9.0;
        return new int[]{
                25 + (int) (Math.signum(-deltaRow) * (20 + factor * Math.abs(deltaRow))),
                25 + (int) (Math.signum(-deltaCol) * (20 + factor * Math.abs(deltaCol)))};
    }

    @NotNull
    protected Controller.DIRECTION nextDirection() {
        int[] prob = probabilityCalculator(); // probabilities
        Controller.Pos pos = Controller.getInstance().getPos(actorId);

        int roll = random.nextInt(100);
        Controller.DIRECTION direction;
        // probability: [0 ~> prob[0]):UP [prob[0] ~> 50):DOWN [50 ~> prob[1]):UP [prob[1] ~> 100):DOWN
        if (roll < prob[0]) direction = Controller.DIRECTION.UP;
        else if (roll < 50) direction = Controller.DIRECTION.DOWN;
        else if (roll < 50 + prob[1]) direction = Controller.DIRECTION.LEFT;
        else direction = Controller.DIRECTION.RIGHT;

        // those two lines assure, that move is always inside the board
        if (pos.getRow() == 0 && direction == Controller.DIRECTION.UP) return Controller.DIRECTION.DOWN;
        if (pos.getRow() == Controller.size - 1 && direction == Controller.DIRECTION.DOWN)
            return Controller.DIRECTION.UP;
        if (pos.getCol() == 0 && direction == Controller.DIRECTION.LEFT) return Controller.DIRECTION.RIGHT;
        if (pos.getCol() == Controller.size - 1 && direction == Controller.DIRECTION.RIGHT)
            return Controller.DIRECTION.LEFT;
        else return direction;
    }


    @Override
    public Void call() throws Exception {
        while (!isCancelled()) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    for (int flag = 0; flag < 2; flag++) {
                        try {
                            Controller.DIRECTION direction = nextDirection();
                            vcs.fireVetoableChange("pos", null, direction);
                            break; // exit loop
                        } catch (PropertyVetoException pve) {
                        } // veto of move => try move once again
                        catch (NullPointerException npe) {
                            cancel();
                        }
                    }
                }
            });

            Thread.sleep(500 + random.nextInt(1000));
        }
        return null;
    }


    int getId() { return actorId; }

    void setId(int actorId) { this.actorId = actorId; }

    public void addVetoableChangeListener(VetoableChangeListener listener) { vcs.addVetoableChangeListener(listener); }

    public void removeVetoableChangeListener(VetoableChangeListener listener) { vcs.removeVetoableChangeListener(listener); }

}
