package edu.umich.robustnet.appcontroller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.SensorManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ControlActivity extends Activity {
	private static final String TAG = "CONTROL_ALF";
	Context self;
	String configfoldername="/sdcard/AppControllerConfig";
	String configFilename="config_AppController";
	String configFileFullname="config_AppController";
	String nettypeFilename="NetworkType";
	String deviceinfofilename="DeviceInfo";
	static String outputFileRoot="/sdcard/AppControllerData";
	String outputfoldername="/sdcard/AppControllerData";
	String INTERNAL_DATA_PATH = "/data/data/edu.umich.robustnet.appcontroller";
	String assetsFilename;
	long outputfoldernameChangeTime;
	String ctrlfile;
	String tcpdumpfile="tcpdump";
    Button startcontrol;
    TextView appversiontext;
    
    
//    String[] targetapps={"Facebook","GoogleSearch","GoogleSearchChrome"};
    String[] targetapps={"Facebook","GoogleSearch","GoogleSearchChrome","YouTube"};
    Spinner chooseTargetApp;
    ArrayAdapter<String> chooseTargetAppAdapter;
    
    String[] parameterdecision={"No","Yes"};
    ArrayAdapter<String> parameterdecisionAdapter;
    
    EditText configfileinput;
    
    Button browsefile;
    final int ACTIVITY_CHOOSE_FILE = 1;
    final int SUMSUNG_FILE_REQUEST_CODE = 6384;
    int tcpdumpTrunk=0;
    //default: 0
    int DEFAULT_TRUNK=0; //no truncate
    int VIDEO_TRUNK=500;
    
    Button loadconfigurationfile;    
    Button saveconfigurationfile;
    Button appcontrollerparameters;
    Button targetappparameters;
    
    Dialog settingdialog;
    
    Boolean withctrlfileinstalled;
    Boolean tcpdumpstarted;
    Boolean incontrol;
    Boolean incontrolthenpause;
    
	private Timer timer;
	private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private WindowManager mWindowManager;
    private WakeLock mWakeLock;    
    
    //AppController Parameter
	private int controlIdleTime=20*60;//seconds	
	private int INCONTROLWAITTIME=10000; //ms
	private int oneFolderMode=0; //1: one folder
	private String appName="Facebook";   
	private int collectPcap=1;
	private int collectMainLogcat=0;	
    private int collectEventLogcat=0;
    private int collectRadioLogcat=0;
    private int clickSleepTime=1500;
    private int webViewFacebook=0;
    private long webViewFacebookPull=2*60;
    
    EditText et_controlIdleTime;
    Spinner sp_collectPcap;
    Spinner sp_collectMainLogcat;
    Spinner sp_collectEventLogcat;
    Spinner sp_collectRadioLogcat;
    Spinner sp_oneFolderMode;
    
    //Facebook Parameter
    int Facebook_actionIdleTime=10000;
 	int Facebook_updateStatusWaitTime=5000;
 	int Facebook_uploadPhotoWaitTime=20000;
 	int Facebook_checkInWaitTime=5000;
 	int Facebook_AppPullToUpdateWaitTime=120;
 	int Facebook_numberOfPhotoToUpload=2;
 	int Facebook_randomChoosePhoto=1;
 	String Facebook_testClickSleepTime="1500";
 	String Facebook_actionList="5,2,3,6";
 	int Facebook_numberOfRepeat=1;
 	String Facebook_interActionTime="10000";
 	int Facebook_onlyOutputFirstPullToUpdate=1;
 	
 	EditText et_Facebook_actionIdleTime;
 	EditText et_Facebook_updateStatusWaitTime;
 	EditText et_Facebook_uploadPhotoWaitTime;
 	EditText et_Facebook_checkInWaitTime;
 	EditText et_Facebook_AppPullToUpdateWaitTime;
 	EditText et_Facebook_numberOfPhotoToUpload;
 	Spinner sp_Facebook_randomChoosePhoto;
 	EditText et_Facebook_testClickSleepTime;
 	EditText et_Facebook_actionList;
 	EditText et_Facebook_numberOfRepeat;
 	EditText et_Facebook_interActionTime;
 	Spinner sp_Facebook_onlyOutputFirstPullToUpdate;
 	
 	//GoogleSearch Parameter
 	int GoogleSearch_interCharacterTime=500;
 	String GoogleSearch_keywordListFilename="KeywordList";
 	int GoogleSearch_interKeywordTime=5000;
 	int GoogleSearch_randomChooseKeyword=0;
 	int GoogleSearch_numOfkeywordToEnter=-1;
 	
 	EditText et_GoogleSearch_interCharacterTime;
 	EditText et_GoogleSearch_keywordListFilename;
 	EditText et_GoogleSearch_interKeywordTime;
 	Spinner sp_GoogleSearch_randomChooseKeyword;
 	EditText et_GoogleSearch_numOfkeywordToEnter;
 	
	//GoogleSearchChrome Parameter
 	int GoogleSearchChrome_interCharacterTime=500;
 	String GoogleSearchChrome_keywordListFilename="KeywordList";
 	int GoogleSearchChrome_interKeywordTime=5000;
 	int GoogleSearchChrome_randomChooseKeyword=0;
 	int GoogleSearchChrome_numOfkeywordToEnter=-1;
 	int GoogleSearchChrome_UrlVisitMode=0;
 	
 	
 	
 	EditText et_GoogleSearchChrome_interCharacterTime;
 	EditText et_GoogleSearchChrome_keywordListFilename;
 	EditText et_GoogleSearchChrome_interKeywordTime;
 	Spinner sp_GoogleSearchChrome_randomChooseKeyword;
 	EditText et_GoogleSearchChrome_numOfkeywordToEnter;
 	Spinner sp_GoogleSearchChrome_UrlVisitMode;
 	
 	//YouTube Parameter
	int YouTube_interVideoTime=5000;
    private int YouTube_randomChooseVideo=0;
    int YouTube_numOfvideoToWatch=-1;
    int YouTube_caseSensitive=1;    
    int YouTube_pullingIdleTime=1; 
    String YouTube_videoListFilename="videoList";	  
    int YouTube_whetherSkipAds=1; 
    
    EditText et_YouTube_interVideoTime;
 	EditText et_YouTube_numOfvideoToWatch;
 	EditText et_YouTube_pullingIdleTime;
 	EditText et_YouTube_videoListFilename;
 	Spinner sp_YouTube_randomChooseVideo;
 	Spinner sp_YouTube_caseSensitive;
 	Spinner sp_YouTube_whetherSkipAds;
 	
	boolean screenon;
	boolean intesting;
//    private ARODataCollector mApp;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		self=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control);
		withctrlfileinstalled=false;
		tcpdumpstarted=false;
		settingdialog=null;
		outputfoldernameChangeTime=-1;
		configFileFullname=configfoldername+"/"+configFilename;
		
		KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("MyKeyguardLock");
		kl.disableKeyguard();
		
//		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, getClass().getName());
        

		incontrol=false;
		incontrolthenpause=false;
		intesting=false;
		
		appversiontext=(TextView)findViewById(R.id.appversiontext);
		try {
			appversiontext.setText("AppController Version: "+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		parameterdecisionAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,parameterdecision);  
		parameterdecisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		
		
		chooseTargetApp=(Spinner)findViewById(R.id.choosetargetapp);
		chooseTargetAppAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,targetapps);  
		chooseTargetAppAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
		chooseTargetApp.setAdapter(chooseTargetAppAdapter);  
		chooseTargetApp.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				appName=targetapps[arg2];
				Log.d(TAG,""+appName);
				targetappparameters.setText(appName+" Controller Parameters");
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				appName=targetapps[0];
				targetappparameters.setText(appName+" Controller Parameters");
			}
			
		});  
		
		configfileinput=(EditText)findViewById(R.id.configfileinput);
		configfileinput.setText(configFileFullname);
		
		
		browsefile=(Button)findViewById(R.id.browsefile);
		browsefile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent samsungIntent = new Intent();
				samsungIntent.setAction("com.sec.android.app.myfiles.PICK_DATA");
				samsungIntent.putExtra("CONTENT_TYPE", "*/*");
				try {
				startActivityForResult(samsungIntent, SUMSUNG_FILE_REQUEST_CODE);
				
				} catch (ActivityNotFoundException e ) { 
		            e.printStackTrace( );
		            
					Intent chooseFile;
					Intent intent;
					chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
					chooseFile.setType("file/*");
					intent = Intent.createChooser(chooseFile, "Choose a file");				
				    startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
		            
		        }
				
			}
		});
		
		loadconfigurationfile=(Button)findViewById(R.id.loadconfigurationfile);
		loadconfigurationfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				configFileFullname=configfileinput.getText().toString();
				readConfig();
				updateUI();
			}
			
		});
		
		saveconfigurationfile=(Button)findViewById(R.id.saveconfigurationfile);
		saveconfigurationfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				configFileFullname=configfileinput.getText().toString();
				writeConfig(configFileFullname);
			}
			
		});
		
		appcontrollerparameters=(Button)findViewById(R.id.appcontrollerparameters);
		appcontrollerparameters.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				settingdialog = new Dialog(self);
				settingdialog.setTitle("AppController Parameters:");
				LinearLayout layout = new LinearLayout(self);
			//	LayoutParams tvparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
		    //            LayoutParams.WRAP_CONTENT);
		        layout.setOrientation(LinearLayout.VERTICAL);
		 //       layout.addView(tv);
		        
		        /*
		        AppController_controlIdleTime=100
		        AppController_appName=GoogleSearchChrome
		        (hidden) AppController_clickSleepTime=1500;
		        AppController_collectPcap=1;
		        AppController_collectMainLogcat=1
		        AppController_collectEventLogcat=0
		        AppController_collectRadioLogcat=0
		        AppController_oneFolderMode=1		        
		         (hidden)webViewFacebook=0;
                 (hidden)webViewFacebookPull=2*60;
		        */
		        
		        LinearLayout parameterlayout_controlIdleTime = new LinearLayout(self);
		        parameterlayout_controlIdleTime.setOrientation(LinearLayout.HORIZONTAL);
		        TextView tv_controlIdleTime=new TextView(self);		        
		        et_controlIdleTime=new EditText(self);    
		        TextView tvs_controlIdleTime=new TextView(self);
		        tv_controlIdleTime.setText("controlIdleTime"+"=");
		        et_controlIdleTime.setText(""+controlIdleTime);
		        et_controlIdleTime.setInputType(InputType.TYPE_CLASS_NUMBER);
		        tvs_controlIdleTime.setText("sec");
		        parameterlayout_controlIdleTime.addView(tv_controlIdleTime);
		        parameterlayout_controlIdleTime.addView(et_controlIdleTime);
		        parameterlayout_controlIdleTime.addView(tvs_controlIdleTime);
		        layout.addView(parameterlayout_controlIdleTime);
		        
		        LinearLayout parameterlayout_collectPcap = new LinearLayout(self);
		        parameterlayout_collectPcap.setOrientation(LinearLayout.HORIZONTAL);
		        TextView tv_collectPcap=new TextView(self);
		        sp_collectPcap=new Spinner(self);	
		        tv_collectPcap.setText("collectPcap"+"=");
		        sp_collectPcap.setAdapter(parameterdecisionAdapter);
		        sp_collectPcap.setSelection(collectPcap);
		        parameterlayout_collectPcap.addView(tv_collectPcap);
		        parameterlayout_collectPcap.addView(sp_collectPcap);
		        layout.addView(parameterlayout_collectPcap);
		        
		        LinearLayout parameterlayout_collectMainLogcat = new LinearLayout(self);
		        parameterlayout_collectMainLogcat.setOrientation(LinearLayout.HORIZONTAL);
		        TextView tv_collectMainLogcat=new TextView(self);
		        sp_collectMainLogcat=new Spinner(self);	
		        tv_collectMainLogcat.setText("collectMainLogcat"+"=");
		        sp_collectMainLogcat.setAdapter(parameterdecisionAdapter);
		        sp_collectMainLogcat.setSelection(collectMainLogcat);
		        parameterlayout_collectMainLogcat.addView(tv_collectMainLogcat);
		        parameterlayout_collectMainLogcat.addView(sp_collectMainLogcat);
		        layout.addView(parameterlayout_collectMainLogcat);
		        
		        LinearLayout parameterlayout_collectEventLogcat = new LinearLayout(self);
		        parameterlayout_collectEventLogcat.setOrientation(LinearLayout.HORIZONTAL);
		        TextView tv_collectEventLogcat=new TextView(self);
		        sp_collectEventLogcat=new Spinner(self);	
		        tv_collectEventLogcat.setText("collectEventLogcat"+"=");
		        sp_collectEventLogcat.setAdapter(parameterdecisionAdapter);
		        sp_collectEventLogcat.setSelection(collectEventLogcat);
		        parameterlayout_collectEventLogcat.addView(tv_collectEventLogcat);
		        parameterlayout_collectEventLogcat.addView(sp_collectEventLogcat);
		        layout.addView(parameterlayout_collectEventLogcat);
		        
		        LinearLayout parameterlayout_collectRadioLogcat = new LinearLayout(self);
		        parameterlayout_collectRadioLogcat.setOrientation(LinearLayout.HORIZONTAL);
		        TextView tv_collectRadioLogcat=new TextView(self);
		        sp_collectRadioLogcat=new Spinner(self);	
		        tv_collectRadioLogcat.setText("collectRadioLogcat"+"=");
		        sp_collectRadioLogcat.setAdapter(parameterdecisionAdapter);
		        sp_collectRadioLogcat.setSelection(collectRadioLogcat);
		        parameterlayout_collectRadioLogcat.addView(tv_collectRadioLogcat);
		        parameterlayout_collectRadioLogcat.addView(sp_collectRadioLogcat);
		        layout.addView(parameterlayout_collectRadioLogcat);
		        
		        LinearLayout parameterlayout_oneFolderMode = new LinearLayout(self);
		        parameterlayout_oneFolderMode.setOrientation(LinearLayout.HORIZONTAL);
		        TextView tv_oneFolderMode=new TextView(self);
		        sp_oneFolderMode=new Spinner(self);	
		        tv_oneFolderMode.setText("oneFolderMode"+"=");
		        sp_oneFolderMode.setAdapter(parameterdecisionAdapter);
		        sp_oneFolderMode.setSelection(oneFolderMode);
		        parameterlayout_oneFolderMode.addView(tv_oneFolderMode);
		        parameterlayout_oneFolderMode.addView(sp_oneFolderMode);
		        layout.addView(parameterlayout_oneFolderMode);
		        
		        LinearLayout decisionlayout = new LinearLayout(self);
		        decisionlayout.setOrientation(LinearLayout.HORIZONTAL);
		        Button savebtn=new Button(self);
		        savebtn.setText("Save");
		        savebtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						controlIdleTime=Integer.parseInt(et_controlIdleTime.getText().toString());
						collectPcap=sp_collectPcap.getSelectedItemPosition();
						collectMainLogcat=sp_collectMainLogcat.getSelectedItemPosition();
						collectEventLogcat=sp_collectEventLogcat.getSelectedItemPosition();
						collectRadioLogcat=sp_collectRadioLogcat.getSelectedItemPosition();
						oneFolderMode=sp_oneFolderMode.getSelectedItemPosition();
						settingdialog.dismiss();
						settingdialog=null;
					}		        	
		        });
		        Button cancelbtn=new Button(self);
		        cancelbtn.setText("Cancel");
		        cancelbtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						settingdialog.dismiss();
						settingdialog=null;
					}
		        	
		        });
		        decisionlayout.addView(savebtn);
		        decisionlayout.addView(cancelbtn);
		        decisionlayout.setGravity(Gravity.CENTER_HORIZONTAL);
		        
		        layout.addView(decisionlayout); 
		        settingdialog.setContentView(layout);
		        settingdialog.show();

			}
			
		});
		
		targetappparameters=(Button)findViewById(R.id.targetappparameters);
		targetappparameters.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				settingdialog = new Dialog(self);
				settingdialog.setTitle(appName+" Control Parameters:");
				LinearLayout layout = new LinearLayout(self);
			//	LayoutParams tvparams = new LayoutParams(LayoutParams.WRAP_CONTENT,
		    //            LayoutParams.WRAP_CONTENT);
		        layout.setOrientation(LinearLayout.VERTICAL);
		 //       layout.addView(tv);
		        
		        /*
		        Facebook_actionIdleTime=10000
				Facebook_updateStatusWaitTime=5000
				Facebook_uploadPhotoWaitTime=20000
				Facebook_checkInWaitTime=5000
				Facebook_numberOfPhotoToUpload=2
				Facebook_AppPullToUpdateWaitTime=120
				Facebook_randomChoosePhoto=0
				(hidden) Facebook_testClickSleepTime=1000
				Facebook_actionList=1
				(hidden) Facebook_numberOfRepeat=1
				(hidden) Facebook_interActionTime=5000
				Facebook_onlyOutputFirstPullToUpdate=0
		        */
		        if (appName.equals("Facebook")){
			        LinearLayout parameterlayout_Facebook_actionIdleTime = new LinearLayout(self);
			        parameterlayout_Facebook_actionIdleTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_Facebook_actionIdleTime=new TextView(self);
			        et_Facebook_actionIdleTime=new EditText(self);    
			        TextView tvs_Facebook_actionIdleTime=new TextView(self);
			        tv_Facebook_actionIdleTime.setText("actionIdleTime"+"=");
			        et_Facebook_actionIdleTime.setText(""+Facebook_actionIdleTime);
			        et_Facebook_actionIdleTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_Facebook_actionIdleTime.setText("msec");
			        parameterlayout_Facebook_actionIdleTime.addView(tv_Facebook_actionIdleTime);
			        parameterlayout_Facebook_actionIdleTime.addView(et_Facebook_actionIdleTime);
			        parameterlayout_Facebook_actionIdleTime.addView(tvs_Facebook_actionIdleTime);
			        layout.addView(parameterlayout_Facebook_actionIdleTime);
			        
			        LinearLayout parameterlayout_Facebook_updateStatusWaitTime = new LinearLayout(self);
			        parameterlayout_Facebook_updateStatusWaitTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_Facebook_updateStatusWaitTime=new TextView(self);
			        et_Facebook_updateStatusWaitTime=new EditText(self);    
			        TextView tvs_Facebook_updateStatusWaitTime=new TextView(self);
			        tv_Facebook_updateStatusWaitTime.setText("updateStatusWaitTime"+"=");
			        et_Facebook_updateStatusWaitTime.setText(""+Facebook_updateStatusWaitTime);
			        et_Facebook_updateStatusWaitTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_Facebook_updateStatusWaitTime.setText("msec");
			        parameterlayout_Facebook_updateStatusWaitTime.addView(tv_Facebook_updateStatusWaitTime);
			        parameterlayout_Facebook_updateStatusWaitTime.addView(et_Facebook_updateStatusWaitTime);
			        parameterlayout_Facebook_updateStatusWaitTime.addView(tvs_Facebook_updateStatusWaitTime);
			        layout.addView(parameterlayout_Facebook_updateStatusWaitTime);
			        
			        LinearLayout parameterlayout_Facebook_uploadPhotoWaitTime = new LinearLayout(self);
			        parameterlayout_Facebook_uploadPhotoWaitTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_Facebook_uploadPhotoWaitTime=new TextView(self);
			        et_Facebook_uploadPhotoWaitTime=new EditText(self);    
			        TextView tvs_Facebook_uploadPhotoWaitTime=new TextView(self);
			        tv_Facebook_uploadPhotoWaitTime.setText("uploadPhotoWaitTime"+"=");
			        et_Facebook_uploadPhotoWaitTime.setText(""+Facebook_uploadPhotoWaitTime);
			        et_Facebook_uploadPhotoWaitTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_Facebook_uploadPhotoWaitTime.setText("msec");
			        parameterlayout_Facebook_uploadPhotoWaitTime.addView(tv_Facebook_uploadPhotoWaitTime);
			        parameterlayout_Facebook_uploadPhotoWaitTime.addView(et_Facebook_uploadPhotoWaitTime);
			        parameterlayout_Facebook_uploadPhotoWaitTime.addView(tvs_Facebook_uploadPhotoWaitTime);
			        layout.addView(parameterlayout_Facebook_uploadPhotoWaitTime);
			        
			        
			        LinearLayout parameterlayout_Facebook_checkInWaitTime = new LinearLayout(self);
			        parameterlayout_Facebook_checkInWaitTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_Facebook_checkInWaitTime=new TextView(self);
			        et_Facebook_checkInWaitTime=new EditText(self);    
			        TextView tvs_Facebook_checkInWaitTime=new TextView(self);
			        tv_Facebook_checkInWaitTime.setText("checkInWaitTime"+"=");
			        et_Facebook_checkInWaitTime.setText(""+Facebook_checkInWaitTime);
			        et_Facebook_checkInWaitTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_Facebook_checkInWaitTime.setText("msec");
			        parameterlayout_Facebook_checkInWaitTime.addView(tv_Facebook_checkInWaitTime);
			        parameterlayout_Facebook_checkInWaitTime.addView(et_Facebook_checkInWaitTime);
			        parameterlayout_Facebook_checkInWaitTime.addView(tvs_Facebook_checkInWaitTime);
			        layout.addView(parameterlayout_Facebook_checkInWaitTime);
			        
			        LinearLayout parameterlayout_Facebook_numberOfPhotoToUpload = new LinearLayout(self);
			        parameterlayout_Facebook_numberOfPhotoToUpload.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_Facebook_numberOfPhotoToUpload=new TextView(self);
			        et_Facebook_numberOfPhotoToUpload=new EditText(self);    
			        tv_Facebook_numberOfPhotoToUpload.setText("numberOfPhotoToUpload"+"=");
			        et_Facebook_numberOfPhotoToUpload.setText(""+Facebook_numberOfPhotoToUpload);
			        et_Facebook_numberOfPhotoToUpload.setInputType(InputType.TYPE_CLASS_NUMBER);
			        parameterlayout_Facebook_numberOfPhotoToUpload.addView(tv_Facebook_numberOfPhotoToUpload);
			        parameterlayout_Facebook_numberOfPhotoToUpload.addView(et_Facebook_numberOfPhotoToUpload);
			        layout.addView(parameterlayout_Facebook_numberOfPhotoToUpload);
			        
			        LinearLayout parameterlayout_Facebook_AppPullToUpdateWaitTime = new LinearLayout(self);
			        parameterlayout_Facebook_AppPullToUpdateWaitTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_Facebook_AppPullToUpdateWaitTime=new TextView(self);
			        et_Facebook_AppPullToUpdateWaitTime=new EditText(self);    
			        TextView tvs_Facebook_AppPullToUpdateWaitTime=new TextView(self);
			        tv_Facebook_AppPullToUpdateWaitTime.setText("AppPullToUpdateWaitTime"+"=");
			        et_Facebook_AppPullToUpdateWaitTime.setText(""+Facebook_AppPullToUpdateWaitTime);
			        et_Facebook_AppPullToUpdateWaitTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_Facebook_AppPullToUpdateWaitTime.setText("sec");
			        parameterlayout_Facebook_AppPullToUpdateWaitTime.addView(tv_Facebook_AppPullToUpdateWaitTime);
			        parameterlayout_Facebook_AppPullToUpdateWaitTime.addView(et_Facebook_AppPullToUpdateWaitTime);
			        parameterlayout_Facebook_AppPullToUpdateWaitTime.addView(tvs_Facebook_AppPullToUpdateWaitTime);
			        layout.addView(parameterlayout_Facebook_AppPullToUpdateWaitTime);
			        
			        LinearLayout parameterlayout_Facebook_randomChoosePhoto = new LinearLayout(self);
			        parameterlayout_Facebook_randomChoosePhoto.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_Facebook_randomChoosePhoto=new TextView(self);
			        sp_Facebook_randomChoosePhoto=new Spinner(self);	
			        tv_Facebook_randomChoosePhoto.setText("randomChoosePhoto"+"=");
			        sp_Facebook_randomChoosePhoto.setAdapter(parameterdecisionAdapter);
			        sp_Facebook_randomChoosePhoto.setSelection(Facebook_randomChoosePhoto);
			        parameterlayout_Facebook_randomChoosePhoto.addView(tv_Facebook_randomChoosePhoto);
			        parameterlayout_Facebook_randomChoosePhoto.addView(sp_Facebook_randomChoosePhoto);
			        layout.addView(parameterlayout_Facebook_randomChoosePhoto);
			        
			        LinearLayout parameterlayout_Facebook_actionList = new LinearLayout(self);
			        parameterlayout_Facebook_actionList.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_Facebook_actionList=new TextView(self);
			        et_Facebook_actionList=new EditText(self);    
			        tv_Facebook_actionList.setText("actionList(separated by comma)"+"=");
			        et_Facebook_actionList.setText(""+Facebook_actionList);
			        parameterlayout_Facebook_actionList.addView(tv_Facebook_actionList);
			        parameterlayout_Facebook_actionList.addView(et_Facebook_actionList);
			        layout.addView(parameterlayout_Facebook_actionList);
			        
			        LinearLayout parameterlayout_Facebook_onlyOutputFirstPullToUpdate = new LinearLayout(self);
			        parameterlayout_Facebook_onlyOutputFirstPullToUpdate.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_Facebook_onlyOutputFirstPullToUpdate=new TextView(self);
			        sp_Facebook_onlyOutputFirstPullToUpdate=new Spinner(self);	
			        tv_Facebook_onlyOutputFirstPullToUpdate.setText("onlyOutputFirstPullToUpdate"+"=");
			        sp_Facebook_onlyOutputFirstPullToUpdate.setAdapter(parameterdecisionAdapter);
			        sp_Facebook_onlyOutputFirstPullToUpdate.setSelection(Facebook_onlyOutputFirstPullToUpdate);
			        parameterlayout_Facebook_onlyOutputFirstPullToUpdate.addView(tv_Facebook_onlyOutputFirstPullToUpdate);
			        parameterlayout_Facebook_onlyOutputFirstPullToUpdate.addView(sp_Facebook_onlyOutputFirstPullToUpdate);
			        layout.addView(parameterlayout_Facebook_onlyOutputFirstPullToUpdate);
		        }
		        
		        /*
		         GoogleSearch_interCharacterTime=500
				 GoogleSearch_keywordListFilename=KeywordList
				 GoogleSearch_interKeywordTime=5000
			  	 GoogleSearch_randomChooseKeyword=0
				 GoogleSearch_numOfkeywordToEnter=-1
		         */
		        if (appName.equals("GoogleSearch")){
		        	LinearLayout parameterlayout_GoogleSearch_interCharacterTime = new LinearLayout(self);
			        parameterlayout_GoogleSearch_interCharacterTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearch_interCharacterTime=new TextView(self);
			        et_GoogleSearch_interCharacterTime=new EditText(self);    
			        TextView tvs_GoogleSearch_interCharacterTime=new TextView(self);
			        tv_GoogleSearch_interCharacterTime.setText("interCharacterTime"+"=");
			        et_GoogleSearch_interCharacterTime.setText(""+GoogleSearch_interCharacterTime);
			        et_GoogleSearch_interCharacterTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_GoogleSearch_interCharacterTime.setText("msec");
			        parameterlayout_GoogleSearch_interCharacterTime.addView(tv_GoogleSearch_interCharacterTime);
			        parameterlayout_GoogleSearch_interCharacterTime.addView(et_GoogleSearch_interCharacterTime);
			        parameterlayout_GoogleSearch_interCharacterTime.addView(tvs_GoogleSearch_interCharacterTime);
			        layout.addView(parameterlayout_GoogleSearch_interCharacterTime);
			        
			        
			        LinearLayout parameterlayout_GoogleSearch_keywordListFilename = new LinearLayout(self);
			        parameterlayout_GoogleSearch_keywordListFilename.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearch_keywordListFilename=new TextView(self);
			        et_GoogleSearch_keywordListFilename=new EditText(self);    
			        tv_GoogleSearch_keywordListFilename.setText("keywordListFilename"+"=");
			        et_GoogleSearch_keywordListFilename.setText(""+GoogleSearch_keywordListFilename);
			        parameterlayout_GoogleSearch_keywordListFilename.addView(tv_GoogleSearch_keywordListFilename);
			        parameterlayout_GoogleSearch_keywordListFilename.addView(et_GoogleSearch_keywordListFilename);
			        layout.addView(parameterlayout_GoogleSearch_keywordListFilename);
			        
			        LinearLayout parameterlayout_GoogleSearch_interKeywordTime = new LinearLayout(self);
			        parameterlayout_GoogleSearch_interKeywordTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearch_interKeywordTime=new TextView(self);
			        et_GoogleSearch_interKeywordTime=new EditText(self);    
			        TextView tvs_GoogleSearch_interKeywordTime=new TextView(self);
			        tv_GoogleSearch_interKeywordTime.setText("interKeywordTime"+"=");
			        et_GoogleSearch_interKeywordTime.setText(""+GoogleSearch_interKeywordTime);
			        et_GoogleSearch_interKeywordTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_GoogleSearch_interKeywordTime.setText("msec");
			        parameterlayout_GoogleSearch_interKeywordTime.addView(tv_GoogleSearch_interKeywordTime);
			        parameterlayout_GoogleSearch_interKeywordTime.addView(et_GoogleSearch_interKeywordTime);
			        parameterlayout_GoogleSearch_interKeywordTime.addView(tvs_GoogleSearch_interKeywordTime);
			        layout.addView(parameterlayout_GoogleSearch_interKeywordTime);
			        
			        LinearLayout parameterlayout_GoogleSearch_randomChooseKeyword = new LinearLayout(self);
			        parameterlayout_GoogleSearch_randomChooseKeyword.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearch_randomChooseKeyword=new TextView(self);
			        sp_GoogleSearch_randomChooseKeyword=new Spinner(self);	
			        tv_GoogleSearch_randomChooseKeyword.setText("randomChooseKeyword"+"=");
			        sp_GoogleSearch_randomChooseKeyword.setAdapter(parameterdecisionAdapter);
			        sp_GoogleSearch_randomChooseKeyword.setSelection(GoogleSearch_randomChooseKeyword);
			        parameterlayout_GoogleSearch_randomChooseKeyword.addView(tv_GoogleSearch_randomChooseKeyword);
			        parameterlayout_GoogleSearch_randomChooseKeyword.addView(sp_GoogleSearch_randomChooseKeyword);
			        layout.addView(parameterlayout_GoogleSearch_randomChooseKeyword);
			        
			        LinearLayout parameterlayout_GoogleSearch_numOfkeywordToEnter = new LinearLayout(self);
			        parameterlayout_GoogleSearch_numOfkeywordToEnter.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearch_numOfkeywordToEnter=new TextView(self);
			        et_GoogleSearch_numOfkeywordToEnter=new EditText(self);    
			        tv_GoogleSearch_numOfkeywordToEnter.setText("numOfkeywordToEnter"+"=");
			        et_GoogleSearch_numOfkeywordToEnter.setText(""+GoogleSearch_numOfkeywordToEnter);
			        et_GoogleSearch_numOfkeywordToEnter.setInputType(InputType.TYPE_CLASS_NUMBER);
			        parameterlayout_GoogleSearch_numOfkeywordToEnter.addView(tv_GoogleSearch_numOfkeywordToEnter);
			        parameterlayout_GoogleSearch_numOfkeywordToEnter.addView(et_GoogleSearch_numOfkeywordToEnter);
			        layout.addView(parameterlayout_GoogleSearch_numOfkeywordToEnter);
			        
		        }
		        
		        /*
		          GoogleSearchChrome_interCharacterTime=500
				  GoogleSearchChrome_keywordListFilename=KeywordList
				  GoogleSearchChrome_interKeywordTime=5000
				  GoogleSearchChrome_randomChooseKeyword=0
				  GoogleSearchChrome_numOfkeywordToEnter=-1
				  GoogleSearchChrome_UrlVisitMode=0
		         */
		        if (appName.equals("GoogleSearchChrome")){
		        	LinearLayout parameterlayout_GoogleSearchChrome_interCharacterTime = new LinearLayout(self);
			        parameterlayout_GoogleSearchChrome_interCharacterTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearchChrome_interCharacterTime=new TextView(self);
			        et_GoogleSearchChrome_interCharacterTime=new EditText(self);    
			        TextView tvs_GoogleSearchChrome_interCharacterTime=new TextView(self);
			        tv_GoogleSearchChrome_interCharacterTime.setText("interCharacterTime"+"=");
			        et_GoogleSearchChrome_interCharacterTime.setText(""+GoogleSearchChrome_interCharacterTime);
			        et_GoogleSearchChrome_interCharacterTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_GoogleSearchChrome_interCharacterTime.setText("msec");
			        parameterlayout_GoogleSearchChrome_interCharacterTime.addView(tv_GoogleSearchChrome_interCharacterTime);
			        parameterlayout_GoogleSearchChrome_interCharacterTime.addView(et_GoogleSearchChrome_interCharacterTime);
			        parameterlayout_GoogleSearchChrome_interCharacterTime.addView(tvs_GoogleSearchChrome_interCharacterTime);
			        layout.addView(parameterlayout_GoogleSearchChrome_interCharacterTime);
			        
			        
			        LinearLayout parameterlayout_GoogleSearchChrome_keywordListFilename = new LinearLayout(self);
			        parameterlayout_GoogleSearchChrome_keywordListFilename.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearchChrome_keywordListFilename=new TextView(self);
			        et_GoogleSearchChrome_keywordListFilename=new EditText(self);    
			        tv_GoogleSearchChrome_keywordListFilename.setText("keywordListFilename"+"=");
			        et_GoogleSearchChrome_keywordListFilename.setText(""+GoogleSearchChrome_keywordListFilename);
			        parameterlayout_GoogleSearchChrome_keywordListFilename.addView(tv_GoogleSearchChrome_keywordListFilename);
			        parameterlayout_GoogleSearchChrome_keywordListFilename.addView(et_GoogleSearchChrome_keywordListFilename);
			        layout.addView(parameterlayout_GoogleSearchChrome_keywordListFilename);
			        
			        LinearLayout parameterlayout_GoogleSearchChrome_interKeywordTime = new LinearLayout(self);
			        parameterlayout_GoogleSearchChrome_interKeywordTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearchChrome_interKeywordTime=new TextView(self);
			        et_GoogleSearchChrome_interKeywordTime=new EditText(self);    
			        TextView tvs_GoogleSearchChrome_interKeywordTime=new TextView(self);
			        tv_GoogleSearchChrome_interKeywordTime.setText("interKeywordTime"+"=");
			        et_GoogleSearchChrome_interKeywordTime.setText(""+GoogleSearchChrome_interKeywordTime);
			        et_GoogleSearchChrome_interKeywordTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_GoogleSearchChrome_interKeywordTime.setText("msec");
			        parameterlayout_GoogleSearchChrome_interKeywordTime.addView(tv_GoogleSearchChrome_interKeywordTime);
			        parameterlayout_GoogleSearchChrome_interKeywordTime.addView(et_GoogleSearchChrome_interKeywordTime);
			        parameterlayout_GoogleSearchChrome_interKeywordTime.addView(tvs_GoogleSearchChrome_interKeywordTime);
			        layout.addView(parameterlayout_GoogleSearchChrome_interKeywordTime);
			        
			        LinearLayout parameterlayout_GoogleSearchChrome_randomChooseKeyword = new LinearLayout(self);
			        parameterlayout_GoogleSearchChrome_randomChooseKeyword.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearchChrome_randomChooseKeyword=new TextView(self);
			        sp_GoogleSearchChrome_randomChooseKeyword=new Spinner(self);	
			        tv_GoogleSearchChrome_randomChooseKeyword.setText("randomChooseKeyword"+"=");
			        sp_GoogleSearchChrome_randomChooseKeyword.setAdapter(parameterdecisionAdapter);
			        sp_GoogleSearchChrome_randomChooseKeyword.setSelection(GoogleSearchChrome_randomChooseKeyword);
			        parameterlayout_GoogleSearchChrome_randomChooseKeyword.addView(tv_GoogleSearchChrome_randomChooseKeyword);
			        parameterlayout_GoogleSearchChrome_randomChooseKeyword.addView(sp_GoogleSearchChrome_randomChooseKeyword);
			        layout.addView(parameterlayout_GoogleSearchChrome_randomChooseKeyword);
			        
			        LinearLayout parameterlayout_GoogleSearchChrome_numOfkeywordToEnter = new LinearLayout(self);
			        parameterlayout_GoogleSearchChrome_numOfkeywordToEnter.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearchChrome_numOfkeywordToEnter=new TextView(self);
			        et_GoogleSearchChrome_numOfkeywordToEnter=new EditText(self);    
			        tv_GoogleSearchChrome_numOfkeywordToEnter.setText("numOfkeywordToEnter"+"=");
			        et_GoogleSearchChrome_numOfkeywordToEnter.setText(""+GoogleSearchChrome_numOfkeywordToEnter);
			        et_GoogleSearchChrome_numOfkeywordToEnter.setInputType(InputType.TYPE_CLASS_NUMBER);
			        parameterlayout_GoogleSearchChrome_numOfkeywordToEnter.addView(tv_GoogleSearchChrome_numOfkeywordToEnter);
			        parameterlayout_GoogleSearchChrome_numOfkeywordToEnter.addView(et_GoogleSearchChrome_numOfkeywordToEnter);
			        layout.addView(parameterlayout_GoogleSearchChrome_numOfkeywordToEnter);
			        
			        LinearLayout parameterlayout_GoogleSearchChrome_UrlVisitMode = new LinearLayout(self);
			        parameterlayout_GoogleSearchChrome_UrlVisitMode.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_GoogleSearchChrome_UrlVisitMode=new TextView(self);
			        sp_GoogleSearchChrome_UrlVisitMode=new Spinner(self);	
			        tv_GoogleSearchChrome_UrlVisitMode.setText("UrlVisitMode"+"=");
			        sp_GoogleSearchChrome_UrlVisitMode.setAdapter(parameterdecisionAdapter);
			        sp_GoogleSearchChrome_UrlVisitMode.setSelection(GoogleSearchChrome_UrlVisitMode);
			        parameterlayout_GoogleSearchChrome_UrlVisitMode.addView(tv_GoogleSearchChrome_UrlVisitMode);
			        parameterlayout_GoogleSearchChrome_UrlVisitMode.addView(sp_GoogleSearchChrome_UrlVisitMode);
			        layout.addView(parameterlayout_GoogleSearchChrome_UrlVisitMode);
			        
		        }
		        
		        /*
		        int YouTube_interVideoTime=5000;
		        int YouTube_numOfvideoToWatch=-1;
		        int YouTube_pullingIdleTime=1; 
		        String YouTube_videoListFilename="videoList";	
		        private int YouTube_randomChooseVideo=0;
		        int YouTube_caseSensitive=1;      
		        int YouTube_whetherSkipAds=1; 
		        */
		        if (appName.equals("YouTube")){
		        	LinearLayout parameterlayout_YouTube_interVideoTime = new LinearLayout(self);
			        parameterlayout_YouTube_interVideoTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_YouTube_interVideoTime=new TextView(self);
			        et_YouTube_interVideoTime=new EditText(self);    
			        TextView tvs_YouTube_interVideoTime=new TextView(self);
			        tv_YouTube_interVideoTime.setText("interVideoTime"+"=");
			        et_YouTube_interVideoTime.setText(""+YouTube_interVideoTime);
			        et_YouTube_interVideoTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_YouTube_interVideoTime.setText("msec");
			        parameterlayout_YouTube_interVideoTime.addView(tv_YouTube_interVideoTime);
			        parameterlayout_YouTube_interVideoTime.addView(et_YouTube_interVideoTime);
			        parameterlayout_YouTube_interVideoTime.addView(tvs_YouTube_interVideoTime);
			        layout.addView(parameterlayout_YouTube_interVideoTime);
			        
			        
			        LinearLayout parameterlayout_YouTube_numOfvideoToWatch = new LinearLayout(self);
			        parameterlayout_YouTube_numOfvideoToWatch.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_YouTube_numOfvideoToWatch=new TextView(self);
			        et_YouTube_numOfvideoToWatch=new EditText(self);    
			        tv_YouTube_numOfvideoToWatch.setText("numOfvideoToWatch"+"=");
			        et_YouTube_numOfvideoToWatch.setText(""+YouTube_numOfvideoToWatch);
			        et_YouTube_numOfvideoToWatch.setInputType(InputType.TYPE_CLASS_NUMBER);
			        parameterlayout_YouTube_numOfvideoToWatch.addView(tv_YouTube_numOfvideoToWatch);
			        parameterlayout_YouTube_numOfvideoToWatch.addView(et_YouTube_numOfvideoToWatch);
			        layout.addView(parameterlayout_YouTube_numOfvideoToWatch);
			        
			        LinearLayout parameterlayout_YouTube_pullingIdleTime = new LinearLayout(self);
			        parameterlayout_YouTube_pullingIdleTime.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_YouTube_pullingIdleTime=new TextView(self);
			        et_YouTube_pullingIdleTime=new EditText(self);    
			        TextView tvs_YouTube_pullingIdleTime=new TextView(self);
			        tv_YouTube_pullingIdleTime.setText("pullingIdleTime"+"=");
			        et_YouTube_pullingIdleTime.setText(""+YouTube_pullingIdleTime);
			        et_YouTube_pullingIdleTime.setInputType(InputType.TYPE_CLASS_NUMBER);
			        tvs_YouTube_pullingIdleTime.setText("msec");
			        parameterlayout_YouTube_pullingIdleTime.addView(tv_YouTube_pullingIdleTime);
			        parameterlayout_YouTube_pullingIdleTime.addView(et_YouTube_pullingIdleTime);
			        parameterlayout_YouTube_pullingIdleTime.addView(tvs_YouTube_pullingIdleTime);
			        layout.addView(parameterlayout_YouTube_pullingIdleTime);
			        
			        LinearLayout parameterlayout_YouTube_videoListFilename = new LinearLayout(self);
			        parameterlayout_YouTube_videoListFilename.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_YouTube_videoListFilename=new TextView(self);
			        et_YouTube_videoListFilename=new EditText(self);    
			        tv_YouTube_videoListFilename.setText("videoListFilename"+"=");
			        et_YouTube_videoListFilename.setText(""+YouTube_videoListFilename);
			        parameterlayout_YouTube_videoListFilename.addView(tv_YouTube_videoListFilename);
			        parameterlayout_YouTube_videoListFilename.addView(et_YouTube_videoListFilename);
			        layout.addView(parameterlayout_YouTube_videoListFilename);
			        
			        LinearLayout parameterlayout_YouTube_randomChooseVideo = new LinearLayout(self);
			        parameterlayout_YouTube_randomChooseVideo.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_YouTube_randomChooseVideo=new TextView(self);
			        sp_YouTube_randomChooseVideo=new Spinner(self);	
			        tv_YouTube_randomChooseVideo.setText("randomChooseVideo"+"=");
			        sp_YouTube_randomChooseVideo.setAdapter(parameterdecisionAdapter);
			        sp_YouTube_randomChooseVideo.setSelection(YouTube_randomChooseVideo);
			        parameterlayout_YouTube_randomChooseVideo.addView(tv_YouTube_randomChooseVideo);
			        parameterlayout_YouTube_randomChooseVideo.addView(sp_YouTube_randomChooseVideo);
			        layout.addView(parameterlayout_YouTube_randomChooseVideo);
			        
			        LinearLayout parameterlayout_YouTube_caseSensitive = new LinearLayout(self);
			        parameterlayout_YouTube_caseSensitive.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_YouTube_caseSensitive=new TextView(self);
			        sp_YouTube_caseSensitive=new Spinner(self);	
			        tv_YouTube_caseSensitive.setText("caseSensitive"+"=");
			        sp_YouTube_caseSensitive.setAdapter(parameterdecisionAdapter);
			        sp_YouTube_caseSensitive.setSelection(YouTube_caseSensitive);
			        parameterlayout_YouTube_caseSensitive.addView(tv_YouTube_caseSensitive);
			        parameterlayout_YouTube_caseSensitive.addView(sp_YouTube_caseSensitive);
			        layout.addView(parameterlayout_YouTube_caseSensitive);
			        
			        LinearLayout parameterlayout_YouTube_whetherSkipAds = new LinearLayout(self);
			        parameterlayout_YouTube_whetherSkipAds.setOrientation(LinearLayout.HORIZONTAL);
			        TextView tv_YouTube_whetherSkipAds=new TextView(self);
			        sp_YouTube_whetherSkipAds=new Spinner(self);	
			        tv_YouTube_whetherSkipAds.setText("whetherSkipAds"+"=");
			        sp_YouTube_whetherSkipAds.setAdapter(parameterdecisionAdapter);
			        sp_YouTube_whetherSkipAds.setSelection(YouTube_whetherSkipAds);
			        parameterlayout_YouTube_whetherSkipAds.addView(tv_YouTube_whetherSkipAds);
			        parameterlayout_YouTube_whetherSkipAds.addView(sp_YouTube_whetherSkipAds);
			        layout.addView(parameterlayout_YouTube_whetherSkipAds);
		        }
		        
		        
		        LinearLayout decisionlayout = new LinearLayout(self);
		        decisionlayout.setOrientation(LinearLayout.HORIZONTAL);
		        Button savebtn=new Button(self);
		        savebtn.setText("Save");
		        savebtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (appName.equals("Facebook")){
						Facebook_actionIdleTime=Integer.parseInt(et_Facebook_actionIdleTime.getText().toString());
						Facebook_updateStatusWaitTime=Integer.parseInt(et_Facebook_updateStatusWaitTime.getText().toString());
						Facebook_uploadPhotoWaitTime=Integer.parseInt(et_Facebook_uploadPhotoWaitTime.getText().toString());
						Facebook_checkInWaitTime=Integer.parseInt(et_Facebook_checkInWaitTime.getText().toString());	
						Facebook_AppPullToUpdateWaitTime=Integer.parseInt(et_Facebook_AppPullToUpdateWaitTime.getText().toString());	
						Facebook_numberOfPhotoToUpload=Integer.parseInt(et_Facebook_numberOfPhotoToUpload.getText().toString());
						Facebook_randomChoosePhoto=sp_Facebook_randomChoosePhoto.getSelectedItemPosition();
						Facebook_actionList=et_Facebook_actionList.getText().toString();	
						Facebook_onlyOutputFirstPullToUpdate=sp_Facebook_onlyOutputFirstPullToUpdate.getSelectedItemPosition();
						}
						
						if (appName.equals("GoogleSearch")){
						GoogleSearch_interCharacterTime=Integer.parseInt(et_GoogleSearch_interCharacterTime.getText().toString());
						GoogleSearch_keywordListFilename=et_GoogleSearch_keywordListFilename.getText().toString();
						GoogleSearch_interKeywordTime=Integer.parseInt(et_GoogleSearch_interKeywordTime.getText().toString());						
						GoogleSearch_randomChooseKeyword=sp_GoogleSearch_randomChooseKeyword.getSelectedItemPosition();
						GoogleSearch_numOfkeywordToEnter=Integer.parseInt(et_GoogleSearch_numOfkeywordToEnter.getText().toString());
						}
						
						if (appName.equals("GoogleSearchChrome")){
						GoogleSearchChrome_interCharacterTime=Integer.parseInt(et_GoogleSearchChrome_interCharacterTime.getText().toString());
						GoogleSearchChrome_keywordListFilename=et_GoogleSearchChrome_keywordListFilename.getText().toString();
						GoogleSearchChrome_interKeywordTime=Integer.parseInt(et_GoogleSearchChrome_interKeywordTime.getText().toString());						
						GoogleSearchChrome_randomChooseKeyword=sp_GoogleSearchChrome_randomChooseKeyword.getSelectedItemPosition();
						GoogleSearchChrome_numOfkeywordToEnter=Integer.parseInt(et_GoogleSearchChrome_numOfkeywordToEnter.getText().toString());
						GoogleSearchChrome_UrlVisitMode=sp_GoogleSearchChrome_UrlVisitMode.getSelectedItemPosition();
						}
					        
						if (appName.equals("YouTube")){
							YouTube_interVideoTime=Integer.parseInt(et_YouTube_interVideoTime.getText().toString());
							YouTube_numOfvideoToWatch=Integer.parseInt(et_YouTube_numOfvideoToWatch.getText().toString());
							YouTube_pullingIdleTime=Integer.parseInt(et_YouTube_pullingIdleTime.getText().toString());
							YouTube_videoListFilename=et_YouTube_videoListFilename.getText().toString();
							YouTube_randomChooseVideo=sp_YouTube_randomChooseVideo.getSelectedItemPosition();
							YouTube_caseSensitive=sp_YouTube_caseSensitive.getSelectedItemPosition();
							YouTube_whetherSkipAds=sp_YouTube_whetherSkipAds.getSelectedItemPosition();
						}
						
						settingdialog.dismiss();
						settingdialog=null;
					}
		        	
		        });
		        Button cancelbtn=new Button(self);
		        cancelbtn.setText("Cancel");
		        cancelbtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						settingdialog.dismiss();
						settingdialog=null;
					}
		        	
		        });
		        decisionlayout.addView(savebtn);
		        decisionlayout.addView(cancelbtn);
		        decisionlayout.setGravity(Gravity.CENTER_HORIZONTAL);
		        
		        layout.addView(decisionlayout); 
		        settingdialog.setContentView(layout);
		        settingdialog.show();
				
			}
			
		});
		
		startcontrol=(Button)findViewById(R.id.launchcontrol);	
	//	mAlarmManagerBroadcastReceiver=new AlarmManagerBroadcastReceiver();
		startcontrol.setOnClickListener(
    		    new View.OnClickListener() {
    		    	boolean capSwitch=true;
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
												
						if (capSwitch){
						//	mAlarmManagerBroadcastReceiver.setAlarm(MainActivity.this);
							
							
							
							if (oneFolderMode==1) {
								String dd = new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(Calendar.getInstance().getTime());
								//	Log.d(TAG,"Date: "+dd);
								outputfoldername=outputFileRoot+"/"+appName+"_"+dd;
								File file = new File(outputfoldername);		
						    	file.mkdirs();
						    	outputfoldernameChangeTime=file.lastModified();
							}
					  //  	readConfig();
					    	updateCtrlFileAndInstall();
						    	
					    	if (controlIdleTime>0){
					    		setViewsEnabled(false);
								timer = new Timer();							
								timer.schedule(new TimerTask() {
								   @Override
								   public void run() {		
						//			   mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK| PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, getClass().getName());
									   Log.d(TAG,"acquire weak lock");
									   mWakeLock.acquire();
									   Log.d(TAG, "time's up: "+controlIdleTime);
								//	   screenon=false;
								//	   turnScreenOn();
								//	   while (!screenon);
									   try {
										 Thread.sleep(1000);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									   
									    if (oneFolderMode!=1) {
											String dd = new SimpleDateFormat("yyyyMMdd_HH-mm-ss").format(Calendar.getInstance().getTime());
											//	Log.d(TAG,"Date: "+dd);
											outputfoldername=outputFileRoot+"/"+appName+"_"+dd;
											File file = new File(outputfoldername);		
									    	file.mkdirs();
									    	outputfoldernameChangeTime=file.lastModified();
										}
									   
									   startAppControl();
								   }
								  }, 0, controlIdleTime*1000);
								capSwitch=false;
								startcontrol.setText(R.string.stopcontrol);
					    	}
					    	else {
					    		Log.d(TAG,"acquire weak lock");
					    		mWakeLock.acquire();
					    		try {
									 Thread.sleep(1000);
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					    		
					    		startAppControl();
					    	}
							
						}
						else {
							Log.d(TAG,"Stop periodically control");
					//		mAlarmManagerBroadcastReceiver.cancelAlarm(MainActivity.this);
							if (mWakeLock.isHeld()) {
						      Log.d(TAG,"release weak lock");
							  mWakeLock.release();
							}
							
							StopTCPDumpThread stdt;
					    	stdt=new StopTCPDumpThread();
					    	stdt.start();
					    	
					    	tcpdumpstarted=false;

							incontrol=false;
							incontrolthenpause=false;
					    	
							timer.cancel();
							capSwitch=true;
							startcontrol.setText(R.string.launchcontrol);
							setViewsEnabled(true);
						}
					}
    		    	
    		    }
	    );
		outputDeviceInfo();
		
		readConfig();
		updateUI();
		
    //	readConfig();   		
	//	mApp = (ARODataCollector) getApplication();		
	}	
	
	void setViewsEnabled(Boolean v){
		Log.d(TAG,"lalala "+v);
		chooseTargetApp.setEnabled(v);
	    configfileinput.setEnabled(v);
	    browsefile.setEnabled(v);	    
	    loadconfigurationfile.setEnabled(v);    
	    saveconfigurationfile.setEnabled(v);
	    appcontrollerparameters.setEnabled(v);
	    targetappparameters.setEnabled(v);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG,"requestCode: "+requestCode);
		switch(requestCode) {
			case ACTIVITY_CHOOSE_FILE: {
				if (resultCode == RESULT_OK){
					Uri uri = data.getData();
					configFileFullname = uri.getPath();
					updateUI();
				}
			};
			case SUMSUNG_FILE_REQUEST_CODE: {
				if (resultCode == RESULT_OK){
					Uri uri = data.getData();
					configFileFullname = uri.getPath();
					updateUI();
				}
			}
			
	    }
	}
	
	void resetDefaultParameter(){
		controlIdleTime=20*60;//seconds
		appName="Facebook";    
		collectPcap=1;
		collectMainLogcat=0;
	    collectEventLogcat=0;
	    collectRadioLogcat=0;
	    clickSleepTime=1500;
	    webViewFacebook=0;
	    webViewFacebookPull=2*60;
	    
	    //Facebook Parameter
	    Facebook_actionIdleTime=10000;
	 	Facebook_updateStatusWaitTime=5000;
	 	Facebook_uploadPhotoWaitTime=20000;
	 	Facebook_checkInWaitTime=5000;
	 	Facebook_randomChoosePhoto=2;
	 	Facebook_testClickSleepTime="1500";
	 	Facebook_actionList="5,2,3,6";
	 	Facebook_numberOfRepeat=1;
	 	Facebook_interActionTime="10000";
	 	Facebook_onlyOutputFirstPullToUpdate=1;
	 	
	 	//GoogleSearch Parameter
	 	GoogleSearch_interCharacterTime=500;
	 	GoogleSearch_keywordListFilename="KeywordList";
	 	GoogleSearch_interKeywordTime=5000;
	 	GoogleSearch_randomChooseKeyword=0;
	 	GoogleSearch_numOfkeywordToEnter=-1;
	 	
		//GoogleSearchChrome Parameter
	 	GoogleSearchChrome_interCharacterTime=500;
	 	GoogleSearchChrome_keywordListFilename="KeywordList";
	 	GoogleSearchChrome_interKeywordTime=5000;
	 	GoogleSearchChrome_randomChooseKeyword=0;
	 	GoogleSearchChrome_numOfkeywordToEnter=-1;
	 	GoogleSearchChrome_UrlVisitMode=0;
	}
	
	protected void updateUI(){
		runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	int i=0;
            	for (;i<targetapps.length;i++)
            		if (appName.equals(targetapps[i]))
            			break;
            		
            	if (i>=targetapps.length){
            		AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(self);                      
    	 		    dlgAlert.setMessage("Unknown app name! Please check configuration.");    
    	 		    dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
    	 				@Override
    	 				public void onClick(DialogInterface dialog, int which) {
    	 					// TODO Auto-generated method stub
    	 			//		itself.finish();
    	 					
    	 				}
    	 				
    	 			});
    	 		    dlgAlert.setCancelable(true);
    	 		    dlgAlert.create().show();
            	}
            	else 
            		chooseTargetApp.setSelection(i);
            	configfileinput.setText(configFileFullname);
            }
        });
	}
	public void startAppControl(){		
		intesting=true;
		
		SetWlan sw;
		sw=new SetWlan();
	//	sw.start();		
    	StartAppControlThread sact;
    	sact=new StartAppControlThread();
    	
    	if (collectPcap==1){
	    	StartTCPDumpThread stdt;
	    	stdt=new StartTCPDumpThread();
	    	stdt.start();
    	}

    	writeConfig(outputfoldername+"/"+configFilename);
    	
     	appcontrolStart();
  	    sact.start();
  /*  	String Command = "mkdir alfdir\n";
		rc.start();
	*/	
	}
	
    class StartAppControlThread extends Thread{
     	
    	public void run(){
    		try {
    			while (!withctrlfileinstalled);
    			while (!tcpdumpstarted);
    			
    			ControlThread ct=new ControlThread();
				ct.start();	
				
			} catch (Exception e) {
				e.printStackTrace();
			}   	        
    	}    	
     }
    

    class StartTCPDumpThread extends Thread{
     	
    	public void run(){
    		try {
    			tcpdumpstarted=false;
    			installTCPDump();    			
    			Process sh = Runtime.getRuntime().exec("su");		    
				DataOutputStream os = new DataOutputStream(sh.getOutputStream());
				
				//set truncate data
				tcpdumpTrunk=DEFAULT_TRUNK;
				if (appName.equals("YouTube")) 
					tcpdumpTrunk=VIDEO_TRUNK;
				
				String Command = INTERNAL_DATA_PATH+"/"+tcpdumpfile+" -s "+tcpdumpTrunk+" -w "+outputfoldername+"/traffic.cap\n";
		  //      Process sh = Runtime.getRuntime().exec(Command);		    
		        Log.d(TAG, Command);
		        os.writeBytes(Command); 
		        os.close();
		        tcpdumpstarted=true;
			} catch (Exception e) {
				e.printStackTrace();
			}   	        
    	}    	
     }
    
	class SetWlan extends Thread{
    
     	
    	public void run(){
    		try {
    			while (intesting){
	    			Process sh = Runtime.getRuntime().exec("su");		    
					DataOutputStream os = new DataOutputStream(sh.getOutputStream());
					String Command = "ifconfig wlan0 0.0.0.0\n";
			  //      Process sh = Runtime.getRuntime().exec(Command);		    
			        Log.d(TAG, Command);
			        os.writeBytes(Command); 
			        os.close();
			        Thread.sleep(1000);
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}  
    	}
    }        
	
     class StopTCPDumpThread extends Thread{
     	
    	public void run(){
    		try {
    			Log.d(TAG,"Stop tcpdump");
    			killProcess(tcpdumpfile);
    			
			} catch (Exception e) {
				e.printStackTrace();
			}   	        
    	}    	
     }
     
     
     class StopLogcatThread extends Thread{
      	
     	public void run(){
     		try {
     			Log.d(TAG,"Stop logcat");
     			killProcess("logcat");
     			
 			} catch (Exception e) {
 				e.printStackTrace();
 			}   	        
     	}    	
      }    
     
    void installTCPDump(){
    	try {
    	//	createFolder(foldername);
    		
	    	File file = new File(INTERNAL_DATA_PATH+"/"+tcpdumpfile);		
	    	if (!file.exists()){			
	    		InputStream is = getResources().openRawResource(R.raw.tcpdump);
				OutputStream os = new FileOutputStream(file);
			    byte[] buffer = new byte[256];
			    int bytesRead = 0;
			    while ((bytesRead = is.read(buffer)) != -1) {
		//	        System.out.println(bytesRead);
			        os.write(buffer, 0, bytesRead);
			    }
			    os.close();
			    
			    String Command = "chmod -R 777 "+INTERNAL_DATA_PATH+"/"+tcpdumpfile;
			    Process sh = Runtime.getRuntime().exec("su");		    
				DataOutputStream dos = new DataOutputStream(sh.getOutputStream());
		        Log.d(TAG, Command);
		        dos.writeBytes(Command); 
		        dos.close();
			}
			
			
	    } catch (Exception e) {
			e.printStackTrace();
		}
    	
    /*	InstallApk ia=new InstallApk(configfoldername+"/"+ctrlfile);
		ia.start();		
	*/
    	installApk(configfoldername+"/"+ctrlfile);
    	if (assetsFilename.equals("AndroidTestCaseTool_GoogleSearchChrome.apk")){
    		clearChromeCache();
    	}	
    }   
        
    class ControlThread extends Thread{
     	
    	public void run(){
    		try {
    			while (!incontrolthenpause) {
					Process sh = Runtime.getRuntime().exec("su");		
					incontrol=true;
					DataOutputStream os = new DataOutputStream(sh.getOutputStream());
			        String Command = "am instrument -e class com.robustnet.appcontroller.AppController -w com.google.android.apps.authenticator2.test/android.test.InstrumentationTestRunner\n";
			  //      Process sh = Runtime.getRuntime().exec(Command);		    
			        Log.d(TAG, Command);
			        os.writeBytes(Command); 
			        os.close();      
			        Thread.sleep(INCONTROLWAITTIME);
    			}
		        
			} catch (Exception e) {
				e.printStackTrace();
			} 
    	}
     }   
    
    
    void updateCtrlFileAndInstall(){
    	assetsFilename="AndroidTestCaseTool_"+appName+".apk";
    	ctrlfile="ctrlcode_"+appName+".apk";
    	try {
    	//	createFolder(foldername);
    		
	    	File file = new File(configfoldername+"/"+ctrlfile);		
	    	file.getParentFile().mkdirs();
	    	if (file.exists()){			
				file.delete();
			}
			if (!file.exists()){				
				file.createNewFile();
			}
			InputStream is = getResources().getAssets().open(assetsFilename);
			OutputStream os = new FileOutputStream(file);
		    byte[] buffer = new byte[256];
		    int bytesRead = 0;
		    while ((bytesRead = is.read(buffer)) != -1) {
	//	        System.out.println(bytesRead);
		        os.write(buffer, 0, bytesRead);
		    }
		    os.close();
			
	    } catch (Exception e) {
			e.printStackTrace();
		}
    	
    /*	InstallApk ia=new InstallApk(configfoldername+"/"+ctrlfile);
		ia.start();		
	*/
    	installApk(configfoldername+"/"+ctrlfile);
    	if (assetsFilename.equals("AndroidTestCaseTool_GoogleSearchChrome.apk")){
    		clearChromeCache();
    	}
    	if (assetsFilename.equals("AndroidTestCaseTool_AndroidBrowser.apk")){
    		clearAndroidBrowserCache();
    	}
    	if (assetsFilename.equals("AndroidTestCaseTool_FirefoxBrowser.apk")){
    		clearFirefoxBrowserCache();
    	}	
    }
    
    
 /*   void createFolder(String fn){
      File theDir = new File(fn);
      Log.d(TAG, "here "+theDir);
  	  // if the directory does not exist, create it
  	  if (!theDir.exists()) {
  	      Log.d(TAG, "here1 "+theDir);
  		  
  	    boolean result = theDir.mkdir();  

	      Log.d(TAG, "here2 "+result);
  	  }
    }*/
    
 /*   class InstallApk extends Thread{
    	String apkfile;
    	public InstallApk(String af){
    		apkfile=new String(af);
    	}
     	
    	public void run(){
    		try {
    			withctrlfileinstalled=false;
    			Process sh = Runtime.getRuntime().exec("su");		    
				DataOutputStream os = new DataOutputStream(sh.getOutputStream());

		        String Command = "pm install -r "+apkfile+"\n";
		        Log.d(TAG, Command);
		        os.writeBytes(Command); 
		        os.close();
		        withctrlfileinstalled=true;
			} catch (Exception e) {
				e.printStackTrace();
			}    	
    	        
    	        
    	}
    }
    */
    
    void installApk(String apkfile) {
		try {
			withctrlfileinstalled=false;
			Process sh = Runtime.getRuntime().exec("su");		    
			DataOutputStream os = new DataOutputStream(sh.getOutputStream());

	        String Command = "pm install -r "+apkfile+"\n";
	        Log.d(TAG, Command);
	        os.writeBytes(Command); 
	        os.close();
	        Thread.sleep(2000);
	        withctrlfileinstalled=true;
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
    
    
    void clearChromeCache() {
    	try {
			Process sh = Runtime.getRuntime().exec("su");		    
			DataOutputStream os = new DataOutputStream(sh.getOutputStream());

	        String Command = "rm -fr /data/data/com.android.chrome/files /data/data/com.android.chrome/databases /data/data/com.android.chrome/cache /data/data/com.android.chrome/app*\n";
	        Log.d(TAG, Command);
	        os.writeBytes(Command); 
	        os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}  
    }
    
    void clearAndroidBrowserCache(){
    	try {
			Process sh = Runtime.getRuntime().exec("su");		    
			DataOutputStream os = new DataOutputStream(sh.getOutputStream());

	        String Command = "rm -fr /data/data/com.android.browser/cache\n";
	        Log.d(TAG, Command);
	        os.writeBytes(Command); 
	        os.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    
    void clearFirefoxBrowserCache(){
    	try {
			Process sh = Runtime.getRuntime().exec("su");		    
			DataOutputStream os = new DataOutputStream(sh.getOutputStream());

	        String Command = "rm -fr /data/data/org.mozilla.firefox/cache\n";
	        Log.d(TAG, Command);
	        os.writeBytes(Command); 
	        os.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    
    
    public void onPause() {
    	super.onPause();
    //	Log.d(TAG,"app pause!");
    	if (incontrol){
    		incontrolthenpause=true;
    	}
    }
    
    public void onResume() {
		super.onResume();	
		Log.d(TAG,"app resume!");
		if (incontrolthenpause){
			appcontrolFinished();
		}
	}
    
    public void appcontrolStart() {
		Log.d(TAG,"app control start!");
		try {
			String Command = "logcat -c -b main -b radio -b events\n";
			Runtime.getRuntime().exec(Command);		
			Log.d(TAG,Command);
			
			 
			
			if (collectMainLogcat==1){
				Command = "logcat  -v threadtime -b main -f "+ outputfoldername+"/logcat_main\n";
				runCmd rc=new runCmd(Command, true);
				rc.start();
			}
			
			if (collectRadioLogcat==1){
				Command = "logcat -v threadtime -b radio -f "+ outputfoldername+"/logcat_radio\n";
				runCmd rc=new runCmd(Command, true);
				rc.start();
			}
			
			
			if (collectEventLogcat==1){
				Command = "logcat -v threadtime -b events -f "+ outputfoldername+"/logcat_events\n";
				runCmd rc=new runCmd(Command, true);
				rc.start();
			}			
			
			String nettype=getNetworkType();
			report((new Date()).getTime()+" ["+getNetworkType()+"]", nettypeFilename);
			outputDeviceInfo();

		} catch (Exception e) {
			e.printStackTrace();
		}    	
		
    }
    
    public void appcontrolFinished() {
		Log.d(TAG,"app control finish!");
		//data output
		/*String arofoldername=findOutputFolderPath("/sdcard/ARO/");
		File arofolder = new File("/sdcard/ARO/"+arofoldername);
		if (arofolder.lastModified() >= outputfoldernameChangeTime){
			String Command = "mv "+ "/sdcard/ARO/"+arofoldername+" "+outputfoldername+"/arodata\n";
			runCmd rc=new runCmd(Command, false);
			rc.start();

		}
		*/
		
		incontrol=false;
		incontrolthenpause=false;
		
		
		StopTCPDumpThread stdt;
    	stdt=new StopTCPDumpThread();
    	stdt.start();
    	
    	StopLogcatThread slt;
    	slt=new StopLogcatThread();
    	slt.start();
    	
		if (mWakeLock.isHeld()) {
			Log.d(TAG,"release weak lock");
			mWakeLock.release();
		}
		intesting=false;
    }
    
    class runCmd extends Thread{
    	String cmd;
    	boolean su;
    	public runCmd(String Command, boolean usingroot){
    		cmd=Command;
    		su=usingroot;
    	}
     	
    	public void run(){
    		try {
    			if (su){
    				Process sh = Runtime.getRuntime().exec("su");		    
    				DataOutputStream os = new DataOutputStream(sh.getOutputStream());
    		        Log.d(TAG, cmd);
    		        os.writeBytes(cmd); 
    		        os.close();
    			}
    			else {
    				Log.d(TAG,cmd);
    				Runtime.getRuntime().exec(cmd);	
    			}
			} catch (Exception e) {
				e.printStackTrace();
			}    	
  
    	}
    }
    
    public static String findOutputFolderPath(String path){
		 
		 String files;
		 File folder = new File(path);
		 File[] listOfFiles = folder.listFiles(); 
		 long maxtime=-1;
		 String respath=null;
		 for (int i = 0; i < listOfFiles.length; i++) {		 
			if (listOfFiles[i].isDirectory()) {
				long lasttime=listOfFiles[i].lastModified();
				if (lasttime>maxtime){
					maxtime=lasttime;
					respath=listOfFiles[i].getName();
				}
  	     	}
		 }
		 return respath;
	}
    
    
    void readConfig(){		
		Log.d(TAG,"read AppController configuration");
		resetDefaultParameter();
		File file = new File(configFileFullname);		
    	file.getParentFile().mkdirs();
    //	boolean appchanged=false;
		if (file.exists()){				
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line=null;
				while ((line=br.readLine())!=null){
					if (line.startsWith("AppController_")){
						String[] lines=line.split("=");
						String entry=lines[0].substring("AppController_".length());
						if (entry.equals("controlIdleTime")){
							controlIdleTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("appName")){
			/*				if (!appName.equals(lines[1])){
								appchanged=true;
							}
				*/			appName=lines[1];
						}
						if (entry.equals("collectPcap")){
							collectPcap=Integer.parseInt(lines[1]);
						}						
						if (entry.equals("collectMainLogcat")){
							collectMainLogcat=Integer.parseInt(lines[1]);
						}
						if (entry.equals("collectEventLogcat")){
							collectEventLogcat=Integer.parseInt(lines[1]);
						}
						if (entry.equals("collectRadioLogcat")){
							collectRadioLogcat=Integer.parseInt(lines[1]);
						}
						if (entry.equals("clickSleepTime")){
							clickSleepTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("oneFolderMode")){
							oneFolderMode=Integer.parseInt(lines[1]);
						}		
						if (entry.equals("webViewFacebook")){
							webViewFacebook=Integer.parseInt(lines[1]);							
						}
						if (entry.equals("webViewFacebookPull")){
							webViewFacebookPull=Integer.parseInt(lines[1]);							
						}	
						
					}
				}
				
				
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(TAG, e.toString());
				e.printStackTrace();
			}
			
			readFacebookConfig();
			readGoogleSearchConfig();
			readGoogleSearchChromeConfig();
			readYouTubeConfig();
		}
	/*	else 
			appchanged=true;
	 	
		if (appchanged || replaceapk){
			updateCtrlFileAndInstall();
		}
		else
			withctrlfileinstalled=true;
			*/
	}
	
	void writeConfig(String outputfilename){
		try {
			File file = new File(outputfilename);
	    	file.getParentFile().mkdirs();
			
			if (!file.exists()){			
				file.createNewFile();
			}
			
			BufferedWriter fop = new BufferedWriter(new FileWriter(file));
 			fop.write("AppController_controlIdleTime="+controlIdleTime+"\n");
 			fop.write("AppController_appName="+appName+"\n");
 			fop.write("AppController_clickSleepTime="+clickSleepTime+"\n");
 			fop.write("AppController_collectPcap="+collectPcap+"\n"); 			
 			fop.write("AppController_collectMainLogcat="+collectMainLogcat+"\n");
 			fop.write("AppController_collectEventLogcat="+collectEventLogcat+"\n");
 			fop.write("AppController_collectRadioLogcat="+collectRadioLogcat+"\n");
 			fop.write("AppController_oneFolderMode="+oneFolderMode+"\n");
 			fop.write("AppController_webViewFacebook="+webViewFacebook+"\n");
 			fop.write("AppController_webViewFacebookPull="+webViewFacebookPull+"\n"); 			
 			
 			
 			fop.close();
    	}
      catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
      }
		
		
	  writeFacebookConfig(outputfilename);
	  writeGoogleSearchConfig(outputfilename);
	  writeGoogleSearchChromeConfig(outputfilename);
	  writeYouTubeConfig(outputfilename);
	}
	
	void readFacebookConfig(){		
		Log.d(TAG,"read Facebook configuration");
		File file = new File(configFileFullname);		
    	file.getParentFile().mkdirs();
		if (file.exists()){				
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line=null;
				while ((line=br.readLine())!=null){
					if (line.startsWith("Facebook_")){
						String[] lines=line.split("=");
						String entry=lines[0];						
						if (entry.equals("Facebook_actionIdleTime")){
							Facebook_actionIdleTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("Facebook_updateStatusWaitTime")){
							Facebook_updateStatusWaitTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("Facebook_uploadPhotoWaitTime")){
							Facebook_uploadPhotoWaitTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("Facebook_checkInWaitTime")){
							Facebook_checkInWaitTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("Facebook_AppPullToUpdateWaitTime")){
							Facebook_AppPullToUpdateWaitTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("Facebook_numberOfPhotoToUpload")){
							Facebook_numberOfPhotoToUpload=Integer.parseInt(lines[1]);
						}
						if (entry.equals("Facebook_randomChoosePhoto")){
							Facebook_randomChoosePhoto=Integer.parseInt(lines[1]);
						}
						if (entry.equals("Facebook_testClickSleepTime")){
							Facebook_testClickSleepTime=lines[1];
						}						
						if (entry.equals("Facebook_actionList")){
							Facebook_actionList=lines[1];
						}
						if (entry.equals("Facebook_numberOfRepeat")){
							Facebook_numberOfRepeat=Integer.parseInt(lines[1]);
						}
						if (entry.equals("Facebook_interActionTime")){
							Facebook_interActionTime=lines[1];
						}					
						if (entry.equals("Facebook_onlyOutputFirstPullToUpdate")){
							Facebook_onlyOutputFirstPullToUpdate=Integer.parseInt(lines[1]);
						}
					}
				}
				
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(TAG, e.toString());
				e.printStackTrace();
			}
		}
	}
	
	
	void writeFacebookConfig(String outputfilename){
		try {
			File file = new File(outputfilename);
	    	file.getParentFile().mkdirs();
			
			if (!file.exists()){			
				file.createNewFile();
			}			
			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
			fop.write("Facebook_actionIdleTime="+Facebook_actionIdleTime+"\n");
 			fop.write("Facebook_updateStatusWaitTime="+Facebook_updateStatusWaitTime+"\n");
 			fop.write("Facebook_uploadPhotoWaitTime="+Facebook_uploadPhotoWaitTime+"\n");
 			fop.write("Facebook_checkInWaitTime="+Facebook_checkInWaitTime+"\n");
 			fop.write("Facebook_AppPullToUpdateWaitTime="+Facebook_AppPullToUpdateWaitTime+"\n");
 			fop.write("Facebook_numberOfPhotoToUpload="+Facebook_numberOfPhotoToUpload+"\n");
 			fop.write("Facebook_randomChoosePhoto="+Facebook_randomChoosePhoto+"\n");
 			fop.write("Facebook_testClickSleepTime="+Facebook_testClickSleepTime+"\n");
 			fop.write("Facebook_actionList="+Facebook_actionList+"\n");
 			fop.write("Facebook_numberOfRepeat="+Facebook_numberOfRepeat+"\n");
 			fop.write("Facebook_interActionTime="+Facebook_interActionTime+"\n");
 			fop.write("Facebook_onlyOutputFirstPullToUpdate="+Facebook_onlyOutputFirstPullToUpdate+"\n");
 			
 			fop.close();
    	}
      catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
      }
	}
	
	void readGoogleSearchConfig(){		
		Log.d(TAG,"read GoogleSearch configuration");
		File file = new File(configFileFullname);		
    	file.getParentFile().mkdirs();
		if (file.exists()){				
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line=null;
				while ((line=br.readLine())!=null){
					if (line.startsWith("GoogleSearch_")){
						String[] lines=line.split("=");
						String entry=lines[0];
						if (entry.equals("GoogleSearch_interCharacterTime")){
							GoogleSearch_interCharacterTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("GoogleSearch_keywordListFilename")){
							GoogleSearch_keywordListFilename=lines[1];
						}
						if (entry.equals("GoogleSearch_interKeywordTime")){
							GoogleSearch_interKeywordTime=Integer.parseInt(lines[1]);
						}
					    if (entry.equals("GoogleSearch_randomChooseKeyword")){
					    	GoogleSearch_randomChooseKeyword=Integer.parseInt(lines[1]);
						}
					    if (entry.equals("GoogleSearch_numOfkeywordToEnter")){
					    	GoogleSearch_numOfkeywordToEnter=Integer.parseInt(lines[1]);
						}
						
						
					}
				}
				
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(TAG, e.toString());
				e.printStackTrace();
			}
		}
	}
	
	void writeGoogleSearchConfig(String outputfilename){
		try {
			File file = new File(outputfilename);
	    	file.getParentFile().mkdirs();
			
			if (!file.exists()){			
				file.createNewFile();
			}
		
			
			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
 			fop.write("GoogleSearch_interCharacterTime="+GoogleSearch_interCharacterTime+"\n");
 			fop.write("GoogleSearch_keywordListFilename="+GoogleSearch_keywordListFilename+"\n");
 			fop.write("GoogleSearch_interKeywordTime="+GoogleSearch_interKeywordTime+"\n");
 			fop.write("GoogleSearch_randomChooseKeyword="+GoogleSearch_randomChooseKeyword+"\n");
 			fop.write("GoogleSearch_numOfkeywordToEnter="+GoogleSearch_numOfkeywordToEnter+"\n");
 		
 			fop.close();
    	}
      catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
      }
	}
	
	void readGoogleSearchChromeConfig(){		
		Log.d(TAG,"read AppController configuration");
		File file = new File(configFileFullname);		
    	file.getParentFile().mkdirs();
		if (file.exists()){				
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line=null;
				while ((line=br.readLine())!=null){
					if (line.startsWith("GoogleSearchChrome_")){
						String[] lines=line.split("=");
						String entry=lines[0];
						if (entry.equals("GoogleSearchChrome_interCharacterTime")){
							GoogleSearchChrome_interCharacterTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("GoogleSearchChrome_keywordListFilename")){
							GoogleSearchChrome_keywordListFilename=lines[1];
						}
						if (entry.equals("GoogleSearchChrome_interKeywordTime")){
							GoogleSearchChrome_interKeywordTime=Integer.parseInt(lines[1]);
						}
					    if (entry.equals("GoogleSearchChrome_randomChooseKeyword")){
					    	GoogleSearchChrome_randomChooseKeyword=Integer.parseInt(lines[1]);
						}
					    if (entry.equals("GoogleSearchChrome_numOfkeywordToEnter")){
					    	GoogleSearchChrome_numOfkeywordToEnter=Integer.parseInt(lines[1]);
						}
					    if (entry.equals("GoogleSearchChrome_UrlVisitMode")){
					    	GoogleSearchChrome_UrlVisitMode=Integer.parseInt(lines[1]);
						}		
						
					}
				}
				
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(TAG, e.toString());
				e.printStackTrace();
			}
		}
	}
	
	void writeGoogleSearchChromeConfig(String outputfilename){
		try {
			File file = new File(outputfilename);
	    	file.getParentFile().mkdirs();
			
			if (!file.exists()){			
				file.createNewFile();
			}
		
			
			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
 			fop.write("GoogleSearchChrome_interCharacterTime="+GoogleSearchChrome_interCharacterTime+"\n");
 			fop.write("GoogleSearchChrome_keywordListFilename="+GoogleSearchChrome_keywordListFilename+"\n");
 			fop.write("GoogleSearchChrome_interKeywordTime="+GoogleSearchChrome_interKeywordTime+"\n");
 			fop.write("GoogleSearchChrome_randomChooseKeyword="+GoogleSearchChrome_randomChooseKeyword+"\n");
 			fop.write("GoogleSearchChrome_numOfkeywordToEnter="+GoogleSearchChrome_numOfkeywordToEnter+"\n");
 			fop.write("GoogleSearchChrome_UrlVisitMode="+GoogleSearchChrome_UrlVisitMode+"\n");
 			
 			fop.close();
    	}
      catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
      }
	}
	
	void readYouTubeConfig(){		
		Log.d(TAG,"read AppController configuration");
		File file = new File(configFileFullname);		
    	file.getParentFile().mkdirs();
		if (file.exists()){				
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String line=null;
				while ((line=br.readLine())!=null){
					if (line.startsWith("YouTube_")){
						String[] lines=line.split("=");
						String entry=lines[0].substring("YouTube_".length());
						if (entry.equals("interVideoTime")){
							YouTube_interVideoTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("randomChooseVideo")){
							YouTube_randomChooseVideo=Integer.parseInt(lines[1]);
						}
						if (entry.equals("numOfvideoToWatch")){
							YouTube_numOfvideoToWatch=Integer.parseInt(lines[1]);
						}
						if (entry.equals("caseSensitive")){
							YouTube_caseSensitive=Integer.parseInt(lines[1]);
						}
						if (entry.equals("pullingIdleTime")){
							YouTube_pullingIdleTime=Integer.parseInt(lines[1]);
						}
						if (entry.equals("videoListFilename")){
							YouTube_videoListFilename=lines[1].trim();
						}
						if (entry.equals("whetherSkipAds")){
							YouTube_whetherSkipAds=Integer.parseInt(lines[1]);
						}
						
					}
				}
				
				br.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(TAG, e.toString());
				e.printStackTrace();
			}
		}
	}
	
	void writeYouTubeConfig(String outputfilename){	    
		try {
			File file = new File(outputfilename);
	    	file.getParentFile().mkdirs();
			
			if (!file.exists()){			
				file.createNewFile();
			}
		
			
			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
 			fop.write("YouTube_interVideoTime="+YouTube_interVideoTime+"\n");
 			fop.write("YouTube_randomChooseVideo="+YouTube_randomChooseVideo+"\n");
 			fop.write("YouTube_numOfvideoToWatch="+YouTube_numOfvideoToWatch+"\n");
 			fop.write("YouTube_caseSensitive="+YouTube_caseSensitive+"\n");
 			fop.write("YouTube_pullingIdleTime="+YouTube_pullingIdleTime+"\n");
 			fop.write("YouTube_videoListFilename="+YouTube_videoListFilename+"\n");
 			fop.write("YouTube_whetherSkipAds="+YouTube_whetherSkipAds+"\n");
 			
 		
 			fop.close();
    	}
      catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
      }
	}
	
	public void report(String rep, String filename){
		
    		try {
				File file = new File(outputfoldername+"/"+filename);
		    	file.getParentFile().mkdirs();
				
				if (!file.exists()){			
					file.createNewFile();
				}
				
				BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
	 			fop.write(rep+"\n");
	 			
	 			fop.close();
	    	}
    		catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		      }
	}
	
