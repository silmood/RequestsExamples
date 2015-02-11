package mx.devf.eventfulrequests;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.ArrayList;

import mx.devf.eventfulrequests.async.AsyncTaskRequest;
import mx.devf.eventfulrequests.model.Event;
import mx.devf.eventfulrequests.retrofit.request.EventfulApiClient;
import mx.devf.eventfulrequests.retrofit.request.EventsRequestModel;
import mx.devf.eventfulrequests.volley.VolleySingleton;
import retrofit.Callback;
import retrofit.RetrofitError;

import static android.view.View.OnClickListener;


public class MainActivity extends ActionBarActivity implements OnClickListener,AsyncTaskRequest.AsyncRespose{

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private Button btnAsync,btnVolley, btnRetrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAsync = (Button) findViewById(R.id.btn_asynctask);
        btnVolley = (Button) findViewById(R.id.btn_volley);
        btnRetrofit = (Button) findViewById(R.id.btn_retrofit);

        btnAsync.setOnClickListener(this);
        btnVolley.setOnClickListener(this);
        btnRetrofit.setOnClickListener(this);

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

    @Override
    public void onClick(View viewClicked) {
        switch (viewClicked.getId()){
            case R.id.btn_asynctask:
                new AsyncTaskRequest(this)
                        .execute("San Diego");
                break;

            case R.id.btn_volley:
                volleyEventfulRequest("San Diego");
                break;

            case R.id.btn_retrofit:
                retrofitEventfulRequest("San Diego");
                break;

            default:
                break;
        }
    }

    private void retrofitEventfulRequest(String location) {
        EventfulApiClient retrofitClient = new EventfulApiClient();

        retrofitClient.getApiContract().findEvents(EventfulApiKeys.APP_KEY , EventfulApiKeys.VALUE_THIS_WEEK , location,
            new Callback<EventsRequestModel.EventsModelResponse>() {
                @Override
                public void success(EventsRequestModel.EventsModelResponse eventsModelResponse, retrofit.client.Response response) {
                    for(EventsRequestModel.EventGson event: eventsModelResponse.getListEvents())
                        Log.i(LOG_TAG, event.getTitle());
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
    }

    public void volleyEventfulRequest(String location) {

        StringRequest eventfulRequest = new StringRequest(Request.Method.GET, EventfulApiKeys.getSearchEventsUrl(location),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            for(Event event : EventfulApiKeys.parseEventsFromJson(response) )
                                Log.i(LOG_TAG, event.getTitle());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(LOG_TAG, error.toString());
                    }
                });

        VolleySingleton.getInstance(this)
                .addToRequestQueue(eventfulRequest);
    }

    @Override
    public void onResponse(ArrayList<Event> events) {
        for (Event event : events)
            Log.i(LOG_TAG, event.getTitle());
    }

    @Override
    public void onError() {

    }
}
