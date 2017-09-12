package com.bigranch.android.criminalintent;


import android.app.Fragment;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CrimeCameraFragment extends Fragment {
    private static final String TAG = "CrimeCameraFragment";
    private Camera mCamera;
    private SurfaceView mSurfaceView;
    private View mProgressContainer;

    public CrimeCameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_camera, parent, false);

        mProgressContainer = v.findViewById(R.id.crime_camera_progressContainer);
        mProgressContainer.setVisibility(View.INVISIBLE);

        Button takePictureButton = (Button)v.findViewById(R.id.crime_camera_takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mCamera != null) {
                    mCamera.takePicture(mShutterCallback, null, mJpegCallback);
                }
            }
        });

        mSurfaceView = (SurfaceView)v.findViewById(R.id.crime_camera_surfaceView);
        SurfaceHolder holder = mSurfaceView.getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                // Tell the camera to use this surface as its preview area
                try {
                    if (mCamera != null) {
                        mCamera.setPreviewDisplay(holder);
                    }
                } catch (IOException exception) {
                    Log.e(TAG, "Error setting up preview display", exception);
                }
            }
            public void surfaceDestroyed(SurfaceHolder holder) {
                // We can no longer display on this surface, so stop the preview.
                if (mCamera != null) {
                    mCamera.stopPreview();
                }
            }
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if (mCamera == null) return;

                // The surface has changed size; update the camera preview size
                Camera.Parameters parameters = mCamera.getParameters();
                Camera.Size s = getBestSupportedSize(parameters.getSupportedPreviewSizes(), w, h);
                parameters.setPreviewSize(s.width, s.height);
                s = getBestSupportedSize(parameters.getSupportedPictureSizes(), w, h);
                parameters.setPictureSize(s.width, s.height);
                mCamera.setParameters(parameters);
                try {
                    mCamera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    mCamera.release();
                    mCamera = null;
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera = Camera.open(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /** A simple algorithm to get the largest size available. For a more
     * robust version, see CameraPreview.java in the ApiDemos sample app from Android. */
    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            // Display the progress indicator
            mProgressContainer.setVisibility(View.VISIBLE);
        }
    };
    private Camera.PictureCallback mJpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // Create a filename
            String filename = UUID.randomUUID().toString() + ".jpg";
            // Save the jpeg data to disk
            FileOutputStream os = null;
            boolean success = true;
            try {
                os = getActivity().openFileOutput(filename, Context.MODE_PRIVATE);
                os.write(data);
            } catch (Exception e) {
                Log.e(TAG, "Error writing to file " + filename, e);
                success = false;
            } finally {
                try {
                    if (os != null)
                        os.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing file " + filename, e);
                    success = false;
                }
            }
            if (success) {
                Log.i(TAG, "JPEG saved at " + filename);
            }
            getActivity().finish();
        }
    };
}
