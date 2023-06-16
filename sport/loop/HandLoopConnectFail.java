package com.desay.sport.loop;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.desay.pstest.toast.showToast;
import com.desay.sport.data.PublicUtils;
import com.desay.sport.data.SportData;
import com.desay.sport.db.sportDB;
import com.desay.sport.main.BuildConfig;
import com.desay.sport.main.MainTab;
import com.desay.sport.main.R;
import com.desay.sport.slidepage.MoveBg;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HandLoopConnectFail extends Activity implements OnClickListener
{
	String tag = "Loop2Activity";
	ImageView iv_return = null;
	TextView title = null;
	TextView tab_front = null;	
	Button btn_connect = null;	
	private BroadcastReceiver receiver=null;
	public ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.hand_loop_fail);
		(new sportDB(HandLoopConnectFail.this)).delIwanMac(SportData.getUserName(HandLoopConnectFail.this));
		initView();
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(GattUtils.CONNECTE_SUCCESS);
		intentFilter.addAction(GattUtils.CONNECTE_FAILD);
		intentFilter.addAction(BluetoothLeService.CHANGE_ACTIVITY);
		receiver=new ReadInfoBroadcast();
		registerReceiver(receiver, intentFilter);
		progressDialog=new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(getString(R.string.bluetooth_connecting));
	}
	
	private void initView()
	{
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_connect.setOnClickListener(this);
		title = (TextView) findViewById(R.id.tv_title);
		title.setText(getString(R.string.loop_monitor));
		iv_return = (ImageView) findViewById(R.id.iv_return);
		iv_return.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onBackPressed();
			}
		});

	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_connect:			
					//2.1开始连接蓝牙的代码							
					if(!progressDialog.isShowing())
					{
						progressDialog.setMessage(getString(R.string.bluetooth_connecting));
						progressDialog.show();
					}	
					BluetoothService.resetThread_TimeOut();
					Intent i=new Intent();
					i.setAction(MainTab.START_SCAN);
					sendBroadcast(i);
				break;
		}
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		sendBroadcast(new Intent(MainTab.BOX_REMOVEBOND));
		super.onBackPressed();
	}
	
	class ReadInfoBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			if(action.equals(GattUtils.CONNECTE_FAILD)){
				if(progressDialog!=null && progressDialog.isShowing())
				progressDialog.dismiss();
				if(intent.getBooleanExtra(SportData.IFFOUNDED, true))
				{
					showToast.normaltoast(HandLoopConnectFail.this,getString(R.string.bluetooth_failure),showToast.TWO_SECOND);
				}
				else
				{
					showToast.normaltoast(HandLoopConnectFail.this,getString(R.string.bluetooth_failure_connect),showToast.TWO_SECOND);
				}
			}else if(action.equals(GattUtils.CONNECTE_SUCCESS))
			{
				if(progressDialog.isShowing())
					progressDialog.setMessage(getString(R.string.loop_confirm));
			}
			else if(action.equals(BluetoothLeService.CHANGE_ACTIVITY)){
				if(progressDialog.isShowing())
					progressDialog.dismiss();
				MainTab.ifConnect=true;
				startActivity(new Intent(HandLoopConnectFail.this,AddLoopActivity.class));
				finish();
			}
		}
		
	}	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		BluetoothService.resetThread_TimeOut();
		unregisterReceiver(receiver);
		super.onDestroy();
	}
	
}
		