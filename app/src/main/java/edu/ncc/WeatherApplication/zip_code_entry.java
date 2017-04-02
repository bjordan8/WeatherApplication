package edu.ncc.WeatherApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by bjord on 4/2/2017.
 */

public class zip_code_entry extends AppCompatActivity {
    Button enter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zip_code_dialogue_layout);

        enter = (Button)findViewById(R.id.enter_button);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText)findViewById(R.id.zip_code_box);
                String result = editText.getText().toString();
                Log.i("The result zip", "Zip code is " + result);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result",result);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }
}
