package com.example.picture2pixel;


import org.json.JSONException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Enumeration;

/**
 * @author 张天野
 */
public class ButtonUtil extends JFrame implements ActionListener {

    private final JButton pictureSource;

    private final JTextField pictureSourceTxt = new JTextField(25);
    //    private final JTextField pictureTarget;
    private final JTextField pixelSize;

    private final JButton start;

    public ButtonUtil() {
        super("把图片变成马赛克");
        InitGlobalFont(new Font("alias", Font.PLAIN, 22));  //统一设置字体
        Container c = getContentPane();
        GridLayout layout = new GridLayout();
        layout.setColumns(1);
        layout.setRows(4);
        c.setLayout(layout);
        //原图片地址
        pictureSource = new JButton();
        pictureSource.setText("浏览");
        pictureSource.addActionListener(this);
        c.add(pictureSource);

        c.add(pictureSourceTxt);

        //输出地址
//        pictureTarget = new JTextField();
//        pictureTarget.setText("输出地址");
//        c.add(pictureTarget);

        //输出地址
        pixelSize = new JTextField();
        pixelSize.setText("模糊度,填写数字,不填默认30,数字越大,图片越模糊");
        c.add(pixelSize);

        //开始按钮
        start = new JButton("给我变");
        start.addActionListener(this);
        c.add(start);

        Dimension d = new Dimension(600, 800);
        setPreferredSize(d);
        setSize(d);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == start) {
            try {
                int size;
                try {
                    size = Integer.parseInt(pixelSize.getText());
                } catch (Exception ignored) {
                    size = 30;
                }

                ImageUtil.mosaic(pictureSourceTxt.getText(),
                        null,
                        new ImageArea(0, 0, 809, 1284), size);
            } catch (IOException e) {
                System.err.println("-" + LocalDateTime.now() + "IOException出错了！！！！");
                e.printStackTrace();
            } catch (JSONException e) {
                System.err.println("-" + LocalDateTime.now() + "JSONException出错了！！！！");
                e.printStackTrace();
            }
        } else if (event.getSource() == pictureSource) {
            JFileChooser fc = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter("就挑个图吧", "gif", "png", "jpg");
            fc.setFileFilter(filter);
            //文件打开对话框
            int val = fc.showOpenDialog(null);
            if (val == JFileChooser.APPROVE_OPTION) {
                //正常选择文件
                pictureSourceTxt.setText(fc.getSelectedFile().toString());
            }
        }
    }

    /**
     * 统一设置字体，父界面设置之后，所有由父界面进入的子界面都不需要再次设置字体
     */
    private static void InitGlobalFont(Font font) {
        FontUIResource fontRes = new FontUIResource(font);
        for (Enumeration<Object> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontRes);
            }
        }
    }
}