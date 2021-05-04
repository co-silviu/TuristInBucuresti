package ro.turist.bucharest.free.util;

import ro.turist.bucharest.free.TiBCentrulVechi;
import ro.turist.bucharest.free.common.StepIndexSingleton;
import android.view.MotionEvent;
import android.view.View;

public class ActivitySwipeDetector implements View.OnTouchListener {

	static final String logTag = "ActivitySwipeDetector";
	private TiBCentrulVechi activity;
	static final int MIN_DISTANCE = 100;
	private float downX, downY, upX, upY;
	private StepIndexSingleton stepIndex;

	public ActivitySwipeDetector(TiBCentrulVechi activity) {
		this.activity = activity;
		stepIndex = StepIndexSingleton.getInstance();
	}

	public void onRightToLeftSwipe() {
		int countIndex = stepIndex.getIncreasedStepCount();
		//Log.i("TuristInBucurestiActivity", "onRightToLeftSwipe: " + countIndex);
		activity.updateInterface();
		
	}

	public void onLeftToRightSwipe() {
		//Log.i(logTag, "LeftToRightSwipe!");
		int countIndex = stepIndex.getDecreasedStepCount();		
		activity.updateInterface();
	}

	public void onTopToBottomSwipe() {
		
	}

	public void onBottomToTopSwipe() {
		
	}

	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			downX = event.getX();
			downY = event.getY();
			return true;
		}
		case MotionEvent.ACTION_UP: {
			upX = event.getX();
			upY = event.getY();

			float deltaX = downX - upX;
			float deltaY = downY - upY;

			// swipe horizontal?
			if (Math.abs(deltaX) > MIN_DISTANCE) {
				// left or right
				if (deltaX < 0) {
					this.onLeftToRightSwipe();
					return true;
				}
				if (deltaX > 0) {
					this.onRightToLeftSwipe();
					return true;
				}
			} else {
				
				return false; // We don't consume the event
			}

			// swipe vertical?
			if (Math.abs(deltaY) > MIN_DISTANCE) {
				// top or down
				if (deltaY < 0) {
					this.onTopToBottomSwipe();
					return true;
				}
				if (deltaY > 0) {
					this.onBottomToTopSwipe();
					return true;
				}
			} else {
				
				return false; // We don't consume the event
			}

			return true;
		}
		}
		return false;
	}

}