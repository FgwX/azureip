package com.azureip.swingui.util;

import javax.swing.*;
import java.awt.*;

public class SwingUtils {

    /**
     * 设置窗口尺寸并居中
     */
    public static void setSizeAndCentralize(JFrame f, int w, int h) {
        /*Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width / 2 - w / 2;
        int y = screenSize.height / 2 - h / 2;
        System.out.println("WindowSize: " + w + "*" + h + ", Location: " + x + "," + y);
        setLocation(x, y);*/
        f.setSize(new Dimension(w, h));
        f.setLocationRelativeTo(null);
    }
}
