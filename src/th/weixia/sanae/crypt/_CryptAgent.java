package th.weixia.sanae.crypt;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Message;

import th.weixia.common.http.WxHttpHandler;
import th.weixia.common.http.exceptions.CusHttpException;
import th.weixia.common.http.rsp.BaseResponse;
import th.weixia.common.log.WxLog;
import th.weixia.sanae.encrypt.AgentCallbackListener;
import th.weixia.sanae.encrypt.CallbackListener;
import th.weixia.sanae.encrypt.FileCryptCallbackListener;
import th.weixia.sanae.encrypt.TextCryptCallbackListener;
import th.weixia.sanae.http.BaseCryptRequest;
import th.weixia.sanae.http.GetGroupKeyReq;
import th.weixia.sanae.http.GetP2pKeyReq;
import th.weixia.sanae.http.HttpMsgo;
import th.weixia.sanae.http.InitReq;
import th.weixia.sanae.http.InitRsp;
import th.weixia.sanae.http.MaintenanceGroupReq;
import th.weixia.sanae.utils.Base64;
import th.weixia.sanae.utils.ErrorMessage;
import th.weixia.sanae.utils.MD5Utils;
import th.weixia.sanae.utils.WxStringUtil;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sy on 2016/10/12.
 */
public class _CryptAgent {

    private final String TAG = "CryptSDK";

    private Context context;

    public String url = "";

    private String sec = "";

    public _CryptAgent(Context context) {
        this.context = context;
        WxLog.init(TAG, context.getExternalFilesDir(null).getAbsolutePath() + "/" + TAG);
        WxLog.d("on new");
    }

    public void init(String token, String appKey, String sec, AgentCallbackListener listener) {

        InputData inputData = new InputData();
        inputData.listener = listener;

        Keys.token = token;
        BaseCryptRequest.APPKEY = appKey;

        this.sec = sec;

        generateSessionId(creatTask(TASK_INIT, inputData));
    }

    public void maintainGroupInfo(String groupId, List<String> addUserIds, List<String> delUserIds, AgentCallbackListener listener) {

        if (Keys.needInit()) {
            listener.onError(ErrorMessage.errorUninitCode, ErrorMessage.errorUninitMessage);
            return;
        }

        InputData inputData = new InputData();
        inputData.ids.add(groupId);
        inputData.addUsers = addUserIds;
        inputData.delUsers = delUserIds;
        inputData.listener = listener;
        int taskId = creatTask(TASK_MAINTANENCEGROUP, inputData);

        if (Keys.getSessionId() == null) {
            generateSessionId(taskId);
            return;
        }
        maintanGroup(taskId);
    }

    public void encryptTextAsyn(boolean isGroup, String token, String id, String data, TextCryptCallbackListener listener) {

        if (!Keys.doAuthentication(token)) {
            listener.onError(ErrorMessage.errorUninitCode, ErrorMessage.errorUninitMessage);
            return;
        }

        InputData inputData = new InputData();
        inputData.dataString = data;
        inputData.ids.add(id);
        inputData.listener = listener;
        inputData.encrypt = true;
        inputData.isGroup = isGroup;

        try {

            String result = encryptTextSync(inputData);

            if (result == null) {
                listener.onError(ErrorMessage.errorCryptFailed, ErrorMessage.errorCryptFailedString);
            } else {
                listener.onFinish(result);
            }

        } catch (NoKeyFoundException e) {
            getKeyForAsyn(isGroup, creatTask(TASK_ENCRYPTTEXT, inputData));
        }
    }

    public void decryptTextAsyn(boolean isGroup, String token, String id, String data, TextCryptCallbackListener listener) {

        if (!Keys.doAuthentication(token)) {
            listener.onError(ErrorMessage.errorUninitCode, ErrorMessage.errorUninitMessage);
            return;
        }

        InputData inputData = new InputData();
        inputData.dataString = data;
        inputData.ids.add(id);
        inputData.listener = listener;
        inputData.encrypt = false;
        inputData.isGroup = isGroup;

        try {
            String result = decryptTextSync(inputData);
            if (result == null) {
                listener.onError(ErrorMessage.errorCryptFailed, ErrorMessage.errorCryptFailedString);
            } else {
                listener.onFinish(result);
            }
        } catch (NoKeyFoundException e) {
            getKeyForAsyn(isGroup, creatTask(TASK_DECRYPTTEXT, inputData));
        }
    }

