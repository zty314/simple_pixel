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

    public static List<Color> colorList = new ArrayList<>();

    public static List<Color> loadExistColor() throws IOException, JSONException {

        String path = System.getProperties().getProperty("user.dir");
        File config = new File(path + "/config.json");
        if (!config.exists()) {
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
            colorList.add(color);
        }
        return colorList;
    }


    public static void main(String[] args) throws JSONException, IOException {
        List<Color> colors = loadExistColor();
        System.out.println(JSONObject.wrap(colors));
    }

    /**
     * 返回颜色编号，在colorList中读取具体色号
     *
     * @param target
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static Integer searchClosest(Color target) throws JSONException, IOException {
        if (CollectionUtils.isEmpty(colorList)) {
            loadExistColor();
        }
        //有相同的直接返回
        if (colorList.contains(target)) {
            return colorList.indexOf(target);
        }
        //可以看做是三维坐标的勾股定理
        int targetIndex = 0;
        double minDiff = 0.0;
        for (int i = 0; i < colorList.size(); i++) {
            Color source = colorList.get(i);
            int r = target.getRed() - source.getRed();
            int g = target.getGreen() - source.getGreen();
            int b = target.getBlue() - source.getBlue();
            double diff = Math.sqrt(r * r + g * g + b * b);
            if (i == 0 || diff < minDiff) {
                minDiff = diff;
                targetIndex = i;
            }
        }

        return targetIndex;
    }


    private static void extracted(File config) throws IOException {
        config.createNewFile();
        Writer write = new OutputStreamWriter(Files.newOutputStream(config.toPath()), StandardCharsets.UTF_8);
        write.write("[[250, 225, 195], [236, 211, 168], [255, 233, 171], [211,170 ,112], [220,185,155], [248,187," +
                "161], [255,216,197], [235,195,182], [253,198,168], [255,204,160], [255,168,162], [239,196,185], " +
                "[225,122,91], [218,105,217], [252,79,101], [253,204,126], [253,215,97], [255,139,99], [254,140,61], " +
                "[255,207,59], [255,196,59], [255,144,76], [252,112,44], [242,65,59], [255,108,96], [255,179,109], " +
                "[255,170,38], [253,225,150], [255,255,129], [255,244,110], [232,32,52], [255,46,51], [242,29,34], " +
                "[254,212,234], [239,169,227], [239,216,246], [198,149,214], [209,147,213], [200,122,215], [125,55," +
                "171], [225,125,195], [255,111,198], [250,120,190], [250,217,218], [250,200,225], [255,210,230], " +
                "[200,255,250], [155,235,230],  [138,212,225], [165,220,240], [125,195,240], [110,219,255], [58,193," +
                "246], [33,183,252], [57,125,236], [97,134,211], [32,47,181], [55,58,123], [28,144,199], [168,231," +
                "204], [208,238,139], [105,253,156], [192,234,108], [233,244,117], [117,203,144], [155,157,142], " +
                "[171,145,120], [112,129,59], [53,154,108], [73,149,115], [34,120,143], [202,188,187], [195,182,185]," +
                " [160,143,147], [97,84,96], [184,118,103], [123,47,49], [171,78,53], [175,59,52], [153,49,42], [254," +
                "251,251], [240,240,252], [255,255,255], [0,0,0], [225,130,30], [225,214,210], [204,191,189], [55,33," +
                "48], [245,145,100], [247,107,45], [227,85,57], [254,130,128], [255,104,87], [184,27,29], [211,23," +
                "52], [138,90,44], [72,77,62], [49,71,193], [79,60,78], [182,134,116], [164,72,42], [246,169,129], " +
                "[253,183,75], [241,36,134], [252,157,162], [193,186,157], [218,207,181], [186,174,107], [218,116," +
                "59]]");
        write.flush();
        write.close();
    }
}
