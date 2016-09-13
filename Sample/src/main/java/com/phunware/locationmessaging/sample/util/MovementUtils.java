package com.phunware.locationmessaging.sample.util;

import com.google.android.gms.maps.model.LatLng;

/**
 * Source: http://stackoverflow.com/questions/5857523/
 * calculate-latitude-and-longitude-having-meters-distance-from-another-latitude-lo
 */
public final class MovementUtils {

    private static final double MINUTES_TO_METERS = 1852d;

    private static final double DEGREE_TO_MINUTES = 60d;

    public static LatLng moveTo(LatLng start, double heading, double distance) {
        final double rHd = Math.toRadians(heading);
        final double rDt = Math.toRadians(distance / MINUTES_TO_METERS / DEGREE_TO_MINUTES);

        final double lat1 = Math.toRadians(start.latitude);
        final double lng1 = Math.toRadians(start.longitude);

        final double lat = Math.asin(Math.sin(lat1) * Math.cos(rDt) + Math.cos(lat1)
                * Math.sin(rDt) * Math.cos(rHd));
        final double dlng = Math.atan2(Math.sin(rHd) * Math.sin(rDt) * Math.cos(lat1),
                Math.cos(rDt) - Math.sin(lat1) * Math.sin(lat));
        final double lng = (lng1 + dlng + Math.PI) % (2 * Math.PI) - Math.PI;

        return new LatLng(Math.toDegrees(lat), Math.toDegrees(lng));
    }

    private MovementUtils() { }
}
