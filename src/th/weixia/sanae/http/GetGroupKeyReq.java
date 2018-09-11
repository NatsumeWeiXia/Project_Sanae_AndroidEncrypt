package th.weixia.sanae.http;

import th.weixia.common.log.WxLog;
import th.weixia.sanae.utils.WxStringUtil;


import java.util.List;

/**
 * Created by sy on 16.10.14.
 */
public class GetGroupKeyReq extends BaseCryptRequest {

    private String sessionId;
    private List<String> groupIds;

    public GetGroupKeyReq(String url) {
        super(url, "mobileark.secret.getgroupkey", "1.0");
    }

    @Override
    public String getHttpReqBody() {
        super.getHttpReqBody();
        try {
            json.put("sessionid", sessionId);
            StringBuffer sbGroupId = new StringBuffer();
            for (String groupId:groupIds) {
                sbGroupId.append(groupId).append(",");
            }
            json.put("groupids", sbGroupId.deleteCharAt(sbGroupId.length() - 1).toString());
        } catch (Exception e) {
            WxLog.e("GetGroupKeyReq", e.getMessage(), e);
        }
        return WxStringUtil.json2Normal(json.toString());
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setUserIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }
}