    public void encryptFileAsyn(boolean isGroup, String token, String id, InputStream in, OutputStream out, FileCryptCallbackListener listener) {

        if (!Keys.doAuthentication(token)) {
            listener.onError(ErrorMessage.errorUninitCode, ErrorMessage.errorUninitMessage);
            return;
        }

        InputData inputData = new InputData();
        inputData.in = in;
        inputData.out = out;
        inputData.ids.add(id);
        inputData.listener = listener;
        inputData.encrypt = true;
        inputData.isGroup = isGroup;

        try {
            encryptFileSync(inputData);
        } catch (NoKeyFoundException e) {
            getKeyForAsyn(isGroup, creatTask(TASK_ENCRYPTFILE, inputData));
        }
    }

    public void decryptFileAsyn(boolean isGroup, String token, String id, InputStream in, OutputStream out, FileCryptCallbackListener listener) {

        if (!Keys.doAuthentication(token)) {
            listener.onError(ErrorMessage.errorUninitCode, ErrorMessage.errorUninitMessage);
            return;
        }

        InputData inputData = new InputData();
        inputData.in = in;
        inputData.out = out;
        inputData.ids.add(id);
        inputData.listener = listener;
        inputData.encrypt = false;
        inputData.isGroup = isGroup;

        try {
            decryptFileSync(inputData);
        } catch (NoKeyFoundException e) {
            getKeyForAsyn(isGroup, creatTask(TASK_DECRYPTFILE, inputData));
        }
    }

//    public void generateP2pKey(List<String> imAccounts, AgentCallbackListener listener) {
//        generateP2pKey(imAccounts, listener, null, null, true);
//    }

//    public void generateGroupKey(List<String> imAccounts, AgentCallbackListener listener) {
//        generateGroupKey(imAccounts, listener, null, null, true);
//    }

    /************************* 逻辑处理 ****************************/

    private void handleMessage(Message msg) {

        switch (msg.what) {
            case HttpMsgo.INIT:
                InitRsp initRsp = (InitRsp) msg.obj;
                Task taskINIT = getTask(msg.arg1);
                if (taskINIT == null) {
                    WxLog.e("HTTP_I", "NULL TASK_" + msg.arg1);
                    break;
                }
                if (initRsp.isOK()) {
                    Keys.setSessionId(initRsp.getSessionId(), initRsp.getSessionTimeout());
                    continuePreviousTask(true, msg.arg1);
                } else {
                    WxLog.e("HTTP_MG", initRsp.getResultcode() + ":" + initRsp.getResultmessage());
                    Keys.setSessionId(null, -1);
                    if (taskINIT.getType() == TASK_INIT) {
                        taskINIT.getInputData().listener.onError(initRsp.getResultcode()
                                , initRsp.getResultmessage());
                        taskINIT.finish();
                    } else
                        continuePreviousTask(false, msg.arg1);
                }
                break;

            case HttpMsgo.GET_P2P_KEY:
                GetP2pKeyRsp getP2pKeyRsp = (GetP2pKeyRsp) msg.obj;
                Task taskGET_P2P_KEY = getTask(msg.arg1);
                if (taskGET_P2P_KEY == null) {
                    WxLog.e("HTTP_GPK", "NULL TASK_" + msg.arg1);
                    break;
                }
                if (!getP2pKeyRsp.isOK()) {
                    WxLog.e("HTTP_MG", getP2pKeyRsp.getResultcode() + ":" + getP2pKeyRsp.getResultmessage());
                }
                continuePreviousTask(false, msg.arg1);
                break;

            case HttpMsgo.GET_GROUP_KEY:
                GetGroupKeyRsp getGroupKeyRsp = (GetGroupKeyRsp) msg.obj;
                Task taskGET_GROUP_KEY = getTask(msg.arg1);
                if (taskGET_GROUP_KEY == null) {
                    WxLog.e("HTTP_GGK", "NULL TASK_" + msg.arg1);
                    break;
                }
                if (!getGroupKeyRsp.isOK()) {
                    WxLog.e("HTTP_MG", getGroupKeyRsp.getResultcode() + ":" + getGroupKeyRsp.getResultmessage());
                }
                continuePreviousTask(false, msg.arg1);
                break;

            case HttpMsgo.MAINTENANCE_GROUP:
                MaintenanceGroupRsp maintenanceGroupRsp = (MaintenanceGroupRsp) msg.obj;
                Task taskMAINTENANCE_GROUP = getTask(msg.arg1);
                if (taskMAINTENANCE_GROUP == null) {
                    WxLog.e("HTTP_MG", "NULL TASK_" + msg.arg1);
                    break;
                }
                if (!maintenanceGroupRsp.isOK()) {
                    WxLog.e("HTTP_MG", maintenanceGroupRsp.getResultcode() + ":" + maintenanceGroupRsp.getResultmessage());
                    taskMAINTENANCE_GROUP.getInputData().listener.onError(maintenanceGroupRsp.getResultcode()
                            , maintenanceGroupRsp.getResultmessage());
                } else {
                    ((AgentCallbackListener) taskMAINTENANCE_GROUP.getInputData().listener).onSuccess();
                }
                taskMAINTENANCE_GROUP.finish();
                break;
        }
    }

