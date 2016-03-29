package com.nshmura.strictmodenotifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class ViolationStore {
  private static final String NAME = "strictmode";
  private static final String KEY = "reports";
  private static final int MAX_REPORTS = 50;

  private final Context context;

  public ViolationStore(Context context) {
    this.context = context;
  }

  /**
   * Read the object from Base64 string.
   *
   * @see {http://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string}
   */
  private static Object fromString(String s) throws IOException, ClassNotFoundException {
    byte[] data = Base64.decode(s, Base64.DEFAULT);
    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
    Object o = ois.readObject();
    ois.close();
    return o;
  }

  /**
   * Write the object to a Base64 string.
   *
   * @see {http://stackoverflow.com/questions/134492/how-to-serialize-an-object-into-a-string}
   */
  private static String toString(Serializable o) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(o);
    oos.close();
    return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
  }

  public ArrayList<StrictModeViolation> getAll() {
    try {
      String serialized = getPrefs().getString(KEY, null);
      if (serialized != null) {
        //noinspection unchecked
        return (ArrayList<StrictModeViolation>) fromString(serialized);
      }
    } catch (Exception e) {
      //ignore
    }
    return new ArrayList<>();
  }

  public void append(StrictModeViolation report) throws IOException {
    ArrayList<StrictModeViolation> reports = getAll();

    if (reports.size() > MAX_REPORTS) {
      reports.subList(0, MAX_REPORTS);
    }

    reports.add(0, report);
    getPrefs().edit().putString(KEY, toString(reports)).apply();
  }

  public void remove(StrictModeViolation target) {
    ArrayList<StrictModeViolation> reports = getAll();
    Integer targetIndex = null;
    for(int i = 0; i < reports.size(); i++) {
      StrictModeViolation report = reports.get(i);
      if (report.logKey.equals(target.logKey) && report.time == target.time) {
        targetIndex = i;
        break;
      }
    }
    if (targetIndex != null) {
      reports.remove(targetIndex.intValue());
      try {
        getPrefs().edit().putString(KEY, toString(reports)).apply();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void clear() {
    getPrefs().edit().clear().apply();
  }

  private SharedPreferences getPrefs() {
    return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
  }
}