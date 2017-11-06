/*
Sound dictionary application.
*/


package com.sounddictionary.sounddictionary;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

import static com.sounddictionary.sounddictionary.R.layout.activity_main;


public class MainActivity extends AppCompatActivity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener{

    DatabaseHelper db ;

    private GestureDetectorCompat mDetector;
    //string variables to store temporary results
    String language, word,meaning,result="",listenResult;

    //Object to the TextToSpeech class
    TextToSpeech ttsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);

        db = new DatabaseHelper(this);

        mDetector = new GestureDetectorCompat(this,this);

            //to set default language(not necessary)
            ttsObject = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if(i==TextToSpeech.SUCCESS){
                        ttsObject.setLanguage(Locale.ENGLISH);
                    }else{
                        Toast.makeText(getApplicationContext(),"Not Supported",Toast.LENGTH_LONG).show();
                    }
                }
            });




    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {

        }

        return super.onOptionsItemSelected(item);
    }



    //function to handle convert text to speach
    private void speakResult( String wordToSpeak) {
        if(ttsObject!= null){
            ttsObject.stop();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsObject.speak(wordToSpeak,TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    //function to find meaning of the input word
    private String findMeaning(String word) throws SQLException {

        Cursor cursor=db.getData(word);
        if(cursor.getCount()==0){
            return "No result found";
        }
        cursor.moveToFirst();
        result=cursor.getString(0);
        return result;
    }

    //function to handle convert speach to text
    private void listen() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.LANGUAGE_MODEL_FREE_FORM, RecognizerIntent.EXTRA_LANGUAGE_MODEL);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        startActivityForResult(intent, 10);

    }

    //function that recieves the result from the Sound Recognizer
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==10){
                if(resultCode== RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    word=result.get(0);
                    try {
                        meaning = findMeaning(word);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    speakResult(word+meaning);
                }
            }
    }




    //demo function to handle language change, not used in the app
    private void changeLanguage(String language) {
        ttsObject = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i==TextToSpeech.SUCCESS){
                    switch ("language"){
                        case "English":ttsObject.setLanguage(Locale.ENGLISH);
                            break;
                        case "Malayalam":ttsObject.setLanguage(Locale.ENGLISH);
                            break;
                    }

                }else{
                    Toast.makeText(getApplicationContext(),"Not Supported",Toast.LENGTH_LONG).show();
                }
            }
        });

    }




    @Override
    protected void onDestroy() {
        super.onDestroy();

        //in-order to stop audio output, if any
        if(ttsObject!= null){
            ttsObject.stop();
            ttsObject.shutdown();
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if(ttsObject!= null){
            ttsObject.stop();
        }
        //Toast.makeText(getApplicationContext(), "Listening for search word", Toast.LENGTH_LONG).show();
        listen();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
