package com.e.ewhazp.facedetector;
//메인기능!
import android.util.Log;

import androidx.annotation.NonNull;

import com.e.ewhazp.preprocessing.FrameMetadata;
import com.e.ewhazp.preprocessing.GraphicOverlay;
import com.e.ewhazp.preprocessing.VisionProcessorBase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.util.List;

/** Face Detector Demo. */
public class FaceDetectionProcessor extends VisionProcessorBase<List<FirebaseVisionFace>> {

    private static final String TAG = "FaceDetectionProcessor";

    private final FirebaseVisionFaceDetector detector;
    //옵션 컨투어
    FaceDetectionProcessor() {
        FirebaseVisionFaceDetectorOptions options =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)//@@@@@@@
                        .build();

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        Log.e(TAG,"Building FaceDetectionProcessor");
    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionFace>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(
            @NonNull List<FirebaseVisionFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();

        //카메라에서 보낸 프리뷰에 오버레이 덧씌움, 프레임마다 돌아감
        for (int i = 0; i < faces.size(); ++i) {
            //모든 프레임 받아옴
            FirebaseVisionFace face = faces.get(i);

            //프리뷰에 오버레이 덧씌움
            FaceGraphic faceGraphic = new FaceGraphic(graphicOverlay);
            graphicOverlay.add(faceGraphic);
            faceGraphic.updateFace(face);

            //프레임마다
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Face detection failed " + e);
    }
}
