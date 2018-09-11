package th.weixia.sanae;

import android.content.Context;

import th.weixia.sanae.crypt._CryptAgent;
import th.weixia.sanae.encrypt.AgentCallbackListener;
import th.weixia.sanae.encrypt.FileCryptCallbackListener;
import th.weixia.sanae.encrypt.TextCryptCallbackListener;
import th.weixia.sanae.utils.ErrorMessage;
import th.weixia.sanae.utils.WxStringUtil;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 加解密类
 *
 * Created by sy on 2016/10/12.
 */
public class SanaeCryptAgent {

	private static SanaeCryptAgent mInstance;

	private _CryptAgent agent;

	private SanaeCryptAgent(Context context) {
		agent = new _CryptAgent(context);
	}

	public static SanaeCryptAgent getInstance(Context context) {
		if (context == null) {
			return null;
		}

		if (mInstance == null) {
			synchronized (SanaeCryptAgent.class) {
				if (mInstance == null) {
					mInstance = new SanaeCryptAgent(context);
				}
			}
		}
		return mInstance;
	}

	public boolean setAddress(String url) {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			agent.url = url;
			return true;
		}
		return false;
	}

	/**
	 * 初始化
	 * @return
     */
	public void initAgent(String token, String appKey, String sec, AgentCallbackListener listener) {

		if (listener == null) {
			return;
		}
		if (WxStringUtil.hasEmptyString(token, appKey, sec)) {
			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
			return;
		}

		agent.init(token, appKey, sec, listener);
	}

//	/**
//	 * 同步加密文本
//	 * @param isGroup 是否为群组
//	 * @param imAccountOrGroupId 目标用户ImAccount或者群组Id
//	 * @param data 加密内容
//   * @return
//   */
//	public String encryptTextSync(boolean isGroup, String imAccountOrGroupId, String data)
//			throws NoKeyFoundException, UninitException {
//
//		if (WxStringUtil.hasEmptyString(imAccountOrGroupId, data)) {
//			return null;
//		}
//
//		return agent.encryptTextSync(isGroup, imAccountOrGroupId, data);
//	}
//
//	/**
//	 * 同步加密文件
//	 * @param isGroup 是否为群组
//	 * @param imAccountOrGroupId 目标用户ImAccount或者群组Id
//	 * @param data 加密内容
//	 * @return
//	 */
//	public byte[] encryptBytesSync(boolean isGroup, String imAccountOrGroupId, byte[] data)
//			throws NoKeyFoundException, UninitException {
//
//		if (WxStringUtil.isEmpty(imAccountOrGroupId) || data == null || data.length < 1) {
//			return null;
//		}
//
//		return agent.encryptBytesSync(isGroup, imAccountOrGroupId, data);
//	}
//
//	/**
//	 * 同步解密文本
//	 * @param isGroup 是否为群组
//	 * @param imAccountOrGroupId 目标用户ImAccount或者群组Id
//	 * @param data 解密内容
//	 * @return
//	 */
//	public String decryptTextSync(boolean isGroup, String imAccountOrGroupId, String data)
//			throws NoKeyFoundException, UninitException {
//
//		if (WxStringUtil.hasEmptyString(imAccountOrGroupId, data)) {
//			return null;
//		}
//
//		return agent.decryptTextSync(isGroup, imAccountOrGroupId, data);
//	}
//
//	/**
//	 * 同步解密文件
//	 * @param isGroup 是否为群组
//	 * @param imAccountOrGroupId 目标用户ImAccount或者群组Id
//	 * @param data 解密内容
//	 * @return
//	 */
//	public byte[] decryptBytesSync(boolean isGroup, String imAccountOrGroupId, byte[] data)
//			throws NoKeyFoundException, UninitException {
//
//		if (WxStringUtil.isEmpty(imAccountOrGroupId) || data == null || data.length < 1) {
//			return null;
//		}
//
//		return agent.decryptBytesSync(isGroup, imAccountOrGroupId, data);
//	}
//
//	/**
//	 * 获取点对点密钥
//	 * @param imAccounts 目标用户的ImAccount 支持多用户
//	 * @param listener 回调
//	 * @return
//   */
//	public void generateP2pKey(List<String> imAccounts, CryptCallbackListener listener) {
//
//		if (listener == null) {
//			return;
//		}
//		if (imAccounts == null || imAccounts.isEmpty()) {
//			listener.onCallBack(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString, null, null);
//			return;
//		}
//
//		agent.generateP2pKey(imAccounts, listener);
//	}
//
//	/**
//	 * 获取群组密钥
//	 * @param groupIds 群组Id 支持多群组
//	 * @param listener 回调
//	 * @return
//   */
//	public void generateGroupKey(List<String> groupIds, CryptCallbackListener listener) {
//
//		if (listener == null) {
//			return;
//		}
//		if (groupIds == null || groupIds.isEmpty()) {
//			listener.onCallBack(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString, null, null);
//			return;
//		}
//
//		agent.generateGroupKey(groupIds, listener);
//	}

