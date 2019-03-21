package tooster.L2;

import com.sun.istack.internal.Nullable;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class Model {
    private static class SingletonHolder {
        private static Model instance = new Model();
    }

    private Random random = new Random();
    private Actor zero;
    static final int size = 10;
    ArrayList<Thread> actorThreads = new ArrayList<>();

    static Model getInstance() { return SingletonHolder.instance; }

    private Field[][] fields = new Field[size][size];

    { // instance initialization block
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                fields[i][j] = new Field(i, j);
    }

    enum DIRECTION {UP, DOWN, LEFT, RIGHT}

    class Field {
        private Actor owner;
        private int row, col;

        public Field(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Nullable
        synchronized Actor tryLock(Actor client) {
            if (owner == null)
                owner = client;
            return owner;
        }

        synchronized void unlock(Actor client) {
            if (owner == client)
                owner = null;
        }

        @Nullable
        synchronized Actor getOwner() { return owner; }

        synchronized void setOwner(Actor owner) { this.owner = owner; }

        synchronized int[] getCords() { return new int[]{row, col};}

        @Nullable
        Field getNeighbor(DIRECTION direction) {
            switch (direction) {
                case UP:
                    return row > 0 ? getField(row - 1, col) : null;
                case DOWN:
                    return row < size ? getField(row + 1, col) : null;
                case LEFT:
                    return col > 0 ? getField(row, col - 1) : null;
                case RIGHT:
                    return col < size ? getField(row, col + 1) : null;
                default:
                    return null;
            }
        }

    }

    void reset() {

        zero = null;
        for (Thread actor : actorThreads) {
            actor.interrupt();
            try {
                actor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        actorThreads.clear();
        Node node = Controller.getInstance().getGrid().getChildren().get(0); // node 0 is weird - it has grid lines (?)
        Controller.getInstance().getGrid().getChildren().clear();
        Controller.getInstance().getGrid().getChildren().add(0, node);

        for (Field[] row : fields)
            for (Field field : row) {
                Actor actor = field.tryLock(null);
                field.unlock(actor);
            }

        ArrayList<Field> choosenFields = new ArrayList<>();
        int cnt = 0;

        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (random.nextInt(3) == 0) { // 33% for spawn
                    cnt++;
                    choosenFields.add(fields[i][j]);
                }

        Collections.shuffle(choosenFields);

        int id = -1;
        for (Field field : choosenFields) {
            Actor actor = Actor.createActor(++id, field);
            if (id == 0) zero = actor;
            field.setOwner(actor);

            Thread th = new Thread(actor);
            th.setDaemon(true);
            actorThreads.add(th);
            th.start();
        }
    }

    // return array [zeroRow, zeroCol]
    synchronized int[] getZero() { return getInstance().zero.getField().getCords(); }

    Field getField(int row, int col) { return fields[row][col];}
}
