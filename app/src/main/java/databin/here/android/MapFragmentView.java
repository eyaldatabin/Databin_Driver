/*
 * Copyright (c) 2011-2017 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package databin.here.android;

//package com.here.android.example.guidance;


import java.lang.ref.WeakReference;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import java.util.List;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.PositionIndicator;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;

import android.telephony.TelephonyManager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import databin.here.android.interfaces.DatabinApi;
import databin.here.android.models.WorkPlan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;




import org.w3c.dom.Text;

import databin.here.android.R;

/**
 * This class encapsulates the properties and functionality of the Map view.It also triggers a
 * turn-by-turn navigation from HERE Burnaby office to Langley BC.The Evaluation SDK doesn't support
 * downloading current voice skins from the HERE server, however there is a sample voice skin
 * bundled within the SDK package, please refer to the Developer's guide for the usage.
 */
public class MapFragmentView {
    private final static int MAX_GPS_TIMOUT = 50;
    private MapFragment m_mapFragment;
    private Activity m_activity;
    private Button m_naviControlButton;
    private Map m_map;
    private NavigationManager m_navigationManager;
    private GeoBoundingBox m_geoBoundingBox;
    private Route m_route;
    private PositioningManager m_positioningManager;
    private DatabinApi databinApi;
    private WorkPlan m_workPlan;

    public MapFragmentView(Activity activity, WorkPlan workplan) {
        m_activity = activity;
        m_workPlan = workplan;
        Log.d("workplan", "Driver id: " + workplan.getDriverId());
        initMapFragment();
        initNaviControlButton();
    }

