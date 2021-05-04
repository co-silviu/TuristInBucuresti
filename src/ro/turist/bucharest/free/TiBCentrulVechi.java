/**
 *   GNU LGPL information
  --------------------

    This project is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ro.turist.bucharest.free;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import ro.turist.bucharest.free.common.StepIndexSingleton;
import ro.turist.bucharest.free.common.TiBConstants;
import ro.turist.bucharest.free.util.FullScreenImage;
import ro.turist.bucharest.free.util.SettingsActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Silviu
 * 
 */
public class TiBCentrulVechi extends Activity implements OnClickListener,
		OnItemSelectedListener, OnInitListener, TiBConstants {

	private int MY_DATA_CHECK_CODE = 0;
	private TextToSpeech tts;

	private static int countIndex = 0;
	private static int stepMax = 3;
	protected Button goToBtn;
	protected Button backBtn;
	protected Button nextBtn;
	protected Button backBtnMenu;
	protected Button goUpBtn;
	// protected Button speakBtn;
	protected Button rateBtn;
	protected TextView infoStepLabel;
	protected ScrollView scroolView;
	protected Spinner stepSpinner;
	protected ImageView imageInfo;
	protected View layoutInfo;
	protected View layoutTopSpin;
	protected View layoutBtn3;
	protected View layoutBtn2;
	protected View layoutRoot;

	protected int displayWidth;
	protected int displayHeight;
	protected Boolean initialDisplay = true;

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;

	// private static final String LOGID = "TiBCentrulVechi";
	private static HashMap<String, String> stepNamesHash = null;
	public static final String PREFS_NAME = "MyPrefShare";

	View.OnTouchListener gestureListener;
	StepIndexSingleton stepIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// Log.i(LOGID, "in onCreate..");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initializeUI();

		// Gesture detection
		gestureDetector = new GestureDetector(new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		setListenerGesture(infoStepLabel);
		setListenerGesture(scroolView);
		setListenerGesture(imageInfo);
		setListenerGesture(layoutInfo);
		setListenerGesture(layoutTopSpin);
		setListenerGesture(layoutBtn3);
		setListenerGesture(layoutBtn2);
		setListenerGesture(layoutRoot);

	}

	private void setListenerGesture(View view) {
		// Log.(LOGID, "+++__++++=== Gesture..  view id: "+view.getId());
		view.setOnClickListener(TiBCentrulVechi.this);
		view.setOnTouchListener(gestureListener);
	}

	/**
	 * 
	 * Swipe screen to left/right
	 * 
	 */
	class MyGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// Log.i(LOGID, "onFling .. countIndex: "+countIndex);

			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;

				// right to left swipe
				if ((e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math
						.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
						&& countIndex < stepMax) {
					countIndex = stepIndex.getIncreasedStepCount();
					updateInterface();
				} else if ((e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math
						.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
						&& countIndex > 0) {
					countIndex = stepIndex.getDecreasedStepCount();
					updateInterface();
				}
			} catch (Exception e) {
				// nothing
			}

			return false;
		}
	}

	/**
	 * Initialize interface
	 */
	public void initializeUI() {
		// Log.i(LOGID, "in InitializeUI..");

		stepIndex = StepIndexSingleton.getInstance();
		countIndex = stepIndex.getStepCount();

		goToBtn = (Button) findViewById(R.id.goToBtn);
		backBtn = (Button) findViewById(R.id.backBtn);
		nextBtn = (Button) findViewById(R.id.nextBtn);
		goUpBtn = (Button) findViewById(R.id.goUpBtn);
		// speakBtn = (Button) findViewById(R.id.speakBtn);
		rateBtn = (Button) findViewById(R.id.rateBtn);
		backBtnMenu = (Button) findViewById(R.id.backBtnMenu);
		infoStepLabel = (TextView) findViewById(R.id.infoStep);
		stepSpinner = (Spinner) findViewById(R.id.dropdown_list);
		scroolView = (ScrollView) findViewById(R.id.scroolView);
		imageInfo = (ImageView) findViewById(R.id.imageInfo);
		layoutInfo = (View) findViewById(R.id.layoutInfo);
		layoutTopSpin = (View) findViewById(R.id.layoutTopSpin);
		layoutBtn3 = (View) findViewById(R.id.layoutBtn3);
		layoutBtn2 = (View) findViewById(R.id.layoutBtn2);
		layoutRoot = (View) findViewById(R.id.layoutRoot);

		goToBtn.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		goUpBtn.setOnClickListener(this);
		// speakBtn.setOnClickListener(this);
		rateBtn.setOnClickListener(this);
		imageInfo.setOnClickListener(this);
		backBtnMenu.setOnClickListener(this);

		infoStepLabel.setVisibility(BIND_AUTO_CREATE);
		infoStepLabel.setMovementMethod(LinkMovementMethod.getInstance());

		// scroolView.setOnKeyListener(this);
		scroolView.setFocusable(true);

		selectStep();

		// get display width and height
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		displayWidth = display.getWidth();
		displayHeight = display.getHeight();
		// Log.i(LOGID, "displayWidth:" + displayWidth + " - displayHeight: " +
		// displayHeight);

		stepMax = getStepMaxProps();

		updateInterface();
	}

	/**
	 * Create menu option
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Log.i(LOGID, "in onCreateOptionsMenu.. ");

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Check what menu button was pressed
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		// Log.i(LOGID, "in onOptionsItemSelected.. item: " + item.getItemId());

		switch (item.getItemId()) {

		case R.id.about: {
			backBtnMenu.setVisibility(View.VISIBLE);
			imageInfo.setVisibility(View.GONE);
			layoutBtn3.setVisibility(View.GONE);
			infoStepLabel.setText("\n \t" + getStringRes("TIB.about"));
			return true;
		}
		case R.id.source: {
			backBtnMenu.setVisibility(View.VISIBLE);
			imageInfo.setVisibility(View.GONE);
			layoutBtn3.setVisibility(View.GONE);
			infoStepLabel.setText("\n \t" + buildSourceList());
			scrollToTop();
			return true;
		}
		case R.id.settings: {
			// Log.i(LOGID, "s-a ales settings");
			Intent settingsIntent = new Intent(layoutInfo.getContext(),
					SettingsActivity.class);
			settingsIntent.putExtra("textSize", infoStepLabel.getTextSize());
			final int result = 1;
			startActivityForResult(settingsIntent, result);
			return true;

		}
		case R.id.mail: {
			backBtnMenu.setVisibility(View.VISIBLE);
			imageInfo.setVisibility(View.GONE);
			layoutBtn3.setVisibility(View.GONE);
			sendFeedback();
			scrollToTop();
			return true;
		}
		case R.id.speak: {
			//Log.i("Tib", "speak..");
			
			try {
				Intent checkIntent = new Intent();
				checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
				
				backBtnMenu.setVisibility(View.VISIBLE);
				imageInfo.setVisibility(View.GONE);
				layoutBtn3.setVisibility(View.GONE);
	
				String text = getTextSpeak();
	
				if (text != null && text.length() > 0) 
				{
					Toast.makeText(TiBCentrulVechi.this, "Reading ..",
							Toast.LENGTH_SHORT).show();
					try {
						tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
					} catch (Exception e) {
						Toast.makeText(TiBCentrulVechi.this,
								"TextToSpeech " + getStringRes("tts_error") + "..", Toast.LENGTH_SHORT).show();
					}
				}
				
			}catch (Exception e) {
				Toast.makeText(TiBCentrulVechi.this,
						"TextToSpeech " + getStringRes("tts_error") + "..", Toast.LENGTH_SHORT).show();
			}

			backBtnMenu.setVisibility(View.GONE);
			imageInfo.setVisibility(View.VISIBLE);
			layoutBtn3.setVisibility(View.VISIBLE);

			return true;
			
		}

		case R.id.exit: {
			this.finish();
			return true;
		}
		}

		return super.onOptionsItemSelected(item);

	}

	/**
	 * 
	 * @return number of steps from properties file
	 */
	private int getStepMaxProps() {
		int stepMaxProps = 1;
		String stepMaxProp = getPropValByKey("tib.cv.step.max");
		if (stepMaxProp != null && !"".equalsIgnoreCase(stepMaxProp.trim())) {
			stepMaxProps = Integer.parseInt(stepMaxProp);
		}

		return stepMaxProps;
	}

	/**
	 * check which item is pressed
	 */
	@Override
	public void onClick(View v) {
		// Log.i(LOGID, "in onClick - view: " + v+ "click - countIndex: " +
		// countIndex);

		nextBtn.setVisibility(View.VISIBLE);
		backBtn.setVisibility(View.VISIBLE);
		layoutTopSpin.setVisibility(View.GONE);
		v.setPressed(true);

		if (v == goToBtn) {
			// Log.i(LOGID, " s-a apasat go to  "+ countIndex);
			if (countIndex == 0) {
				backBtn.setVisibility(View.GONE);
			} else {
				backBtn.setVisibility(View.VISIBLE);
			}

			layoutTopSpin.setVisibility(View.VISIBLE);
			stepSpinner.performClick();
			// updateInterface();

		} else if (v == backBtn) {
			// Log.i(LOGID, " s-a apasat back " + countIndex);
			if (countIndex > 0) {
				countIndex = stepIndex.getDecreasedStepCount();
			}
			updateInterface();

		} else if (v == nextBtn) {
			// Log.i(LOGID, " s-a apasat next " + countIndex);
			if (countIndex < stepMax) {
				countIndex = stepIndex.getIncreasedStepCount();
			}
			updateInterface();
		} else if (v == goUpBtn) {
			// Log.i(LOGID, " s-a apasat up " + countIndex);
			layoutTopSpin.setVisibility(View.GONE);
			scrollToTop();
			updateInterface();

		} else if (v == imageInfo) {

			String prefixF = TIB_PREFIX + countIndex + ".foto";
			String imagePath = getStringRes(prefixF);
			int resId = this.getResources().getIdentifier(imagePath,
					"drawable", this.getPackageName());
			if (resId != 0) {
				try {
					Intent fullScreenIntent = new Intent(v.getContext(),
							FullScreenImage.class);
					fullScreenIntent.putExtra(TiBCentrulVechi.class.getName(),
							resId);
					startActivity(fullScreenIntent);
				} catch (Exception e) {
					// Log.e(LOGID, e.getMessage());
				}

			}

		} else if (v == backBtnMenu) {

			// Log.i(LOGID, " s-a apasat backBtnMenu din menu " + countIndex);
			backBtnMenu.setVisibility(View.GONE);
			layoutBtn3.setVisibility(View.VISIBLE);
			updateInterface();
			// }
			// else if (v == speakBtn) {
			// String text = getTextSpeak();
			// if (text != null && text.length() > 0) {
			// Toast.makeText(TiBCentrulVechi.this, "Reading ..",
			// Toast.LENGTH_SHORT).show();
			// try
			// {
			// tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
			// }catch (Exception e) {
			// Toast.makeText(TiBCentrulVechi.this,
			// "TextToSpeech "+getStringRes("tts_error")+ "..",
			// Toast.LENGTH_SHORT).show();
			// }
			// }

		} else if (v == rateBtn) {
			// Log.i(LOGID, " s-a apasat rateBtn din menu " + countIndex);
			launchMarket();

		} else {
			// Log.i(LOGID, " s-a apasat altceva view id: " + v.getId() +
			// " - countIndex: " + countIndex);
			updateInterface();
		}

		scrollToTop();

	}

	/**
	 * Launch Google Play Market for Rate
	 */
	private void launchMarket() {
		// Log.i(LOGID, "launchMarket ");

		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(R.string.rate)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent marketIntent = new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://market.android.com/details?id="
												+ getPackageName()));
								marketIntent
										.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(marketIntent);
								finish();
							}
						}).setNegativeButton(R.string.cancel, null).show();
	}

	/**
	 * 
	 * @param contentUri
	 * @return
	 */
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);

		if (cursor == null)
			return null;

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * 
	 * 
	 */
	private String getTextSpeak() {
		String prefix = TIB_PREFIX + countIndex;
		String sName = prefix + ".name";
		String sInfo = prefix + ".info";
		return getStringRes(sName) + ". " + getStringRes(sInfo);
	}

	/**
	 * Update the interface based on countIndex step
	 */
	public void updateInterface() {
		// Log.i(LOGID, "updateInterface - countIndex: " + countIndex);

		try {

			// citesc valorile din strings
			String prefix = TIB_PREFIX + countIndex;
			String sName = prefix + ".name";
			String sInfo = prefix + ".info";
			String sFoto = prefix + ".foto";
			// Log.i(LOGID, " Caut sName:" + sName +
			// " - sInfo:" + sInfo + " - sSource: " + sSource + " - sFoto:" +
			// sFoto);

			String sFotoVal = "";
			String sNameVal = "";
			String sInfoVal = "";

			if (countIndex >= 0 && countIndex <= stepMax) {

				sNameVal = getStringRes(sName);
				sInfoVal = "\n\t\t" + sNameVal + " \n \n\t"
						+ getStringRes(sInfo) + " \n ";
				// String sourceVal = getStringRes(sSource);
				//
				// // daca am si sursa in fisier atunci o afisez
				// if (sourceVal != null &&
				// !"".equalsIgnoreCase(sourceVal.trim())) {
				// sInfoVal = sInfoVal + " \n "
				// + this.getString(R.string.source) + ": "
				// + sourceVal;
				// }

				sFotoVal = getStringRes(sFoto);

			}

			// setez poza
			int resId = this.getResources().getIdentifier(sFotoVal, "drawable",
					this.getPackageName());
			// Log.i(LOGID, " fotoFile: " + sFotoVal + " for resId: " + resId);

			if (resId == 0) {
				imageInfo.setVisibility(View.GONE);
			} else {
				imageInfo.setVisibility(View.VISIBLE);
				imageInfo.setImageResource(resId);

				// Log.i(LOGID, "imageInfo.getHeight(): "+
				// imageInfo.getHeight() + " - imageInfo.getWidth() : " +
				// imageInfo.getWidth());

				if (imageInfo.getHeight() > displayHeight) {
					imageInfo.setMaxHeight(displayHeight);
				}

				if (imageInfo.getWidth() > displayWidth) {
					imageInfo.setMaxWidth(displayWidth);
				}
			}

			if (countIndex < 1) {
				backBtn.setVisibility(View.GONE);
			}

			if (countIndex > stepMax - 1) {
				nextBtn.setVisibility(View.GONE);
			}

			infoStepLabel.setMovementMethod(ScrollingMovementMethod
					.getInstance());
			infoStepLabel
					.setText(new String(sInfoVal.getBytes("UTF8"), "UTF8"));

			//

			SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
			float textSize = settings.getFloat("textSize", 0);
			if (textSize != 0 & infoStepLabel.getTextSize() != textSize) {
				infoStepLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			}

			if (tts.isSpeaking()) {
				tts.stop();
			}

		} catch (Exception e) {
			// Log.e(LOGID, e.getMessage());
		}
	}

	/**
	 * After a button is pressed cursor will be on top of the interface
	 */
	private void scrollToTop() {
		scroolView.post(new Runnable() {
			public void run() {
				scroolView.smoothScrollTo(0, layoutInfo.getTop());

			}
		});
	}

	@Override
	public void onItemSelected(AdapterView<?> spinner, View view, int position,
			long id) {
		// Log.i(LOGID, "in onItemSelected spinner: "+ spinner + " - id: " + id
		// + " - pos: " + position+ " - countIndex: " + countIndex);

		if (!initialDisplay) {
			String indexSelected = (String) spinner.getSelectedItem();
			// Log.i(LOGID, "selected index: "+ indexSelected + " - position: "
			// + position);
			int indexSpin = 0;
			try {
				indexSpin = Integer.parseInt(stepNamesHash.get(indexSelected));
			} catch (ParseException e) {
				// Log.e(LOGID, e.getMessage());
			}

			// Log.i(LOGID, "selected val hash spin : "+ indexSpin);
			stepIndex.setStepCount(indexSpin);
			countIndex = indexSpin;
			updateInterface();

			try {
				layoutTopSpin.post(new Runnable() {
					public void run() {
						layoutTopSpin.requestFocus();
						layoutTopSpin.setVisibility(View.GONE);
					}
				});
			} catch (Exception e) {
				// Log.e(LOGID, e.toString());

			}
		}

		initialDisplay = false;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public String getPropValByKey(String key) {
		// Log.i(LOGID, "in getPropValByKey - get prop val for key: " + key);
		Resources resources = this.getResources();
		AssetManager assetManager = resources.getAssets();
		String propValue = "";

		// Read from the /assets directory
		try {
			Properties properties = new Properties();
			InputStream inputStream = assetManager.open("tib.properties");
			properties.load(inputStream);

			if (properties.containsKey(key)) {
				propValue = (String) properties.getProperty(key);
				propValue = new String(propValue.getBytes("UTF-8"), "UTF-8");
			}

			// Log.i(LOGID, properties.toString());
			// Log.i(LOGID, "find value " + propValue+ " for key " + key);

			inputStream.close();
		} catch (Exception e) {
			// Log.e("Error parsing properties file: ", e.getMessage() + e);
		}

		return propValue;
	}

	/**
	 * Return the value from strings.xml based on given stringCode
	 * 
	 * @param stringCode
	 * @return
	 */
	private String getStringRes(String stringCode) {
		// Log.i(LOGID,/ "In getStringRes - search for code: " + stringCode);

		String text = "";
		int resId = this.getResources().getIdentifier(stringCode, "string",
				this.getPackageName());
		text = this.getString(resId);
		return text;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Log.i(LOGID, "in onConfigurationChanged..");
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.main);
		initializeUI();
	}

	/**
	 * 
	 * @return step source
	 */
	protected String buildSourceList() {
		StringBuffer sourceListBuffer = new StringBuffer();
		sourceListBuffer.append(" \t" + getStringRes("menu_source") + " "
				+ getStringRes("TIB.about") + "\n");
		String sName;
		int stepMaxProp = getStepMaxProps();

		for (int i = 1; i <= stepMaxProp; i++) {
			sName = TIB_PREFIX + i + ".name";
			sourceListBuffer.append(i + " - " + getStringRes(sName) + ": \n");

			if (getStringRes(TIB_PREFIX + i + ".source") != null
					&& !"".equalsIgnoreCase(getStringRes(
							TIB_PREFIX + i + ".source").trim())) {
				sourceListBuffer.append(getStringRes("source") + ": "
						+ getStringRes(TIB_PREFIX + i + ".source") + "\n");
			}

			if (getStringRes(TIB_PREFIX + i + ".foto_source") != null
					&& !"".equalsIgnoreCase(getStringRes(
							TIB_PREFIX + i + ".foto_source").trim())) {
				sourceListBuffer.append(getStringRes("picture") + ": "
						+ getStringRes(TIB_PREFIX + i + ".foto_source") + "\n");
			}
		}

		// Log.i(LOGID, "buildSourceList: " + sourceListBuffer.toString());
		return sourceListBuffer.toString();
	}

	/**
	 * Return step name list
	 * 
	 * @return
	 */
	protected String[] buildStepList() {
		StringBuffer stepListBuffer = new StringBuffer();
		String sName;
		int stepMaxProp = getStepMaxProps();

		if (stepNamesHash == null
				|| !stepNamesHash.containsKey(getStringRes(TIB_PREFIX + 1
						+ ".name"))) {
			stepNamesHash = new HashMap<String, String>();
		}

		// add first step
		String stepName;

		for (int i = 1; i < stepMaxProp; i++) {
			sName = TIB_PREFIX + i + ".name";
			stepName = getStringRes(sName);
			stepListBuffer.append(stepName + "#");
			stepNamesHash.put(stepName, "" + i);
		}

		return (stepListBuffer.toString()).split("#");
	}

	/**
	 * Make a spinner with steps list
	 */
	private void selectStep() {
		String[] stepNames = buildStepList();
		int stepMaxProp = getStepMaxProps();
		// Log.i(LOGID, "stepNames " + stepNames.toString());

		List<String> listBuff = Arrays.asList(stepNames);
		List<String> listSteps = new ArrayList<String>();
		listSteps.addAll(listBuff);
		Collections.sort(listSteps);

		// first possition
		String sName = TIB_PREFIX + "0" + ".name";
		String stepName = getStringRes(sName);
		listSteps.add(0, stepName);
		stepNamesHash.put(stepName, "0");

		sName = TIB_PREFIX + stepMaxProp + ".name";
		stepName = getStringRes(sName);
		listSteps.add(listSteps.size(), stepName);
		stepNamesHash.put(stepName, "" + stepMaxProp);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listSteps);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stepSpinner.setAdapter(adapter);
		stepSpinner.setOnItemSelectedListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_S) {
			Toast.makeText(TiBCentrulVechi.this,
					getStringRes("app_name") + " by Silviu", Toast.LENGTH_SHORT)
					.show();
			return true;
		} else {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				// Ask the user if they want to quit
				new AlertDialog.Builder(this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setMessage(R.string.quit)
						.setPositiveButton(R.string.yes,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										// Stop the activity
										TiBCentrulVechi.this.finish();
									}
								}).setNegativeButton(R.string.cancel, null)
						.show();

				return true;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Log.i(LOGID, "onActivityResult - requestCode: " +
		// requestCode+" - resultCode: "+resultCode);

		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				try {
					tts = new TextToSpeech(this, this);
				} catch (Exception e) {
					// Log.d(TIB_PREFIX, "Error TTS");
					try {
						tts = new TextToSpeech(this, this);
					} catch (Exception e2) {
						
					}
				}
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
		updateInterface();
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			// Toast.makeText(TiBCentrulVechi.this,
			// "Text-To-Speech engine is initialized",
			// Toast.LENGTH_LONG).show();
		} else if (status == TextToSpeech.ERROR) {
			Toast.makeText(TiBCentrulVechi.this,
					"Error occurred while initializing Text-To-Speech engine",
					Toast.LENGTH_LONG).show();
		}
	}

	private void sendFeedback() {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/html");
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "turistinbucuresti@gmail.com" });
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback "
				+ getPackageName());
		String phoneInfo = "Phone Model: " + Build.MANUFACTURER + " - "
				+ Build.MODEL;
		emailIntent.putExtra(Intent.EXTRA_TEXT,
				Html.fromHtml(phoneInfo + " <br/> "));

		try {
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(TiBCentrulVechi.this,
					"There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}