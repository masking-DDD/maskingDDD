package com.e.ewhazp.preprocessing;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.e.ewhazp.preprocessing.CameraSource.SizePair;
import com.google.android.gms.common.images.Size;
import com.google.firebase.samples.apps.mlkit.R;

/** Utility class to retrieve shared preferences. */
public class PreferenceUtils {

    @Nullable
    public static SizePair getCameraPreviewSizePair(Context context, int cameraId) {
        if (!(cameraId == CameraSource.CAMERA_FACING_FRONT)) {
            throw new RuntimeException("Invalid cameraId: " + cameraId);
        }
        String previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size);
        String pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size);
//        if (cameraId == CameraSource.CAMERA_FACING_BACK) {
//            previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size);
//            pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size);
//        } else {
//            previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size);
//            pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size);
//        }

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return new SizePair(
                    Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)),
                    Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)));
        } catch (Exception e) {
            return null;
        }
    }
}
