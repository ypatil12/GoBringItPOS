package com.example.gbiprint.connections;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Your IP address must be static otherwise this will not work. You //can get your Ip address
    //From Network and security in Windows.
    String ip = "us-cdbr-iron-east-02.cleardb.net:3306";
    // This is default if you are using JTDS driver.
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    // Name Of your database.
    String db = "heroku_90543e805916f04?reconnect=true";
    String un = "bb03b38239159d";
    String password = "55716adb";

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL;
        System.out.println("inside conn");
        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + "/" + db + ";user=" + un + ";password="
                    + password + ";";
            System.out.println(ConnURL);
            conn = DriverManager.getConnection(ConnURL);
            System.out.println(conn);
        }
        catch (SQLException se)
        {
            System.out.println("SQL ERROR");
            Log.e("safiya", se.getMessage());
            System.out.println(se.getErrorCode());
            System.out.println(se.getSQLState());

        }
        catch (ClassNotFoundException e) {
            System.out.println("CLASS WAS NOT FOUND");
        }
        catch (Exception e) {
            System.out.println("SOMETHING ELSE");
            Log.e("error", e.getMessage());
        }
        return conn;
    }
}