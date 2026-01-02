package utilities;

import com.example.musicdemoapp.AudioModel;

import java.util.ArrayDeque;

public class QueueManager {
    /* A class to manage an ArrayDeque for queueing songs. */
    private static final ArrayDeque<AudioModel> queue = new ArrayDeque<>();

    public static ArrayDeque<AudioModel> getQueue() {
        return queue;
    }

    public static void add(AudioModel songPath){ queue.addLast(songPath); }

    public static void pop(){
        queue.removeLast();
    }

    public static void removeRecent(){
        queue.removeFirst();
    }

    public static void removeBy(AudioModel song){
        queue.removeFirstOccurrence(song);
    }

    public static AudioModel getHead(){
        return queue.getFirst();
    }

    public static void clearQueue(){
        queue.clear();
    }

    public static void next() {
        removeRecent();
    }

    public static boolean isEmpty(){ return queue.isEmpty(); }
}
