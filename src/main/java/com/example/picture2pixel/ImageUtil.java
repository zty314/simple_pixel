package com.example.picture2pixel;

import org.json.JSONException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 图片处理类
 *
 * @author hty
 */
public class ImageUtil {
    /**
     * 给图片指定位置打马赛克
     *
     * @param filePath   图片位置
     * @param targetPath 打码后的图片保存位置，若为空则保存路径默认为原图片路径
     * @param x          图片要打码区域左上角的横坐标
     * @param y          图片要打码区域左上角的纵坐标
     * @param width      图片要打码区域的宽度
     * @param height     图片要打码区域的高度
     * @param mosaicSize 马赛克尺寸，即每个矩形的长宽
     * @return
     * @throws IOException
     */
    @SuppressWarnings("static-access")
    public static boolean mosaic(String filePath, String targetPath,
                                 int x, int y, int width, int height, int mosaicSize) throws IOException,
            JSONException {
        //1. 初始化图像处理各变量
        if (!filePath.endsWith(".png") && !filePath.endsWith(".jpg") &&
                !filePath.endsWith(".gif")) {
            System.err.println("ImageUtil>>>文件名非法，不是正确的图片文件名");
            return false;
        }
        int index = filePath.lastIndexOf(".");
        String suffix = filePath.substring(index + 1);
        if (targetPath != null && !targetPath.isEmpty() && !targetPath.endsWith(suffix)) {
            System.err.println("ImageUtil>>>目标文件后缀应与源文件后缀一致");
            return false;
        }
        File file = new File(filePath);
        if (!file.isFile()) {
            System.err.println("ImageUtil>>>" + filePath + "不是一个文件！");
            return false;
        }
        BufferedImage bi = ImageIO.read(file); // 读取该图片
        BufferedImage spinImage = new BufferedImage(bi.getWidth(),
                bi.getHeight(), bi.TYPE_INT_RGB);
        if (bi.getWidth() < mosaicSize || bi.getHeight() < mosaicSize || mosaicSize <= 0) { // 马赛克格尺寸太大或太小
            System.err.println("马赛克尺寸设置不正确");
            return false;
        }

        /*暂时修改为整个图形尺寸*/
        width = bi.getWidth();
        height = bi.getHeight();

        //2. 设置各方向绘制的马赛克块个数
        int xcount = 0; // 方向绘制个数
        int ycount = 0; // y方向绘制个数
        if (width % mosaicSize == 0) {
            xcount = width / mosaicSize;
        } else {
            xcount = width / mosaicSize + 1;
        }
        if (height % mosaicSize == 0) {
            ycount = height / mosaicSize;
        } else {
            ycount = height / mosaicSize + 1;
        }

        //3. 绘制马赛克(绘制矩形并填充颜色)
        Graphics gs = spinImage.getGraphics();
        gs.drawImage(bi, 0, 0, null);
        int xTmp = x;
        int yTmp = y;

//        List<List<Integer>> listk = new ArrayList<>();

        for (int i = 0; i < xcount; i++) {
            for (int j = 0; j < ycount; j++) {
                //马赛克矩形格大小
                int mwidth = mosaicSize;
                int mheight = mosaicSize;
                if (i == xcount - 1) {   //横向最后一个比较特殊，可能不够一个size
                    mwidth = width - xTmp;
                }
                if (j == ycount - 1) {  //同理
                    mheight = height - yTmp;
                }
                //矩形颜色取中心像素点RGB值
                int centerX = xTmp;
                int centerY = yTmp;
                if (mwidth % 2 == 0) {
                    centerX += mwidth / 2;
                } else {
                    centerX += (mwidth - 1) / 2;
                }
                if (mheight % 2 == 0) {
                    centerY += mheight / 2;
                } else {
                    centerY += (mheight - 1) / 2;
                }
//                Color color = new Color(bi.getRGB(centerX, centerY));
                Color color = new Color(ConfigUtil.searchClosest(bi.getRGB(centerX, centerY)));
//                ArrayList<Integer> rgb = new ArrayList<>();
//                rgb.add(color.getRed());
//                rgb.add(color.getGreen());
//                rgb.add(color.getBlue());
//                listk.add(rgb);
                gs.setColor(color);
                gs.fillRect(xTmp, yTmp, mwidth, mheight);
                yTmp = yTmp + mosaicSize;// 计算下一个矩形的y坐标
            }
            yTmp = y;// 还原y坐标
            xTmp = xTmp + mosaicSize;// 计算x坐标
        }
        gs.dispose();
        if (targetPath == null || targetPath.isEmpty()) {
            int i = filePath.lastIndexOf(".");
            StringBuilder builder = new StringBuilder(filePath);
            builder.insert(i, "_pixel");
            targetPath = builder.toString();
        }
        File sf = new File(targetPath);
        ImageIO.write(spinImage, suffix, sf); // 保存图片

        return true;
    }

