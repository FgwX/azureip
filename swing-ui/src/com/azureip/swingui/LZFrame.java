package com.azureip.swingui;

import com.azureip.swingui.util.SwingUtils;

import javax.swing.*;
import java.awt.*;

public class LZFrame {
    public static void main(String[] args) {
        // test2();
        JFrame f = initFrame("Test frame 3");
        // f.setLayout(new BorderLayout(2, 2));
	    JPanel jPanel=new JPanel();    //创建面板
	    JPanel jPane2=new JPanel();    //创建面板
	    JPanel jPane3=new JPanel();    //创建面板
	    JPanel jPane4=new JPanel();    //创建面板
	    JButton btn1=new JButton("1");    //创建按钮
	    JButton btn2=new JButton("2");
	    JButton btn3=new JButton("3");
	    JButton btn4=new JButton("4");
	    JButton btn5=new JButton("5");
	    JButton btn6=new JButton("6");
	    JButton btn7=new JButton("7");
	    JButton btn8=new JButton("8");
	    JButton btn9=new JButton("9");
	    jPanel.add(btn1);    //面板中添加按钮
	    jPanel.add(btn2);
	    jPane2.add(btn3);
	    jPane2.add(btn4);
	    jPane3.add(btn5);
	    jPane3.add(btn6);
	    jPane4.add(btn7);
	    jPane4.add(btn8);
	    jPane4.add(btn9);
	    f.add(jPanel);
	    f.add(jPane2);
	    f.add(jPane3);
	    f.add(jPane4);
	    f.setLayout(new FlowLayout(FlowLayout.LEADING,50,50));
	    f.setVisible(true);
    }

    private static void test2() {
        JFrame f = initFrame("Java的第二个GUI程序");

        JPanel p = new JPanel();
        p.setBackground(new Color(235, 235, 235, 100));
        JLabel l = new JLabel();
        // l.setBackground(new Color(91, 191, 0));// 无效
        l.setText("这是放在JPanel上的标签");
        l.setFont(new Font("微软雅黑", 0, 12));

        f.add(p);
        p.add(l);
    }

    private static JFrame initFrame(String title) {
        JFrame frame = new JFrame(title);
        Image icon = frame.getToolkit().getImage("D:/Project/IDEA/azureip/swing-ui/resources/libra.png");
        frame.setIconImage(icon);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtils.setSizeAndCentralize(frame, 600, 400);
        frame.setMinimumSize(new Dimension(600, 400));
        // frame.setVisible(true);
        // frame.setResizable(false);
        return frame;
    }
}
