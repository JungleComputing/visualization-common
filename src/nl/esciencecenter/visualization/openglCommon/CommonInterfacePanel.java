package nl.esciencecenter.visualization.openglCommon;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

public abstract class CommonInterfacePanel extends JPanel {
    private static final long serialVersionUID = -4937089344123608040L;

    public CommonInterfacePanel() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        setLayout(new BorderLayout(0, 0));

        setVisible(true);
    }
}
