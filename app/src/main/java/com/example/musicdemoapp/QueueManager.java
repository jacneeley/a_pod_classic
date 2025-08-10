package com.example.musicdemoapp;

import java.util.ArrayDeque;
import java.util.stream.Collectors;

public class QueueManager {
    /* A class to manage an ArrayDeque for queueing songs. */
    private static ArrayDeque<String> queue = new ArrayDeque<>();

    public static ArrayDeque<String> getQueue() {
        return queue;
    }

    public static void add(String songPath){
        queue.addLast(songPath);
    }

    public static void pop(){
        queue.removeLast();
    }

    public static void removeRecent(){
        queue.removeFirst();
    }

    public static void removeBy(String songPath){
        queue.removeFirstOccurrence(songPath);
    }

    public static String getHead(){
        return queue.getFirst();
    }

    public static void clearQueue(){
        queue.clear();
    }
}
