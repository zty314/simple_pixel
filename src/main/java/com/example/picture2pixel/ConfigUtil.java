package com.example.picture2pixel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张天野
 */
public class ConfigUtil {

    public static List<Integer> colorList = new ArrayList<>();

    public static List<Integer> loadExistColor() throws IOException, JSONException {

        String path = System.getProperty("user.dir");
        File config = new File(path + "/config.json");
        if(!config.exists()){
            extracted(config);
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(config));
        StringBuilder strLine = new StringBuilder();
        String line;
        while (null != (line = bufferedReader.readLine())) {
            strLine.append(line);
        }
        JSONArray rgbJson = new JSONArray(strLine.toString());
        for (int i = 0; i < rgbJson.length(); i++) {
            //描述颜色的rgb数组
            JSONArray rgb = rgbJson.getJSONArray(i);
            Integer r = (Integer) rgb.get(0);
            Integer g = (Integer) rgb.get(1);
            Integer b = (Integer) rgb.get(2);
            Color color = new Color(r, g, b);
            colorList.add(color.getRGB());
        }
        return colorList;
    }


    public static void main(String[] args) throws JSONException, IOException {
        List<Integer> colors = loadExistColor();
        System.out.println(JSONObject.wrap(colors));
    }

    public static Integer searchClosest(Integer color) throws JSONException, IOException {
        if (CollectionUtils.isEmpty(colorList)) {
            loadExistColor();
        }

        //有相同的直接返回
        if (colorList.contains(color)) {
            return color;
        }
        //没有相同的，找最接近的
        //初始化两数之差
        List<Integer> distance = new ArrayList<>();
        //暴力法，对比每个已有数
        for (Integer integer : colorList) {
            distance.add(integer - color);
        }
        //初始化最小数
        int min = Integer.MAX_VALUE;
        //最小下标
        int minIndex = 0;
        for (int i = 0; i < distance.size(); i++) {
            if (i == 0) {
                min = distance.get(i);
            }
            if (Math.abs(min) > Math.abs(distance.get(i))) {
                min = distance.get(i);
                minIndex = i;
            }
        }
        return colorList.get(minIndex);
    }


    private static void extracted(File config) throws IOException {
        config.createNewFile();
        Writer write = new OutputStreamWriter(Files.newOutputStream(config.toPath()), StandardCharsets.UTF_8);
        write.write("[[9, 11, 10], [12, 12, 10], [13, 11, 16], [14, 12, 13], [14, 14, 12], [16, 18, 15], " +
                " [33, 29, 26], [33, 29, 28], [33, 30, 25], [33, 34, 29], [34, 29, 23], [34, 29, 26], " +
                " [50, 41, 36], [50, 45, 41], [50, 47, 42], [50, 48, 49], [50, 50, 48], [50, 51, 53], " +
                " [51, 33, 21], [51, 40, 38], [51, 41, 39], [51, 41, 40], [51, 42, 37], [51, 43, 32], " +
                "[51, 46, 40], [51, 46, 42], [51, 47, 36], [51, 47, 44], [51, 48, 43], [51, 49, 54], " +
                " [65, 56, 51], [65, 60, 56], [65, 60, 57], [65, 61, 58], [65, 67, 64], [65, 70, 74], " +
                " [108, 106, 109], [108, 107, 112], [109, 99, 98], [109, 104, 110], [109, 107, 112]," +
                " [201, 197, 198], [202, 160, 136], [202, 165, 156], [202, 191, 189], [203, 159, 148], " +
                " [213, 167, 144], [213, 168, 145], [213, 170, 151], [213, 173, 161], [213, 180, 171], " +
                " [216, 201, 198], [216, 202, 199], [217, 172, 149], [217, 178, 171], [217, 179, 160], " +
                " [227, 216, 220], [227, 217, 216], [227, 217, 218], [228, 214, 211], [228, 217, 215], " +
                " [233, 222, 220], [235, 221, 221], [235, 225, 226], [238, 218, 217]]");
        write.flush();
        write.close();
    }
}
