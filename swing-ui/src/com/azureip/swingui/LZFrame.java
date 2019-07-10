package com.azureip.swingui;

import com.azureip.swingui.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class LZFrame {
    public static void main(String[] args) {
        JFrame f = initFrame("Java的第二个GUI程序");

        JPanel p = new JPanel();
        p.setBackground(new Color(235, 235, 235, 100));
        JLabel l = new JLabel();
        // l.setBackground(new Color(91, 191, 0));// 无效
        l.setText("这是放在JPanel上的标签");
        l.setFont(new Font("微软雅黑",0,12));

        f.add(p);
        p.add(l);
    }

    private static JFrame initFrame(String title) {
        JFrame frame = new JFrame(title);
        Image icon = frame.getToolkit().getImage("D:/Project/IDEA/azureip/swing-ui/resources/libra.png");
        frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtils.setSizeAndCentralize(frame, 600, 400);
        frame.setMinimumSize(new Dimension(600,400));
        frame.setVisible(true);
        // frame.setResizable(false);
        return frame;
    }
}