    private void initMapFragment() {
        /* Locate the mapFragment UI element */
        m_mapFragment = (MapFragment) m_activity.getFragmentManager()
                .findFragmentById(R.id.mapfragment);

        if (m_mapFragment != null) {
            /* Initialize the MapFragment, results will be given via the called back. */
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                    if (error == Error.NONE) {
                        Locale locale = new Locale("heb");
                        m_map = m_mapFragment.getMap();
                        m_map.setMapDisplayLanguage(locale);
                        Log.d("workplan", "map lang: " + m_map.getMapDisplayLanguage());
                        Log.d("workplan", "locale: " + Locale.getDefault().getLanguage());
                        // Set the site coordinate
                        // Start position tracking
                        m_positioningManager = PositioningManager.getInstance();
                        m_positioningManager.start(PositioningManager.LocationMethod.GPS_NETWORK);
                        while (!m_positioningManager.hasValidPosition()) {
//                            Log.d("workplan", "Engine Complete: " + m_positioningManager.hasValidPosition());
                        }
                        // Set positioning indicator visible
                        PositionIndicator positionIndicator = m_mapFragment.getPositionIndicator();
                        positionIndicator.setVisible(true);
                        m_naviControlButton.performClick();


//                        Log.d("workplan", "coordinates: " + positioningManager.getPosition().getCoordinate());
//                        Log.d("workplan", "user lat: " + positioningManager.getPosition().getCoordinate().getLatitude() + positioningManager.getPosition().getCoordinate().getLongitude());
                        m_map.setCenter(new GeoCoordinate(m_positioningManager.getPosition().getCoordinate()),
                                Map.Animation.LINEAR);
                        m_map.setZoomLevel(13.2);
                        /*
                         * Get the NavigationManager instance.It is responsible for providing voice
                         * and visual instructions while driving and walking
                         */
                        m_navigationManager = NavigationManager.getInstance();

                        Toast.makeText(m_activity,
                                "ERROR: Cannot initialize Map with error " + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    private void createRoute() {


        /* Initialize a CoreRouter */
        CoreRouter coreRouter = new CoreRouter();

        /* Initialize a RoutePlan */
        RoutePlan routePlan = new RoutePlan();

        /*
         * Initialize a RouteOption.HERE SDK allow users to define their own parameters for the
         * route calculation,including transport modes,route types and route restrictions etc.Please
         * refer to API doc for full list of APIs
         */
        RouteOptions routeOptions = new RouteOptions();
        /* Other transport modes are also available e.g Pedestrian */
        routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);

        /* Disable highway in this route. */
        routeOptions.setHighwaysAllowed(false);
        /* Calculate the shortest route available. */
        routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
        /* Calculate 1 route. */
        routeOptions.setRouteCount(1);
        /* Finally set the route option */
        routePlan.setRouteOptions(routeOptions);

        PositioningManager positioningManager = PositioningManager.getInstance();
        positioningManager.start(PositioningManager.LocationMethod.GPS);

        Log.d("workplan", "location: " + positioningManager.getPosition().getCoordinate());


        /* Define waypoints for the route */
        /* START: 4350 Still Creek Dr */
        RouteWaypoint startPoint = null;

        Log.d("workplan", "hasValidPostion: " + positioningManager.hasValidPosition());

        if (positioningManager.hasValidPosition() == true) {
            startPoint = new RouteWaypoint(new GeoCoordinate(positioningManager.getPosition().getCoordinate()));
            Log.d("workplan", "true!");
        }
        else if (positioningManager.getLastKnownPosition().isValid()) {
            Log.d("workplan", "isValid; " + positioningManager.getLastKnownPosition().getCoordinate());
            startPoint = new RouteWaypoint(new GeoCoordinate(positioningManager.getLastKnownPosition().getCoordinate()));
        }
        else {
            Log.d("workplan", "GPS issue!");
        }

        routePlan.addWaypoint(startPoint);

        if (m_workPlan != null) {
            for (int i = 0; i < m_workPlan.getBinsInfo().size(); i++) {
                Log.d("Workplan", "item: " + m_workPlan.getBinsInfo().get(i).getBinId());
                routePlan.addWaypoint(new RouteWaypoint(new GeoCoordinate(m_workPlan.getBinsInfo().get(i).getBinLocation().getBinLatitude(), m_workPlan.getBinsInfo().get(i).getBinLocation().getBinLongitude())));
            }
        }

        /*Set number of bins to present in dialog*/
        View newDialog = m_activity.findViewById(R.id.new_plan_dialog);
//        newDialog.findViewById()
//        TextView numOfBinsTextView = (TextView);
//        numOfBinsTextView.setText("bla bla");


        Log.d("workplan", "Waypoints: " + routePlan.getWaypointCount());

        /* Trigger the route calculation,results will be called back via the listener */
        coreRouter.calculateRoute(routePlan,
                new Router.Listener<List<RouteResult>, RoutingError>() {

                    @Override
                    public void onProgress(int i) {
                        /* The calculation progress can be retrieved in this callback. */
                    }

                    @Override
                    public void onCalculateRouteFinished(List<RouteResult> routeResults,
                            RoutingError routingError) {
                        /* Calculation is done.Let's handle the result */
                        if (routingError == RoutingError.NONE) {
                            if (routeResults.get(0).getRoute() != null) {

                                m_route = routeResults.get(0).getRoute();
                                /* Create a MapRoute so that it can be placed on the map */
                                MapRoute mapRoute = new MapRoute(routeResults.get(0).getRoute());

                                /* Show the maneuver number on top of the route */
                                mapRoute.setManeuverNumberVisible(true);

                                /* Add the MapRoute to the map */
                                m_map.addMapObject(mapRoute);

                                /*
                                 * We may also want to make sure the map view is orientated properly
                                 * so the entire route can be easily seen.
                                 */
                                m_geoBoundingBox = routeResults.get(0).getRoute().getBoundingBox();
                                m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE,
                                        Map.MOVE_PRESERVE_ORIENTATION);

                                startNavigation(m_route);
                            } else {
                                Toast.makeText(m_activity,
                                        "Error:route results returned is not valid",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(m_activity,
                                    "Error:route calculation returned error code: " + routingError,
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    private void initNaviControlButton() {
        m_naviControlButton = (Button) m_activity.findViewById(R.id.naviCtrlButton);
//        Dialog dialog = new Dialog(m_activity.getApplicationContext());
//        dialog.setContentView(R.layout.new_available_plan_dialog);
        m_naviControlButton.setText(R.string.start_navi);
        m_naviControlButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                /*
                 * To start a turn-by-turn navigation, a concrete route object is required.We use
                 * the same steps from Routing sample app to create a route from 4350 Still Creek Dr
                 * to Langley BC without going on HWY.
                 *
                 * The route calculation requires local map data.Unless there is pre-downloaded map
                 * data on device by utilizing MapLoader APIs,it's not recommended to trigger the
                 * route calculation immediately after the MapEngine is initialized.The
                 * INSUFFICIENT_MAP_DATA error code may be returned by CoreRouter in this case.
                 *
                 */
                if (m_route == null) {
                    createRoute();
                } else {
                    m_navigationManager.stop();
                    /*
                     * Restore the map orientation to show entire route on screen
                     */
                    m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE, 0f);
                    m_naviControlButton.setText(R.string.start_navi);
                    m_route = null;
                }
            }
        });
    }

    private void startNavigation(Route route) {
        m_naviControlButton.setText(R.string.stop_navi);
        /* Display the position indicator on map */
        m_map.getPositionIndicator().setVisible(true);
        /* Configure Navigation manager to launch navigation on current map */
        m_navigationManager.setMap(m_map);
        /*
         * Start the turn-by-turn navigation.Please note if the transport mode of the passed-in
         * route is pedestrian, the NavigationManager automatically triggers the guidance which is
         * suitable for walking. Simulation and tracking modes can also be launched at this moment
         * by calling either simulate() or startTracking()
         */
        m_navigationManager.startNavigation(route);

        /*
         * Set the map update mode to ROADVIEW.This will enable the automatic map movement based on
         * the current location.If user gestures are expected during the navigation, it's
         * recommended to set the map update mode to NONE first. Other supported update mode can be
         * found in HERE Android SDK API doc
         */
        m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);

//        Log.d("workplan", "Instruction: " + m_navigationManager.);

        /*
         * NavigationManager contains a number of listeners which we can use to monitor the
         * navigation status and getting relevant instructions.In this example, we will add 2
         * listeners for demo purpose,please refer to HERE Android SDK API documentation for details
         */
        addNavigationListeners();
    }

    private void addNavigationListeners() {

        /*
         * Register a NavigationManagerEventListener to monitor the status change on
         * NavigationManager
         */
        m_navigationManager.addNavigationManagerEventListener(
                new WeakReference<NavigationManager.NavigationManagerEventListener>(
                        m_navigationManagerEventListener));

        /* Register a PositionListener to monitor the position updates */
        m_navigationManager.addPositionListener(
                new WeakReference<NavigationManager.PositionListener>(m_positionListener));

        m_navigationManager.addNewInstructionEventListener(
                new WeakReference<NavigationManager.NewInstructionEventListener>(m_newInstructionEventListener));

    }

    private NavigationManager.PositionListener m_positionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(GeoPosition geoPosition) {
            /* Current position information can be retrieved in this callback */
            Log.d("workplan", "coordinates: " + geoPosition.getCoordinate());
        }
    };

    private PositioningManager.OnPositionChangedListener positionListener = new
            PositioningManager.OnPositionChangedListener() {
                @Override
                public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
                    Log.d("workplan", "PositioningManager.OnPositionChangedListener: " + geoPosition.getCoordinate());
                }

                @Override
                public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {

                }
            };


    private NavigationManager.NewInstructionEventListener m_newInstructionEventListener = new NavigationManager.NewInstructionEventListener() {
        @Override
        public void onNewInstructionEvent() {
            Maneuver maneuver = m_navigationManager.getNextManeuver();
            String heb_turn = "";

            if (maneuver.getTurn().toString() == "QUITE_RIGHT") {
                heb_turn = "ימינה";
            }

            Log.d("workplan", "בעוד " + maneuver.getDistanceToNextManeuver() + "מטרים, פנה" + heb_turn + maneuver.getNextRoadName());
            Log.d("workplan", "Instruction: " + maneuver.getTurn());

//            textview.setText(maneuver.getTurn().toString());
            Maneuver.Icon turnIcon = maneuver.getIcon();
            ImageView turnImage = (ImageView) m_activity.findViewById(R.id.turn_image);
            switch (turnIcon) {
                case QUITE_RIGHT:
                    turnImage.setImageResource(R.mipmap.turn_left);
                    break;
                case QUITE_LEFT:
                    turnImage.setImageResource(R.mipmap.turn_left);
                    break;

                default:
                    Log.d("workplan", "invalid turn icon");
            }



        }

    };

    private NavigationManager.NavigationManagerEventListener m_navigationManagerEventListener = new NavigationManager.NavigationManagerEventListener() {
        @Override
        public void onRunningStateChanged() {
            Toast.makeText(m_activity, "Running state changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNavigationModeChanged() {
            Toast.makeText(m_activity, "Navigation mode changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnded(NavigationManager.NavigationMode navigationMode) {
            Toast.makeText(m_activity, navigationMode + " was ended", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
            Toast.makeText(m_activity, "Map update mode is changed to " + mapUpdateMode,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRouteUpdated(Route route) {
            Toast.makeText(m_activity, "Route updated", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCountryInfo(String s, String s1) {
            Toast.makeText(m_activity, "Country info updated from " + s + " to " + s1,
                    Toast.LENGTH_SHORT).show();
        }
    };


    public void onDestroy() {
        /* Stop the navigation when app is destroyed */
        if (m_navigationManager != null) {
            m_navigationManager.stop();
        }
    }
}
