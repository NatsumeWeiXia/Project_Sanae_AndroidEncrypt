package com.fiberhome.cryptsdkdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fiberhome.mobileark.encrypt.AgentCallbackListener;
import com.fiberhome.mobileark.encrypt.FileCryptCallbackListener;
import com.fiberhome.mobileark.encrypt.MobilearkCryptAgent;
import com.fiberhome.mobileark.encrypt.TextCryptCallbackListener;
import com.fiberhome.mobileark.http.BaseCryptRequest;
import com.fiberhome.mobileark.utils.WxStringUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends Activity {

	private MobilearkCryptAgent agent;
	
	private Button btnInit;
	private Button btnET;
	private Button btnDT;
	private Button btnEF;
	private Button btnDF;
	private Button btnMG;

	private RadioButton rbtn;

	private EditText etUrl;
	private EditText etAppKey;
	private EditText etToken;
	private EditText etSec;
	private EditText etId;
	private EditText etMsg;
	private EditText etFile;
	private EditText etFileE;
	private EditText etEFile;
	private EditText etFileD;
	private EditText etGroupId;
	private EditText etAdders;
	private EditText etDelers;
	
	private String encryptedText;

	private String address = "http://192.168.160.87:8080/thirdpartaccess";

	private String appKey = "EPM";

	private String token = "aab55217-653d-455a-ad34-6dbbdb8b2ba0";

	private String sec = "2e6e4728485c359e92af20abca1cc004";

	private String id = "im_wanglijun";

	private String msg = "待加密文字";

	private File path = Environment.getExternalStorageDirectory();

	private String filePath = path.getAbsolutePath() + File.separator + "1.jpg";

	private String fileEPath = path.getAbsolutePath() + File.separator + "1E.jpg";

	private String eFilePath = path.getAbsolutePath() + File.separator + "1E.jpg";

	private String fileDPath = path.getAbsolutePath() + File.separator + "1D.jpg";

	private String groupId = "group_test";

	private List<String> addUsers = new ArrayList<>();

	private List<String> delUsers = new ArrayList<>();

	private long startTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initData();
		initView();
		initEvent();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
					break;

			}
		}
	};

	private void initEvent() {
		// TODO Auto-generated method stub
		btnInit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (WxStringUtil.hasEmptyString(etUrl.getText().toString(), etToken.getText().toString()
						, etAppKey.getText().toString(), etSec.getText().toString())) {
					Toast.makeText(MainActivity.this, "参数填写有误", Toast.LENGTH_SHORT).show();
					return;
				}

				agent.setAddress(etUrl.getText().toString());

				agent.initAgent(etToken.getText().toString(), etAppKey.getText().toString(), etSec.getText().toString()
						, new AgentCallbackListener() {
					
					@Override
					public void onError(String arg0, String arg1) {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.what = 1;
						msg.obj = "CODE: " + arg0 + " MSG: " + arg1;
						handler.sendMessage(msg);
					}
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						Message msg = new Message();
						msg.what = 1;
						msg.obj = "SUCCESS";
						handler.sendMessage(msg);
					}
				});
			}
		});
		
		btnET.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (WxStringUtil.hasEmptyString(etToken.getText().toString(), etId.getText().toString(), etMsg.getText().toString())) {
					Toast.makeText(MainActivity.this, "参数填写有误", Toast.LENGTH_SHORT).show();
					return;
				}

				if (rbtn.isChecked()) {
					agent.groupTextEncrypt(etToken.getText().toString(), etId.getText().toString(), etMsg.getText().toString()
							, new TextCryptCallbackListener() {

						@Override
						public void onError(String arg0, String arg1) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.what = 1;
							msg.obj = "CODE: " + arg0 + " MSG: " + arg1;
							handler.sendMessage(msg);
						}

						@Override
						public void onFinish(String arg0) {
							// TODO Auto-generated method stub
							encryptedText = arg0;
							Message msg = new Message();
							msg.what = 1;
							msg.obj = "FINISH: " + arg0;
							handler.sendMessage(msg);
						}
					});
				} else {
					agent.p2pTextEncrypt(etToken.getText().toString(), etId.getText().toString(), etMsg.getText().toString()
							, new TextCryptCallbackListener() {

						@Override
						public void onError(String arg0, String arg1) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.what = 1;
							msg.obj = "CODE: " + arg0 + " MSG: " + arg1;
							handler.sendMessage(msg);
						}

						@Override
						public void onFinish(String arg0) {
							// TODO Auto-generated method stub
							encryptedText = arg0;
							Message msg = new Message();
							msg.what = 1;
							msg.obj = "FINISH: " + arg0;
							handler.sendMessage(msg);
						}
					});
				}
			}
		});
		
		btnDT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (WxStringUtil.hasEmptyString(etToken.getText().toString(), etId.getText().toString())) {
					Toast.makeText(MainActivity.this, "参数填写有误", Toast.LENGTH_SHORT).show();
					return;
				}
				if (WxStringUtil.hasEmptyString(encryptedText)) {
					Toast.makeText(MainActivity.this, "请先加密", Toast.LENGTH_SHORT).show();
					return;
				}

				if (rbtn.isChecked()) {
					agent.groupTextDecrypt(etToken.getText().toString(), etId.getText().toString(), encryptedText, new TextCryptCallbackListener() {

						@Override
						public void onError(String arg0, String arg1) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.what = 1;
							msg.obj = "CODE: " + arg0 + " MSG: " + arg1;
							handler.sendMessage(msg);
						}

						@Override
						public void onFinish(String arg0) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.what = 1;
							msg.obj = "FINISH: " + arg0;
							handler.sendMessage(msg);
						}
					});
				} else {
					agent.p2pTextDecrypt(etToken.getText().toString(), etId.getText().toString(), encryptedText, new TextCryptCallbackListener() {

						@Override
						public void onError(String arg0, String arg1) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.what = 1;
							msg.obj = "CODE: " + arg0 + " MSG: " + arg1;
							handler.sendMessage(msg);
						}

						@Override
						public void onFinish(String arg0) {
							// TODO Auto-generated method stub
							Message msg = new Message();
							msg.what = 1;
							msg.obj = "FINISH: " + arg0;
							handler.sendMessage(msg);
						}
					});
				}
			}
		});
		
		btnEF.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (WxStringUtil.hasEmptyString(etToken.getText().toString(), etId.getText().toString()
						, etFile.getText().toString(), etFileE.getText().toString())) {
					Toast.makeText(MainActivity.this, "参数填写有误", Toast.LENGTH_SHORT).show();
					return;
				}

				try {
					File file = new File(etFile.getText().toString());
					final FileInputStream in = new FileInputStream(file);
			
					File fileE = new File(etFileE.getText().toString());
					if (!fileE.exists()) {
						fileE.createNewFile();
					}
					final FileOutputStream outE = new FileOutputStream(fileE);
					
					final OutputStream out = new OutputStream() {

						@Override
						public void write(byte[] buffer) throws IOException {
							outE.write(buffer);
						}

						@Override
						public void write(int oneByte) throws IOException {

						}
					};

					if (rbtn.isChecked()) {
						agent.groupFileEncrypt(etToken.getText().toString(), etId.getText().toString()
								, in, out, new FileCryptCallbackListener() {

							@Override
							public void onError(String arg0, String arg1) {
								// TODO Auto-generated method stub
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "CODE: " + arg0 + " MSG: " + arg1;
								handler.sendMessage(msg);
							}

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								startTime = new Date().getTime();
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "ONSTART";
								handler.sendMessage(msg);
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								try {
									in.close();
									out.close();
									outE.flush();
									outE.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "ONFINISH TIME SPEND : " + ((new Date().getTime() - startTime) / 1000 + "S");
								handler.sendMessage(msg);
							}
						});
					} else {
						agent.p2pFileEncrypt(etToken.getText().toString(), etId.getText().toString()
								, in, out, new FileCryptCallbackListener() {

							@Override
							public void onError(String arg0, String arg1) {
								// TODO Auto-generated method stub
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "CODE: " + arg0 + " MSG: " + arg1;
								handler.sendMessage(msg);
							}

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								startTime = new Date().getTime();
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "ONSTART";
								handler.sendMessage(msg);
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								try {
									in.close();
									out.close();
									outE.flush();
									outE.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "ONFINISH TIME SPEND : " + ((new Date().getTime() - startTime) / 1000 + "S");
								handler.sendMessage(msg);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		btnDF.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (WxStringUtil.hasEmptyString(etToken.getText().toString(), etId.getText().toString()
						, etEFile.getText().toString(), etFileD.getText().toString())) {
					Toast.makeText(MainActivity.this, "参数填写有误", Toast.LENGTH_SHORT).show();
					return;
				}

				try {
					File eFile = new File(etEFile.getText().toString());
					final FileInputStream in = new FileInputStream(eFile);
			
					File fileD = new File(etFileD.getText().toString());
					if (!fileD.exists()) {
						fileD.createNewFile();
					}
					final FileOutputStream outE = new FileOutputStream(fileD);
					
					final OutputStream out = new OutputStream() {
						@Override
						public void write(byte[] buffer) throws IOException {
							outE.write(buffer);
						}

						@Override
						public void write(int oneByte) throws IOException {

						}
					};

					if (rbtn.isChecked()) {
						agent.groupFileDecrypt(etToken.getText().toString(), etId.getText().toString()
								, in, out, new FileCryptCallbackListener() {

							@Override
							public void onError(String arg0, String arg1) {
								// TODO Auto-generated method stub
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "CODE: " + arg0 + " MSG: " + arg1;
								handler.sendMessage(msg);
							}

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								startTime = new Date().getTime();
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "ONSTART";
								handler.sendMessage(msg);
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								try {
									in.close();
									out.close();
									outE.flush();
									outE.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "ONFINISH TIME SPEND : " + ((new Date().getTime() - startTime) / 1000 + "S");
								handler.sendMessage(msg);
							}
						});
					} else {
						agent.p2pFileDecrypt(etToken.getText().toString(), etId.getText().toString()
								, in, out, new FileCryptCallbackListener() {

							@Override
							public void onError(String arg0, String arg1) {
								// TODO Auto-generated method stub
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "CODE: " + arg0 + " MSG: " + arg1;
								handler.sendMessage(msg);
							}

							@Override
							public void onStart() {
								// TODO Auto-generated method stub
								startTime = new Date().getTime();
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "ONSTART";
								handler.sendMessage(msg);
							}

							@Override
							public void onFinish() {
								// TODO Auto-generated method stub
								try {
									in.close();
									out.close();
									outE.flush();
									outE.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Message msg = new Message();
								msg.what = 1;
								msg.obj = "ONFINISH TIME SPEND : " + ((new Date().getTime() - startTime) / 1000 + "S");
								handler.sendMessage(msg);
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnMG.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (WxStringUtil.hasEmptyString(etGroupId.getText().toString())) {
					Toast.makeText(MainActivity.this, "参数填写有误", Toast.LENGTH_SHORT).show();
					return;
				}

				String[] adders = etAdders.getText().toString().split(",");
				for (int i = 0; i < adders.length; i++) {
					addUsers.add(adders[i]);
				}
				String[] delers = etDelers.getText().toString().split(",");
				for (int i = 0; i < delers.length; i++) {
					delUsers.add(adders[i]);
				}

//				agent.maintenanceGroupInfo(etGroupId.getText().toString(), addUsers, delUsers, new AgentCallbackListener() {
//
//					@Override
//					public void onSuccess() {
//						Message msg = new Message();
//						msg.what = 1;
//						msg.obj = "SUCCESS";
//						handler.sendMessage(msg);
//					}
//
//					@Override
//					public void onError(String code, String message) {
//						Message msg = new Message();
//						msg.what = 1;
//						msg.obj = "CODE: " + code + " MSG: " + message;
//						handler.sendMessage(msg);
//					}
//				});
			}
		});
	}

	private void initData() {
		// TODO Auto-generated method stub
		agent = MobilearkCryptAgent.getInstance(this);
	}

	private void initView() {
		// TODO Auto-generated method stub
		btnInit = (Button) findViewById(R.id.btn_init);
		btnET = (Button) findViewById(R.id.btn_e_t);
		btnDT = (Button) findViewById(R.id.btn_d_t);
		btnEF = (Button) findViewById(R.id.btn_e_f);
		btnDF = (Button) findViewById(R.id.btn_d_f);
		btnMG = (Button) findViewById(R.id.btn_mg);
		btnMG.setVisibility(View.GONE);

		rbtn = (RadioButton) findViewById(R.id.rb_isgroup);
		rbtn.setChecked(false);

		etUrl = (EditText) findViewById(R.id.et_address);
		etUrl.setText(address);
		etAppKey = (EditText) findViewById(R.id.et_appkey);
		etAppKey.setText(appKey);
		etToken = (EditText) findViewById(R.id.et_token);
		etToken.setText(token);
		etSec = (EditText) findViewById(R.id.et_sec);
		etSec.setText(sec);
		etId = (EditText) findViewById(R.id.et_id);
		etId.setText(id);
		etMsg = (EditText) findViewById(R.id.et_msg);
		etMsg.setText(msg);
		etFile = (EditText) findViewById(R.id.et_file);
		etFile.setText(filePath);
		etFileE = (EditText) findViewById(R.id.et_file_e);
		etFileE.setText(fileEPath);
		etEFile = (EditText) findViewById(R.id.et_e_file);
		etEFile.setText(eFilePath);
		etFileD = (EditText) findViewById(R.id.et_file_d);
		etFileD.setText(fileDPath);
		etGroupId = (EditText) findViewById(R.id.et_groupid);
		etGroupId.setText(groupId);
		etAdders = (EditText) findViewById(R.id.et_group_a);
		etDelers = (EditText) findViewById(R.id.et_group_d);
	}
}
