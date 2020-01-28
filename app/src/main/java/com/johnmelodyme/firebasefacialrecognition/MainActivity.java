package com.johnmelodyme.firebasefacialrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.johnmelodyme.firebasefacialrecognition.Helper.GraphicOverlay;
import com.johnmelodyme.firebasefacialrecognition.Helper.RectOverlay;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;
import java.util.List;
import dmax.dialog.SpotsDialog;

/**
 * @Author: John Melody Melissa
 * @Project: Firebase Face Detection
 * @Inpired : By GF TAN SIN DEE <3
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private Button DETECTION;
    private CameraView CAMERA_VIEW;
    private GraphicOverlay GRAPHIC_OVERLAY;
    private AlertDialog ALERT_PROMPT;
    private Context Here;

    private void DECLARATION(){
        Here = MainActivity.this;
        DETECTION = findViewById(R.id.detect_button);
        CAMERA_VIEW = findViewById(R.id.CAMERA);
        GRAPHIC_OVERLAY = findViewById(R.id.Graphic_overlay);
        ALERT_PROMPT = new SpotsDialog
                .Builder()
                .setContext(Here)
                .setMessage("Please Wait, Loading....")
                .setCancelable(false)
                .build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DECLARATION();
        Log.w(TAG,"Firebase Face Detector" + ":" + "Application Started?" + "\t" + "========> {1}" );

        DETECTION.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "Firebase Face Detector" + ":" + "DETECTION_BUTTON CLICKED " + "===========>  {1}");
                CAMERA_VIEW.start();
                CAMERA_VIEW.captureImage();
                GRAPHIC_OVERLAY.clear();
            }
        });

        CAMERA_VIEW.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                ALERT_PROMPT.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, CAMERA_VIEW.getWidth(), CAMERA_VIEW.getHeight(), false);
                CAMERA_VIEW.stop();

                PROCESS_FACE_DETECTION(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

    }

    private void PROCESS_FACE_DETECTION(Bitmap bitmap) {
        FirebaseVisionImage firebaseVisionImage;
        firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions;
        firebaseVisionFaceDetectorOptions = new FirebaseVisionFaceDetectorOptions
                .Builder()
                .build();

        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);

        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        getFaceResult(firebaseVisionFaces);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toaster("Error: " +  exception.getMessage());
                Log.w(TAG,"Firebase Face Detector" + ":" + " Error: " +  exception.getMessage());
            }
        });
    }

    private void getFaceResult(List<FirebaseVisionFace> firebaseVisionFaces) {
        int COUNTER = 0x0;
        for (FirebaseVisionFace visionFace : firebaseVisionFaces){
            Rect rect = visionFace.getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(GRAPHIC_OVERLAY, rect);

            GRAPHIC_OVERLAY.add(rectOverlay);

            COUNTER = COUNTER + 1;
        }
        ALERT_PROMPT.dismiss();
    }

    @Override
    public void onPause(){
        super.onPause();
        CAMERA_VIEW.stop();
    }

    @Override
    protected void onResume(){
        super.onResume();
        CAMERA_VIEW.start();
    }

    public void Toaster(String string){
        Toast.makeText(Here, string,
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.sourcecode) {
            String URL;
            URL = getResources().getString(R.string.url);
            Intent SOURCE_CODE;
            SOURCE_CODE = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
            startActivity(SOURCE_CODE);
            Log.w(TAG, "Firebase Face Detector" + "SOURCE_CODE ======> {REQUESTED:GITHUB --> OK}");
            return true;
        }
        if (id == R.id.about){
            String ABOUT;
            ABOUT = getResources().getString(R.string.about_me);
            Intent ABOUT_ME;
            ABOUT_ME = new Intent(Intent.ACTION_VIEW, Uri.parse(ABOUT));
            startActivity(ABOUT_ME);
            Log.w(TAG, "Firebase Face Detector" + "ABOUT ======> {REQUESTED:JOHN_MELODY_MELISSA||SIN_DEE --> OK}");
        }
        return super.onOptionsItemSelected(item);
    }
}
