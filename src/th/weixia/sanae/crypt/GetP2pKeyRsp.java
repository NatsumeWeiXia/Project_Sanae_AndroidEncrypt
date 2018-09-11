package th.weixia.sanae.crypt;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import th.weixia.common.http.rsp.BaseJsonResponseMsg;
import th.weixia.common.log.WxLog;

/**
 * Created by sy on 16.10.14.
 */
public class GetP2pKeyRsp extends BaseJsonResponseMsg {

    @Override
    public void init(HttpResponse httpresponse) {
        super.init(httpresponse);
        if (isOK()) {
            try {
                JSONArray jUserKeys = jso.getJSONArray("userkeys");
                for (int i = 0; i < jUserKeys.length(); i++) {
                    JSONObject jUserKey = jUserKeys.getJSONObject(i);
                    JSONArray jSecretKeys = jUserKey.getJSONArray("secretkeys");
                    HashMap<String, String> secretKeyPairs = new HashMap<>();
                    for (int j = 0; j < jSecretKeys.length(); j ++) {
                        JSONObject jSecretKey = jSecretKeys.getJSONObject(j);
                        secretKeyPairs.put(jSecretKey.getString("type"), jSecretKey.getString("secret"));
                    }
                    Keys.addP2pKey(jUserKey.getString("userid"), secretKeyPairs);
                }
            } catch (JSONException e) {
                WxLog.e("GetP2pKeyRsp", e.getMessage(), e);
                resultcode = "";
            }
        } else {
            WxLog.e("GetP2pKeyRsp", "code: " + resultcode + " message: " + resultmessage);
        }
    }
}