//	/**
//	 * 维护群组密钥
//	 * @param groupId 群组Id
//	 * @param addUserIds 添加的成员列表
//	 * @param delUserIds 删除的成员列表
//   * @return
//   */
//	public void maintainGroupInfo(String groupId, List<String> addUserIds, List<String> delUserIds, AgentCallbackListener listener) {
//
//		if (listener == null) {
//			return;
//		}
//		if (WxStringUtil.isEmpty(groupId)) {
//			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
//			return;
//		}
//
//		agent.maintainGroupInfo(groupId, addUserIds, delUserIds, listener);
//	}

	/**
	 * 点对点异步加密文字
     */
	public void p2pTextEncrypt(String token, String imAccount, String data, TextCryptCallbackListener listener) {

		if (listener == null) {
			return;
		}
		if (WxStringUtil.hasEmptyString(token, imAccount, data)) {
			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
			return;
		}

		agent.encryptTextAsyn(false, token, imAccount, data, listener);
	}

	/**
	 * 群组异步加密文字
	 */
	public void groupTextEncrypt(String token, String groupId, String data, TextCryptCallbackListener listener) {

		if (listener == null) {
			return;
		}
		if (WxStringUtil.hasEmptyString(token, groupId, data)) {
			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
			return;
		}

		agent.encryptTextAsyn(true, token, groupId, data, listener);
	}

	/**
	 * 点对点异步加密文件
	 */
	public void p2pFileEncrypt(String token, String imAccount, InputStream in, OutputStream out, FileCryptCallbackListener listener) {

		if (listener == null) {
			return;
		}
		if (WxStringUtil.hasEmptyString(token)) {
			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
			return;
		}

		agent.encryptFileAsyn(false, token, imAccount, in, out, listener);
	}

	/**
	 * 群组异步加密文件
	 */
	public void groupFileEncrypt(String token, String groupId, InputStream in, OutputStream out, FileCryptCallbackListener listener) {

		if (listener == null) {
			return;
		}
		if (WxStringUtil.hasEmptyString(token)) {
			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
			return;
		}

		agent.encryptFileAsyn(true, token, groupId, in, out, listener);
	}

	/**
	 * 点对点异步解密文字
	 */
	public void p2pTextDecrypt(String token, String imAccount, String data, TextCryptCallbackListener listener) {

		if (listener == null) {
			return;
		}
		if (WxStringUtil.hasEmptyString(token, imAccount, data)) {
			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
			return;
		}

		agent.decryptTextAsyn(false, token, imAccount, data, listener);
	}

	/**
	 * 群组异步解密文字
	 */
	public void groupTextDecrypt(String token, String groupId, String data, TextCryptCallbackListener listener) {

		if (listener == null) {
			return;
		}
		if (WxStringUtil.hasEmptyString(token, groupId, data)) {
			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
			return;
		}

		agent.decryptTextAsyn(true, token, groupId, data, listener);
	}

	/**
	 * 点对点异步解密文件
	 */
	public void p2pFileDecrypt(String token, String imAccount, InputStream in, OutputStream out, FileCryptCallbackListener listener) {

		if (listener == null) {
			return;
		}
		if (WxStringUtil.hasEmptyString(token)) {
			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
			return;
		}

		agent.decryptFileAsyn(false, token, imAccount, in, out, listener);
	}

	/**
	 * 群组异步解密文件
	 */
	public void groupFileDecrypt(String token, String groupId, InputStream in, OutputStream out, FileCryptCallbackListener listener) {

		if (listener == null) {
			return;
		}
		if (WxStringUtil.hasEmptyString(token)) {
			listener.onError(ErrorMessage.errorNullInputCode, ErrorMessage.errorNullInputString);
			return;
		}

		agent.decryptFileAsyn(true, token, groupId, in, out, listener);
	}
}
