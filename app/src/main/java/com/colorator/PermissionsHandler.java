package com.colorator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsHandler {
    AppCompatActivity mActivity;

    private String lastRequestSent;

    PermissionsHandler(AppCompatActivity activity) {
        mActivity = activity;
    }

    public void checkPermission(String permission) {
        if (ContextCompat.checkSelfPermission(mActivity, permission)
                == PackageManager.PERMISSION_DENIED) {
            lastRequestSent = permission;
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{permission},
                    0);
        }
    }

    public void onRequestPermissionsResult(int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        switch (lastRequestSent) {
            case Manifest.permission.CAMERA:
                handleCameraPermissionDenied();
                break;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                handleWriteStoragePermissionDenied();
                break;
            default:
                Toast.makeText(mActivity.getApplicationContext(),
                        "No permission handler defined. Developer, please check PermissionHandler class.",
                        Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void handleCameraPermissionDenied() {
        Toast.makeText(mActivity.getApplicationContext(),
                "Sorry, Colorator Cannot work without camera permissions, and it is going to close it self",
                Toast.LENGTH_LONG).show();
        mActivity.finishAffinity();
    }

    private void handleWriteStoragePermissionDenied() {
        Toast.makeText(mActivity.getApplicationContext(),
                "Sorry,You cannot take pictures without granting permission to write to external storage",
                Toast.LENGTH_LONG).show();
    }
}