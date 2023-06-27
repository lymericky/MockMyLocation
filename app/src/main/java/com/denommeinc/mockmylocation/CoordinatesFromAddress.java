package com.denommeinc.mockmylocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class CoordinatesFromAddress {
    public final int houseNumber;
    public final int zipcode;
    public final String street;
    public final String town;
    public final String state;
    public String searchAddress;

    public Geocoder geocoder;

    public double latitude, longitude;
    public final Context context;
    final StringBuilder sb = new StringBuilder();
    public List<Address> addressList;

    public CoordinatesFromAddress(Context context, int houseNumber, int zipcode,
                                  String street, String town, String state) {
        this.houseNumber = houseNumber;
        this.zipcode = zipcode;
        this.street = street;
        this.town = town;
        this.state = state;
        this.context = context;
        sb.append(houseNumber);
        sb.append(street);
        sb.append(town);
        sb.append(state);
        sb.append(zipcode);
        init();
    }

    public void init() {

        geocoder = new Geocoder(context);
        searchAddress = sb.toString();
        try {
            addressList = geocoder.getFromLocationName(searchAddress, 1);
            latitude = addressList.get(0).getLatitude();
            longitude = addressList.get(0).getLongitude();

        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
        }

    }

}
