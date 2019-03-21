package tooster.L2;

import com.sun.istack.internal.NotNull;
import javafx.concurrent.Task;

import java.util.Random;

public class Actor extends Task<Void> {

    private int id;

    private static Random random = new Random();
    private Renderable renderable;
    private Model.Field field;

    // Actor factory method to bind with renderable
    static Actor createActor(int id, @NotNull Model.Field field) {
        Actor actor = id != 0 ?
                new Actor(id, field) :
                new Actor(id, field) {
                    // probability is equal in all axes
                    @Override
                    protected int[] probabilityCalculator(int deltaRow, int deltaCol) { return new int[]{25, 25, 25};}

//                    @Override
//                    protected void randomMove() {
//                        Model.Field nextField = field.getNeighbor(nextDirection());
//
//                    }

                };
        actor.renderable = new Renderable(actor); // bind renderable to actor
        return actor;
    }

    private Actor(int id, Model.Field field) {
        super();
        this.id = id;
        this.field = field;
    }

    @Override
    protected Void call() throws Exception {
        try {
            while (!isCancelled()) {
                randomMove();
                Thread.sleep(50 + random.nextInt(100));
            }
        } catch (InterruptedException e) {
            field.unlock(this);
        }
        return null;
    }


    // returns probability for move in given direction where dR dC are delta of zero position minus self position
    // probabilities sum up to 100. Returned array in form [up, down, left], right is implicit 100-u-d-l
    protected int[] probabilityCalculator(int deltaRow, int deltaCol) {
        double factor = (-20.0) / 9.0;
        return new int[]{
                25 + (int) (Math.signum(-deltaRow) * (20 + factor * Math.abs(deltaRow))),
                25 - (int) (Math.signum(-deltaRow) * (20 + factor * Math.abs(deltaRow))),
                25 + (int) (Math.signum(-deltaCol) * (20 + factor * Math.abs(deltaCol)))};
    }

    @NotNull
    protected Model.DIRECTION nextDirection() {
        Model model = Model.getInstance();
        int[] cords = field.getCords();
        int[] zero = model.getZero();
        int[] prob = probabilityCalculator(zero[0] - cords[0], zero[1] - cords[1]); // probabilities

        int roll = random.nextInt(100);
        Model.DIRECTION direction;
        if (roll < prob[0]) direction = Model.DIRECTION.UP;
        else if (roll < prob[0] + prob[1]) direction = Model.DIRECTION.DOWN;
        else if (roll < prob[0] + prob[1] + prob[2]) direction = Model.DIRECTION.LEFT;
        else direction = Model.DIRECTION.RIGHT;

        // those two lines assure, that move is always inside the board
        if (cords[0] == 0 && direction == Model.DIRECTION.UP) return Model.DIRECTION.DOWN;
        if (cords[0] == Model.size - 1 && direction == Model.DIRECTION.DOWN) return Model.DIRECTION.UP;
        if (cords[1] == 0 && direction == Model.DIRECTION.LEFT) return Model.DIRECTION.RIGHT;
        if (cords[1] == Model.size - 1 && direction == Model.DIRECTION.RIGHT) return Model.DIRECTION.LEFT;
        else return direction;
    }

    protected void randomMove() {

        @NotNull Model.Field nextField = field.getNeighbor(nextDirection());

        if (this != nextField.tryLock(this)) {
            nextField = field.getNeighbor(nextDirection());
            nextField.tryLock(this);
        }

        if (nextField.getOwner() == this) {
            renderable.moveAnimation(nextField); // play animation on renderable
            field.unlock(this);
            field = nextField;
        }
    }

    int getActorId() { return id; }

    Model.Field getField() { return field; }
}
