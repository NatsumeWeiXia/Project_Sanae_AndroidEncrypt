package th.weixia.sanae.http;

import th.weixia.common.log.WxLog;
import th.weixia.sanae.utils.WxStringUtil;

import java.util.List;

/**
 * Created by sy on 16.10.24.
 */
public class MaintenanceGroupReq extends BaseCryptRequest {

    private String sessionId;

    private String groupId;

    private List<String> addUserIds;

    private List<String> delUserIds;

    public MaintenanceGroupReq(String url) {
        super(url, "mobileark.secret.maintenance", "1.0");
    }

    @Override
    public String getHttpReqBody() {
        super.getHttpReqBody();
        try {
            json.put("sessionid", sessionId);
            json.put("groupid", groupId);

            StringBuffer sbAddUserIds = new StringBuffer();
            for (String addUserId:addUserIds) {
                sbAddUserIds.append(addUserId).append(",");
            }
            json.put("adduserids", sbAddUserIds.deleteCharAt(sbAddUserIds.length() - 1).toString());

            StringBuffer sbDelUserIds = new StringBuffer();
            for (String delUserId:delUserIds) {
                sbDelUserIds.append(delUserId).append(",");
            }
            json.put("deluserids", sbDelUserIds.deleteCharAt(sbDelUserIds.length() - 1).toString());
        } catch (Exception e) {
            WxLog.e("MaintenanceGroupReq", e.getMessage(), e);
        }
        return WxStringUtil.json2Normal(json.toString());
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setAddUserIds(List<String> addUserIds) {
        this.addUserIds = addUserIds;
    }

    public void setDelUserIds(List<String> delUserIds) {
        this.delUserIds = delUserIds;
    }
}
