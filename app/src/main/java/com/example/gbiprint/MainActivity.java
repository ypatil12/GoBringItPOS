package com.example.gbiprint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gbiprint.connections.DBConnection;
import com.mysql.jdbc.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Main screen for activity
 *
 * @author Bryant Huang
 */
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
        Intent intent = new Intent(this, ConfirmationActivity.class);
        startActivity(intent);
    }

    /**
     * TODO: implement logging
     */
    private void showOrders(){
        TextView tv = (TextView)findViewById(R.id.textView2);
        DBConnection db = new DBConnection();
        Connection conn = db.CONN();
        System.out.println(conn);

        //set the query

        String query = "select * from orders where service_id=13 and button=1";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            Statement stmnt = (Statement) conn.createStatement();
            ResultSet rs = stmnt.executeQuery(query);
            System.out.println(rs);
            //EXAMPLE CODE TO GET INFO FROM RESULT SET
            while (rs.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supplierID = rs.getInt("SUP_ID");
                float price = rs.getFloat("PRICE");
                int sales = rs.getInt("SALES");
                int total = rs.getInt("TOTAL");
                System.out.println(coffeeName + "\t" + supplierID +
                        "\t" + price + "\t" + sales +
                        "\t" + total);
            }

        } catch (SQLException e) {
            System.out.println("The error is: " + e.getMessage());
            e.printStackTrace();
        }
        tv.setText("insert query from db");
    }

}
