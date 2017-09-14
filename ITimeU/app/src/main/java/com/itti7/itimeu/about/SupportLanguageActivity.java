package com.itti7.itimeu.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.itti7.itimeu.R;

public class SupportLanguageActivity extends AppCompatActivity {

    // Back image button
    ImageButton mBackImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_language);

        // When click this button then finish licenses activity
        mBackImageButton = (ImageButton) findViewById(R.id.back_img_btn_language);
        mBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    // For finishing this activity
    @Override
    public void onBackPressed() {
        finish();
    }
}