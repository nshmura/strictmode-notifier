package com.nshmura.strictmodenotifier.testapp;

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
import android.security.NetworkSecurityPolicy;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.nshmura.strictmodenotifier.LogWatchService;
import com.nshmura.strictmodenotifier.ViolationType;
import com.nshmura.strictmodenotifier.ViolationTypeInfo;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ListView listView = (ListView) findViewById(R.id.list_view);
    //noinspection ConstantConditions
    listView.setAdapter(new Adapter());
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.menu_stop_service:
        stopService(new Intent(this, LogWatchService.class));
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void fireStrictModeError(ViolationTypeInfo info) {
    if (Build.VERSION.SDK_INT < info.minSdkVersion) {
      Toast.makeText(this, "min sdk version " + info.minSdkVersion, Toast.LENGTH_LONG).show();
      return;
    }

    switch (info) {
      case CUSTOM_SLOW_CALL:
        fireSlowCall();
        break;
      case NETWORK:
        fireNetwork();
        break;
      case RESOURCE_MISMATCHES:
        fireResourceMismatches();
        break;
      case CLASS_INSTANCE_LIMIT:
        fireClassInstanceLimit();
        break;
      case CLEARTEXT_NETWORK:
        fireCleartextNetwork();
        break;
      case FILE_URI_EXPOSURE:
        fireFileUriExposure();
        break;
      case LEAKED_CLOSABLE_OBJECTS:
        fireLeakedClosableObjects();
        break;
      case ACTIVITY_LEAKS:
        fireActivityLeaks();
        break;
      case LEAKED_REGISTRATION_OBJECTS:
        fireLeakedRegistrationObjects();
        break;
      case LEAKED_SQL_LITE_OBJECTS:
        fireLeakedSqlLiteObjects();
        break;
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
  @TargetApi(Build.VERSION_CODES.M) private void fireCleartextNetwork() {
    Runnable runnable = new Runnable() {
      @Override public void run() {
        try {
          Method method = NetworkSecurityPolicy.getInstance()
              .getClass()
              .getMethod("setCleartextTrafficPermitted", boolean.class);
          method.invoke(NetworkSecurityPolicy.getInstance(), false);

          URL url = new URL("http://google.com/");
          HttpURLConnection con = (HttpURLConnection) url.openConnection();
          con.setRequestMethod("GET");
          con.connect();

          method.invoke(NetworkSecurityPolicy.getInstance(), true);
        } catch (Exception e) {
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

  class Adapter extends BaseAdapter {

    private final ViolationType[] values;

    public Adapter() {
      ArrayList<ViolationType> list = new ArrayList<>(Arrays.asList(ViolationType.values()));
      list.remove(ViolationType.ACTIVITY_LEAKS);
      list.remove(ViolationType.LEAKED_REGISTRATION_OBJECTS);
      list.remove(ViolationType.LEAKED_SQL_LITE_OBJECTS);
      values = list.toArray(new ViolationType[list.size()]);
    }

    @Override public int getCount() {
      return values.length;
    }

    @Override public ViolationType getItem(int position) {
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
      final ViolationTypeInfo info = ViolationTypeInfo.convert(getItem(position));

      String text = String.format("%s error (minSdk %s)",
          ViolationTypeInfo.convert(info.violationType).violationName(),
          info.minSdkVersion);

      Button button = (Button) convertView.findViewById(R.id.button);
      button.setText(text);
      button.setEnabled(Build.VERSION.SDK_INT >= info.minSdkVersion);

      button.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          fireStrictModeError(info);
        }
      });

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        button.setAllCaps(false);
      }

      return convertView;
    }
  }
}