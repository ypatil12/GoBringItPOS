package com.example.gbiprint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gbiprint.connections.DBConnection;

import java.sql.Connection;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showOrders();
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSecondActivity();
            }
        });
    }

    private void goToSecondActivity() {
        /**
         * todo: add in connection to starprint SDK
         */

        Intent intent = new Intent(this, ConfirmationActivity.class);
        startActivity(intent);
    }

    private void showOrders(){
        TextView tv = (TextView)findViewById(R.id.textView2);
        DBConnection db = new DBConnection();
        Connection conn = db.CONN();
        System.out.println(conn);
        tv.setText("insert query from db");
    }

}
