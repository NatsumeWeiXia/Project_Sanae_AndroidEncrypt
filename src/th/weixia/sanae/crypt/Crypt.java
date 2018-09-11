package th.weixia.sanae.crypt;

import th.weixia.common.log.WxLog;
import th.weixia.sanae.aes.CryptLib;
import th.weixia.sanae.xxtea.XXTEA;

/**
 * Created by sy on 16.10.17.
 */
public class Crypt {

    public static byte[] encryptAES(String key, byte[] data) {
        try {
            CryptLib _crypt = new CryptLib();
            String _key = CryptLib.SHA256(key, 32); //32 bytes = 256 bit
            String _iv = key; //16 bytes = 128 bit

            byte[] output = _crypt.encrypt(data, _key, _iv);

            return output;
        } catch (Exception e) {
            WxLog.e("encrypt", e.getMessage(), e);
        }
        return null;
    }

    public static byte[] encryptTEA(String key, byte[] data) {
        return XXTEA.encrypt(data, key);
    }

    public static byte[] decryptAES(String key, byte[] data) {
        try {
            CryptLib _crypt = new CryptLib();
            String _key = CryptLib.SHA256(key, 32); //32 bytes = 256 bit
            String _iv = key; //16 bytes = 128 bit

            byte[] output = _crypt.decrypt(data, _key, _iv);

            return output;
        } catch (Exception e) {
            WxLog.e("encrypt", e.getMessage(), e);
        }
        return null;
    }

    public static byte[] decryptTEA(String key, byte[] data) {
        return XXTEA.decrypt(data, key);
    }
}