    public static boolean mosaic(String filePath, String targetPath,
                                 ImageArea area, int mosaicSize) throws IOException, JSONException {
        return mosaic(filePath, targetPath, area.getX(), area.getY(),
                area.getWidth(), area.getHeight(), mosaicSize);
    }

    /**
     * 给图片多个指定位置打马赛克
     *
     * @param filePath   图片位置
     * @param targetPath 打码后的图片保存位置，若为空则保存路径默认为原图片路径
     * @param areaList   图片区域对象数组
     * @param mosaicSize 马赛克尺寸，即每个矩形的长宽
     * @return
     * @throws IOException
     */
    @SuppressWarnings("static-access")
    public static boolean mosaic(String filePath, String targetPath,
                                 List<ImageArea> areaList, int mosaicSize) throws IOException {
        //1. 初始化图像处理各变量
        if (!filePath.endsWith(".png") && !filePath.endsWith(".jpg") &&
                !filePath.endsWith(".gif")) {
            System.err.println("ImageUtil>>>文件名非法，不是正确的图片文件名");
            return false;
        }
        int index = filePath.lastIndexOf(".");
        String suffix = filePath.substring(index + 1);
        if (targetPath != null && !targetPath.isEmpty() && !targetPath.endsWith(suffix)) {
            System.err.println("ImageUtil>>>目标文件后缀应与源文件后缀一致");
            return false;
        }
        File file = new File(filePath);
        if (!file.isFile()) {
            System.err.println("ImageUtil>>>" + filePath + "不是一个文件！");
            return false;
        }
        BufferedImage bi = ImageIO.read(file); // 读取该图片
        BufferedImage spinImage = new BufferedImage(bi.getWidth(),
                bi.getHeight(), bi.TYPE_INT_RGB);
        if (bi.getWidth() < mosaicSize || bi.getHeight() < mosaicSize || mosaicSize <= 0) { // 马赛克格尺寸太大或太小
            System.err.println("马赛克尺寸设置不正确");
            return false;
        }

        Graphics gs = spinImage.getGraphics();
        gs.drawImage(bi, 0, 0, null);
        //对每一个局部区域分别绘制马赛克
        for (ImageArea imageArea : areaList) {
            int x = imageArea.getX();
            int y = imageArea.getY();
            int width = imageArea.getWidth();
            int height = imageArea.getHeight();
            //2. 设置各方向绘制的马赛克块个数
            int xcount = 0; // 方向绘制个数
            int ycount = 0; // y方向绘制个数
            if (width % mosaicSize == 0) {
                xcount = width / mosaicSize;
            } else {
                xcount = width / mosaicSize + 1;
            }
            if (height % mosaicSize == 0) {
                ycount = height / mosaicSize;
            } else {
                ycount = height / mosaicSize + 1;
            }

            //3. 绘制马赛克(绘制矩形并填充颜色)
            int xTmp = x;
            int yTmp = y;
            for (int i = 0; i < xcount; i++) {
                for (int j = 0; j < ycount; j++) {
                    //马赛克矩形格大小
                    int mwidth = mosaicSize;
                    int mheight = mosaicSize;
                    if (i == xcount - 1) {   //横向最后一个比较特殊，可能不够一个size
                        mwidth = width - xTmp;
                    }
                    if (j == ycount - 1) {  //同理
                        mheight = height - yTmp;
                    }
                    //矩形颜色取中心像素点RGB值
                    int centerX = xTmp;
                    int centerY = yTmp;
                    if (mwidth % 2 == 0) {
                        centerX += mwidth / 2;
                    } else {
                        centerX += (mwidth - 1) / 2;
                    }
                    if (mheight % 2 == 0) {
                        centerY += mheight / 2;
                    } else {
                        centerY += (mheight - 1) / 2;
                    }
                    Color color = new Color(bi.getRGB(centerX, centerY));
                    gs.setColor(color);
                    gs.fillRect(xTmp, yTmp, mwidth, mheight);
                    yTmp = yTmp + mosaicSize;// 计算下一个矩形的y坐标
                }
                yTmp = y;// 还原y坐标
                xTmp = xTmp + mosaicSize;// 计算x坐标
            }

        }
        gs.dispose();
        if (targetPath == null || targetPath.isEmpty())
            targetPath = filePath;
        File sf = new File(targetPath);
        ImageIO.write(spinImage, suffix, sf); // 保存图片
        return true;
    }
}
