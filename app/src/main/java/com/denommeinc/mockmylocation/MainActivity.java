package com.denommeinc.mockmylocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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

    /*
    * User Enters an Address to Retrieve Coordinates
    * */
    public int houseNumber, zipcode;
    public String streetAddress, state, town;
    public AlertDialog.Builder builder;
    public AlertDialog alertDialog;

    /*
    * Randomly Select Coordinates to Mock
    * from the addressArrayList
    * */
    public int randomSelection = 0;

    /*
    * Creates the Mock Location
    * with LocationManager
    * */
    public MockLocationClass mockLocationClass;

    /*
    * Uses Geocoder to retrieve
    * coordinates from an address
    * */
    public CoordinatesFromAddress coordinatesFromAddress;

    /*
    * boolean to check mock location dev setting
    * */
    public boolean isReady;

    /*
    * Boolean for toggle switch
    * */
    public boolean isActivated;
    public ArrayAdapter<String> adapter;
    public final String[] spinnerSelections = {
            "Massachusetts",
            "Statue of Liberty",
            "Walt Disney World",
            "Hell's Kitchen"
    };
    public EditText
            edt_houseNumber, edt_street, edt_town, edt_state, edt_zipcode;
    public TextView
            lbl_activate, txt_lat, txt_lon, txt_reportedLat,
            txt_reportedLon, txt_userLat, txt_userLon;
    public Button btn_random, btn_search, btn_select;
    public Spinner spn_address;
    public SwitchCompat sw_activate;

    public static void showMessage(String message) {
        Log.i(TAG, message);
    }
    public static void showErrorMessage(String errorMessage) {
        Log.e(TAG_ERROR, errorMessage);
    }

    public void showAlertMessage(String title, String message) {
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton("Deactivate", (dialog, which) -> {
            if (isActivated) {
                sw_activate.toggle();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
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
        txt_userLat = findViewById(R.id.txt_userLat);
        txt_userLon = findViewById(R.id.txt_userLon);
        btn_select = findViewById(R.id.btn_select);
        putTempAddressInEditText();
        try {
            adapter = new ArrayAdapter<>(this,
                    R.layout.spinner_item,
                    spinnerSelections
            );
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spn_address.setAdapter(adapter);
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }

        btn_select.setOnClickListener(v -> initiateMockSequence(true));

        btn_random.setOnClickListener(v -> {
            spn_address.setSelection(chooseRandomLocation() - 1);
            initiateMockSequence(true);
        });

        btn_search.setOnClickListener(v -> {
            retrieveCoordinates();
            Toast.makeText(MainActivity.this,
                    "Getting Coordinates...", Toast.LENGTH_SHORT).show();
            initiateMockSequence(true);
        });

        spn_address.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
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
                    startActivity(new Intent(
                            android.provider.Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                    sw_activate.toggle();
                    showAlertMessage(getString(R.string.change_setting),
                            getString(R.string.notification));

                } else {
                    if (!isActivated) {
                        activateMockLocation(true);
                        lbl_activate.setText(getString(R.string.activated));
                        lbl_activate.setTextColor(getColor(R.color.amber));
                        txt_reportedLon.setTextColor(getColor(R.color.amber));
                        txt_reportedLat.setTextColor(getColor(R.color.amber));
                        showAlertMessage(getString(R.string.alert_title),
                                getString(R.string.active));
                        isActivated = true;

                    } else {
                        mockLocationClass.stopMockLocationUpdates();
                        isActivated = false;
                        lbl_activate.setText(getString(R.string.activate));
                        lbl_activate.setTextColor(getColor(R.color.black));
                        txt_reportedLon.setTextColor(getColor(R.color.black));
                        txt_reportedLon.setText(getString(R.string.off));
                        txt_reportedLat.setTextColor(getColor(R.color.black));
                        txt_reportedLat.setText(getString(R.string.off));
                    }
                }
            } else {
                mockLocationClass.stopMockLocationUpdates();
                isActivated = false;
                lbl_activate.setText(getString(R.string.activate));
                lbl_activate.setTextColor(getColor(R.color.black));
                txt_reportedLon.setTextColor(getColor(R.color.black));
                txt_reportedLon.setText(getString(R.string.off));
                txt_reportedLat.setTextColor(getColor(R.color.black));
                txt_reportedLat.setText(getString(R.string.off));
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

    /*
    *----------------------------------------------------------------------------------------------
    * Remove putTempAddressInEditText
    *  This is for testing only!!!
    * ----------------------------------------------------------------------------------------------
    * */
    @SuppressLint("SetTextI18n")
    public void putTempAddressInEditText() {
        edt_houseNumber.setText("39");
        edt_street.setText("Clinton Ave");
        edt_town.setText("Hope");
        edt_zipcode.setText("02831");
        edt_state.setText("RI");
    }

    public void initiateMockSequence(boolean initiate) {
        if (initiate) {
            Handler handler = new Handler();
            Runnable runnable = () -> {
                sw_activate.toggle();
                Toast.makeText(MainActivity.this,
                                "Mock Activated!", Toast.LENGTH_SHORT)
                        .show();
            };
            handler.postDelayed(runnable, 3000);
        }
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

        } else {
            mockLocationClass = new MockLocationClass(MainActivity.this);
            mockLocationClass.startMockLocationUpdates(mock_latitude, mock_longitude);
            try {
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

            AppOpsManager opsManager = (AppOpsManager)
                    MainActivity.this.getSystemService(Context.APP_OPS_SERVICE);
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

    public void retrieveCoordinates() {
        double tempLat;
        double tempLon;

        try {
            houseNumber = Integer.parseInt(edt_houseNumber.getText().toString());
            zipcode = Integer.parseInt(edt_zipcode.getText().toString());
            streetAddress = edt_street.getText().toString();
            town = edt_town.getText().toString();
            state = edt_state.getText().toString();

            coordinatesFromAddress = new CoordinatesFromAddress(
                    MainActivity.this,
                    houseNumber,
                    zipcode,
                    streetAddress,
                    town,
                    state);
            tempLat = coordinatesFromAddress.latitude;
            tempLon = coordinatesFromAddress.longitude;
            mock_latitude = tempLat;
            mock_longitude = tempLon;
            txt_userLat.setText(String.valueOf(tempLat));
            txt_userLon.setText(String.valueOf(tempLon));
        } catch (NullPointerException e) {
            showErrorMessage(e.getMessage());
        }
    }
}