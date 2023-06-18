package com.denommeinc.mockmylocation;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MAIN_ACTIVITY MESSAGE:";
    public static final String TAG_ERROR = "MAIN_ACTIVITY ERROR:";
    public static double mock_latitude = 0.0F, mock_longitude = 0.0F;
    public static void setMock_latitude(double mock_latitude) {
        MainActivity.mock_latitude = mock_latitude;
    }
    public static void setMock_longitude(double mock_longitude) {
        MainActivity.mock_longitude = mock_longitude;
    }

    public Address address;
    public int houseNumber, zipcode;
    public String streetAddress, state, town;
    public int randomSelection = 0;
    /*
     * Geocoder to get an address from coordinates
     * */
    public Geocoder geocoder;
    public MockLocationClass mockLocationClass;
    /*
    * boolean to check mock location dev setting
    * */
    public boolean isReady;
    public boolean isActivated;
    public ArrayList<Address> addressArrayList;
    public ArrayAdapter adapter;
    public String[] spinnerSelections = {
            "Massachusetts",
            "Statue of Liberty",
            "Walt Disney World",
            "Hell's Kitchen"
    };
    public EditText
            edt_houseNumber, edt_street, edt_town, edt_state, edt_zipcode;
    public TextView lbl_activate, txt_lat, txt_lon,
            txt_reportedLat, txt_reportedLon;
    public Button btn_random, btn_search;
    public Spinner spn_address;
    public SwitchCompat sw_activate;

    public static void showMessage(String message) {
        Log.i(TAG, message);
    }
    public static void showErrorMessage(String errorMessage) {
        Log.e(TAG_ERROR, errorMessage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isReady = isMockLocationEnabled();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edt_houseNumber = findViewById(R.id.edt_houseNumber);
        edt_street = findViewById(R.id.edt_street);
        edt_town = findViewById(R.id.edt_town);
        edt_state = findViewById(R.id.edt_state);
        edt_zipcode = findViewById(R.id.edt_zipcode);
        lbl_activate = findViewById(R.id.lbl_switch);
        txt_lat = findViewById(R.id.txt_lat);
        txt_lon = findViewById(R.id.txt_lon);
        btn_random = findViewById(R.id.btn_random);
        btn_search = findViewById(R.id.btn_search);
        spn_address = findViewById(R.id.spn_prevAddresses);
        sw_activate = findViewById(R.id.sw_activate);
        txt_reportedLat = findViewById(R.id.txt_reportedLat);
        txt_reportedLon = findViewById(R.id.txt_reportedLon);
        adapter = new ArrayAdapter(
                this, android.R.layout.simple_spinner_item,
                spinnerSelections
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_address.setAdapter(adapter);

        btn_random.setOnClickListener(v -> {
            showMessage(String.valueOf(chooseRandomLocation()));
            spn_address.setSelection(chooseRandomLocation() - 1);
            sw_activate.toggle();
        });

        spn_address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mock_latitude = 42.03533440709362F;
                    mock_longitude = -71.68385446171698F;
                } else if (position == 1) {
                    mock_latitude = 40.68931445306322F;
                    mock_longitude = -74.04445748877751F;
                } else if (position == 2) {
                    mock_latitude = 28.377374468371652;
                    mock_longitude = -81.57076146239099;
                } else if (position == 3) {
                    mock_latitude = 40.7640059863778;
                    mock_longitude = -73.99189287430765;
                }

                txt_lat.setText(String.valueOf(mock_latitude));
                txt_lon.setText(String.valueOf(mock_longitude));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                /*set a default selection*/
            }
        });


        sw_activate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (sw_activate.isChecked()) {
                if (!isReady) {
                    Toast.makeText(this, R.string.notification, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                    sw_activate.toggle();
                } else {
                    if (!isActivated) {
                        activateMockLocation(true);
                    } else {
                        mockLocationClass.stopMockLocationUpdates();
                        isActivated = false;
                    }
                }
            } else {
                mockLocationClass.stopMockLocationUpdates();
                isActivated = false;
            }
        });

        txt_lat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mock_latitude = Double.parseDouble(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt_lon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mock_longitude = Double.parseDouble(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private int chooseRandomLocation() {
        int max = 4;
        int min = 1;
        Random random = new Random();
        randomSelection = random.nextInt((max - min) + 1) + min;
        return randomSelection;
    }

    public void activateMockLocation(boolean activated) {
        isActivated = activated;

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {

            mockLocationClass = new MockLocationClass(MainActivity.this);
            mockLocationClass.startMockLocationUpdates(mock_latitude, mock_longitude);
            Location networkLoc = MockLocationClass.mock_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location gpsLoc = MockLocationClass.mock_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            try {

                Log.i(TAG, "LATITUDE:\t" + String.valueOf(mock_latitude));

                Log.i(TAG, "LONGITUDE:\t" + String.valueOf(mock_longitude));
                txt_reportedLat.setText(String.valueOf(mock_latitude));
                txt_reportedLon.setText(String.valueOf(mock_longitude));
            } catch (Exception e) {
                Log.e("ERROR2", e.getMessage());
            }


        }
    }

    private boolean isMockLocationEnabled()
    {
        boolean isMockLocation;
        try {
            AppOpsManager opsManager = (AppOpsManager) MainActivity.this.getSystemService(Context.APP_OPS_SERVICE);
            isMockLocation = (
                    Objects.requireNonNull(opsManager).checkOp(
                            AppOpsManager.OPSTR_MOCK_LOCATION,
                            android.os.Process.myUid(),
                            BuildConfig.APPLICATION_ID)
                            == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            return false;
        }
        return isMockLocation;
    }


}