package de.t_dankworth.secscanqr.activities.generator;


import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.MultiFormatWriter;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.MainActivity;
import de.t_dankworth.secscanqr.util.GeneralHandler;

/**
 * Created by Thore Dankworth
 * Last Update: 13.12.2019
 * Last Update by Thore Dankworth
 *
 * This class is all about the geo location to QR-Code Generate Activity.
 */
public class GeoGeneratorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText tfLatitude, tfLongtitude;
    CheckBox cbLatitude, cbLongtitude;
    Boolean north = true, east = true;
    int format;
    Button btnGenerate;
    String latitude, longtitude, geo;
    MultiFormatWriter multiFormatWriter;

    final Activity activity = this;
    private static final String STATE_LATITUDE = MainActivity.class.getName();
    private static final String STATE_LONGTITUDE = "";
    private static final String STATE_NORTH = "north";
    private static final String STATE_EAST = "east";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneralHandler generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_geo_generator);
        tfLatitude = (EditText) findViewById(R.id.tfLatitude);
        tfLongtitude = (EditText) findViewById(R.id.tfLongtitude);
        cbLatitude = (CheckBox) findViewById(R.id.cbLatitude);
        cbLongtitude = (CheckBox) findViewById(R.id.cbLongtitude);
        btnGenerate = (Button) findViewById(R.id.btnGenerateGeo);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitude = tfLatitude.getText().toString().trim();
                longtitude = tfLongtitude.getText().toString().trim();
                if(latitude.isEmpty() || longtitude.isEmpty()){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_geo_first), Toast.LENGTH_SHORT).show();
                } else {
                    multiFormatWriter = new MultiFormatWriter();
                    try{
                        String temp1 = "geo:";
                        String temp2 = ",";
                        if(!north)
                            temp1 = "geo:-";
                        if(!east)
                            temp2 = ",-";
                        geo = temp1 + latitude + temp2 + longtitude;
                        openResultActivity();
                    } catch (Exception e){
                        Toast.makeText(activity.getApplicationContext(), getResources().getText(R.string.error_generate), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.formats_geo_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //If the device were rotated then restore information
        if(savedInstanceState != null){
            latitude = (String) savedInstanceState.get(STATE_LATITUDE);
            tfLatitude.setText(latitude);
            longtitude = (String) savedInstanceState.get(STATE_LONGTITUDE);
            tfLongtitude.setText(longtitude);
            north = (Boolean) savedInstanceState.get(STATE_NORTH);
            cbLatitude.setChecked(north);
            east = (Boolean) savedInstanceState.get(STATE_EAST);
            cbLongtitude.setChecked(east);
        }
    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString(STATE_LATITUDE, latitude);
        savedInstanceState.putString(STATE_LONGTITUDE, longtitude);
        savedInstanceState.putBoolean(STATE_NORTH, north);
        savedInstanceState.putBoolean(STATE_EAST, east);
    }

    /**
     * Generates the chosen format from the spinner menu
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String compare = parent.getItemAtPosition(position).toString();
        format = 9;
        if(compare.equals("AZTEC")){
            format = 10;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        format = 9;
    }

    /**
     * Handles functionality behind the Checkboxes
     */
    public void onClickCheckboxes(View v){
        // ottimizzata
        north = cbLatitude.isChecked();
        east = cbLongtitude.isChecked();
    }

    /**
     *  This method will launch a new Activity were the generated QR-Code will be displayed.
     */
    private void openResultActivity(){
        Intent intent = new Intent(this, GeneratorResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CODE", geo);
        bundle.putInt("FORMAT", format);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
