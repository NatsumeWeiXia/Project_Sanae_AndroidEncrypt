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
public class GetGroupKeyRsp extends BaseJsonResponseMsg {

    @Override
    public void init(HttpResponse httpresponse) {
        super.init(httpresponse);
        if (isOK()) {
            try {
                JSONArray jGroupKeys = jso.getJSONArray("groupkeys");
                for (int i = 0; i < jGroupKeys.length(); i++) {
                    JSONObject jGroupKey = jGroupKeys.getJSONObject(i);
                    JSONArray jSecretKeys = jGroupKey.getJSONArray("secretkeys");
                    HashMap<String, String> secretKeyPairs = new HashMap<>();
                    for (int j = 0; j < jSecretKeys.length(); j ++) {
                        JSONObject jSecretKey = jSecretKeys.getJSONObject(j);
                        secretKeyPairs.put(jSecretKey.getString("type"), jSecretKey.getString("secret"));
                    }
                    Keys.addGroupKey(jGroupKey.getString("groupid"), secretKeyPairs);
                }
            } catch (JSONException e) {
                WxLog.e("GetGroupKeyRsp", e.getMessage(), e);
                resultcode = "";
            }
        } else {
            WxLog.e("GetGroupKeyRsp", "code: " + resultcode + " message: " + resultmessage);
        }
    }
}