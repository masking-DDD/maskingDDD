package com.e.ewhazp;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.e.ewhazp.GraphicOverlay.Graphic;
import com.google.android.gms.vision.CameraSource;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;

import java.util.List;


/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
public class FaceGraphic extends Graphic {
    private static final String TAG = "FaceGraphic";

    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private static final int[] COLOR_CHOICES = {
            Color.BLUE //, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW
    };
    private static int currentColorIndex = 0;

    private int facing;

    private final Paint facePositionPaint;
    private final Paint idPaint;
    private final Paint boxPaint;
    private final Paint textPaint;

    private volatile FirebaseVisionFace firebaseVisionFace;
    double EARValue;

    double getDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(Math.abs(x1-x2), 2) + Math.pow(Math.abs(y1-y2), 2));
    }
    //양쪽 눈의 EAR값 평균 계산
    private double CalEARMean(List<FirebaseVisionPoint> leftEyePointsList, List<FirebaseVisionPoint> rightEyePointsList) {

        double lp0p8 = getDistance(leftEyePointsList.get(0).getX(), leftEyePointsList.get(0).getY(), leftEyePointsList.get(8).getX(), leftEyePointsList.get(8).getY());
        double lp3p13 = getDistance(leftEyePointsList.get(3).getX(), leftEyePointsList.get(3).getY(), leftEyePointsList.get(13).getX(), leftEyePointsList.get(13).getY());
        double lp5p11 = getDistance(leftEyePointsList.get(5).getX(), leftEyePointsList.get(5).getY(), leftEyePointsList.get(11).getX(), leftEyePointsList.get(11).getY());
        double rp0p8 = getDistance(rightEyePointsList.get(0).getX(), rightEyePointsList.get(0).getY(), rightEyePointsList.get(8).getX(), rightEyePointsList.get(8).getY());
        double rp3p13 = getDistance(rightEyePointsList.get(3).getX(), rightEyePointsList.get(3).getY(), rightEyePointsList.get(13).getX(), rightEyePointsList.get(13).getY());
        double rp5p11 = getDistance(rightEyePointsList.get(5).getX(), rightEyePointsList.get(5).getY(), rightEyePointsList.get(11).getX(), rightEyePointsList.get(11).getY());

        double LeftEyeEAR = (lp3p13+lp5p11)/(2*lp0p8);
        double RightEyeEAR = (rp3p13+rp5p11)/(2*rp0p8);
        return EARValue = (LeftEyeEAR+RightEyeEAR)/2;
    }

    public FaceGraphic(GraphicOverlay overlay) {
        super(overlay);

        currentColorIndex = (currentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[currentColorIndex];

        facePositionPaint = new Paint();
        facePositionPaint.setColor(selectedColor);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50f);


        idPaint = new Paint();
        idPaint.setColor(selectedColor);
        idPaint.setTextSize(ID_TEXT_SIZE);

        boxPaint = new Paint();
        boxPaint.setColor(selectedColor);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    /**
     * Updates the face instance from the detection of the most recent frame. Invalidates the relevant
     * portions of the overlay to trigger a redraw.
     */
    public void updateFace(FirebaseVisionFace face, int facing) {
        firebaseVisionFace = face;
        this.facing = facing;
        postInvalidate();
    }

    /** Draws the face annotations for position on the supplied canvas. */
    @SuppressLint("DefaultLocale")
    @Override
    public void draw(Canvas canvas) {
        FirebaseVisionFace face = firebaseVisionFace;
        if (face == null) {
            return;
        }

        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getBoundingBox().centerX());
        float y = translateY(face.getBoundingBox().centerY());
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint);

        if (facing == CameraSource.CAMERA_FACING_FRONT) {
            //윤곽선에 점 찍기
            FirebaseVisionFaceContour lefteye = face.getContour(FirebaseVisionFaceContour.LEFT_EYE);
            List<FirebaseVisionPoint> lefteyeList = lefteye.getPoints();
            for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : lefteye.getPoints()) {
                float px = translateX(point.getX());
                float py = translateY(point.getY());
                canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
                Log.d("lefteye getpoints", String.valueOf(lefteye.getPoints()));
            }
            FirebaseVisionFaceContour righteye = face.getContour(FirebaseVisionFaceContour.RIGHT_EYE);
            List<FirebaseVisionPoint> righteyeList = righteye.getPoints();

            for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : righteye.getPoints()) {
                float px = translateX(point.getX());
                float py = translateY(point.getY());
                canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
            }
//            FirebaseVisionFaceContour upperlip_bottom = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM);
//////            for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : upperlip_bottom.getPoints()) {
//////                float px = translateX(point.getX());
//////                float py = translateY(point.getY());
//////                canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
//////            }
//////            FirebaseVisionFaceContour lowerlip_top = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP);
//////            for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : lowerlip_top.getPoints()) {
//////                float px = translateX(point.getX());
//////                float py = translateY(point.getY());
//////                canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
//////            }
            //EAR 계산
            double EAR = CalEARMean(lefteyeList, righteyeList);
            canvas.drawText("EAR Mean is: "+ EAR, x, y, textPaint);
            if(EAR <= 0.24) {
                Log.wtf(TAG,"EAR Mean is: "+ CalEARMean(lefteyeList, righteyeList));
                Log.wtf(TAG,"@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"); //drowsiness alart!
            }

            //canvas.drawText("ML Kit value is: "+(face.getRightEyeOpenProbability()+face.getLeftEyeOpenProbability())/2, x,y-50,textPaint);
           // Log.wtf(TAG,"ML Kit value is: "+(face.getRightEyeOpenProbability()+face.getLeftEyeOpenProbability())/2);
        }
        else {
            FirebaseVisionFaceContour lefteye = face.getContour(FirebaseVisionFaceContour.LEFT_EYE);
            for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : lefteye.getPoints()) {
                float px = translateX(point.getX());
                float py = translateY(point.getY());
                canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
                Log.d("lefteye getpoints", String.valueOf(lefteye.getPoints()));
            }
            FirebaseVisionFaceContour righteye = face.getContour(FirebaseVisionFaceContour.RIGHT_EYE);
            for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : righteye.getPoints()) {
                float px = translateX(point.getX());
                float py = translateY(point.getY());
                canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
            }
            FirebaseVisionFaceContour upperlip_bottom = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM);
            for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : upperlip_bottom.getPoints()) {
                float px = translateX(point.getX());
                float py = translateY(point.getY());
                canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
            }
            FirebaseVisionFaceContour lowerlip_top = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP);
            for (com.google.firebase.ml.vision.common.FirebaseVisionPoint point : lowerlip_top.getPoints()) {
                float px = translateX(point.getX());
                float py = translateY(point.getY());
                canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint);
            }
        }

        // Draws a bounding box around the face.
        float xOffset = scaleX(face.getBoundingBox().width() / 2.0f);
        float yOffset = scaleY(face.getBoundingBox().height() / 2.0f);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;
        canvas.drawRect(left, top, right, bottom, boxPaint);
    }
}