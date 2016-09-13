package com.phunware.locationmessaging.sample.views;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.phunware.locationmessaging.entities.Geozone;
import com.phunware.locationmessaging.sample.R;
import com.phunware.locationmessaging.sample.util.MovementUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GeozoneMapView extends MapView implements OnMapReadyCallback {

    private static final String TAG = GeozoneMapView.class.getSimpleName();

    private final Set<Geozone> mGeozones = new HashSet<>();
    private final Map<Long, Circle> mZoneCircles = new HashMap<>();

    private boolean mMapReady = false;
    private GoogleMap mMap;

    public GeozoneMapView(Context context) {
        this(context, null);
    }

    public GeozoneMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GeozoneMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMapReady = true;
        if (!mGeozones.isEmpty()) {
            addGeozones(mGeozones);
            centerCamera();
        }
    }

    /**
     * Adds a group of geozones to the map.
     * @param geozones The geozones to add.
     */
    public void addGeozones(Collection<Geozone> geozones) {
        for (Geozone zone : geozones) {
            addGeozone(zone);
        }
        if (mMapReady) {
            centerCamera();
        }
    }

    /**
     * Adds a new geozone to the map.
     * @param geozone The geozone to add.
     */
    public void addGeozone(Geozone geozone) {
        mGeozones.add(geozone);
        if (!mMapReady) {
            return;
        }

        Circle mapCircle = mMap.addCircle(createCircleOptionsForGeozone(geozone));
        mZoneCircles.put(geozone.id(), mapCircle);
    }

    /**
     * Updates an existing geozone on the map. Setting it's color based
     * on it's current status.
     *
     * @param geozone The geozone to update.
     */
    public void updateGeozone(Geozone geozone) {
        mGeozones.add(geozone);
        if (!mMapReady) {
            return;
        }

        Circle circle = mZoneCircles.get(geozone.id());
        if (circle == null) {
            addGeozone(geozone);
            return;
        }

        circle.setCenter(new LatLng(geozone.latitude(), geozone.longitude()));
        circle.setRadius(geozone.radius());
        circle.setFillColor(getFillColor(geozone));
        circle.setStrokeColor(getStrokeColor(geozone));
    }

    /**
     * Removes a geozone from the map. Since GoogleMap doesn't actually support
     * removal, we'll just hide it.
     *
     * @param geozone The geozone to remove.
     */
    public void removeGeozone(Geozone geozone) {
        mGeozones.remove(geozone);
        if (!mMapReady) {
            return;
        }

        Circle circle = mZoneCircles.remove(geozone.id());
        if (circle == null) {
            return;
        }
        circle.setVisible(false);
    }

    /**
     * We create circle options based on the Geozone settings, including latitude,
     * longitude, radius, and current status.
     *
     * @param geozone The geozone that the circle will represent.
     * @return A CircleOptions object that can be used to identify this geozone on the map.
     */
    private CircleOptions createCircleOptionsForGeozone(Geozone geozone) {
        final LatLng location = new LatLng(geozone.latitude(), geozone.longitude());
        final int fillColor = getFillColor(geozone);
        final int strokeColor = getStrokeColor(geozone);

        return new CircleOptions()
                .center(location)
                .radius(geozone.radius())
                .fillColor(fillColor)
                .strokeColor(strokeColor);
    }

    /**
     * Gets the fill color based on the state of the geozone.
     * @param geozone The geozone to get the color for.
     * @return The fill color to identify this geozone.
     */
    private int getFillColor(Geozone geozone) {
        if (geozone.isInside()) {
            return ContextCompat.getColor(getContext(), R.color.zone_inside_fill);
        } else if (geozone.isMonitored()) {
            return ContextCompat.getColor(getContext(), R.color.zone_monitored_fill);
        } else {
            return ContextCompat.getColor(getContext(), R.color.zone_default_fill);
        }
    }

    /**
     * Gets a stroke color based on the state of the geozone.
     * @param geozone The geozone to get the color for.
     * @return The stroke color to identify this geozone.
     */
    private int getStrokeColor(Geozone geozone) {
        if (geozone.isInside()) {
            return ContextCompat.getColor(getContext(), R.color.zone_inside_stroke);
        } else if (geozone.isMonitored()) {
            return ContextCompat.getColor(getContext(), R.color.zone_monitored_stroke);
        } else {
            return ContextCompat.getColor(getContext(), R.color.zone_default_stroke);
        }
    }

    /**
     * Centers and zooms the camera to all currently bound geozones.
     */
    public void centerCamera() {
        if (!mMapReady || mZoneCircles.isEmpty()) {
            return;
        }

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (Circle circle : mZoneCircles.values()) {
            double rad = circle.getRadius();
            LatLng loc = circle.getCenter();
            bounds.include(MovementUtils.moveTo(loc, 0, rad));
            bounds.include(MovementUtils.moveTo(loc, 90, rad));
            bounds.include(MovementUtils.moveTo(loc, 180, rad));
            bounds.include(MovementUtils.moveTo(loc, 270, rad));
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100));
    }
}
