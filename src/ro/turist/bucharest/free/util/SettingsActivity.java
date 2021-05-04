package ro.turist.bucharest.free.util;

import ro.turist.bucharest.free.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {

	protected Button textPlus;
	protected Button textMinus;
	protected Button backFromSettBtn;
	protected SeekBar textSizeBar;

	// protected TextView infoStepLabel;
	protected TextView settingsText;

	protected float textSize = 0;
	
	public static final String PREFS_NAME = "MyPrefShare";

	protected void onCreate(Bundle savedInstanceState) {
		//Log.d("SettingsActivity", "onCreate.. ");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_menu);

		textPlus = (Button) findViewById(R.id.plusText);
		textMinus = (Button) findViewById(R.id.minusText);
		backFromSettBtn = (Button) findViewById(R.id.backFromSettBtn);
		settingsText = (TextView) findViewById(R.id.settingsText);
		textSizeBar = (SeekBar)findViewById(R.id.textSizeBar);

		textPlus.setOnClickListener(this);
		textMinus.setOnClickListener(this);
		backFromSettBtn.setOnClickListener(this);
		textSizeBar.setOnSeekBarChangeListener(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			
			try {
				//String value = extras.getFloat("textSize");
				textSize = extras.getFloat("textSize");
				//Log.d("SettingsActivity", "value passed: " + textSize);
			} catch (Exception e) {
				//Log.d("SettingsActivity", "err" + e.getMessage());
			}
		}
		textSizeBar.setProgress((int)(textSize));
		showText("");
	}

	@Override
	public void onClick(View v) {
		//Log.d("SettingsActivity", "onClick.. ");
		// float sizeText = infoStepLabel.getTextSize();
		//Log.d("SettingsActivity", "sizeText now:  " + textSize);
		if (v == textPlus) {
			//Log.d("SettingsActivity", "plus text.. ");
			if (textSize < 30) {
				textSize++;
				showText("");
			} else {
				showText("Marime text prea mare");
			}

		} else if (v == textMinus) {
			//Log.d("SettingsActivity", "minus.. ");
			if (textSize > 5) {
				// infoStepLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX,
				// sizeText - 1);
				textSize --;
				showText("");
			} else {
				showText("Marime text prea mica");
			}

		} else if (v == backFromSettBtn) {
			//Log.d("SettingsActivity", "back.. ");
//			
			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putFloat("textSize", textSize);
		    // Commit the edits!
		    editor.commit();
			
			//Log.i("SettingsActivity", " s-a apasat back ");
			Intent intent = new Intent();
			intent.putExtra("textSize", textSize);
			setResult(RESULT_OK, intent);
			finish();
			
			//finishActivity(RESULT_OK);
		}

	}

	private void showText(String addText) {
		//Log.d("SettingsActivity", "in show text: "+addText);
		String text = ""+ (int)(textSize + 5);

//		try {
//			//int resTextId = this.getResources().getIdentifier("font_size", "string", this.getPackageName());			
//			//String testText = this.getString(resTextId);
//			// textSize = infoStepLabel.getTextSize();
//			// textSize = ""+textSize;
//			//text = testText + (int)textSize + " \n " + addText;
//			text = ""+ (int)textSize;
//
//		} catch (Exception e) {
//			Log.e("SettingsActivity", "Eroare: " + e.getMessage());
//			//text = "Mesaj text in err: " + (int)textSize + " \n " + addText;
//		}
		
		settingsText.setText(text);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean arg2) 
	{
	 // Log.d("SettingsActivity", "onProgressChanged - progress: " + progress+" - arg2: "+arg2);
	  if(seekBar == textSizeBar) 
	  {
		 if (progress == 0)
		 {
			 textSize = (float)(progress + 1);
			 settingsText.setText(""+(int)(textSize+4));
		 }
		 else
		 {
			 textSize = (float)(progress);
			 settingsText.setText(""+(int)(textSize+5));
		 }
		 	
		 //int resTextId = this.getResources().getIdentifier("font_size", "string", this.getPackageName());
   		 //String testText = this.getString(resTextId);
		 
	  }
	  
	  //asa fac un alert dialog
//	  AlertDialog.Builder dialog = new AlertDialog.Builder(this);	
//	  dialog.setTitle("titlu mesaj");
//	  dialog.setMessage("text size: "+(int)textSize);
//	  dialog.show();

	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

@Override
	public void onBackPressed() {
	   //Log.d(LOGID, "onBackPressed Called");
	   Intent setIntent = new Intent(Intent.ACTION_MAIN);
	   setIntent.addCategory(Intent.CATEGORY_HOME);
	   setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	   startActivity(setIntent);
	}

}
