package org.sankozi.jlogfilter.gui;

/**
 *
 */
public class Configuration {
    DetailPaneLocation detailPaneLocation = DetailPaneLocation.BOTTOM;
    int logEntriesTableSize = 500;

}


enum DetailPaneLocation {
    NONE,
    LEFT,
    RIGHT,
    BOTTOM
}
