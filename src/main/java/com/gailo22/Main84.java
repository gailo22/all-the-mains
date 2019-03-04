package com.gailo22;

import java.util.function.Consumer;

public class Main84 {

	public static void main(String[] args) {

        MyBuilder<String> builder = Main84.builder();
        Main84 build = builder.build();

        System.out.println(build);
    }

    private static Main84 support() {
        return new Main84();
    }

	static final class MyBuilderImpl<T> implements MyBuilder<T> {

	    private String str = "";

        @Override
        public void accept(T t) {

        }

        @Override
        public Main84 build() {
            return Main84.support();
        }
    }


    public static<T> MyBuilder<T> builder() {
        return new Main84.MyBuilderImpl<>();
    }

	interface MyBuilder<T> extends Consumer<T> {

	    @Override
        void accept(T t);

        default MyBuilder<T> add(T t) {
            accept(t);
            return this;
        }

        Main84 build();
    }

}
