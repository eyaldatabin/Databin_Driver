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

//package com.here.android.example.guidance;

package databin.here.android;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.here.android.mpa.mapping.MapFragment;

import java.util.ArrayList;
import java.util.List;

import databin.here.android.interfaces.DatabinApi;
import databin.here.android.models.WorkPlan;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Main activity which launches map view and handles Android run-time requesting permission.
 */

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private MapFragmentView m_mapFragmentView;
    private DatabinApi m_databinApi;
    private String m_getSimSerialNumber;
    private WorkPlan m_workplan;

    public DatabinApi getM_databinApi() {
        return m_databinApi;
    }

    public void setM_databinApi(DatabinApi m_databinApi) {
        this.m_databinApi = m_databinApi;
    }

    public String getM_getSimSerialNumber() {
        return m_getSimSerialNumber;
    }

    public void setM_getSimSerialNumber(String m_getSimSerialNumber) {
        this.m_getSimSerialNumber = m_getSimSerialNumber;
    }

    public WorkPlan getM_workplan() {
        return m_workplan;
    }

    public void setM_workplan(WorkPlan m_workplan) {
        this.m_workplan = m_workplan;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("workplan", "print#1");
        setContentView(R.layout.activity_main);
        requestPermissions();

        Intent i = new Intent(this, MapFragment.class);
        i.putExtra("simSerialNumber", m_getSimSerialNumber);



    }

    @Override
    protected void onStart() {
        super.onStart();
//        new HttpRequestTask().execute();
    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
//            new HttpRequestTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    */

    /**
     * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
     * needs when the app is running.
     */
    private void requestPermissions() {

        final List<String> requiredSDKPermissions = new ArrayList<String>();
        requiredSDKPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredSDKPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredSDKPermissions.add(Manifest.permission.INTERNET);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        requiredSDKPermissions.add(Manifest.permission.READ_PHONE_STATE);

        ActivityCompat.requestPermissions(this,
                requiredSDKPermissions.toArray(new String[requiredSDKPermissions.size()]),
                REQUEST_CODE_ASK_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /**
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                permissions[index])) {
                            Toast.makeText(this,
                                    "Required permission " + permissions[index] + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this,
                                    "Required permission " + permissions[index] + " not granted",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }

                /**
                 * All permission requests are being handled.Create map fragment view.Please note
                 * the HERE SDK requires all permissions defined above to operate properly.
                 */
//                Log.d("workplan", "getM_workplan" + m_workplan.getDriverId());

                TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                m_getSimSerialNumber = telemamanger.getSimSerialNumber();

                Gson gson = new GsonBuilder().create();

                Retrofit retrofit = new Retrofit.Builder().
                        baseUrl("https://data-bins.com/serverApi/v1/")
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();

                m_databinApi = retrofit.create(DatabinApi.class);

                Call workPlanCall = m_databinApi.getWorkPlan(m_getSimSerialNumber);

                workPlanCall.enqueue(new Callback<WorkPlan>() {
                    @Override
                    public void onResponse(Call<WorkPlan> call, Response<WorkPlan> response) {
                        int statusCode = response.code();

                        m_workplan = response.body();

//                setM_workplan(response.body());

                        Log.d("workplan", "d: " + m_workplan.getDriverId());

                        Log.d("Workplan", "onResponse: " + statusCode);
                        if (m_workplan != null) {
                            for (int i = 0; i < m_workplan.getBinsInfo().size(); i++) {
                                Log.d("Workplan", "item: " + m_workplan.getBinsInfo().get(i).getBinId());
                            }
                        }

                        sendActivity();
                    }

                    @Override
                    public void onFailure(Call<WorkPlan> call, Throwable t) {
                        Log.d("Workplan", "onResponse: " + t.getMessage());
                    }
                });

                break;
            }


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void sendActivity() {
        m_mapFragmentView = new MapFragmentView(this, m_workplan);
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.new_available_plan_dialog);
        dialog.show();
    }

    @Override
    public void onDestroy() {
        m_mapFragmentView.onDestroy();
        super.onDestroy();
    }
}
