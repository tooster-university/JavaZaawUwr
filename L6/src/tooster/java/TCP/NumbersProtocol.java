package tooster.java.TCP;

import java.util.Random;

class NumbersProtocol {
    private static final String FORFEIT = "forfeit";
    private static final String WIN_RESPONSE = "you won";
    private static final String LOSS_RESPONSE = "you lose";
    private static final String LESS_RESPONSE = "less";
    private static final String MORE_RESPONSE = "more";
    private static final String UNRECOGNIZED = "unrecognized input";

    private static final int MIN_VALUE = 1000;
    private static final int MAX_VALUE = 9999;
    private static final int MAX_TURNS = 12;

    private int requestsCnt = 0;
    private int currentNumber = 0;
    private Random random = new Random();

    NumbersProtocol() { reset(); }

    public int getRequestsCnt() { return requestsCnt; }

    public int getCurrentNumber() { return currentNumber; }

    String processInput(String input) {
        String response = null;

        if (input == null) // reset game on null
            reset();
        else if (FORFEIT.equalsIgnoreCase(input)) {
            reset();
            response = LOSS_RESPONSE;
        } else {
            try {
                int guess = Integer.parseInt(input);
                if (guess < MIN_VALUE || guess > MAX_VALUE)
                    response = UNRECOGNIZED;
                else if (guess == currentNumber) {
                    reset();
                    response = WIN_RESPONSE;
                } else {
                    if (++requestsCnt >= MAX_TURNS) {
                        reset();
                        response = LOSS_RESPONSE;
                    }
                    else
                        response = guess > currentNumber ? LESS_RESPONSE : MORE_RESPONSE;
                }

            } catch (NumberFormatException e) {
                response = UNRECOGNIZED;
            }
        }

        return response;
    }

    void reset() {
        requestsCnt = 0;
        currentNumber = random.nextInt(MAX_VALUE + 1 - MIN_VALUE) + MIN_VALUE;
    }
}
