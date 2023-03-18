package com.example.influencer.Core;

public class Event<T> {
    private T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }
}
