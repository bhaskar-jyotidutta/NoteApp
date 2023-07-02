package com.example.architectureexample;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

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
            Without this app crash. Here fallbackToDestructiveMigration will delete the database if version number
            is incremented*/
            instance = Room.databaseBuilder(context.getApplicationContext(), NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration().build();

        }

        return instance;

    }

}
