package com.gailo22;

import java.util.ArrayList;
import java.util.List;

public class Main63 {

    public static void main(String[] args) {

        Store store = Store.create();

        Subscription sub = store.subscribe(state -> {
            // do some updates based on the new state
        });


        // later
        sub.unsubscribe();
    }

}

interface Action {
}

interface Reducer<S> {
    S reduce(S oldState, Action action);
}

interface Subscription {
    void unsubscribe();
}

interface Subscriber<S> {
    void onChange(S state);
}

class Store<S> {
    private S currentState;
    private Reducer<S> reducer;
    private List<Subscriber<S>> subscribers = new ArrayList<>();

    public Store(S initialState, Reducer<S> rootReducer) {
        this.currentState = initialState;
        this.reducer = rootReducer;
    }

    public static Store create() {
        return null;
    }

    public S getState() {
        return this.currentState;
    }

    public Subscription subscribe(Subscriber<S> subscriber) {
        subscribers.add(subscriber);

        subscriber.onChange(this.currentState);

        return () -> {
            subscribers.remove(subscriber);
        };
    }

    private void notifySubscribers() {
        subscribers.forEach(subscriber -> subscriber.onChange(this.currentState));
    }

    public void dispatch(Object action) {
        S oldState = this.currentState;

        S newState = reducer.reduce(this.currentState, (Action) action);

        if (oldState != newState && !oldState.equals(newState)) {
            this.currentState = newState;

            notifySubscribers();
        }
    }
}

// implementation
class AddItemAction implements Action {
    private final String value;

    public AddItemAction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // equals & hashcode
}

class TodoState {
    private final List<String> items;

    public TodoState(List<String> items) {
        this.items = items;
    }

    public List<String> getItems() {
        return items;
    }

    public TodoState withItems(List<String> items) {
        if (items.equals(this.items)) {
            return this;
        } else {
            return new TodoState(items);
        }
    }
}

class TodoReducer implements Reducer<TodoState> {

    @Override
    public TodoState reduce(TodoState oldState, Action action) {
        if (action instanceof AddItemAction) {
            AddItemAction addItemAction = (AddItemAction) action;

            List<String> oldItems = oldState.getItems();

            List<String> newItems = new ArrayList<>(oldItems);
            newItems.add(addItemAction.getValue());

            return oldState.withItems(newItems);
        }

        return oldState;
    }
}
