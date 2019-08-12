package com.example.gbiprint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gbiprint.backend.updatedReadEmails;

import java.io.File;
import java.io.FileInputStream;


/**
 * Main screen for activity
 *
 * I think the idea is to have the button idea implemented as we do in the dankery code. Create
 * some polling mechanism which checks for button = 0 and displays the orders. Need to figure out
 * how to send information to receipt printer as a graphics file, thats the only format the
 * printer accepts.
 *
 * @author Bryant Huang
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSecondActivity();
            }
        });
        showOrders(getApplicationContext());
    }

    /**
     * this needs to send the data to printer
     */
    private void goToSecondActivity() {
        Intent intent = new Intent(this, ConfirmationActivity.class);
        startActivity(intent);
    }

    /**
     * TODO: figure out what data we actually need to display, connect the StarPrint sdk
     */
    private void showOrders(Context context){

        try {
            updatedReadEmails.runMain(this,context);
        } catch (Exception e) {
            System.out.println("The error is: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
