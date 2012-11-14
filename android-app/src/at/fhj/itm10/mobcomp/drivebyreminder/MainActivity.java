package at.fhj.itm10.mobcomp.drivebyreminder;

import android.os.Bundle;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

public class MainActivity extends RoboSherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//getMenuInflater().inflate(R.menu.activity_main, menu);
	}

}
