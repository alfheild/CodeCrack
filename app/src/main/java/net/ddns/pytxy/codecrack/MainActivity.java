package net.ddns.pytxy.codecrack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public final static int TYPE_JAVA = 1;
    public final static int TYPE_PHP = 2;
    public final static int TYPE_JS = 3;
    public final static int TYPE_HTML = 4;

    public static ScoreStorage scoreboard;

    @Override
    protected void onResume() {
        super.onResume();
        setScoreStorage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setScoreStorage();

        // Buttons
        ((Button) findViewById(R.id.b_java)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(TYPE_JAVA);
            }
        });
        ((Button) findViewById(R.id.b_html)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(TYPE_HTML);
            }
        });
        ((Button) findViewById(R.id.b_php)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(TYPE_PHP);
            }
        });
        ((Button) findViewById(R.id.b_js)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGame(TYPE_JS);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startGame(int code) {
        Intent i = new Intent(MainActivity.this, Game.class);
        i.putExtra("type", code);
        startActivityForResult(i, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            //scoreboard
            if (scoreboard != null) {
                scoreboard.saveScore(data.getExtras().getInt("score"),
                        PreferenceManager.getDefaultSharedPreferences(this).getString("user_name", "Anonymous"),
                        data.getExtras().getInt("type"));
                openScoreboard();
            }
        }
    }

    private void openScoreboard() {
        startActivity(new Intent(this, Scoreboard.class));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scoreboard) {
            openScoreboard();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher)
                    .setTitle(R.string.app_name)
                    .setView(messageView)
                    .create()
                    .show();
        } else if (id == R.id.nav_live) {
            String url = "http://pytxy.ddns.net/res/live.html";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setScoreStorage() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("score_storage", false)) {
            //online
            scoreboard = new ScoreStorageFirebase(this);
        } else {
            //offline
            scoreboard = new ScoreStorageLocal(this);
        }
    }
}
