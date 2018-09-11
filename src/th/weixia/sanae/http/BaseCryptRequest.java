package th.weixia.sanae.http;

import th.weixia.common.http.event.BaseRequest;
import th.weixia.common.log.WxLog;
import th.weixia.sanae.utils.WxStringUtil;

import org.json.JSONObject;

/**
 * Created by sy on 16.10.17.
 */
public class BaseCryptRequest extends BaseRequest {

    public static String APPKEY;

    protected String method;
    protected String version;

    protected int taskId;

    protected JSONObject json = new JSONObject();

    public BaseCryptRequest(String url, String method, String version) {
        super(url);
        this.method = method;
        this.version = version;
    }

    @Override
    public String getHttpReqBody() {
        try {
            json.put("method", method);
            json.put("v", version);
            json.put("format", "json");
            json.put("locale", "zh_CN");
            json.put("appKey", APPKEY);
        } catch (Exception e) {
            WxLog.e("basereq", e.getMessage(), e);
        }
        return WxStringUtil.json2Normal(json.toString());
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int dataId) {
        this.taskId = dataId;
    }
}
