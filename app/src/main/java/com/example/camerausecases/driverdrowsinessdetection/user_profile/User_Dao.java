package com.example.camerausecases.driverdrowsinessdetection.user_profile;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@androidx.room.Dao
public interface User_Dao {

    // below method is use to
    // add data to database.
    @Insert
    void insert(User_Profile model);

    // below method is use to update
    // the data in our database.
    @Update
    void update(User_Profile model);

    // below line is use to delete a
    // specific course in our database.
    @Delete
    void delete(User_Profile model);

    @Query("SELECT * FROM user_profile ORDER BY name ASC")
    LiveData<List<User_Profile>> getAllUser();
}
