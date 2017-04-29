package net.ddns.pytxy.codecrack;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by ludovic on 26/03/17.
 */
public class ScoreStorageLocal extends SQLiteOpenHelper implements ScoreStorage {
    private Context context;
    private String[] types = {"JAVA", "PHP", "JS", "HTML"};

    public ScoreStorageLocal(Context context) {
        super(context, "scoreboard", null, 1);
        this.context = context;
    }

    @Override
    public void saveScore(int score, String name, int type) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("INSERT INTO scoreboard VALUES (null, "
                + score + ",'"
                + name + "',"
                + (type-1) + ","
                + Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("list_difficulty", "60")) + ")");
        db.close();
    }

    @Override
    public ArrayList<String> listScoreboard(int max) {
        ArrayList<String> scores = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM scoreboard WHERE difficulty=" +
                Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString("list_difficulty", "60"))
                + " ORDER BY score DESC, type ASC LIMIT " + max, null);
        while (cursor.moveToNext())
            scores.add(cursor.getInt(1) + " | " + cursor.getString(2) + " | " + types[cursor.getInt(3)]);
        cursor.close();
        db.close();
        return scores;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE scoreboard (id INTEGER PRIMARY KEY AUTOINCREMENT, score INTEGER, name TEXT, type INTEGER, difficulty INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }
}
