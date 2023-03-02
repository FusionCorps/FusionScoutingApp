package com.scout.ui;

import javafx.application.Platform;

public class TimerText extends LimitedTextField {
    private boolean running = false;
    private boolean paused = false;
    private double startTime;
    private double currentTime;
    private double elapsedTime;

    public TimerText() {
        super();
        update();
        startTime = 0.0;
    }

    public void start() {
        if (!running) {
            running = true;
            if (paused) startTime  = System.currentTimeMillis()- elapsedTime;
            else startTime = System.currentTimeMillis();
            paused = false;
            new Thread(() -> {
                while (running) {
                    currentTime = System.currentTimeMillis();
                    elapsedTime = currentTime - startTime;
                    Platform.runLater(this::update);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void pause() {
        running = false;
        paused = true;
    }

    public void reset() {
        running = false;
        paused = false;
        startTime = 0;
        currentTime = 0;
        elapsedTime = 0;
        update();
    }

    private void update() {
        int seconds = (int) (elapsedTime / 1000);
        int milliseconds = (int)elapsedTime/100 % 10;
        setText(String.format("%d.%d", seconds, milliseconds));
    }
}
