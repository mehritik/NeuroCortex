package io.neurocortex;

import static io.neurocortex.MainActivity.csvData;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ReportActivity extends AppCompatActivity {
    TextView csvDataTV,outputTV;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();

    }

    private void initViews() {
//        csvDataTV = findViewById(R.id.tv_csv_data);
        outputTV = findViewById(R.id.tv_output);

//        csvDataTV.setText(csvData);

        outputTV.setText(readTxt());

        Toast.makeText(this, "Result Fetched Successfully", Toast.LENGTH_SHORT).show();
    }

    private String  readTxt(){
        try{
            InputStream inputStream = getResources().openRawResource(R.raw.sample_report);
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

}
