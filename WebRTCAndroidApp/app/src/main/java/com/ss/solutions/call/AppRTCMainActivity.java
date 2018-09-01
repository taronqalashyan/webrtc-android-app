/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.ss.solutions.call;

import android.Manifest;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.google.gson.Gson;
import com.ss.solutions.R;
import com.ss.solutions.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.ss.solutions.utils.Constants.EXTRA_ROOMID;

/**
 * Handles the initial setup where the user selects which room to join.
 */
public class AppRTCMainActivity extends AppCompatActivity {

    private static final int CONNECTION_REQUEST = 1;
    private static final int RC_CALL = 111;

    private ActivityMainBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.connectButton.setOnClickListener(v -> connect());
        binding.roomEdittext.requestFocus();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_CALL)
    private void connect() {
        final String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            connectToRoom(binding.roomEdittext.getText().toString());
        } else {
            EasyPermissions.requestPermissions(this, "Need some permissions", RC_CALL, perms);
        }
    }

    private void connectToRoom(String roomId) {
        new Thread(() -> {
            try {
                createConference("room", "high", "mosaic");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        final Intent intent = new Intent(this, CallActivity.class);
        intent.putExtra(EXTRA_ROOMID, roomId);
        startActivityForResult(intent, CONNECTION_REQUEST);
    }

    private void createConference(String name, String profile, String recordingLayout) {
        final Gson gson = new Gson();
        final PostRequestData postRequestData = new PostRequestData();
        postRequestData.setName(name);
        postRequestData.setProfile(profile);
        postRequestData.setRecordingLayout(recordingLayout);
        final String json = gson.toJson(postRequestData);
        final String url = "https://demo.api.mind.com/3392f844-9dbc-4251-b0ae-8a44efae95d1";
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIzMzkyZjg0NC05ZGJjLTQyNTEtYjBhZS04YTQ0ZWZhZTk1ZDEiLCJleHAiOjE1NDIxODQwNzMsIm1jZCI6NzIwfQ.4AqD2e3wPhCwDJFmhjdzl7WN8SxiDNFjqBRq7E7p_xrAUJIyrm2Sf4mjq4kL8hRTQJL2CfP8renXYXmhK6PTkM8jX4IEsDpJ1k5aAdRf36xBK9IZn2T99CBa5rA-GYaQVHWgf5bqanIaaU_Aa6epeNzbYN6LMFIvIGe3NoTbsDNzR01ORZ-2b4djDunou9_4KcOMLxvfCrMnDoPhnlly1vrw7y2wQoNrpnmAkE0ooNmSzvei_a-bP7OhuRloE1GJYL0YYfrvF3eP7qjiM3Q7HRIY0JkVs3fWjqk4AEtjIjkPlgoaeW9QTD_hdZGHPMee8H90aAbbSYwAbXAsmeJudIEbTFADF8NkO2gxksXQncKO1tnYRhGQ9Hy2k6YZgnOqWNEvp86s1G4-f64FitjZ9g2atWR5sb1OygdzABPFiqBrtIbexHcIKTBsAh4Xb8rXTflTxrpcJbHxxe4xH9R2--E0h4f6TLke7OQX3afHDm0Z_kLX5hpJ6L2VE17MNiCt")
                .post(body)
                .build();

        Callback responseCallBack = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v("Fail Message", "fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    String meetingId = object.getString("id");
                    Log.v("Response", meetingId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };
        OkHttpClient client = new OkHttpClient();
        Call call = client.newCall(request);
        call.enqueue(responseCallBack);
    }
}
