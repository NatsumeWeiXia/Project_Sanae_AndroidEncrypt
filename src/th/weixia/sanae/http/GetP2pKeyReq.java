package th.weixia.sanae.http;

import th.weixia.common.log.WxLog;
import th.weixia.sanae.utils.WxStringUtil;

import java.util.List;

/**
 * Created by sy on 16.10.14.
 */
public class GetP2pKeyReq extends BaseCryptRequest {

    private String sessionId;
    private List<String> userIds;

    public GetP2pKeyReq(String url) {
        super(url, "mobileark.secret.getkey", "1.0");
    }

    @Override
    public String getHttpReqBody() {
        super.getHttpReqBody();
        try {
            json.put("sessionid", sessionId);
            StringBuffer sbUserId = new StringBuffer();
            for (String userId:userIds) {
                sbUserId.append(userId).append(",");
            }
            json.put("userids", sbUserId.deleteCharAt(sbUserId.length() - 1).toString());
        } catch (Exception e) {
            WxLog.e("GetP2pKeyReq", e.getMessage(), e);
        }
        return WxStringUtil.json2Normal(json.toString());
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
