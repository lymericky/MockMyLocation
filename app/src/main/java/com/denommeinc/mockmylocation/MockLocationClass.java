package com.denommeinc.mockmylocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.provider.ProviderProperties;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MockLocationClass extends AppCompatActivity {

    public static Location mock_location;
    public static LocationManager mock_manager;
    public final Handler handler;
    public Runnable runnable;
    public static double mockLat, mockLon;

    MockLocationClass(Context context) {
        mock_manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        handler = new Handler();
    }

    void startMockLocationUpdates(final double mock_latitude, final double mock_longitude) {
        runnable = () -> {
                MainActivity.setMock_latitude(mock_latitude);
                mockLat = mock_latitude;
                MainActivity.setMock_longitude(mock_longitude);
                mockLon = mock_longitude;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setMock(LocationManager.GPS_PROVIDER, mock_latitude, mock_longitude);
            }
            Log.i("MOCK", "GPS \t" + "lat:\t" + mock_latitude + "\tlon:\t" + mock_longitude);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setMock(LocationManager.NETWORK_PROVIDER, mock_latitude, mock_longitude);
            }
            Log.i("MOCK", "Network \t" + "lat:\t" + mock_latitude + "\tlon:\t" + mock_longitude);

            handler.postDelayed(runnable, 1000);

        };
        handler.post(runnable);
    }

    void stopMockLocationUpdates() {
        handler.removeCallbacks(runnable);
        mock_manager.removeTestProvider(LocationManager.NETWORK_PROVIDER);
        mock_manager.removeTestProvider(LocationManager.GPS_PROVIDER);
        Log.i("MOCK:", "LOCATION DEACTIVATED");
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void setMock(String mock_provider, double mock_latitude, double mock_longitude) {
        MainActivity.showMessage("MLC mockLat\t" + mock_latitude);
        mock_manager.addTestProvider(
                mock_provider,
                false,
                false,
                false,
                false,
                false,
                true,
                true,
                ProviderProperties.POWER_USAGE_LOW,
                ProviderProperties.ACCURACY_FINE);

        mock_location = new Location(mock_provider);
        mock_location.setLatitude(mock_latitude);
        mock_location.setLongitude(mock_longitude);
        mock_location.setAltitude(3F);
        mock_location.setTime(System.currentTimeMillis());
        mock_location.setSpeed(0.01F);
        mock_location.setBearing(1F);
        mock_location.setAccuracy(3F);
        mock_location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

        mock_location.setBearingAccuracyDegrees(0.1F);
        mock_location.setVerticalAccuracyMeters(0.1F);
        mock_location.setSpeedAccuracyMetersPerSecond(0.01F);

        mock_manager.setTestProviderEnabled(mock_provider, true);
        mock_manager.setTestProviderLocation(mock_provider, mock_location);

    }
}
