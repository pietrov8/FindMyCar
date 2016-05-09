package com.example.piotr.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CameraViewActivity extends Activity implements
		SurfaceHolder.Callback, OnLocationChangedListener, OnAzimuthChangedListener, SensorEventListener{

	private Camera mCamera = null;
	private SurfaceHolder mSurfaceHolder;
	private boolean isCameraviewOn = false;
	private AugmentedPOI mPoi;

	private double mAzimuthReal = 0;
	private double mAzimuthTeoretical = 0;
	private static double AZIMUTH_ACCURACY = 5;
	private double mMyLatitude = 0;
	private double mMyLongitude = 0;
	private float currentDegree = 0f;

	private MyCurrentAzimuth myCurrentAzimuth;
	private MyCurrentLocation myCurrentLocation;
	GeomagneticField geoField;

	TextView descriptionTextView;
	TextView descriptionTextView2;
	TextView cameraDistance;
	ImageView pointerIcon;
	ImageView arrowLeftIcon;
	ImageView arrowRightIcon;

	// device sensor manager
	private SensorManager mSensorManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_view);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setupListeners();
		setupLayout();
		setAugmentedRealityPoint();

		Location loc1 = new Location("A");
		loc1.setLatitude(mMyLatitude);
		loc1.setLongitude(mMyLongitude);

		Bundle bundle_list = getIntent().getExtras();
		final String name_item = bundle_list.getString("name");

		JSONObject toSend = new JSONObject();
		try {
			toSend.put("action", "getAllMarkers");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String address = preferences.getString("address","");
		if(!address.equalsIgnoreCase("")) {
			JSONTransmitter asyncTask = (JSONTransmitter) new JSONTransmitter(new JSONTransmitter.AsyncResponse() {
				@Override
				public void processFinish(String output) {
					try {
						JSONArray pages = new JSONArray(output);
						for (int i = 0; i < pages.length(); ++i) {
							JSONObject rec = pages.getJSONObject(i);
							String name_task = rec.getString("nazwa");
							double latitude = Double.parseDouble(rec.getString("latitude"));
							double longituide = Double.parseDouble(rec.getString("longitude"));
							String description = rec.getString("opis");
							String name_item_title = String.valueOf(R.string.marker_title);
							String coordinates_title = String.valueOf(R.string.marker_coordinates);
							String description_title = String.valueOf(R.string.marker_description);
							if (name_task.equals(name_item)) {
								descriptionTextView.setText(name_item + "\n" + description);
								mPoi = new AugmentedPOI(
										name_item,
										description,
										latitude, longituide
								);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}).execute(toSend,address);
		} else {
			Toast.makeText(this, "Ustaw poprawny adres w ustawieniach aplikacji", Toast.LENGTH_LONG).show();
		}

		// initialize your android device sensor capabilities
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		/*** CHECK GPS ***/
		final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			buildAlertMessageNoGps();
		}

	}

	public void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.gps_disabled_message)
				.setCancelable(false)
				.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void setAugmentedRealityPoint() {
	}

	private List<Double> calculateAzimuthAccuracy(double azimuth) {
		double minAngle = azimuth - AZIMUTH_ACCURACY;
		double maxAngle = azimuth + AZIMUTH_ACCURACY;
		List<Double> minMax = new ArrayList<Double>();

		if (minAngle < 0)
			minAngle += 360;

		if (maxAngle >= 360)
			maxAngle -= 360;

		minMax.clear();
		minMax.add(minAngle);
		minMax.add(maxAngle);

		return minMax;
	}

	private boolean isBetween(double minAngle, double maxAngle, double azimuth) {
		if (minAngle > maxAngle) {
			if (isBetween(0, maxAngle, azimuth) && isBetween(minAngle, 360, azimuth))
				return true;
		} else {
			if (azimuth > minAngle && azimuth < maxAngle)
				return true;
		}
		return false;
	}

	private void updateDescription() {
		Location loc1 = new Location("A");
		loc1.setLatitude(mMyLatitude);
		loc1.setLongitude(mMyLongitude);

		descriptionTextView2.setText("Twoja lokalizajca: " + mMyLatitude+", "+mMyLongitude);

		Bundle bundle_list = getIntent().getExtras();
		final double longitiude = Double.parseDouble(bundle_list.getString("long"));
		final double latitiude = Double.parseDouble(bundle_list.getString("lat"));

		Location loc2 = new Location("B");
		loc2.setLatitude(latitiude);
		loc2.setLongitude(longitiude);

		float distanceInMeters = loc1.distanceTo(loc2);
		DecimalFormat df = new DecimalFormat("#.#");
		String dx = df.format(distanceInMeters);

		cameraDistance.setText("Odległość do znacznika: " + dx + " m");


		float bearing = loc1.bearingTo(loc2);
		bearing = normalizeDegree(bearing);

		pointerIcon = (ImageView) findViewById(R.id.icon);
		arrowLeftIcon = (ImageView) findViewById(R.id.icon_arrow_left);
		arrowRightIcon = (ImageView) findViewById(R.id.icon_arrow_right);

		if (((bearing - 5) < currentDegree) && ((bearing + 5) > currentDegree)) {
			pointerIcon.setVisibility(View.VISIBLE);
			arrowLeftIcon.setVisibility(View.INVISIBLE);
			arrowRightIcon.setVisibility(View.INVISIBLE);
		} else if((bearing - 5) < currentDegree) {
			pointerIcon.setVisibility(View.INVISIBLE);
			arrowLeftIcon.setVisibility(View.VISIBLE);
			arrowRightIcon.setVisibility(View.INVISIBLE);
		} else if ((bearing + 5) > currentDegree) {
			pointerIcon.setVisibility(View.INVISIBLE);
			arrowLeftIcon.setVisibility(View.INVISIBLE);
			arrowRightIcon.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		mMyLatitude = location.getLatitude();
		mMyLongitude = location.getLongitude();
		updateDescription();

	}

	private float normalizeDegree(float value) {
		if (value >= 0.0f && value <= 180.0f) {
			return value;
		} else {
			return 180 + (180 + value);
		}
	}

	@Override
	public void onAzimuthChanged(float azimuthChangedFrom, float azimuthChangedTo) {

	}

	@Override
	protected void onStop() {
		myCurrentAzimuth.stop();
		myCurrentLocation.stop();
		mSensorManager.unregisterListener(this);
		super.onStop();

	}

	@Override
	protected void onResume() {
		super.onResume();
		myCurrentAzimuth.start();
		myCurrentLocation.start();
		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	private void setupListeners() {
		myCurrentLocation = new MyCurrentLocation(this);
		myCurrentLocation.buildGoogleApiClient(this);
		myCurrentLocation.start();

		myCurrentAzimuth = new MyCurrentAzimuth(this, this);
		myCurrentAzimuth.start();
	}

	private void setupLayout() {
		descriptionTextView = (TextView) findViewById(R.id.cameraTextView);
		descriptionTextView2 = (TextView) findViewById(R.id.cameraTextView2);
		cameraDistance = (TextView) findViewById(R.id.cameraDistance);

		getWindow().setFormat(PixelFormat.UNKNOWN);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.cameraview);
		mSurfaceHolder = surfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
							   int height) {
		if (isCameraviewOn) {
			mCamera.stopPreview();
			isCameraviewOn = false;
		}

		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(mSurfaceHolder);
				mCamera.startPreview();
				isCameraviewOn = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		mCamera.setDisplayOrientation(90);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
		isCameraviewOn = false;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float degree = Math.round(event.values[0]);
		currentDegree = degree;

		updateDescription();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}
}
