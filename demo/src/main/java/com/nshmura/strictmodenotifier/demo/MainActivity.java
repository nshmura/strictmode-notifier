package com.nshmura.strictmodenotifier.demo;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ListView listView = (ListView) findViewById(R.id.list_view);
    //noinspection ConstantConditions
    listView.setAdapter(new Adapter());
  }

  private void fireStrictModeError(Actions action) {

    if (Build.VERSION.SDK_INT < action.minSdkVersion) {
      Toast.makeText(this, "min sdk version " + action.minSdkVersion, Toast.LENGTH_LONG).show();
      return;
    }

    switch (action) {
      case Slow_Call:
        fireSlowCall();
        break;
      case Network:
        fireNetwork();
        break;
      case Resource_Mismatches:
        fireResourceMismatches();
        break;
      case Class_Instance_Limit:
        fireClassInstanceLimit();
        break;
      case Cleartext_Network:
        fireCleartextNetwork();
        break;
      case File_Uri_Exposure:
        fireFileUriExposure();
        break;
      case Leaked_Closable_Objects:
        fireLeakedClosableObjects();
        break;
      /*
      case Leaked_Registration_Objects:
        fireLeakedRegistrationObjects();
        break;
      case Leaked_Sql_Lite_Objects:
        fireLeakedSqlLiteObjects();
        break;
       */
    }
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) private void fireSlowCall() {
    StrictMode.noteSlowCall("fireSlowCall");
  }

  private void fireNetwork() {
    try {
      URL url = new URL("http://google.com/");
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      con.connect();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void fireResourceMismatches() {
    TypedArray names = getResources().obtainTypedArray(R.array.sample_names);
    names.getInt(0, 0);
    names.recycle();
  }

  private void fireClassInstanceLimit() {
    ClassInstanceLimitActivity.start(this, 2);
  }

  // https://koz.io/android-m-and-the-war-on-cleartext-traffic/
  private void fireCleartextNetwork() {
    Runnable runnable = new Runnable() {
      @Override public void run() {
        try {
          URL url = new URL("http://google.com/");
          HttpURLConnection con = (HttpURLConnection) url.openConnection();
          con.setRequestMethod("GET");
          con.connect();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    new Thread(runnable).start();
  }

  private void fireFileUriExposure() {
    File file = Environment.getExternalStorageDirectory();
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("file://" + file.toString()));
    startActivity(intent);
  }

  private void fireLeakedClosableObjects() {
    LeakedClosableObjectsActivity.start(this);
  }

  //FIXME not working
  private void fireActivityLeaks() {

  }

  //FIXME not working
  private void fireLeakedRegistrationObjects() {
    Intent i = new Intent("LeakAction");
    sendBroadcast(i);
  }

  //FIXME not working
  private void fireLeakedSqlLiteObjects() {
    LeakedSQLiteOpenHelper helper = new LeakedSQLiteOpenHelper(getApplicationContext());
    SQLiteDatabase db = helper.getWritableDatabase();

    ContentValues values = new ContentValues();
    values.put("data", "data");
    db.insert("sample", null, values);

    Cursor cursor =
        db.query("sample", new String[] { "_id", "data" }, null, null, null, null, "_id DESC");
    cursor.moveToNext();
    cursor.close();
    //db.close();
  }

  enum Actions {
    // ThreadPolicy
    Slow_Call(Build.VERSION_CODES.HONEYCOMB),
    Network(Build.VERSION_CODES.GINGERBREAD),
    Resource_Mismatches(Build.VERSION_CODES.M),

    // VmPolicy
    Class_Instance_Limit(Build.VERSION_CODES.HONEYCOMB),
    Cleartext_Network(Build.VERSION_CODES.M),
    File_Uri_Exposure(Build.VERSION_CODES.JELLY_BEAN_MR2),
    Leaked_Closable_Objects(Build.VERSION_CODES.HONEYCOMB),
    //Activity_Leaks(Build.VERSION_CODES.HONEYCOMB),
    //Leaked_Registration_Objects(Build.VERSION_CODES.JELLY_BEAN),
    //Leaked_Sql_Lite_Objects(Build.VERSION_CODES.GINGERBREAD)
    ;

    public final int minSdkVersion;

    Actions(int minSdkVersion) {
      this.minSdkVersion = minSdkVersion;
    }
  }

  class Adapter extends BaseAdapter {

    private final Actions[] values;

    public Adapter() {
      values = Actions.values();
    }

    @Override public int getCount() {
      return values.length;
    }

    @Override public Actions getItem(int position) {
      return values[position];
    }

    @Override public long getItemId(int position) {
      return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {

      if (convertView == null) {
        convertView =
            LayoutInflater.from(MainActivity.this).inflate(R.layout.row_actions, parent, false);
      }
      final Actions action = getItem(position);

      String text = String.format("%s error (minSdk %s)", action.name().replace("_", " "),
          action.minSdkVersion);

      Button button = (Button) convertView.findViewById(R.id.button);
      button.setText(text);
      button.setEnabled(Build.VERSION.SDK_INT >= action.minSdkVersion);

      button.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          fireStrictModeError(action);
        }
      });

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        button.setAllCaps(false);
      }

      return convertView;
    }
  }
}