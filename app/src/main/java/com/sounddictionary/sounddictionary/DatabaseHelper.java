package com.sounddictionary.sounddictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

    public class DatabaseHelper extends SQLiteOpenHelper {

        //Android's default system path of your application database.
        private static String DB_PATH = "/data/data/com.sounddictionary.sounddictionary/databases/";

        private static String DB_NAME = "dictionary.db";

        private SQLiteDatabase myDataBase;

        private final Context myContext;


         //Constructor, Takes and keeps a reference of the passed context in order to access to the application assets and resources.

        public DatabaseHelper(Context context) {

            super(context, DB_NAME, null, 1);
            this.myContext = context;
            DB_PATH= myContext.getDatabasePath(DB_NAME).getPath();
            Log.d("DB PATH","");
        }

        //Creates a empty database on the system and rewrites it with your own database.
        public void createDataBase() throws IOException {

            boolean dbExist = checkDataBase();

            if(!dbExist){
                this.getWritableDatabase();

                try {

                    copyDataBase();
                    Log.w("Copy Successfull","");

                } catch (IOException e) {

                    throw new Error("Error copying database");

                }

            }

        }

        //Check if the database already exist to avoid re-copying the file each time you open the application
        private boolean checkDataBase(){
            SQLiteDatabase checkDB = null;

            try{
                String myPath = DB_PATH ;
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

            }catch(SQLiteException e){

            }

            if(checkDB != null){

                checkDB.close();

            }

            return checkDB != null ? true : false;
        }


         //Copies your database from your local assets-folder to the just created empty database in the

        private void copyDataBase() throws IOException {

            //Open your local db as the input stream
            InputStream myInput = myContext.getAssets().open(DB_NAME);

            // Path to the just created empty db
            String outFileName = DB_PATH ;

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

        }

        public void openDataBase() throws SQLException {

            String myPath = DB_PATH ;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }

        @Override
        public synchronized void close() {

            if(myDataBase != null)
                myDataBase.close();

            super.close();

        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        // run query to search in the table for the definition of word passed. Returns the cursor pointing to the query result

        public Cursor getData(String word) throws SQLException {
            try {
                createDataBase();
                openDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Cursor meaningCursor = myDataBase.rawQuery("Select definition from Dictionary1 Where word=\""+word+"\"",null);
            return meaningCursor;
        }


}

