package utils;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileOperator {

    /**
     * 读文件
     */
    public static List<String> readFile(String filePath) {
        List<String> data = new ArrayList<>();
        BufferedReader br = null;

        try {
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File(filePath)), "UTF-8"));

            String line = null;
            while ((line = br.readLine()) != null) {
                data.add(line);
            }
            br.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }


    //读取json文件
    public static String readJsonFile(String fileName) {
        String jsonStr = "";
        try {
            File jsonFile = new File(fileName);
            FileReader fileReader = new FileReader(jsonFile);
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            jsonStr = sb.toString();
            return jsonStr;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}
