package com.heibai.modeloclock;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.heibai.modeloclock.view.MyAnalogClock;

public class MainActivity extends Activity {

	private LinearLayout mContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContainer = (LinearLayout) findViewById(R.id.ll_container);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300,
				300);
		MyAnalogClock mClock1 = new MyAnalogClock(this);
		mContainer.addView(mClock1, params);

		MyAnalogClock mClock2 = new MyAnalogClock(this);
		mClock2.setmDial(getResources().getDrawable(R.drawable.clock_dail_1));
		mContainer.addView(mClock2, params);
		MyAnalogClock mClock3 = new MyAnalogClock(this);
		mClock3.setmDial(getResources().getDrawable(R.drawable.clock_dail_2));
		mContainer.addView(mClock3, params);

	}

}
