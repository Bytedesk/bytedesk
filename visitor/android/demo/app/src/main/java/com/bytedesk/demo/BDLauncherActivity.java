package com.bytedesk.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class BDLauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, BDMainActivity.class);
        startActivity(intent);
        finish();
    }

}
