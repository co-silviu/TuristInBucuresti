package ro.turist.bucharest.free.util;

import ro.turist.bucharest.free.R;
import ro.turist.bucharest.free.common.StepIndexSingleton;
import ro.turist.bucharest.free.common.TiBConstants;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class FullScreenImage extends Activity implements OnClickListener,
		TiBConstants {
	StepIndexSingleton stepIndex;

	protected ImageView imageFull;

	protected void onCreate(Bundle savedInstanceState) {
		// Log.d("FullScreenImage", "setez layout .. ");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_image);
		// Log.d("FullScreenImage", "Am setat layout .. ");

		imageFull = (ImageView) findViewById(R.id.fullImage);
		imageFull.setOnClickListener(this);

		Intent intent = getIntent();
		// Log.d("FullScreenImage", " data: " + intent.getDataString());
		stepIndex = StepIndexSingleton.getInstance();
		int countIndex = stepIndex.getStepCount();
		String prefixF = TIB_PREFIX + countIndex + ".foto";
		// Log.d("FullScreenImage", " search : " + prefixF);
		// Log.i(LOGID, " s-a cautat: " + prefixF);
		int resId = this.getResources().getIdentifier(prefixF, "string",
				this.getPackageName());
		String imageName = this.getString(resId);
		// Log.d("FullScreenImage", " imageName : " + imageName);
		int imageId = this.getResources().getIdentifier(imageName, "drawable",
				this.getPackageName());

		imageFull.setImageResource(imageId);
		imageFull.setScaleType(ImageView.ScaleType.FIT_XY);

	}

	public void onClick(View v) {
		// Log.i(LOGID, "in onClick - view: " + v+ "click - countIndex: " +
		// countIndex);

		if (v == imageFull) {
			//Log.i("FullScreenImage", " s-a apasat imaginea ");
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
		}

	}
}