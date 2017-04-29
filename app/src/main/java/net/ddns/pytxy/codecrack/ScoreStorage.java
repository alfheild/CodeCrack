package net.ddns.pytxy.codecrack;

import java.util.ArrayList;

/**
 * Created by ludovic on 26/03/17.
 */
public interface ScoreStorage {
    void saveScore(int score,String name,int type);
    ArrayList<String> listScoreboard(int max);
}
