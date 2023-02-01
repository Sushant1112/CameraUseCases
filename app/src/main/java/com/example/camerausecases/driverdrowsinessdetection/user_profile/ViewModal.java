package com.example.camerausecases.driverdrowsinessdetection.user_profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModal extends AndroidViewModel {

    // creating a new variable for course repository.
    private User_Repository repository;

    // below line is to create a variable for live
    // data where all the courses are present.
    private LiveData<List<User_Profile>> allUsers;

    // constructor for our view modal.
    public ViewModal(@NonNull Application application) {
        super(application);
        repository = new User_Repository(application);
        allUsers = repository.getAllUsers();
    }

    // below method is use to insert the data to our repository.
    public void insert(User_Profile model) {
        repository.insert(model);
    }

    // below line is to update data in our repository.
    public void update(User_Profile model) {
        repository.update(model);
    }

    // below line is to delete the data in our repository.
    public void delete(User_Profile model) {
        repository.delete(model);
    }

    // below method is to get all the courses in our list.
    public LiveData<List<User_Profile>> getAllUser() {
        return allUsers;
    }

}
