package tooster.L2;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.util.*;


public class Controller implements VetoableChangeListener {

    private static Controller instance;
    private static Random random = new Random();
    static final int size = 10;
    private ArrayList<Actor> actors = new ArrayList<>();
    private Map<Integer, Thread> threads = new HashMap<>();
    private Integer[][] map = new Integer[size][size];
    private Pos[] positions = new Pos[size * size];
    private Label[][] labels = new Label[size][size];

    enum DIRECTION {UP, DOWN, LEFT, RIGHT}

    // FIXME static ???
    public static class Pos {
        private int row, col;

        int getRow() { return row; }

        public void setRow(int row) { this.row = row; }

        int getCol() { return col; }

        public void setCol(int col) { this.col = col; }

        Pos(int row, int col) {
            this.row = row;
            this.col = col;
        }

        Pos(Pos pos) {
            this.row = pos.row;
            this.col = pos.col;
        }

        void translate(DIRECTION dir) {
            switch (dir) {
                case UP:
                    row -= 1;
                    break;
                case DOWN:
                    row += 1;
                    break;
                case LEFT:
                    col -= 1;
                    break;
                case RIGHT:
                    col += 1;
                    break;
            }
        }

        int manhattanDist(Pos pos) { return Math.abs(pos.col - this.col) + Math.abs(pos.row - this.row); }
    }

    static Controller getInstance() { return instance; }

    static void setInstance(Controller instance) {
        Controller.instance = instance;
    }

    @FXML
    private GridPane grid;

    @FXML
    private Button restart;

    @FXML
    public void initialize() {
        // add empty labels
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                labels[i][j] = new Label("");
                labels[i][j].getStyleClass().add("number-label");
                grid.add(labels[i][j], j, i);
            }
        }
    }

    @FXML
    void restartSim(ActionEvent event) {

        // terminate threads
        for (Map.Entry<Integer, Thread> entry : threads.entrySet())
            entry.getValue().interrupt();
        threads.clear();

        // FIXME should be obsolete if threads are interrupted (?)
//        // previous listeners unregister
//        for (Actor actor : actors) {
//            actor.removePropertyChangeListener(this);
//            actor.removeVetoableChangeListener(this);
//        }
        actors.clear();

        // reset view and data
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                positions[i * size + j] = null; // delete mapping
                map[i][j] = null;
                labels[i][j].setText("");
                labels[i][j].getStyleClass().removeAll("zero-label", "prime-label", "composite-label");
            }
        }

        // spots generation
        ArrayList<Pos> chosenFields = new ArrayList<>();
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (random.nextInt(3) == 0)  // 33% for spawn
                    chosenFields.add(new Pos(i, j));
        Collections.shuffle(chosenFields);

        // spots populate
        int id = 0;
        for (Pos pos : chosenFields) {
            Actor actor = (id == 0) ?
                    new Actor() { // for 0 every move is equally probable
                        @Override
                        protected int[] probabilityCalculator() { return new int[]{25, 25, 25}; }
                    }
                    :
                    new Actor();

            // update data
            actor.setId(id);
            actor.addVetoableChangeListener(this);
            map[pos.row][pos.col] = id;
            positions[id] = pos;
            actors.add(actor);

            // update view
            labels[pos.row][pos.col].setText(Integer.toString(id));
            if (id == 0)
                labels[pos.row][pos.col].getStyleClass().add("zero-label");
            else if (isPrime(id))
                labels[pos.row][pos.col].getStyleClass().add("prime-label");
            else
                labels[pos.row][pos.col].getStyleClass().add("composite-label");

            id++;
        }

        // start all actors
        for (Actor actor : actors) {
            Thread th = new Thread(actor);
            threads.put(actor.getId(), th);
            th.start();
        }
    }

    // returns mutable position of given actor
    Pos getPos(int id) { return positions[id]; }

    // removes actor from grid
    private void gridCellRemove(int id, Pos pos) {
        map[pos.row][pos.col] = null;
        labels[pos.row][pos.col].getStyleClass().removeAll("zero-label", "composite-label", "prime-label");
        labels[pos.row][pos.col].setText(null);
        positions[id] = null;
    }

    // adds actor to the grid
    private void gridCellAdd(int id, Pos pos) {
        map[pos.row][pos.col] = id;
        labels[pos.row][pos.col].getStyleClass().add(id == 0 ? "zero-label" : isPrime(id) ? "prime-label" : "composite-label");
        labels[pos.row][pos.col].setText(Integer.toString(id));
        positions[id] = new Pos(pos);
    }

    @Override
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
        Actor actor = (Actor) evt.getSource();
        int id = actor.getId();
        Pos pos = new Pos(positions[id]);
        pos.translate((DIRECTION) evt.getNewValue());
        Pos oldPos = positions[id];
        Integer targetId = map[pos.row][pos.col];

        if (id == 0) {
            gridCellRemove(id, oldPos); // remove 0 from old position
            if (targetId != null) {
                gridCellRemove(targetId, pos); // remove from new position
                if (isPrime(targetId))
                    gridCellAdd(targetId, oldPos); // swap in the prime
                else
                    threads.get(targetId).interrupt(); // terminate 'eaten' actor


            }
            gridCellAdd(id, pos); // add 0 to new position

        } else {
            if (targetId != null)
                throw new PropertyVetoException(null, evt); // veto the move
            else {
                gridCellRemove(id, oldPos);
                gridCellAdd(id, pos); // accept the move
            }
        }


    }

    static boolean isPrime(int x) {
        switch (x) {
            case 2:
            case 3:
            case 5:
            case 7:
            case 11:
            case 13:
            case 17:
            case 19:
            case 23:
            case 29:
            case 31:
            case 37:
            case 41:
            case 43:
            case 47:
            case 53:
            case 59:
            case 61:
            case 67:
            case 71:
            case 73:
            case 79:
            case 83:
            case 89:
            case 97:
                return true;
            default:
                return false;
        }
    }
}

