package com.pdachoice.restclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class ScreenOneFragment extends Fragment /* implements OnClickListener */{

  private View contentView;
  private Button buttonGet;
  private Button buttonPost;
  private EditText editText;
  private WebView webView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    contentView = inflater.inflate(R.layout.screenone_fragment, container,
        false);

    buttonGet = (Button) contentView.findViewById(R.id.buttonGet);
    // buttonGet.setOnClickListener(this);
    buttonGet.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        performGet();
      }
    });

    buttonPost = (Button) contentView.findViewById(R.id.buttonPost);
    // buttonPost.setOnClickListener(this);
    buttonPost.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        performPost();
      }
    });

    editText = (EditText) contentView.findViewById(R.id.editTextEcho);
    webView = (WebView) contentView.findViewById(R.id.webViewResponse);
    return contentView;
  }

  private static String SERVER_URL = "http://pdachoice.com/ras/service/echo";
  private void performGet() {
    
    // TODO
    AsyncTask<View, Float, String> task = new AsyncTask<View, Float, String>() {
      @Override
      protected void onPreExecute() {
        // do something in the UI thread before starting doInBackground,
        // For example, show wait cursor or dismiss keyboard.
        dismissKeyboard();
      }

      @Override
      protected String doInBackground(View... parms) {
        Log.d("AsyncTask", ">>>doInBackground");
        String resp = null;
        try {
          String pathParm = editText.getText().toString();
          pathParm = URLEncoder.encode(pathParm, "UTF-8");
          resp = httpGet(SERVER_URL + "/" + pathParm);
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
        Log.d("", resp);
        return resp;
      }

      @Override
      protected void onProgressUpdate(Float... progress) {
        Log.d("AsyncTask", ">>>onProgressUpdate");
        // do something in the UI thread during doInBackground,
        // For example, update progress bar
        float downloadedSize = progress[0];
        float expectedSize = progress[1];
      }

      @Override
      protected void onPostExecute(String result) {
        processResultData(result);
      }

    };
    task.execute(buttonGet);
  }

  private void dismissKeyboard() {
    Log.d("AsyncTask", ">>>onPreExecute");
    InputMethodManager imm = (InputMethodManager) getActivity()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
  }

  private void processResultData(String result) {
    Log.d("AsyncTask", ">>>onPostExecute: " + result);
    String htmlString = "";
    try {
      result = URLDecoder.decode(result, "UTF-8");
      JSONObject jo = new JSONObject(result); // JSON String to
                                              // JSONObject
      Iterator<String> keys = jo.keys();
      String key, value;
      while (keys.hasNext()) {
        key = keys.next();
        value = jo.getString(key); // get(...), getXXX, etc.
        htmlString += key + " - " + value;
      }

    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      htmlString = result;
      e.printStackTrace();
    }

    webView.loadData(htmlString, "text/html", "UTF-8");
  }

  private void performPost() {
    AsyncTask<View, Float, String> task = new AsyncTask<View, Float, String>() {
      @Override
      protected void onPreExecute() {
        // do something in the UI thread before starting doInBackground,
        // For example, show wait cursor.
        dismissKeyboard();
      }

      @Override
      protected String doInBackground(View... parms) {
        Log.d("AsyncTask", ">>>doInBackground");

        String resp = null;
        try {
          // form data: key=value&...
          String data = editText.getText().toString();
          data = URLEncoder.encode(data, "UTF-8");
          String formData = "echo=" + data;
          resp = httpPost(SERVER_URL, formData);
        } catch (UnsupportedEncodingException e) {
          e.printStackTrace();
        }
        Log.d("", resp);
        return resp;
      }

      @Override
      protected void onProgressUpdate(Float... progress) {
        Log.d("AsyncTask", ">>>onProgressUpdate");
        // do something in the UI thread during doInBackground,
        // For example, update progress bar
      }

      @Override
      protected void onPostExecute(String result) {
        processResultData(result);
      }

    };
    task.execute(buttonPost);
  }


  // GET data from url
  String httpGet(String myurl) {
    InputStream in = null;
    HttpURLConnection conn = null;

    String httpBody = null;
    try {
      URL url = new URL(myurl);
      // create an HttpURLConnection by openConnection
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
//      conn.setRequestProperty("accept", "application/json");

      int rc = conn.getResponseCode(); // HTTP status code
      String rm = conn.getResponseMessage(); // HTTP response message.
      Log.d("d", String.format("HTTP GET: %d %s", rc, rm));

      // read message body from connection InputStream
      in = conn.getInputStream(); // get inputStream to read data.
      httpBody = readStream(in);
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      conn.disconnect();
    }

    return httpBody;
  }

  // a simple util method that converts InputStream to a String.
  String readStream(InputStream stream) {
    java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  // Send POST data to server
  String httpPost(String myurl, String formData) {
    HttpURLConnection conn = null;
    String httpBody = null;
    try {
      URL url = new URL(myurl);
      // create an HttpURLConnection by openConnection
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      // conn.setRequestProperty("accept", "application/json");

      OutputStream out = new BufferedOutputStream(conn.getOutputStream());
      out.write(formData.getBytes());
      out.close();

      // To handle HTTP response. Same as HTTP GET
      int rc = conn.getResponseCode(); // HTTP status code
      String rm = conn.getResponseMessage(); // HTTP response message
      Log.d("d", String.format("HTTP POST: %d %s", rc, rm));

      // read message body from connection InputStream
      InputStream in = new BufferedInputStream(conn.getInputStream());
      httpBody = readStream(in);
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      conn.disconnect();
    }
    return httpBody;
  }

}
