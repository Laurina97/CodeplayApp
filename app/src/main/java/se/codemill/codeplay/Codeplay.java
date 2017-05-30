package se.codemill.codeplay;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView.OnItemSelectedListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.widget.VideoView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;

import static se.codemill.codeplay.R.id.headerView;
import static se.codemill.codeplay.R.id.listItemID;
import static se.codemill.codeplay.R.id.videoView;
import static se.codemill.codeplay.R.layout.listitem;


public class Codeplay extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView listDescription;
    TextView listTitle;
    ListView listView;
    RelativeLayout videoBar;
    VideoView videoView;
    TextView videoTitle;
    TextView videoTime;
    TextView videoDescription;
    TextView videoCreator;
    TextView videoDate;
    Videolistadapter adapter;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final Intent intent;
        switch(position){
            case 1:
                intent = new Intent(Codeplay.this, SearchField.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(Codeplay.this, CodeplayUserListActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(Codeplay.this, LoginForm.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(Codeplay.this, LoginForm.class);
                startActivity(intent);
                break;
        }

        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codeplay);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        // get reference to the views
        listView = (ListView) findViewById(R.id.listView);
        videoBar = (RelativeLayout)inflater.inflate(R.layout.videobar, null);

        videoView = (VideoView)videoBar.findViewById(R.id.videoView);
        videoTitle = ((TextView) videoBar.findViewById(R.id.videoTitle));
        videoTime = ((TextView) videoBar.findViewById(R.id.videoTime));
        videoDescription = ((TextView) videoBar.findViewById(R.id.videoDescription));
        videoCreator = ((TextView) videoBar.findViewById(R.id.videoCreator));
        videoDate = ((TextView) videoBar.findViewById(R.id.videoDate));

        listView.addHeaderView(videoBar);



        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.menu_titles, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

        // Specify the interface implementation
        spinner.setOnItemSelectedListener(this);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);



        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.12.10.132:8000/listjson.php";

        // Request a string response from the provided URL.
        JsonArrayRequest stringRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                    adapter = new Videolistadapter(getApplicationContext(), listitem, response);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                       listView.smoothScrollToPosition(0);

                       System.out.println("#### setonitemclick " + position);

                       videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener()  {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                    @Override
                                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                          /*
                                           *  add media controller
                                           */
                                        final MediaController mediaController = new MediaController(Codeplay.this);
                                        videoView.setMediaController(mediaController);
                                          /*
                                           * and set its position on screen
                                           */
                                        mediaController.setAnchorView(videoView);
                                    }
                                });
                            }
                       });

                        // ListView Clicked item index
                        int itemPosition = position - 1;

                        JSONObject json = adapter.getItem(itemPosition);
                            System.out.println("#### jsonobject " + json.toString());
                        try {
                            videoView.setVideoPath("http://10.12.10.132:8000/videos/" + json.getString("Video"));
                            videoTitle.setText(json.getString("Title"));
                            videoTime.setText(json.getString("Time"));
                            videoDescription.setText(json.getString("Description"));
                            videoCreator.setText(json.getString("Creator"));
                            videoDate.setText(json.getString("Date"));

                            videoView.start();

                        } catch (Exception e) {
                            System.out.println("#### catch " + e.toString());
                        }

                        // ListView Clicked item value

                       // String  itemValue = (String) parent.getItemAtPosition(position);

                        // Show Alert
                        /*Toast.makeText(getApplicationContext(),
                                "Position :"+itemPosition+"  ListItem : " + itemValue , Toast.LENGTH_LONG)
                                .show(); */
                            }
                        });
                        }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("#### FEL!! " + error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}