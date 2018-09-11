package th.weixia.sanae.utils;

/**
 * Created by sy on 16.10.17.
 */
public class WxStringUtil {

    public static boolean isEmpty(String string) {
        if (string == null) {
            return true;
        }
        if (string.trim().length() < 1) {
            return true;
        }
        return false;
    }

    public static boolean hasEmptyString(String... strings) {
        boolean result = false;
        for (int i = 0; i < strings.length; i++) {
            result = result || isEmpty(strings[i]);
        }
        return result;
    }

    /**
     * 把byte转化成2进制字符串
     * @param b
     * @return
     */
    public static String getBinaryStrFromByte(byte b){
        String result ="";
        byte a = b;
        for (int i = 0; i < 8; i++){
            byte c = a;
            a = (byte) (a >> 1);//每移一位如同将10进制数除以2并去掉余数。
            a = (byte) (a << 1);
            if (a == c) {
                result = "0" + result;
            } else {
                result = "1" + result;
            }
            a = (byte) (a >> 1);
        }
        return result;
    }

    public static String json2Normal(String json) {
        return json.replace("{\"", "").replace("\"}", "").replace("\",\"", "&").replace("\":\"", "=");
    }

}