    private void continuePreviousTask(boolean autoGetKey, int taskId) {

        Task task = getTask(taskId);

        InputData inputData = task.getInputData();

        switch (task.getType()) {
            case TASK_INIT:
                ((AgentCallbackListener) inputData.listener).onSuccess();
                task.finish();
                break;
            case TASK_ENCRYPTTEXT:
            case TASK_DECRYPTTEXT:
            case TASK_ENCRYPTFILE:
            case TASK_DECRYPTFILE:
                if (generateing.containsKey(inputData.ids.get(0))) {
                    List<Integer> ids = new ArrayList<>();
                    ids.addAll(generateing.get(inputData.ids.get(0)));
                    generateing.remove(inputData.ids.get(0));

                    for (Integer id : ids) {
                        InputData data = getTask(id).inputData;

                        if (data.listener instanceof TextCryptCallbackListener) {
                            if (data.encrypt) {
                                try {
                                    ((TextCryptCallbackListener) data.listener).onFinish(encryptTextSync(data));
                                    getTask(id).finish();
                                } catch (NoKeyFoundException e) {
                                    if (autoGetKey) {
                                        getKeyForAsyn(data.isGroup, taskId);
                                    } else {
                                        WxLog.e("C_E_T", "generate key failed: " + data.ids.get(0));
                                        data.listener.onError(ErrorMessage.errorGetKeyCode, ErrorMessage.errorGetKeyMessage);
                                        getTask(id).finish();
                                    }
                                }
                            } else {
                                try {
                                    String result = decryptTextSync(data);
                                    if (result == null) {
                                        data.listener.onError(ErrorMessage.errorCryptFailed, ErrorMessage.errorCryptFailedString);
                                    } else {
                                        ((TextCryptCallbackListener) data.listener).onFinish(result);
                                    }
                                    getTask(id).finish();
                                } catch (NoKeyFoundException e) {
                                    if (autoGetKey) {
                                        getKeyForAsyn(data.isGroup, taskId);
                                    } else {
                                        WxLog.e("C_D_T", "generate key failed: " + data.ids.get(0));
                                        data.listener.onError(ErrorMessage.errorGetKeyCode, ErrorMessage.errorGetKeyMessage);
                                        getTask(id).finish();
                                    }
                                }
                            }
                        } else {
                            if (data.encrypt) {
                                try {
                                    encryptFileSync(data);
                                    getTask(id).finish();
                                } catch (NoKeyFoundException e) {
                                    if (autoGetKey) {
                                        getKeyForAsyn(data.isGroup, taskId);
                                    } else {
                                        WxLog.e("C_E_F", "generate key failed: " + data.ids.get(0));
                                        data.listener.onError(ErrorMessage.errorGetKeyCode, ErrorMessage.errorGetKeyMessage);
                                        getTask(id).finish();
                                    }
                                }
                            } else {
                                try {
                                    decryptFileSync(data);
                                    getTask(id).finish();
                                } catch (NoKeyFoundException e) {
                                    if (autoGetKey) {
                                        getKeyForAsyn(data.isGroup, taskId);
                                    } else {
                                        WxLog.e("C_D_F", "generate key failed: " + data.ids.get(0));
                                        data.listener.onError(ErrorMessage.errorGetKeyCode, ErrorMessage.errorGetKeyMessage);
                                        getTask(id).finish();
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void finishAllGenerateingTask() {

    }

    /********************** 功能函数 ************************/

    private void maintanGroup(int taskId) {

        InputData inputData = getTask(taskId).getInputData();

        MaintenanceGroupReq req = new MaintenanceGroupReq(url);
        req.setSessionId(Keys.getSessionId());
        req.setAddUserIds(inputData.addUsers);
        req.setDelUserIds(inputData.delUsers);
        req.setGroupId(inputData.ids.get(0));
        req.setTaskId(taskId);
        MaintenanceGroupRsp rsp = new MaintenanceGroupRsp();
        rsp.setMsgno(HttpMsgo.MAINTENANCE_GROUP);

        new HttpMsgThread(context, req, rsp).start();
    }

    private void generateSessionId(int taskId) {

        String packageName = context.getApplicationContext().getPackageName();

        InitReq req = new InitReq(url);
        req.setPackageName(packageName);
        req.setAppSign(packageName);
        req.setToken(Keys.token);
        req.setTaskId(taskId);
        req.setSec(sec);
        InitRsp rsp = new InitRsp();
        rsp.setMsgno(HttpMsgo.INIT);
        new HttpMsgThread(context, req, rsp).start();
    }

    private void generateP2pKey(int taskId, List<String> targetIds) {


        GetP2pKeyReq req = new GetP2pKeyReq(url);
        req.setTaskId(taskId);
        req.setSessionId(Keys.getSessionId());
        req.setUserIds(targetIds);
        GetP2pKeyRsp rsp = new GetP2pKeyRsp();
        rsp.setMsgno(HttpMsgo.GET_P2P_KEY);
        new HttpMsgThread(context, req, rsp).start();
    }

    private void generateGroupKey(int taskId, List<String> targetIds) {

        GetGroupKeyReq req = new GetGroupKeyReq(url);
        req.setTaskId(taskId);
        req.setSessionId(Keys.getSessionId());
        req.setUserIds(targetIds);
        GetGroupKeyRsp rsp = new GetGroupKeyRsp();
        rsp.setMsgno(HttpMsgo.GET_GROUP_KEY);
        new HttpMsgThread(context, req, rsp).start();
    }

    /******************* 加解密 **********************/

    private String encryptTextSync(InputData inputData)
            throws NoKeyFoundException {

        byte[] _data = inputData.dataString.getBytes();

        byte ct = 0x00; // 文本
        byte ea = 0x00; // aes
        byte[] head = generateHead(ct, ea);

        String key = getKey(inputData.isGroup, inputData.ids.get(0), head);

        byte[] body = Crypt.encryptAES(key, _data);
        if (body == null) {
            WxLog.e("encryptTextSync", "encrypt failed");
            return null;
        }

        byte[] resultBytes = new byte[body.length + 2];
        resultBytes[0] = head[0];
        resultBytes[1] = head[1];
        for (int i = 0; i < body.length; i++) {
            resultBytes[i + 2] = body[i];
        }
        resultBytes = Base64.encodeBase64(resultBytes);
        String result;
        try {
            result = new String(resultBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            WxLog.e("encryptTextSync", e.getMessage(), e);
            return null;
        }
        return result;
    }

    private String decryptTextSync(InputData inputData)
            throws NoKeyFoundException {

        try {
            byte[] _data = inputData.dataString.getBytes("UTF-8");
            _data = Base64.decodeBase64(_data);
            byte[] head = new byte[2];
            head[0] = _data[0];
            head[1] = _data[1];

            byte[] body = new byte[_data.length - 2];
            for (int i = 0; i < _data.length - 2; i++) {
                body[i] = _data[i + 2];
            }

            String key = getKey(inputData.isGroup, inputData.ids.get(0), head);

            byte[] resultBytes = Crypt.decryptAES(key, body);

            if (resultBytes == null) {
                WxLog.e("decryptTextSync", "decrypt failed");
                return null;
            } else {
                return new String(resultBytes);
            }

        } catch (UnsupportedEncodingException e) {
            WxLog.e("decryptText", e.getMessage(), e);
            return null;
        }
    }

    private void encryptFileSync(InputData inputData)
            throws NoKeyFoundException {

        byte ct = 0x01; // 文件
        byte ea = 0x00; // tea
        byte[] head = generateHead(ct, ea);

        String key = getKey(inputData.isGroup, inputData.ids.get(0), head);
        byte[] dataBuf;
        ((FileCryptCallbackListener) inputData.listener).onStart();

        try {
            inputData.out.write(head);
            while (true) {
                try {
                    if (inputData.in.available() == 0) {
                        break;
                    } else if (inputData.in.available() > 1024 * 1024) {
                        dataBuf = new byte[1024 * 1024];
                    } else {
                        dataBuf = new byte[inputData.in.available()];
                    }
                } catch (IOException e) {
                    dataBuf = new byte[1024 * 1024];
                }
                if (-1 != inputData.in.read(dataBuf)) {

                    byte[] dataEncrypt = Crypt.encryptTEA(key, dataBuf);
                    if (dataEncrypt == null) {
                        WxLog.e("file encrypt", "encrypt failed");
                        inputData.listener.onError(ErrorMessage.errorCryptFailed, ErrorMessage.errorCryptFailedString);
                        return;
                    }

                    inputData.out.write(dataEncrypt);
                } else {
                    byte[] dataEncrypt = Crypt.encryptTEA(key, dataBuf);
                    if (dataEncrypt == null) {
                        WxLog.e("file encrypt", "encrypt failed");
                        inputData.listener.onError(ErrorMessage.errorCryptFailed, ErrorMessage.errorCryptFailedString);
                        return;
                    }

                    inputData.out.write(dataEncrypt);
                    inputData.out.flush();
                    break;
                }
            }

            ((FileCryptCallbackListener) inputData.listener).onFinish();

        } catch (IOException e) {
            WxLog.e("file encrypt", "ioException", e);
            inputData.listener.onError(ErrorMessage.errorIoExceptionCode, ErrorMessage.errorIoExceptionMessage);
        }
    }

    private void decryptFileSync(InputData inputData)
            throws NoKeyFoundException {

        try {
            if (inputData.head == null) {
                inputData.head = new byte[2];
                inputData.in.read(inputData.head);
            }

            String key = getKey(inputData.isGroup, inputData.ids.get(0), inputData.head);

            byte[] bodyBuf;
            boolean first = true;
            while (true) {
                try {
                    if (inputData.in.available() == 0) {
                        break;
                    } else if (inputData.in.available() > 1024 * 1024 + 4) {
                        bodyBuf = new byte[1024 * 1024 + 4];
                    } else {
                        bodyBuf = new byte[inputData.in.available()];
                    }
                } catch (IOException e) {
                    bodyBuf = new byte[1024 * 1024 + 4];
                }
                if (-1 != inputData.in.read(bodyBuf)) {

                    byte[] dataDecrypt = Crypt.decryptTEA(key, bodyBuf);
                    if (dataDecrypt == null) {
                        WxLog.e("file decrypt", "decrypt failed");
                        inputData.listener.onError(ErrorMessage.errorCryptFailed, ErrorMessage.errorCryptFailedString);
                        return;
                    }

                    inputData.out.write(dataDecrypt);
                    if (first) {
                        ((FileCryptCallbackListener) inputData.listener).onStart();
                        first = false;
                    }
                } else {
                    byte[] dataDecrypt = Crypt.decryptTEA(key, bodyBuf);
                    if (dataDecrypt == null) {
                        WxLog.e("file decrypt", "decrypt failed");
                        inputData.listener.onError(ErrorMessage.errorCryptFailed, ErrorMessage.errorCryptFailedString);
                        return;
                    }

                    inputData.out.write(dataDecrypt);
                    inputData.out.flush();
                    break;
                }
            }

            ((FileCryptCallbackListener) inputData.listener).onFinish();

        } catch (IOException e) {
            WxLog.e("file encrypt", "ioException", e);
            inputData.listener.onError(ErrorMessage.errorIoExceptionCode, ErrorMessage.errorIoExceptionMessage);
        }
    }

    /********************* 工具函数 ***********************/

    private String getSignMD5(String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return MD5Utils.GetMD5Code(sign.toCharsString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private synchronized void getKeyForAsyn(boolean isGroup, Integer taskId) {

        WxLog.d("encryptTextAsyn", "no key found");

        Task task = getTask(taskId);

        if (task == null) {
            WxLog.e("G_G_K", "NULL TASK_" + taskId);
            return;
        }

        if (Keys.needInit()) {
            task.getInputData().listener.onError(ErrorMessage.errorUninitCode, ErrorMessage.errorUninitMessage);
            WxLog.d("NEED INIT");
            task.finish();
            return;
        }

        if (Keys.getSessionId() == null) {
            generateSessionId(taskId);
            return;
        }

        String targetId = task.getInputData().ids.get(0);

        if (generateing.containsKey(targetId))
            generateing.get(targetId).add(taskId);
        else {
            List<Integer> taskIds = new ArrayList<>();
            taskIds.add(taskId);
            generateing.put(targetId, taskIds);

            if (isGroup) {
                generateGroupKey(taskId, task.getInputData().ids);
            } else {
                generateP2pKey(taskId, task.getInputData().ids);
            }
        }
    }

    private String getKey(boolean isGroup, String imAccountOrGroupId, byte[] head)
            throws NoKeyFoundException {

        String key;
        try {
            if (isGroup) {
                key = Keys.getGroupKey(imAccountOrGroupId, WxStringUtil.getBinaryStrFromByte(head[1]));
            } else {
                key = Keys.getP2pKey(imAccountOrGroupId, WxStringUtil.getBinaryStrFromByte(head[1]));
            }
        } catch (Exception e) {
            key = null;
        }
        if (key == null) {
            throw new NoKeyFoundException();
        }

        return key;
    }

    private byte[] generateHead(byte ct, byte ea) {
        /** Header Version（hv）	Check Flag（cf）        *
         *  Content Type（ct）	Encrypt Algorithm（ea）**/
        byte[] head = new byte[2];

        byte hv = 0x01; // 0001

        byte cf = (byte) (hv ^ ct ^ ea);

        head[0] = (byte) (hv << 4 | cf);
        head[1] = (byte) (ct << 4 | ea);

        return head;
    }

    /**
     * HTTP传输线程
     */
    private class HttpMsgThread extends Thread {
        private BaseCryptRequest request;
        private BaseResponse response;
        private Context context;

        public HttpMsgThread(Context context, BaseCryptRequest request,
                             BaseResponse response) {
            this.request = request;
            this.response = response;
            this.context = context;
        }

        public void run() {
            try {
                WxHttpHandler handler = new WxHttpHandler(context);
                handler.sendMessage(request, response);
                WxLog.d("send http over... req:" + response.getMsgno());
            } catch (Exception e) {
                e.printStackTrace();

                if (e instanceof CusHttpException) {
                    CusHttpException cEx = (CusHttpException) e;

                    int exNo = cEx.getExNo();

                    if (exNo == CusHttpException.NULL_SESSIION_EX) {

                    } else if (exNo == CusHttpException.RELOGIN_EX) {

                    }
                }
            } finally {

            }


            Message msg = new Message();
            msg.what = response.getMsgno();
            msg.obj = response;
            msg.arg1 = request.getTaskId();
            WxLog.d("rsp: " + response.getMsgno() + "rspCode: " + response.getResultcode() + " rspMessage: " + response.getResultmessage());
            handleMessage(msg);
        }
    }

    /*********  任务队列  *********/
    private static List<Task> taskList = new ArrayList<>();

    private static Map<String, List<Integer>> generateing = new HashMap<>();

    private final int TASK_INIT = 1;
    private final int TASK_MAINTANENCEGROUP = 2;
    private final int TASK_ENCRYPTTEXT = 3;
    private final int TASK_DECRYPTTEXT = 4;
    private final int TASK_ENCRYPTFILE = 5;
    private final int TASK_DECRYPTFILE = 6;

    private int creatTask(int taskType, InputData inputData) {
        synchronized (Task.class) {
            taskList.add(new Task(taskType, inputData));
            WxLog.d("TASK CREAT: TYPE = " + taskType + "ID = " + (taskList.size() - 1));
        }
        return taskList.size() - 1;
    }

    private Task getTask(int taskId) {
        return taskList.get(taskId);
    }

    private class Task {

        public int id;

        private int type;

        private InputData inputData;

        public Task(int type, InputData inputData) {
            this.type = type;
            this.inputData = inputData;
            id = taskList.size();
        }

        public void finish() {
            this.inputData = null;
            WxLog.d("TASK FINISH: TYPE = " + type + "ID = " + id);
            taskList.set(id, null);
        }

        public int getType() {
            return type;
        }

        public InputData getInputData() {
            return inputData;
        }
    }

    private class InputData {
        //flag
        public boolean isGroup;
        public boolean encrypt;
        //target accounts or groupids
        public List<String> ids = new ArrayList<>();
        //datas
        public InputStream in;
        public byte[] head;
        public OutputStream out;
        public String dataString;
        public List<String> addUsers;
        public List<String> delUsers;

        //callback
        public CallbackListener listener;
    }

    /**
     * 未获取KEY异常
     */
    public static class NoKeyFoundException extends Exception {
        public NoKeyFoundException() {
            super("No key found, generate a key before this!");
        }
    }
}
