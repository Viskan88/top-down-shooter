package se.victormattsson.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Victor Mattsson on 2015-12-16.
 */
public class AudioManager {

    public static Music music;
    public static Sound sound;
    private static float volume;
    private static boolean musicOff;

    public static void playMusic(String file) {
        volume = music.getVolume();
        music.stop();
        music = Gdx.audio.newMusic(Gdx.files.internal(file));
        music.play();
        music.setLooping(true);
        music.setVolume(volume);
        musicOff = false;
    }

    public static void stopMusic(){
        music.stop();
        musicOff = true;
    }

    public static void setVolume(float volume){
        music.setVolume(volume);
    }

    public static float getVolume() {
        return music.getVolume();
    }

    public static void dispose() {
        if (music != null) music.dispose();
        if (sound != null) sound.dispose();
    }

    public static boolean isMusicOff() {
        return musicOff;
    }
}
