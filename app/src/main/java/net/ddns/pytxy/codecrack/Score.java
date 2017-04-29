package net.ddns.pytxy.codecrack;

/**
 * Created by ludovic on 27/03/17.
 */
public class Score {
    public String name;
    public int score;
    public int difficulty;
    public int type;

    public Score(String name, int score, int difficulty, int type) {
        this.name = name;
        this.score = score;
        this.difficulty = difficulty;
        this.type = type;
    }
}
