package main;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlaySound {
    ArrayList<AudioClip> notes;
    
    PlaySound(){
        try {
            //load all audio files when initialize
            notes.add(Applet.newAudioClip(new URL("file://localhost/Users/sangbinmun/java_opencv/Virtual_Xylophone/sound/Guitar/Sound 45 L3.wav")));
        } catch (MalformedURLException murle){
            System.out.println(murle);
        }
    }
    
    public void play(int idx) {
        notes.get(idx).play();
    }

}
