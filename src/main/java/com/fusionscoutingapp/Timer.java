package com.fusionscoutingapp;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * Created from scratch with the help of the ChatGPT bot.
 */

public class Timer extends Label {
    private int seconds = 0;
    private final Timeline timeline;

    public Timer() {
        setText("0.00");
        timeline = new Timeline(new KeyFrame(Duration.millis(1), event -> {
            seconds += 0.01;
            setText(String.valueOf(seconds));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void start() {
        timeline.play();
    }

    public void stop() {
        timeline.stop();
    }

    public void reset() {
        stop();
        seconds = 0;
        setText("0.00");
    }

}
