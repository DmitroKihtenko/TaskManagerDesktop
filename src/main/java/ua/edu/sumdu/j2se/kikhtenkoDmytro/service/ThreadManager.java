package ua.edu.sumdu.j2se.kikhtenkoDmytro.service;

import java.util.HashSet;

public class ThreadManager {
    private final HashSet<Thread> threads;

    public ThreadManager() {
        threads = new HashSet<>();
    }

    public void addThread(Thread thread) {
        threads.add(thread);
        thread.start();
    }

    public void removeThread(Thread thread) {
        if(threads.contains(thread)) {
            thread.interrupt();
            threads.remove(thread);
        }
    }

    public void removeAll() {
        for(Thread thread : threads) {
            thread.interrupt();
        }
        threads.clear();
    }
}
