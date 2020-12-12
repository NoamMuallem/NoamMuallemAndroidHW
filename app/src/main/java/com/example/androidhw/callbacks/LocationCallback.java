package com.example.androidhw.callbacks;

public interface LocationCallback {
    /**
     * this interface will be implemented to activity wishing to use
     * the scores fragment to allow location and name to be sent
     * back to the activity to be used and implemented as needed
     * @param lon longitude of the location
     * @param lat altitude of the location
     * @param name the winner's name
     */
    void getLocation(double lon, double lat, String name);
}
