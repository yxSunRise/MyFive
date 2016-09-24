package com.example.myfive;

import static com.example.myfive.Configure.humanType;
import static com.example.myfive.Configure.searchDeep;
import static com.example.myfive.Configure.volume;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


public class RenjiAty extends Activity implements OnTouchListener {
	private Chessboard chessboard;
	private GameView gv;
	private ImageView iv;
	private AIplayer ai;
	private Handler handle;
	private Animation anim;
	private TextView tv;
	private Handler texthandle;
	private Timer timer;
	private TimerTask timertask;
	private int textCount=0;
	private int diaCount=0;
	private int busy=0;
	private Thread thread;
	private Double col_temp;
	private Double row_temp;
	private int humanType_temp;
	private AlertDialog dia;
	private View v_common;
	private ImageView iv_common;
	private TextView tv_common;
	private OnClickListener clistener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId())
			{
			case R.id.newgame:
				if((col_temp-col_temp.intValue()==0) &&
						(row_temp-row_temp.intValue()==0)&&
						(col_temp<=80 && row_temp<=80 )&&
						(col_temp >= 5 || row_temp >= 5)){
						Configure.row=row_temp.intValue();
						Configure.col=col_temp.intValue();
				}
				humanType=humanType_temp;
				
				chessboard=new Chessboard(Configure.col, Configure.row);
				gv.setChessboard(chessboard);
				ai=new AIplayer(chessboard, humanType*(-1));
				
				if(humanType==WHITE){
					Aithink(false);
				}		
				else if(busy==1){
					/**
					 * shut down ai
					 * **/
				}else
					gv.invalidate();
				break;
			case R.id.back:
				if(busy==1){
					/**
					 * shut down ai
					 * **/
				}
				else {
					chessboard.back(2);
					gv.invalidate();
				}
				break;
			case R.id.prompt:
				
				if(busy==0){
					Aithink(true);
				}
					break;
			case R.id.setting:
				eventSetting();
				break;
			default:break;
			}
		}
	};
	
	private final int BLACK = 1;
	private final int WHITE = -1;
	private final int NONE = 0;
	
	@Override
	protected void onStop() {
		if((col_temp-col_temp.intValue()==0) &&
				(row_temp-row_temp.intValue()==0)&&
				(col_temp<=80 && row_temp<=80 )&&
				(col_temp >= 5 || row_temp >= 5)){
				Configure.row=row_temp.intValue();
				Configure.col=col_temp.intValue();
		}
		Configure.humanType=humanType_temp;
		super.onStop();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.renjiaty);
		
		
		
		col_temp=(double) Configure.col;
		row_temp=(double) Configure.row;
		humanType_temp=Configure.humanType;
		
		findViewById(R.id.newgame).setOnClickListener(clistener);
		findViewById(R.id.back).setOnClickListener(clistener);
		findViewById(R.id.prompt).setOnClickListener(clistener);
		findViewById(R.id.setting).setOnClickListener(clistener);
		
		chessboard=new Chessboard(Configure.col, Configure.row);
		
		ai=new AIplayer(chessboard, humanType*(-1));
		
		anim=AnimationUtils.loadAnimation(this,R.anim.rotate_anim);
		anim.setInterpolator(new LinearInterpolator());
		
		
		
		gv=(GameView) findViewById(R.id.gv);
		gv.setChessboard(chessboard);
		
		gv.setOnTouchListener(this);
		
		iv=(ImageView) findViewById(R.id.imageView1);
		tv=(TextView) findViewById(R.id.textView1);
		
		if(humanType==WHITE){
			Aithink(false);
		}

		texthandle=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case 0:tv.setText("Thinking   ");break;
				case 1:tv.setText("Thinking.  ");break;
				case 2:tv.setText("Thinking.. ");break;
				case 3:tv.setText("Thinking...");break;
				default:break;
				}
				super.handleMessage(msg);
			}
		};
		handle=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				stoptask();
				busy=0;
				iv.clearAnimation();
				iv.setImageResource(R.drawable.ai_player);
				tv.setText("  AI player");
				gv.invalidate();
				if(Estimate.hasfive(new Point(msg.arg1, msg.arg2), chessboard)){
					if(!(Boolean) msg.obj){
						v_common= LayoutInflater.from(RenjiAty.this).inflate(R.layout.dia_common, null);
						iv_common=(ImageView) v_common.findViewById(R.id.iv_dia_common);
						tv_common=(TextView) v_common.findViewById(R.id.tv_dia_common);
						tv_common.setText("电脑获胜，请再接再厉 ");
						iv_common.setImageResource(R.drawable.shibai);
						new AlertDialog.Builder(RenjiAty.this).setView(v_common).show();
					}else{
						v_common= LayoutInflater.from(RenjiAty.this).inflate(R.layout.dia_common, null);
						iv_common=(ImageView) v_common.findViewById(R.id.iv_dia_common);
						tv_common=(TextView) v_common.findViewById(R.id.tv_dia_common);
						tv_common.setText("恭喜你获胜 ");
						iv_common.setImageResource(R.drawable.shengli);
						new AlertDialog.Builder(RenjiAty.this).setView(v_common).show();
					}
				}else if((Boolean) msg.obj){
					Aithink(false);
				}
				super.handleMessage(msg);
			}
		};
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(busy==0){
			float x = event.getX();
			float y = event.getY();
			int c = Math.round((x-gv.pad_left)/gv.len);
			int r = Math.round((y-gv.pad_top)/gv.len);
			if((r>=0 && r<Configure.row && c>=0 && c<Configure.col)&&
				(chessboard.getType(new Point(c, r))==NONE)){
							
				chessboard.setChess(new Point(c, r),humanType);
				
				gv.invalidate();
				
				if(Estimate.hasfive(new Point(c, r), chessboard)){
					v_common= LayoutInflater.from(RenjiAty.this).inflate(R.layout.dia_common, null);
					iv_common=(ImageView) v_common.findViewById(R.id.iv_dia_common);
					tv_common=(TextView) v_common.findViewById(R.id.tv_dia_common);
					tv_common.setText("恭喜你获胜 ");
					iv_common.setImageResource(R.drawable.shengli);
					new AlertDialog.Builder(RenjiAty.this).setView(v_common).show();
				}
				else {
					Aithink(false);
				}
			}
			//humanType=humanType*(-1);
		}
		return false;
	}
	private void statask(){
		if(timer==null){
			timer=new Timer();
			timertask=new TimerTask() {
			@Override
			public void run() {
				textCount=++textCount%4;
				texthandle.sendEmptyMessage(textCount);
				}
			};
		timer.schedule(timertask,0, 400);
		}
	}
	private void stoptask(){
		if(timer!=null){
			tv.setText("  AI player");
			timertask.cancel();
			timer.cancel();
			timer=null;
			timertask=null;
		}
	}
	private void Aithink(final boolean isPromote){
		iv.setImageResource(R.drawable.thinking);
		iv.startAnimation(anim);
		statask();
		thread=null;
		thread=new Thread(){
			public void run() {
				if(isPromote)
					ai.reverseType();
				busy=1;
				Point p = ai.move(searchDeep);
				if(isPromote)
					ai.reverseType();
				Message msg=new Message();
				msg.arg1=p.x;
				msg.arg2=p.y;
				msg.obj=isPromote;
				handle.sendMessage(msg);
			};
		};
		thread.start();
	}
	private void eventSetting(){
		/**
		 * 创建并初始化对话框View
		 * **/
		View vv = LayoutInflater.from(RenjiAty.this).inflate(R.layout.renji_setting, null);
		final Spinner sp=(Spinner) vv.findViewById(R.id.spinner1);
		final TextView tv1=(TextView) vv.findViewById(R.id.renji_setting_tv_recommend);
		final ImageView iv=(ImageView) vv.findViewById(R.id.renji_setting_iv_heixian);
		ArrayAdapter<Integer> adapter=new ArrayAdapter<Integer>(RenjiAty.this, android.R.layout.simple_spinner_item);
		final EditText etrow=(EditText) vv.findViewById(R.id.etrow);
		final EditText etcol=(EditText) vv.findViewById(R.id.etcol);
		final SeekBar sb=(SeekBar) vv.findViewById(R.id.seekBar1);
		final RadioButton radHuman=(RadioButton) vv.findViewById(R.id.radio_huaman);
		final RadioButton radAi= (RadioButton) vv.findViewById(R.id.radio_ai);
		if(humanType_temp==1){
			radHuman.setChecked(true);
			radAi.setChecked(false);
		}else{
			radHuman.setChecked(false);
			radAi.setChecked(true);
		}
		sb.setProgress(volume);
		etcol.setText(col_temp.intValue()+"");
		etrow.setText(row_temp.intValue()+"");
		adapter.add(1);
		adapter.add(2);
		adapter.add(3);
		adapter.add(4);
		adapter.add(5);
		sp.setAdapter(adapter);
		sp.setSelection(searchDeep-1);
		sp.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg2<3){
					tv1.setText(null);
					iv.setImageResource(0);
				}else{
					tv1.setText("建议设置在4以下");
					iv.setImageResource(R.drawable.heixian);
				}
				
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		/***********************************************************/
		
	
		dia=new AlertDialog.Builder(RenjiAty.this).
		setView(vv).
		setTitle("游戏设置").
		setIcon(android.R.drawable.ic_menu_manage).
		setNegativeButton("取消", null).
		setPositiveButton("保存",new DialogInterface.OnClickListener(){
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int chessNum=chessboard.chessNum();
				searchDeep=(Integer) sp.getSelectedItem();
				col_temp=Double.parseDouble(etcol.getText().toString());
				row_temp =Double.parseDouble(etrow.getText().toString());
				if((col_temp-col_temp.intValue()!=0) ||
						(row_temp-row_temp.intValue()!=0)||
						(col_temp>80 || row_temp>80 )||
						(col_temp < 5 && row_temp < 5)){
					diaCount++;
					
					v_common= LayoutInflater.from(RenjiAty.this).inflate(R.layout.dia_common, null);
					iv_common=(ImageView) v_common.findViewById(R.id.iv_dia_common);
					tv_common=(TextView) v_common.findViewById(R.id.tv_dia_common);
					switch (diaCount) {
					case 1:
						tv_common.setText(String.format("本程序也是很有原则的\n   不接受无理的数据"));
						iv_common.setImageResource(R.drawable.yi);
						tv_common.setPadding(200, 0, 0, 0);
						iv_common.setPadding(0, 10, 0, 0);
						break;
						
					case 2:
						tv_common.setText(String.format("本程序也是很有原则的\n   不要拿来随意调戏"));
						iv_common.setImageResource(R.drawable.er);
						tv_common.setPadding(200, 0, 0, 0);
						iv_common.setPadding(0, 10, 0, 0);
						break;
						
					case 3:
						v_common= LayoutInflater.from(RenjiAty.this).inflate(R.layout.dia_three, null);
						break;

					default:
						break;
					}
					
					new AlertDialog.Builder(RenjiAty.this).setView(v_common).setTitle("  ").
					setIcon(android.R.drawable.stat_sys_warning).
					setPositiveButton(diaCount!=3?"重新设置":"确定",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(diaCount==3) diaCount=0;
							else dia.show();
							
						}
					}).show();
					
				}else
					diaCount=0;
				
				
				volume=sb.getProgress();
				if(radHuman.isChecked() && !radAi.isChecked()){
					humanType_temp=BLACK;
				}else if(!radHuman.isChecked() && radAi.isChecked()){
					humanType_temp=WHITE;
				}
				if(chessNum==0){
					clistener.onClick(findViewById(R.id.newgame));
				}
			}
		}).show();
	}
}
