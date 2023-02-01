package com.example.camerausecases.driverdrowsinessdetection.user_profile;

import android.graphics.Bitmap;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// below line is for setting table name.
@Entity(tableName = "user_profile")
public class User_Profile {

    // below line is to auto increment
    // id for each course.
    @PrimaryKey()

    // variable for our id.
    private int id;

    // below line is a variable
    // for  name.
    private String name;


    // below line is use for
    // save  image.
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image;

    public User_Profile(int id, String name, byte[] image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
