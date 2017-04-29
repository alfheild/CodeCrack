package net.ddns.pytxy.codecrack;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game extends AppCompatActivity {

    private MyArrayAdapter mAdapter;
    private ListView mListView;
    private boolean mSortable = false; // ソート中かどうか
    private String mDragString; // ドラッグ中のオブジェクト
    private int mPosition = -1; // ドラッグ位置
    private int type;
    private int difficulty;
    private int score = 0;

    private ArrayList<String> original;
    private ProgressBar mProgressBar;
    private CountDownTimer mCountDownTimer;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        setTitle(":thinking:");
        type = getIntent().getExtras().getInt("type");
        mListView = (ListView) findViewById(R.id.listview);
        mAdapter = new MyArrayAdapter(this, R.layout.row_some);
        difficulty = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("list_difficulty", "60"));

        String[] data = loadFromAssets(type);
        for (String p : data) {
            mAdapter.add(p);
        }

        original = (ArrayList<String>) mAdapter.getmStrings().clone();
        mAdapter.shuffle();
        while (original.equals(mAdapter.getmStrings())) mAdapter.shuffle();
        mListView.setAdapter(mAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setMax(difficulty);
        mProgressBar.setProgress(mProgressBar.getMax());
        mCountDownTimer = new CountDownTimer(difficulty * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int prog = mProgressBar.getMax() - (int) Math.round(millisUntilFinished / 1000.0);
                score = (int) millisUntilFinished;
                mProgressBar.setProgress(prog);
            }

            @Override
            public void onFinish() {
                mProgressBar.setProgress(mProgressBar.getMax());
                endGame(0);
            }
        };
        mCountDownTimer.start();
        // 大事な所
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!mSortable) {
                    return false;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        // 現在のポジションを取得し
                        int position = mListView.pointToPosition((int) event.getX(), (int) event.getY());
                        if (position < 0) {
                            break;
                        }
                        // 移動が検出されたら入れ替え
                        if (position != mPosition) {
                            mPosition = position;
                            mAdapter.remove(mDragString);
                            mAdapter.insert(mDragString, mPosition);
                        }
                        return true;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE: {
                        stopDrag();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void startDrag(String string) {
        mPosition = -1;
        mSortable = true;
        mDragString = string;
        mAdapter.notifyDataSetChanged(); // ハイライト反映・解除の為
    }

    public void stopDrag() {
        mPosition = -1;
        mSortable = false;
        mDragString = null;
        mAdapter.notifyDataSetChanged(); // ハイライト反映・解除の為
        if (mAdapter.getmStrings().equals(original))
            endGame(score);
    }

    private void endGame(int score) {
        mCountDownTimer.cancel();
        Intent intent = new Intent();
        intent.putExtra("score", score);
        intent.putExtra("type", type);
        setResult(RESULT_OK, intent);
        finish();
    }

    private String[] readAssetsFile(String file) {
        String[] data = {"Error", "Try Again Later."};
        try {
            InputStream is = getAssets().open(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            List<String> list = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
            br.close();
            if (list.size() > 0) data = list.toArray(new String[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String[] loadFromAssets(int type) {
        String[] data = null;
        switch (type) {
            case MainActivity.TYPE_JAVA: {
                data = readAssetsFile("strings_java");
                setTitle(R.string.title_java);
                break;
            }
            case MainActivity.TYPE_HTML: {
                data = readAssetsFile("strings_html");
                setTitle(R.string.title_html);
                break;
            }
            case MainActivity.TYPE_PHP: {
                data = readAssetsFile("strings_php");
                setTitle(R.string.title_php);
                break;
            }
            case MainActivity.TYPE_JS: {
                data = readAssetsFile("strings_js");
                setTitle(R.string.title_js);
                break;
            }
        }
        return data;
    }

    static class ViewHolder {
        TextView title;
        //        ImageView handle;
        TextView handle;
    }

    public class MyArrayAdapter extends ArrayAdapter<String> {

        private ArrayList<String> mStrings = new ArrayList<String>();
        private LayoutInflater mInflater;
        private int mLayout;

        public MyArrayAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
            this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.mLayout = textViewResourceId;
        }

        public ArrayList<String> getmStrings() {
            return mStrings;
        }

        @Override
        public void add(String row) {
            super.add(row);
            mStrings.add(row);
        }

        public void shuffle() {
            Collections.shuffle(mStrings);
        }

        @Override
        public void insert(String row, int position) {
            super.insert(row, position);
            mStrings.add(position, row);
        }

        @Override
        public void remove(String row) {
            super.remove(row);
            mStrings.remove(row);
        }

        @Override
        public void clear() {
            super.clear();
            mStrings.clear();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            View view = convertView;
            if (view == null) {
                view = mInflater.inflate(this.mLayout, null);
                assert view != null;
                holder = new ViewHolder();
                holder.title = (TextView) view.findViewById(R.id.row_code);
//                holder.handle = (ImageView) view.findViewById(R.id.handle);
                holder.handle = (TextView) view.findViewById(R.id.row_code);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final String string = mStrings.get(position);

            holder.title.setText(string);

            // ハンドルタップでソート開始
            holder.handle.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        startDrag(string);
                        return true;
                    }
                    return false;
                }
            });

            // ドラッグ行のハイライト
            if (mDragString != null && mDragString.equals(string)) {
                view.setBackgroundColor(Color.parseColor("#9933b5e5"));
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }

            return view;
        }
    }
}