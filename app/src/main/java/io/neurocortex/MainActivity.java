package io.neurocortex;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    AppCompatButton uploadBtn,detectBtn;
    ImageView dashLogo;
    boolean isFileSelected = false;

    private static final int REQUEST_CODE_PICK_CSV = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;
    private static final int REQUEST_CODE_MANAGE_STORAGE = 3;


    public static String csvData;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();


        uploadBtn.setOnClickListener(v->{
            if(checkPermissions()){
                pickCSVFile();
                isFileSelected = true;
            }else{
                requestPermissions();
            }
        });
        detectBtn.setOnClickListener(v->{
            if(!isFileSelected){
                Toast.makeText(this, "File not selected", Toast.LENGTH_SHORT).show();
                uploadBtn.setError("Selece CSV File");
            }
            else{
                ProgressDialog dlg = new ProgressDialog(this);
                dlg.setMessage("Please wait...");
                dlg.show();
                new Handler().postDelayed(() -> {
                    dlg.cancel();
                    isFileSelected = false;
                    startActivity(new Intent(getApplicationContext(),ReportActivity.class));
                },3000);
            }
        });

    }

    private void pickCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select CSV file"),
                REQUEST_CODE_PICK_CSV);
    }

    private boolean checkPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            return Environment.isExternalStorageManager();
        }else{
            return ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermissions(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            try{
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent,REQUEST_CODE_MANAGE_STORAGE);
            }catch(Exception e){
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent,REQUEST_CODE_MANAGE_STORAGE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_MANAGE_STORAGE){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                if(Environment.isExternalStorageManager()){
                    pickCSVFile();
                }
                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if(requestCode ==REQUEST_CODE_PICK_CSV && requestCode == Activity.RESULT_OK){
            if(data != null){
                Uri uri = data.getData();
                csvData = readCSV(uri);
            }
        }
    }


    private String  readCSV(Uri uri){
        try{
            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uri);
            if(inputStream == null){
                Toast.makeText(this, "Failed to display CSV file", Toast.LENGTH_SHORT).show();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line2;
            while((line2 = reader.readLine()) != null){
                stringBuilder.append(line2).append("\n");
            }
            reader.close();
            return String.valueOf(stringBuilder);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "null";

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                pickCSVFile();
            }
            else{
                Toast.makeText(this, "Persmission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews() {
        detectBtn = findViewById(R.id.btn_detect);
        uploadBtn = findViewById(R.id.btn_upload);
        dashLogo = findViewById(R.id.dash_logo);

        Animation slideUp = AnimationUtils.loadAnimation(this,R.anim.slide_up);
        Animation fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);

        dashLogo.startAnimation(slideUp);
        slideUp.setAnimationListener(
                new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        detectBtn.setVisibility(View.VISIBLE);
                        uploadBtn.setVisibility(View.VISIBLE);
                        detectBtn.startAnimation(fadeIn);
                        uploadBtn.startAnimation(fadeIn);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                }
        );
    }
}