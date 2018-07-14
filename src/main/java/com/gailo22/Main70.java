package com.gailo22;

import java.util.concurrent.*;

public class Main70 {

    interface Result<T> {
        static <T> Result<T> empty() {
            return new Empty<T>();
        }

        static <T> Result<T> of(T value) {
            return value != null ?
                success(value) :
                Result.failure("Null value");
        }

        static <T> Result<T> success(T value) {
            return new Success<>(value);
        }

        static <T> Result<T> failure(String message) {
            return new Failure<>(message);
        }

        void forEach(Effect<T> c);

        Result<String> forEachOrFail(Effect<T> e);
    }

    interface Effect<T> {
        void apply(T t);
    }

    static class Success<T> implements Result<T> {
        private T value;

        private Success(T value) {
            this.value = value;
        }

        @Override
        public void forEach(Effect<T> e) {
            e.apply(this.value);
        }

        @Override
        public Result<String> forEachOrFail(Effect<T> e) {
            e.apply(this.value);
            return Result.empty();
        }
    }

    static class Empty<T> implements Result<T> {

        @Override
        public void forEach(Effect<T> e) {
            // do nothing
        }

        @Override
        public Result<String> forEachOrFail(Effect<T> e) {
            return Result.empty();
        }
    }

    static class Failure<T> extends Empty<T> {
        private RuntimeException exception;

        private Failure(String message) {
            exception = new IllegalStateException(message);
        }

        @Override
        public Result<String> forEachOrFail(Effect<T> e) {
            return Result.success(exception.getMessage());
        }
    }

    interface Actor<T> {
        static <T> Result<Actor<T>> noSender() {
            return Result.empty();
        }

        Result<Actor<T>> self();

        ActorContext<T> getContext();

        default void tell(T message) {
            tell(message, self());
        }

        void tell(T message, Result<Actor<T>> self);

        void shutdown();

        default void tell(T message, Actor<T> sender) {
            tell(message, Result.of(sender));
        }

        enum Type {SERIAL, PARALLEL}
    }

    interface ActorContext<T> {
        void become(MessageProcessor<T> behavior);

        MessageProcessor<T> getBehavior();
    }

    static abstract class AbstractActor<T> implements Actor<T> {
        private final ActorContext<T> context;
        protected final String id;
        private final ExecutorService executor;

        public AbstractActor(String id, Type type) {
            this.id = id;
            this.executor = type == Type.SERIAL
                ? Executors.newSingleThreadExecutor(new DaemonThreadFactory())
                : Executors.newCachedThreadPool(new DaemonThreadFactory());
            this.context = new ActorContext<T>() {
                MessageProcessor<T> behavior = AbstractActor.this::onReceive;

                @Override
                public synchronized void become(MessageProcessor<T> behavior) {
                    this.behavior = behavior;
                }

                @Override
                public MessageProcessor<T> getBehavior() {
                    return this.behavior;
                }
            };
        }

        abstract void onReceive(T t, Result<Actor<T>> actorResult);

        public Result<Actor<T>> self() {
            return Result.success(this);
        }

        public ActorContext<T> getContext() {
            return this.context;
        }

        @Override
        public void shutdown() {
            this.executor.shutdown();
        }

        public synchronized void tell(T message, Result<Actor<T>> sender) {
            executor.execute(() -> {
                try {
                    context.getBehavior().process(message, sender);
                } catch (RejectedExecutionException ignore) {
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    static class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnableTask) {
            Thread thread = Executors.defaultThreadFactory().newThread(runnableTask);
            thread.setDaemon(true);
            return thread;
        }
    }

    static class Player extends AbstractActor<Integer> {
        private final String sound;
        private final Actor<Integer> referee;

        public Player(String id, String sound, Actor<Integer> referee) {
            super(id, Actor.Type.SERIAL);
            this.referee = referee;
            this.sound = sound;
        }

        @Override
        void onReceive(Integer message, Result<Actor<Integer>> sender) {
            System.out.println(sound + "-" + message);
            if (message >= 10) {
                referee.tell(message, sender);
            } else {
                sender.forEachOrFail(actor -> actor.tell(message + 1, self()))
                    .forEach(ignore -> referee.tell(message, sender));
            }
        }
    }

    interface MessageProcessor<T> {
        void process(T t, Result<Actor<T>> sender);
    }

    private static final Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {
        Actor<Integer> referee =
            new AbstractActor<Integer>("Referee", Actor.Type.SERIAL) {

                @Override
                public void onReceive(Integer message, Result<Actor<Integer>> sender) {
                    System.out.println("Game ended after " + message + " shots");
                    semaphore.release();
                }
            };

        Actor<Integer> player1 = new Player("Player1", "Ping", referee);
        Actor<Integer> player2 = new Player("Player2", "Pong", referee);

        semaphore.acquire();
        player1.tell(1, Result.success(player2));
        semaphore.acquire();
    }

}
