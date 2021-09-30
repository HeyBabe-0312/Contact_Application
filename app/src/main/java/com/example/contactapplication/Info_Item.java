package com.example.contactapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Info_Item extends AppCompatActivity {
    private EditText editName;
    private EditText editSdt;
    private EditText editMail;
    private ImageView imageView;
    final int REQUEST_CODE_GALLERY = 999;
    private int id;
    public static byte[] imgg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item);

        editName = findViewById(R.id.edit_name);
        editSdt = findViewById(R.id.edit_sdt);
        editMail = findViewById(R.id.edit_mail);
        imageView = findViewById(R.id.image_View);

        editSdt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_cancel);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent receive = getIntent();
        if (receive != null){
            id = receive.getIntExtra("id",-1);
            editName.setText(receive.getStringExtra("name"));
            editSdt.setText(receive.getStringExtra("sdt"));
            editMail.setText(receive.getStringExtra("mail"));
            imgg = receive.getByteArrayExtra("img");
            if (imgg != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(imgg,0,imgg.length);
                imageView.setImageBitmap(bitmap);
            }
        }
        if (id==-1) Info_Item.this.setTitle("New Contact");
        else Info_Item.this.setTitle("Edit Contact");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgg == null) chooseImage(); //khi khong co avatar
                else changeImage();          //khi da co avatar
            }
        });
    }
    public void changeImage(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_image);
        TextView txt_cam = dialog.findViewById(R.id.txtCam);
        TextView txt_ga = dialog.findViewById(R.id.txtGallery);
        TextView txt_del = dialog.findViewById(R.id.txt_del);

        txt_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder a = new AlertDialog.Builder(Info_Item.this);
                a.setMessage("Bạn có muốn xoá ?");
                a.setTitle("Cảnh Báo !");
                a.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                a.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        imageView.setImageResource(R.drawable.ic_person);  //Set Default
                        imgg = null;
                        Toast.makeText(Info_Item.this,"Xoá thành công !",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                a.show();
                dialog.dismiss();
            }
        });
        txt_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermissions()) {
                    takePictureFromCamera();
                    dialog.dismiss();
                }
            }
        });

        txt_ga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(Info_Item.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_GALLERY);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void chooseImage(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.new_image);
        TextView txt_cam = dialog.findViewById(R.id.txt_Cam);
        TextView txt_ga = dialog.findViewById(R.id.txt_Gallery);

        txt_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermissions()) {
                    takePictureFromCamera();
                    dialog.dismiss();
                }
            }
        });

        txt_ga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ActivityCompat.requestPermissions(Info_Item.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_GALLERY);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private boolean checkAndRequestPermissions(){
        if(Build.VERSION.SDK_INT >= 23){
            int cameraPermission = ActivityCompat.checkSelfPermission(Info_Item.this, Manifest.permission.CAMERA);
            if(cameraPermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(Info_Item.this, new String[]{Manifest.permission.CAMERA}, 20);
                return false;
            }
        }
        return true;
    }
    private void takePictureFromCamera(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePicture, 2);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == REQUEST_CODE_GALLERY){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
            else {
                Toast.makeText(getApplicationContext(), "Bạn chưa cấp quyền truy cập!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 2:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    Bitmap bitmapImage = (Bitmap) bundle.get("data");
                    imageView.setImageBitmap(bitmapImage);
                    imgg = imageViewToByte(imageView);
                }
                break;
        }
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            Bitmap bitmap = getDownsampledBitmap(uri,300,300);
            imageView.setImageBitmap(bitmap);
            imgg = imageViewToByte(imageView);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private Bitmap getDownsampledBitmap(Uri uri, int targetWidth, int targetHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options outDimens = getBitmapDimensions(uri);

            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, targetWidth, targetHeight);

            bitmap = downsampleBitmap(uri, sampleSize);

        } catch (Exception e) {
            //handle the exception(s)
        }
        return bitmap;
    }
    private BitmapFactory.Options getBitmapDimensions(Uri uri) throws FileNotFoundException, IOException {
        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true;
        InputStream is= getContentResolver().openInputStream(uri);
        BitmapFactory.decodeStream(is, null, outDimens);
        is.close();

        return outDimens;
    }
    private int calculateSampleSize(int width, int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;

        if (height > targetHeight || width > targetWidth) {
            final int heightRatio = Math.round((float) height / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
    private Bitmap downsampleBitmap(Uri uri, int sampleSize) throws FileNotFoundException, IOException {
        Bitmap resizedBitmap;
        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        outBitmap.inJustDecodeBounds = false; // the decoder will return a bitmap
        outBitmap.inSampleSize = sampleSize;

        InputStream is = getContentResolver().openInputStream(uri);
        resizedBitmap = BitmapFactory.decodeStream(is, null, outBitmap);
        is.close();

        return resizedBitmap;
    }
    public static byte[] imageViewToByte(ImageView image) {
        try {
            Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        } catch (Exception e) {
        return null;
        }
    }

    public void sendData(){
        String name = editName.getText().toString();
        String sdt = editSdt.getText().toString();
        String mail = editMail.getText().toString();
        byte[] img = imageViewToByte(imageView);
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(sdt)||TextUtils.isEmpty(mail)) {
            Toast.makeText(Info_Item.this,"Nhập đầy đủ thông tin",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id",id);
        returnIntent.putExtra("name",name);
        returnIntent.putExtra("sdt",sdt);
        returnIntent.putExtra("mail",mail);
        returnIntent.putExtra("img",img);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.mybutton){
            sendData();
        }
        else if (i == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}