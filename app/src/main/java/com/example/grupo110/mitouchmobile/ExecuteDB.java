package com.example.grupo110.mitouchmobile;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.ResultSet;

/**
 * Created by Jonathan on 26/08/2016.
 */
public class ExecuteDB extends AsyncTask<String,Void,ResultSet> {

    private Connection connection;
    private String query;

    public ExecuteDB(Connection connection, String query) {
        this.connection = connection;
        this.query = query;
    }

    @Override
    protected ResultSet doInBackground(String... params) {
        ResultSet resultSet = null;
        try{
            resultSet = connection.prepareStatement(query).executeQuery();
        }catch (Exception e){
            System.out.println("Error: Query! doInBackground catch 1");
            System.out.println("Error:" + e.toString());
        }finally {
            try {
                connection.close();
            }catch (Exception ex){
                System.out.println("Error: doInBackground catch 2");

            }
        }
        return resultSet;
    }
}