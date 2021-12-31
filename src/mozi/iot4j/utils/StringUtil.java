package mozi.iot4j.utils;

public class StringUtil {
    public static boolean isNullOrEmpty(String data){
        return data==null|| data.equals("")||data.length()==0&& data.trim().equals("");
    }
}
