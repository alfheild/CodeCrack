package net.ddns.pytxy.codecrack;

import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by ludovic on 26/03/17.
 */
public class ScoreStorageFirebase implements ScoreStorage {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Context context;
    private String[] types = {"JAVA", "PHP", "JS", "HTML"};

    public ScoreStorageFirebase(Context context) {
        this.context = context;
    }

    @Override
    public void saveScore(int score, String name, int type) {
        Score uscore = new Score(
                name,
                score,
                Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("list_difficulty", "60")),
                (type - 1));
        database.getReference().child("scoreboard").push().setValue(uscore);
    }

    @Override
    public ArrayList<String> listScoreboard(int max) {
        try {
            ReadFirebase tarea = new ReadFirebase();
            tarea.execute(max);
            return tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
//            Toast.makeText(context, "Servidor no disponible", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private class ReadFirebase extends AsyncTask<Integer, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Integer... integers) {
            ArrayList<String> data = new ArrayList<>();
            try {
                URL url = new URL("https://codecrack-9da03.firebaseio.com/scoreboard.json");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    JSONObject json = new JSONObject(reader.readLine());
                    Iterator<String> iter = json.keys();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        try {
                            JSONObject value = (JSONObject) json.get(key);
                            if ((int) value.get("difficulty") ==
                                    Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("list_difficulty", "60")))
                                data.add(value.getInt("score") + " | " + value.get("name") + " | " + types[value.getInt("type")]);
                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Collections.sort(data);
            Collections.reverse(data);
            return data;
        }
    }
}