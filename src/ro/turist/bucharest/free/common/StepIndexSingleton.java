package ro.turist.bucharest.free.common;

public class StepIndexSingleton {

	private int stepCount;
	private static StepIndexSingleton stepSingleton = null;

	private StepIndexSingleton() {
		this.stepCount = 0;
	}

	public static StepIndexSingleton getInstance() {
		if (stepSingleton == null) {
			stepSingleton = new StepIndexSingleton();
		}
		
		return stepSingleton;
	}

	public int getStepCount() {
		return stepCount;
	}

	public void setStepCount(int stepCount) {
		this.stepCount = stepCount;
	}

	public int getIncreasedStepCount() {
		return ++stepCount;
	}

	public int getDecreasedStepCount() {
		return --stepCount;
	}

}
