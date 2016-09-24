package com.example.myfive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

public class MainActivity extends Activity {
	OnClickListener ll= new OnClickListener(){
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button1: startActivity(new Intent(MainActivity.this, RenjiAty.class));
				break;
			case R.id.button2:
				break;
			case R.id.button3:
				break;
			default:break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager wm = this.getWindowManager();
	    Configure.scrWidth = wm.getDefaultDisplay().getWidth();
	    Configure.scrHeight = wm.getDefaultDisplay().getHeight();
	    
		setContentView(R.layout.activity_main);
		findViewById(R.id.button1).setOnClickListener(ll);
		findViewById(R.id.button2).setOnClickListener(ll);
		findViewById(R.id.button3).setOnClickListener(ll);
		
	}
}
