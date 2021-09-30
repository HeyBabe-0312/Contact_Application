package com.example.contactapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.contactapplication.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ContactAdapter.OnNoteListener {
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 1;
    private ActivityMainBinding binding;
    private ContactAdapter contactAdapter;
    private ArrayList<Contact> contacts;
    private SearchView searchView;
    public static ContactHelper contactHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);

        contacts = new ArrayList<Contact>();
        contactAdapter = new ContactAdapter(contacts,this);
        binding.rcvContact.setLayoutManager(new LinearLayoutManager(this));
        binding.rcvContact.setAdapter(contactAdapter);
        contactHelper = new ContactHelper(this,"ContactsA.sql",null,1);
        //Create Table
        contactHelper.queryData("CREATE TABLE IF NOT EXISTS Contacts(Id INTEGER PRIMARY KEY AUTOINCREMENT, Ten VARCHAR(50), Sdt VARCHAR(15), Mail VARCHAR(50), image BLOB)");

        showData();

        binding.imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Info_Item.class);
                startActivityForResult(i, SECOND_ACTIVITY_REQUEST_CODE);
            }
        });
    }
    public void showData() {
        Cursor data = contactHelper.getData("SELECT * FROM Contacts ORDER BY Ten");
        contacts.clear();
        while(data.moveToNext()){
            byte[] img = data.getBlob(4);
            String mail = data.getString(3);
            String sdt = data.getString(2);
            String name = data.getString(1);
            int id = data.getInt(0);
            contacts.add(new Contact(id, name, sdt, mail, img));
        }
        contactAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.menu_setting, menu);

        MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setQueryHint("Nhập tên cần tìm");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Cursor data = contactHelper.getData("SELECT * FROM Contacts WHERE Ten LIKE '%"+s+"%' ORDER BY Ten");
                contacts.clear();
                while (data.moveToNext()){
                    byte[] img = data.getBlob(4);
                    String mail = data.getString(3);
                    String sdt = data.getString(2);
                    String name = data.getString(1);
                    int id = data.getInt(0);
                    contacts.add(new Contact(id, name, sdt, mail, img));
                }
                contactAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int id = data.getIntExtra("id",-1);
                String name = data.getStringExtra("name");
                String sdt = data.getStringExtra("sdt");
                String mail = data.getStringExtra("mail");
                byte[] img = data.getByteArrayExtra("img");
                if (id == -1) {
                    try{
                        contactHelper.insertData(name, sdt, mail, img);
                        Toast.makeText(getApplicationContext(), "Thêm thành công !", Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    try{
                        contactHelper.updateData(name, sdt, mail,img,id);
                        Toast.makeText(MainActivity.this,"Sửa thành công !",Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                showData();
            }
        }
    }

    @Override
    public void onNoteClick(int position) {
        String ten = contacts.get(position).getName();
        String sdt = contacts.get(position).getNum();
        String mail = contacts.get(position).getEmail();
        byte[] avaImage = contacts.get(position).getImg();

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.info_form);

        TextView eName = dialog.findViewById(R.id.editName);
        TextView eSdt = dialog.findViewById(R.id.editSdt);
        TextView eMail = dialog.findViewById(R.id.editMail);
        ImageView imgDel = dialog.findViewById(R.id.imgDel);
        ImageView imgAva = dialog.findViewById(R.id.img_ava);
        ImageView imgEdit = dialog.findViewById(R.id.imgEdit);
        ImageView imgOk = dialog.findViewById(R.id.imgOk);
        eSdt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        if(avaImage!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(avaImage, 0, avaImage.length);
            imgAva.setImageBitmap(bitmap);
        }else imgAva.setImageResource(R.drawable.ic_person);
        eName.setText(ten);
        eSdt.setText(sdt);
        eMail.setText(mail);
        imgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Info_Item.class);
                Cursor data = contactHelper.getData("SELECT * FROM Contacts WHERE Ten = '"+ten+"'");
                while (data.moveToNext()){
                    byte[] img = data.getBlob(4);
                    String mail = data.getString(3);
                    String sdt = data.getString(2);
                    String name = data.getString(1);
                    int id = data.getInt(0);

                    i.putExtra("id",id);
                    i.putExtra("name",name);
                    i.putExtra("sdt",sdt);
                    i.putExtra("mail",mail);
                    i.putExtra("img",img);
                }
                startActivityForResult(i, SECOND_ACTIVITY_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                a.setMessage("Bạn có muốn xoá "+ten+" ?");
                a.setTitle("Cảnh Báo !");
                a.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                a.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"Xoá thành công !",Toast.LENGTH_SHORT).show();
                        contactHelper.queryData("DELETE FROM Contacts WHERE Ten = '"+ten+"'");
                        dialog.dismiss();
                        showData();
                    }
                });
                a.show();
            }
        });
        dialog.show();
    }
}