package com.example.architectureexample;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase{

    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    /*Synchronized is used here so that no two thread can get the method simultaneously
    so that no two database is created*/
    public static synchronized NoteDatabase getInstance(Context context){

        //This makes NoteDatabase a singleton class, ie same object will be returned if already created
        if(instance == null){

            /*Since it is an abstract class so we can not create an object using new keyword. Here we use builder method.
            When we increment the version number of the database then we have to tell Room how to migrate to the new scheme
            Without this app crash. Here fallbackToDestructiveMigration will delete the database and recreated if version number
            is incremented*/
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();

        }

        return instance;

    }

    /*To add some default notes before we add some notes by our selves we use onCrete method*/
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{

        private NoteDao noteDao;

        public PopulateDbAsyncTask(NoteDatabase db){
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1", "Description 1", 1));
            noteDao.insert(new Note("Title 2", "Description 2", 2));
            noteDao.insert(new Note("Title 3", "Description 3", 3));
            return null;
        }
    }

}
