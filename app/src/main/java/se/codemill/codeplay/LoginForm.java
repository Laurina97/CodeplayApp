package se.codemill.codeplay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class LoginForm extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ListView listView;
    boolean mFirstTime = true;


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final Intent intent;
        System.out.println("#### on item selected" + position);
        if (mFirstTime) {
            mFirstTime = false;
            return;
        }

        switch (position) {
            case 0:
                intent = new Intent(LoginForm.this, Codeplay.class);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(LoginForm.this, SearchField.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(LoginForm.this, CodeplayUserListActivity.class);
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
        setContentView(R.layout.login);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        // get reference to the views
        listView = (ListView) findViewById(R.id.listView);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.menu_titles, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);

        spinner.setSelection(2, false);

        // Specify the interface implementation
        spinner.setOnItemSelectedListener(this);

        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(spinnerAdapter);
    }
}