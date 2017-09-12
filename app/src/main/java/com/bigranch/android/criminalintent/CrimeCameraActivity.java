package com.bigranch.android.criminalintent;

import android.app.Fragment;
import android.os.Bundle;
import android.view.WindowManager;

public class CrimeCameraActivity extends SingleNoBarFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Hide the window title.
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        // this didn't work, so created version of SingleFragmentActivity that inherits from Activity instead of AppCompatActivity
        // also tried getSupportActionBar().hide but got null pointer violation

        // Hide the status bar and other OS-level chrome
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
