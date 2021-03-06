package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.jokesdisplayer.DisplayJokeActivity;
import com.example.jokesprovider.tellMeJoke;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;
import com.udacity.gradle.builditbigger.backend.myApi.model.MyBean;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    public static SimpleIdlingResource mIdlingResource;

    @Nullable
    @VisibleForTesting
    public static IdlingResource getIdleResource() {
        if (mIdlingResource == null)
            mIdlingResource = new SimpleIdlingResource();
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get idle resource instance for test
        getIdleResource();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {

        new EndpointsAsyncTask().execute(new tellMeJoke().getJoke());


        //   Toast.makeText(this, mGetJoke , Toast.LENGTH_SHORT).show();
    }

    public  class EndpointsAsyncTask  extends AsyncTask<String, Void, String> {
        private MyApi myApiService = null;
        AsyncTaskListener mListener ;

        public  EndpointsAsyncTask setListener(AsyncTaskListener listener){
            this.mListener = listener ;
            return this ;
        }

        @Override
        protected void onPreExecute() {
            // make test waite until background work finished
            mIdlingResource.setIdleState(false);
        }

        @Override
        protected String doInBackground(String... params) {
            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("http://192.168.1.5:8080/_ah/api")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver
                myApiService = builder.build();
            }

            try {
                return myApiService.sayHi(params[0]).execute().getData();
            } catch (IOException e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
// make test waite until background work finished
            mIdlingResource.setIdleState(true);
            if (mListener != null) this.mListener.onComplete(result);

            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Intent displayJoke = new Intent(MainActivity.this, DisplayJokeActivity.class);
            displayJoke.putExtra("joke", result);
            startActivity(displayJoke);
        }

    }

    public static interface AsyncTaskListener {
        public String onComplete(String result);

    }
}
