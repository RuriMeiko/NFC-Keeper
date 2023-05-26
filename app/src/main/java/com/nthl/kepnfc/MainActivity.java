package com.nthl.kepnfc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.fasterxml.uuid.Generators;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    NfcAdapter nfcAdapter;
    TextView showtext;
    ImageView imageView;
    ImageView imageView2;
    TextView textView;
    TextView popuptext;
    Uri photoURI;
    String mCurrentPhotoPath;
    String mPhotoPathfromNFC;
    String time_textsave;
    String thecumoi;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    public void onRestart() {
        super.onRestart();
        if (readSwitchState()) setContentView(R.layout.activity_main);
        else setContentView(R.layout.oneimg);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        showtext = findViewById(R.id.showtext);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        if (readSwitchState()) {
            imageView2 = findViewById(R.id.imageView2);
            imageView2.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
                if (mPhotoPathfromNFC != null){
                    File imageFile = new File(mPhotoPathfromNFC);
                if(!imageFile.exists())
                    return;
                else {
                    intent.putExtra("image_path", imageFile.getAbsolutePath());
                    startActivity(intent);
                }}
            });
        }

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
            if (mCurrentPhotoPath != null){
                File imageFile = new File(mCurrentPhotoPath);
                if(!imageFile.exists())
                    return;
                else {
                    intent.putExtra("image_path", imageFile.getAbsolutePath());
                    startActivity(intent);
                }}
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(view -> {
                    Toast.makeText(MainActivity.this, "Sẽ thêm sau!", Toast.LENGTH_SHORT).show();
                });

        textView.setText(thecumoi);
        showtext.setText(time_textsave);
        if (readSwitchState()) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            imageView.setImageBitmap(imageBitmap);
            if (mPhotoPathfromNFC != null) {
                imageBitmap = BitmapFactory.decodeFile(mPhotoPathfromNFC);
                imageView2.setImageBitmap(imageBitmap);
            }
        }
        else {
            Bitmap imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            imageView.setImageBitmap(imageBitmap);
            if (mPhotoPathfromNFC != null) {
                imageBitmap = BitmapFactory.decodeFile(mPhotoPathfromNFC);
                imageView.setImageBitmap(imageBitmap);}
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (readSwitchState()) setContentView(R.layout.activity_main);
        else setContentView(R.layout.oneimg);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        showtext = findViewById(R.id.showtext);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        if (readSwitchState()) {
            imageView2 = findViewById(R.id.imageView2);
            imageView2.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
                if (mPhotoPathfromNFC != null){
                    File imageFile = new File(mPhotoPathfromNFC);
                    if(!imageFile.exists())
                        return;
                    else {
                        intent.putExtra("image_path", imageFile.getAbsolutePath());
                        startActivity(intent);
                    }}
            });
        }

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
            if (mCurrentPhotoPath != null){
                File imageFile = new File(mCurrentPhotoPath);
                if(!imageFile.exists())
                    return;
                else {
                    intent.putExtra("image_path", imageFile.getAbsolutePath());
                    startActivity(intent);
                }}
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Sẽ thêm sau!", Toast.LENGTH_SHORT).show();
        });
    }


    private boolean readSwitchState() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        return sharedPreferences.getBoolean("switch_state", false);
    }

    private int readSeekBarState() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        return sharedPreferences.getInt("seek_bar_key", 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.settingbuttom:
                Intent intent = new Intent(MainActivity.this, menusettingaa.class);
                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onButtonShowPopupWindowClick(View view, String textstring) {
        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        popuptext = (TextView) popupView.findViewById(R.id.popuptext);

        popuptext.setText(textstring);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        // start fade in animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        popupView.startAnimation(animation);


        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        if (readSwitchState())
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 100);
        else
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 320);

        // start fade out animation and dismiss the popup window
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
                popupView.startAnimation(animation);
                popupWindow.dismiss();
            }
        }, 750);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private String gentext() {
        UUID uuid = Generators.timeBasedGenerator().generate();      // it will geneate timebased UUID
        return uuid.toString();
    }

    private String timefromuuid(String uuidString) {
        if (uuidString.length() > 10) {
            UUID uuid = UUID.fromString(uuidString);
            long timestamp = (uuid.timestamp() - 122192928000000000L) / 10000;
            Date date = new Date(timestamp);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dateString = formatter.format(date);
            return dateString;
        } else return "null";
    }



    private void requestCameraPermission(String name) {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);


        } else {
            imageView.setImageResource(android.R.color.transparent);
            dispatchTakePictureIntent(name);
        }
    }

    private void dispatchTakePictureIntent(String name) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(name);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.nthl.kepnfc.myfileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File file = new File(mCurrentPhotoPath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 11 - readSeekBarState();  // Lấy một phần của tấm hình gốc
            options.inPreferredConfig = Bitmap.Config.ARGB_8888; // Chỉ định định dạng hình ảnh lưu
            options.inPurgeable = true;
            Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            try {
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            imageBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private File createImageFile(String imageFileName) throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();

    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatchSystem();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if ((parcelables != null) && (parcelables.length > 0)) {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String nameimgran = gentext();
                NdefMessage ndefMessage = createNdefMessage(nameimgran + "");
                String stringtext = readTextFromMessage((NdefMessage) parcelables[0]);
                if (stringtext.equals("@#$%&GG%@!") || (timefromuuid(stringtext).equals("null"))) {
                    writeNdefMessage(tag, ndefMessage);
                    textView.setText("Thẻ mới");
                    thecumoi = "Thẻ mới";
                    time_textsave=("Dữ liệu sẽ hiện ở đây");
                    mPhotoPathfromNFC = null;
                    requestCameraPermission(nameimgran);
                    if (readSwitchState())
                        imageView2.setImageResource(android.R.color.transparent);
                    showtext.setText("Dữ liệu sẽ hiện ở đây");
                } else {
                    time_textsave = timefromuuid(stringtext);
                    showtext.setText(time_textsave);
                    ndefMessage = createNdefMessage("@#$%&GG%@!" + "");
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), stringtext + ".jpg");
                    if (file.exists()) {
                        mPhotoPathfromNFC = file.getAbsolutePath();
                        Bitmap imageBitmap = BitmapFactory.decodeFile(mPhotoPathfromNFC);
                        if (readSwitchState()) {
                            imageView2.setImageBitmap(imageBitmap);
                            requestCameraPermission(stringtext + "-out");
                        }
                        else {
                            imageView.setImageBitmap(imageBitmap);
                            mCurrentPhotoPath = mPhotoPathfromNFC;
                        }
                    } else Toast.makeText(this, "File không tồn tại!", Toast.LENGTH_LONG).show();
                    writeNdefMessage(tag, ndefMessage);
                    textView.setText("Thẻ cũ");
                    thecumoi = "Thẻ cũ";

                }
            } else {
                Toast.makeText(this, "No NDEF messages found!", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private String readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            if (tagContent.equals("@#$%&GG%@!")) return "@#$%&GG%@!";
            else return tagContent;
        } else {
            Toast.makeText(this, "No NDEF records found!", Toast.LENGTH_SHORT).show();
        }
        return "null";
    }


    private void enableForegroundDispatchSystem() {
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
                return;
            }


            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
            Toast.makeText(this, "Đã ghi dữ liệu!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }

    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
        try {
            if (tag == null) {
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // format tag with the ndef format and writes the message.
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Thẻ không thể ghi!", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                onButtonShowPopupWindowClick(this.findViewById(R.id.button2), "Xong!");
            }

        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
        }

    }


    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }


    private NdefMessage createNdefMessage(String content) {

        NdefRecord ndefRecord = createTextRecord(content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }


    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }


}