package com.example.tmapapi_firstattempt;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class URLConnector extends Thread {
    String temp;
    public void run() {

        // http 요청을 쏴서 그에 대한 결과값을 받아옵니다.
        final String output = request("http://amp.paasta.koren.kr/accident_query.php");

        // 결과값이 temp에 담깁니다.
        temp = output;
    }

    public String getTemp(){
        return temp;
    }

    private String request(String urlStr) {
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            System.out.println(url);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            System.out.println("1");
            if (conn != null) {
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                System.out.println("2");

                int resCode = conn.getResponseCode();
                System.out.println("3");
                if (resCode == HttpURLConnection.HTTP_OK) {

                    System.out.println("4");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())) ;
                    String line = "";
                    while(true) {
                        line = reader.readLine();
                        System.out.println("wefwef"+line);
                        if (line == null) {
                            break;
                        }
                        output.append(line + "\n");
                    }

                    reader.close();
                    conn.disconnect();
                }
            }
        } catch(Exception ex) {
            Log.e("SampleHTTP", "Exception in processing response.", ex);
            ex.printStackTrace();
        }
        System.out.println("output.toString():"+output.toString());

        return output.toString();
    }
}