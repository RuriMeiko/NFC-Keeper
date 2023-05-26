package com.nthl.kepnfc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;


public class menusettingaa extends AppCompatActivity {
    private Switch mySwitch;
    private SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menusettingaa);
        mySwitch = findViewById(R.id.switch1);
        seekBar = findViewById(R.id.seekBar2);

        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveSwitchState(isChecked);
            }
        });
        mySwitch.setChecked(readSwitchState());

        // Lưu trạng thái seek bar khi nó thay đổi
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                saveSeekBarState(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Đọc trạng thái seek bar lưu trữ trước đó
        seekBar.setProgress(readSeekBarState());
//////////////
        Button deleten = findViewById(R.id.deleteall);
        deleten.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Xác nhận");
            builder.setMessage("Bạn có chắc chắn xoá?");
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    deleteRecursive(getExternalFilesDir(Environment.DIRECTORY_PICTURES));

                    Toast.makeText(menusettingaa.this, "Đã xoá tất cả ảnh!", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        });
    }
    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();

    }

    private void saveSeekBarState(int progress) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("seek_bar_key", progress);
        editor.apply();
    }


    private void saveSwitchState(boolean isChecked) {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("switch_state", isChecked);
        editor.apply();
    }

    private int readSeekBarState() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        return sharedPreferences.getInt("seek_bar_key", 0);
    }


    private boolean readSwitchState() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("switch_state", false);
    }
}