/*    public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

     public AlarmManagerBroadcastReceiver(){
    	 
     }
     @Override
     public void onReceive(Context context, Intent intent) {
       PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
             PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ALF");
             //Acquire the lock
             wl.acquire();

             Log.d(TAG, "call back!!");
         //    startARO();
             
             //Release the lock
             wl.release();
     }

        public void setAlarm(Context context)
        {
        	Log.d(TAG, "set here!!");
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            //After after 10 seconds
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10 , pi); 
        }

        public void cancelAlarm(Context context)
        {
            Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
        }

        public void setOnetimeTimer(Context context){
         AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
        }
    }*/
	
	protected void killProcess(String tar){
		try {
			Process sh = Runtime.getRuntime().exec("su");
			DataOutputStream os = new DataOutputStream(sh.getOutputStream());
			String Command;

			try {
				ArrayList<String> rows=getPlinesfromPS(tar);
				for (int i=0;i<rows.size();i++){
			    	String[] cols = rows.get(i).split("\\s");
			    	for (int j=1;j<cols.length;j++)
			    		if (cols[j].length()!=0){ 
			      			Command="kill "+ cols[j]+"\n";
			    			 os.writeBytes(Command);
			    		    break;
			    		}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}                    	

			Command="exit\n";
			os.writeBytes(Command);
	        os.flush();
	        os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static ArrayList<String> getPlinesfromPS(String processName){
		String resps=executePS();
		String[] lines = resps.split("\\n");
		ArrayList<String> reslines=new ArrayList<String>();
		for (int i=0;i<lines.length;i++){
			if (lines[i].contains(processName)){
				reslines.add(lines[i]);
			}
		}
		return reslines;
		
	}
	
	protected static String executePS() {
		String line = null;
		try {
		Process process = Runtime.getRuntime().exec("ps");
		InputStreamReader inputStream = new InputStreamReader(process.getInputStream());
		BufferedReader reader = new BufferedReader(inputStream);
			

			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			process.waitFor();

			line = output.toString();
			reader.close();
			inputStream.close();
			reader.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}
	
	protected static int getUidForPid(int pid) {
	    try {
	      BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + pid + "/status")));
	      for(String line = rdr.readLine(); line != null; line = rdr.readLine()) {
	        if(line.startsWith("Uid:")) {
	          String tokens[] = line.substring(4).split("[ \t]+"); 
	          String realUidToken = tokens[tokens[0].length() == 0 ? 1 : 0];
	          try {
	            return Integer.parseInt(realUidToken);
	          } catch(NumberFormatException e) {
	            return -1;
	          }
	        }
	      }
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
	    return -1;
	}
	
	
	String getNetworkType(){
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		int type=info.getType();
		int subType=info.getSubtype();
		if(type==ConnectivityManager.TYPE_WIFI){
            return "WiFi";
        }else if(type==ConnectivityManager.TYPE_MOBILE){
            switch(subType){
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "Cellular_1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "Cellular_CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "Cellular_EDGE"; 
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "Cellular_EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "Cellular_EVDO_A"; 
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "Cellular_GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "Cellular_HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "Cellular_HSPA"; 
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "Cellular_HSUPA"; 
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "Cellular_UMTS"; 
            case TelephonyManager.NETWORK_TYPE_EHRPD: 
                return "Cellular_EHRPD"; 
            case TelephonyManager.NETWORK_TYPE_EVDO_B: 
                return "Cellular_EVDO_B"; 
            case TelephonyManager.NETWORK_TYPE_HSPAP: 
                return "Cellular_HSPAP"; 
            case TelephonyManager.NETWORK_TYPE_IDEN: 
                return "Cellular_IDEN"; 
            case TelephonyManager.NETWORK_TYPE_LTE: 
                return "Cellular_LTE"; 
            // Unknown
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            	return "Cellular_Unkown";
            default:
                return "Unkown";
            }
        }else{
            return "Unkown";
        }
	}
	
	void outputDeviceInfo(){
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  String device= Build.DEVICE;
		  String versioncodename= Build.VERSION.RELEASE;
		  int sdkint= Build.VERSION.SDK_INT;
		  String sdkname="";
		  String deviceip=getLocalIpAddress();
		  switch(sdkint){
	          case 1:
	        	  sdkname="BASE"; break;
	          case 2:
	        	  sdkname="BASE_1_1";break;
	          case 3:
	        	  sdkname="CUPCAKE"; break;
	          case 10000:
	        	  sdkname="CUR_DEVELOPMENT";break;
	          case 4:
	        	  sdkname="DONUT"; break;
	          case 5:
	        	  sdkname="ECLAIR";break;
	          case 6:
	        	  sdkname="ECLAIR_0_1";break;
	          case 7:
	        	  sdkname="ECLAIR_MR1"; break;
	          case 8:
	        	  sdkname="FROYO"; break;
	          case 9:
	        	  sdkname="GINGERBREAD"; break;
	          case 10: 
	        	  sdkname="GINGERBREAD_MR1"; break;
	          case 11: 
	        	  sdkname="HONEYCOMB"; break;
	          case 12: 
	        	  sdkname="HONEYCOMB_MR1"; break;
	          case 13: 
	        	  sdkname="HONEYCOMB_MR2"; break;
	          case 14: 
	        	  sdkname="ICE_CREAM_SANDWICH"; break;
	          case 15:
	        	  sdkname="ICE_CREAM_SANDWICH_MR1";	break;
	          case 16:
	        	  sdkname="JELLY_BEAN";	 break;
	          case 17:
	        	  sdkname="JELLY_BEAN_MR1";	 break;
	          case 18:
	        	  sdkname="JELLY_BEAN_MR2";		break; 
	          case 19:
		           sdkname="KITKAT";        break;   	
	          default:
	        	  sdkname="Unkown";
          };
          
		  try {
				File file = new File(outputfoldername+"/"+deviceinfofilename);
		    	file.getParentFile().mkdirs();
				
				if (!file.exists()){			
					file.createNewFile();
				}
				
				BufferedWriter fop = new BufferedWriter(new FileWriter(file,false));
	 			fop.write("Device IP="+deviceip+"\n");
	 			fop.write("Manufacturer="+manufacturer+"\n");
	 			fop.write("Model="+model+"\n");
	 			fop.write("Industrial Design="+device+"\n");
	 			fop.write("Version="+versioncodename+"\n");
	 			fop.write("SDK="+sdkname+"("+sdkint+")\n");
	 			
	 			
	 			fop.close();
	    	}
  		catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		      }
    }
	
	public String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    String ip = Formatter.formatIpAddress(inetAddress.hashCode());
	                    Log.i(TAG, "***** IP="+ ip);
	                    return ip;
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        Log.e(TAG, ex.toString());
	    }
	    return null;
	}
	
	


}
