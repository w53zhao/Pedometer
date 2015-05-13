package ca.uwaterloo.Lab2_201_19;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       
        LinearLayout layout = ((LinearLayout)findViewById(R.id.layout));
        layout.setOrientation(LinearLayout.VERTICAL);    
        
        // ACCELERATION SENSOR OUTPUT

        final TextView stepText = (TextView)findViewById(R.id.step);
        stepText.setText("Number of steps taken: 0");
        
        Button reset = (Button)findViewById(R.id.reset);
          
        //ACCELERATION SENSOR
        final SensorManager accelsm =
        		(SensorManager) getSystemService(SENSOR_SERVICE);
        final Sensor accelSensor =
        		accelsm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        SensorEventListener accel =
        		new AccelSensorEventListener(stepText, 0);
        accelsm.registerListener(accel, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
        
        //RESET
        reset.setOnClickListener(new View.OnClickListener( ) {
        	@Override
            public void onClick(View v) {
        		stepText.setText("Number of steps taken: 0");
        		SensorEventListener accel =
                		new AccelSensorEventListener(stepText, 0);
        		accelsm.registerListener(accel, accelSensor, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {       
    	// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;       
    }
    
}

class AccelSensorEventListener implements SensorEventListener {
	
	LineGraphView accelGraph;
	
	boolean state1;
	boolean state2; 
	boolean transition;
	int counter;
	TextView counterText;
	
	public AccelSensorEventListener(TextView stepText, int step) {	
		counterText = stepText;
		counter = step;
	}
	
	public void onAccuracyChanged(Sensor s, int i) {}
	
	public void onSensorChanged(SensorEvent se) {

		float[] output = lowpass(se.values);
		
		if (output[1] > 8.3)	{
			state1 = true;
		}
		
		if (state1 == true && output[1] > 8.8) {
			state2 = true;
		}
		
		if (state1 == true && state2 == true && output[1] < 8.3) {
			transition = true;
		}
		
		if (state1 == true && state2 == true && transition == true) {
			counter++;
			counterText.setText("Number of steps taken: " + String.valueOf(counter));
			state1 = false;
			state2 = false;
			transition = false;
		}
			
	}
	
	float[] lowpass(float[] in) {
		float[] out = new float[in.length];
		float a = 0.8f;
		out[0] = 0;
		for(int i = 1; i < in.length; i++) {
		out[i] = a * in[i] + (1-a) * out[i-1];
		}
	
		return out;
	}
	
}