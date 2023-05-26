package com.nthl.kepnfc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class FullImageActivity extends AppCompatActivity {
    ImageView fullImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        fullImageView = findViewById(R.id.full_image_view);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("image_path");
        TextView namefile = findViewById(R.id.textView3);
        String filename=imagePath.substring(imagePath.lastIndexOf("/")+1);
        namefile.setText(filename);
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        fullImageView.setImageBitmap(bitmap);
    }
}
