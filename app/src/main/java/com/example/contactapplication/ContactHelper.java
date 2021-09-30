package com.example.contactapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class ContactHelper extends SQLiteOpenHelper {
    public ContactHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public void queryData(String s){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(s);
    }
    public void insertData(String name,String sdt, String mail, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        if(image==null){
            String sql = "INSERT INTO Contacts VALUES (NULL, ?, ?, ?, NULL)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, name);
            statement.bindString(2, sdt);
            statement.bindString(3, mail);

            statement.executeInsert();
        }
        else {
            String sql = "INSERT INTO Contacts VALUES (NULL, ?, ?, ?, ?)";
            SQLiteStatement statement = database.compileStatement(sql);
            statement.clearBindings();
            statement.bindString(1, name);
            statement.bindString(2, sdt);
            statement.bindString(3, mail);
            statement.bindBlob(4, image);

            statement.executeInsert();
        }
    }
    public void updateData(String name, String sdt, String mail, byte[] image, int id) {
        SQLiteDatabase database = getWritableDatabase();
        if(image!=null) {
            String sql = "UPDATE Contacts SET Ten = ?, Sdt = ?, Mail = ?, image = ? WHERE Id = ?";
            SQLiteStatement statement = database.compileStatement(sql);

            statement.bindString(1, name);
            statement.bindString(2, sdt);
            statement.bindString(3, mail);
            statement.bindBlob(4, image);
            statement.bindDouble(5, (double) id);

            statement.execute();
            database.close();
        }else {
            String sql = "UPDATE Contacts SET Ten = ?, Sdt = ?, Mail = ?, image = null WHERE Id = ?";
            SQLiteStatement statement = database.compileStatement(sql);

            statement.bindString(1, name);
            statement.bindString(2, sdt);
            statement.bindString(3, mail);
            statement.bindDouble(4, (double) id);

            statement.execute();
            database.close();
        }
    }
    public Cursor getData(String s){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(s,null);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
