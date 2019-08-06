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
import java.sql.ResultSet;
import java.sql.SQLException;

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
        showOrders();
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSecondActivity();
            }
        });
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
    private void showOrders(){
        TextView tv = (TextView)findViewById(R.id.textView2);
        DBConnection db = new DBConnection();
        Connection conn = db.CONN();
        System.out.println(conn);

        //set the query

        String query = "select * from orders where service_id=13 and button=1";
        String customer_name = "";
        String date = "" ;
        String orderName = "";
        double price = -1;
        double quantity = -1;
        try {
            Statement stmnt = (Statement) conn.createStatement();
            ResultSet rs = stmnt.executeQuery(query);
            System.out.println(rs);
            //gets the order information from orders DB
            //todo: should move this to another class
            while (rs.next()) {
                customer_name = rs.getString("user_name");
                date = rs.getDate("datetime").toString();
                int order_id= rs.getInt("order_id");
                String query2 = "select * from carts where order_id =" + order_id;
                Statement stmnt2 = (Statement) conn.createStatement();
                ResultSet rs2 = stmnt2.executeQuery(query2);
                //gets the name, price, and quantity from the order list
                while(rs2.next()) {
                    orderName = rs.getString("name");
                    price = rs.getDouble("price");
                    quantity = rs.getInt("quantity");
                }
            }

        } catch (SQLException e) {
            System.out.println("The error is: " + e.getMessage());
            e.printStackTrace();
        }
        tv.setText(customer_name + date + orderName + price + quantity);
    }

}
