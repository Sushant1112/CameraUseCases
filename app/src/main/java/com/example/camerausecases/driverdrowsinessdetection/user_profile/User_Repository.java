package com.example.camerausecases.driverdrowsinessdetection.user_profile;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class User_Repository {
    private User_Dao dao;
    private LiveData<List<User_Profile>> allUser;

    // creating a constructor for our variables
    // and passing the variables to it.
    public User_Repository(Application application) {
        User_Database database = User_Database.getInstance(application);
        dao = database.User_Dao();
       allUser = dao.getAllUser();
    }

    // creating a method to insert the data to our database.
    public void insert(User_Profile model) {
        new InsertUserAsyncTask(dao).execute(model);
    }

    // creating a method to update data in database.
    public void update(User_Profile model) {
        new UpdateUserAsyncTask(dao).execute(model);
    }

    // creating a method to delete the data in our database.
    public void delete(User_Profile model) {
        new DeleteUserAsyncTask(dao).execute(model);
    }

    // below is the method to delete all the courses.


    // below method is to read all the courses.
    public LiveData<List<User_Profile>> getAllUsers() {
        return allUser;
    }

    // we are creating a async task method to insert new course.
    private static class InsertUserAsyncTask extends AsyncTask<User_Profile, Void, Void> {
        private User_Dao dao;

        private InsertUserAsyncTask(User_Dao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(User_Profile... model) {
            // below line is use to insert our modal in dao.
            dao.insert(model[0]);
            return null;
        }
    }

    // we are creating a async task method to update our course.
    private static class UpdateUserAsyncTask extends AsyncTask<User_Profile, Void, Void> {
        private User_Dao dao;

        private UpdateUserAsyncTask(User_Dao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(User_Profile... models) {
            // below line is use to update
            // our modal in dao.
            dao.update(models[0]);
            return null;
        }
    }

    // we are creating a async task method to delete course.
    private static class DeleteUserAsyncTask extends AsyncTask<User_Profile, Void, Void> {
        private User_Dao dao;

        private DeleteUserAsyncTask(User_Dao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(User_Profile... models) {
            // below line is use to delete
            // our course modal in dao.
            dao.delete(models[0]);
            return null;
        }
    }

    // we are creating a async task method to delete all courses.

}
