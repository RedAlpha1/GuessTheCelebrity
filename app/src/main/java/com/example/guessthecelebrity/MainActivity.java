package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
 ArrayList<String> celebsUrls = new ArrayList<String>();
    ArrayList<String> celebsname = new ArrayList<String>();
    int chooseSelect=0;
    int locationOfCorrectAnswer =0;
    String[] answers = new String[4];
    ImageView imageView;
    Button button0,button1,button2,button3;

    public void celebchoosen(View view) {

        Log.e("hello","chlega");

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
        {
            Toast.makeText(getApplicationContext(),"Correct!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"In Correct! "+celebsname.get(chooseSelect),Toast.LENGTH_LONG).show();

        }
    }

    public class ImageDownloder extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();
                connection1.connect();
                InputStream inputStreamReader = connection1.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStreamReader);
                return myBitmap;

            }catch (Exception e)
            {

            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, String, String>
    {

        @Override
        protected String doInBackground(String... urls) {

            String result ="";
            URL url;
            HttpURLConnection connection = null;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(15000);
                connection.setDoOutput(true);
                InputStream in  = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1)
                {
                    char current = (char) data;
                    result+=current;
                    data = reader.read();
                }
                return result;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageview1);
        button0 = findViewById(R.id.button);
        button1= findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3= findViewById(R.id.button3);
        DownloadTask task = new DownloadTask();
        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find())
            {
                celebsUrls.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while (m.find())
            {
               celebsname.add(m.group(1));
            }

            Random random = new Random();
            chooseSelect =random.nextInt(celebsUrls.size());
            ImageDownloder imageTask = new ImageDownloder();
            Bitmap celebImage;
            celebImage = imageTask.execute(celebsUrls.get(chooseSelect)).get();
            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectAnswerLocation;
            for (int i=0;i<4;i++){

                if(i==locationOfCorrectAnswer)
                {
                    answers[i]= celebsname.get(chooseSelect);
                }
                else
                {
                    incorrectAnswerLocation = random.nextInt(celebsUrls.size());

                    while (incorrectAnswerLocation==chooseSelect){
                        incorrectAnswerLocation = random.nextInt(celebsUrls.size());
                    }
                    answers[i] = celebsname.get(incorrectAnswerLocation);
                }

            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
