package com.denommeinc.mockmylocation;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

public class CoordinatesFromAddress {
    public int houseNumber, zipcode;
    public String street;
    public String town;
    public String state;
    public String searchAddress;

    public Address address;
    public Geocoder geocoder;

    public double latitude, longitude;
    public Context context;
    StringBuilder sb = new StringBuilder();
    public List<Address> addressList;
    public void setSearchAddress(String searchAddress) {
        this.searchAddress = searchAddress;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

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
    }

    public CoordinatesFromAddress init() {

        geocoder = new Geocoder(context);
        searchAddress = sb.toString();
        try {
            addressList = geocoder.getFromLocationName(searchAddress, 1);
            setLatitude(addressList.get(0).getLatitude());
            latitude = addressList.get(0).getLatitude();
            Log.i("LATITUDE", String.valueOf(latitude));
            setLongitude(addressList.get(0).getLongitude());
            longitude = addressList.get(0).getLongitude();
            Log.i("LONGITUDE", String.valueOf(longitude));
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
        }

        return null;
    }

}
