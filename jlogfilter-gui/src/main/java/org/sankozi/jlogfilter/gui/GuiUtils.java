package org.sankozi.jlogfilter.gui;

import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import org.sankozi.jlogfilter.Level;

/**
 *
 */
class GuiUtils {
    static <T extends Labeled> T fillLabeledForLevel(T labeled, Level level) {
        switch (level){
            case DEBUG:
                labeled.setText(FontAwesomeIcons.BUG + "");
                labeled.setTextFill(Color.GRAY);
                break;
            case INFO:
                labeled.setText(FontAwesomeIcons.INFO + "");
                labeled.setTextFill(Color.DARKGREEN);
                break;
            case WARN:
                labeled.setText(FontAwesomeIcons.WARNING + "");
                labeled.setTextFill(Color.DARKORANGE);
                break;
            case ERROR:
                labeled.setText(FontAwesomeIcons.ERROR + "");
                labeled.setTextFill(Color.DARKRED);
                break;
            case FATAL:
                labeled.setText(FontAwesomeIcons.FIRE + "");
                labeled.setTextFill(Color.DARKRED);
        }
        return labeled;
    }
}
