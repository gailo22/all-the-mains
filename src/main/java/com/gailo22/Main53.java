package com.gailo22;

import java.util.function.Function;

public class Main53 {
	
	static abstract class List<A> {
		
		protected abstract A head();
		protected abstract List<A> tail();
		public abstract boolean isEmpty();
		public abstract List<A> reverse();
		public abstract int length();
		public abstract <B> List<B> map(Function<A, B> f);
		public abstract List<A> filter(Function<A, Boolean> f);
		public abstract <B> List<B> flatMap(Function<A, List<B>> f);
		// def foldLeft[B](z: B)(f: (B, A) => B): B
		public abstract <B> B foldLeft(B identity, Function<B, Function<A, B>> f);
		// def foldRight[B](z: B)(f: (A, B) => B): B
		public abstract <B> B foldRight(B identity, Function<A, Function<B, B>> f);
		
		@SuppressWarnings("rawtypes")
		public static final List NIL = new Nil();
		
		private List() {}
		
		private static class Nil<A> extends List<A> {

			@Override
			protected A head() {
				throw new IllegalStateException("head called on empty list");
			}

			@Override
			protected List<A> tail() {
				throw new IllegalStateException("tail called on empty list");
			}

			@Override
			public boolean isEmpty() {
				return true;
			}

			@Override
			public List<A> reverse() {
				return this;
			}

			@Override
			public int length() {
				return 0;
			}

			@Override
			public <B> List<B> map(Function<A, B> f) {
				return list();
			}

			@Override
			public List<A> filter(Function<A, Boolean> f) {
				return this;
			}

			@Override
			public <B> List<B> flatMap(Function<A, List<B>> f) {
				return list();
			}

			@Override
			public <B> B foldLeft(B identity, Function<B, Function<A, B>> f) {
				return identity;
			}

			@Override
			public <B> B foldRight(B identity, Function<A, Function<B, B>> f) {
				return identity;
			}
			
		}

		private static class Cons<A> extends List<A> {
			private final A head;
			private final List<A> tail;
			private final int length;
			
			private Cons(A head, List<A> tail) {
				this.head = head;
				this.tail = tail;
				this.length = tail.length() + 1;
			}

			@Override
			protected A head() {
				return head;
			}

			@Override
			protected List<A> tail() {
				return tail;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public List<A> reverse() {
				return reverseRec(list(), this);
			}

			private List<A> reverseRec(List<A> acc, List<A> list) {
				// TODO: use TailCall instead
				return list.isEmpty()
				    ? acc
				    : reverseRec(new Cons<>(list.head(), acc), list.tail());
			}

			@Override
			public int length() {
				return length;
			}

			@Override
			public <B> List<B> map(Function<A, B> f) {
				return foldRight(list(), h -> t -> new Cons<>(f.apply(h), t));
			}

			@Override
			public List<A> filter(Function<A, Boolean> f) {
				return foldRight(list(), h -> t -> f.apply(h)? new Cons<>(h, t) : t);
			}

			@Override
			public <B> List<B> flatMap(Function<A, List<B>> f) {
				return foldRight(list(), h -> t -> concat(f.apply(h), t));
			}

			@Override
			public <B> B foldLeft(B identity, Function<B, Function<A, B>> f) {
				return foldLeftRec(identity, this, f);
			}

			private <B> B foldLeftRec(B acc, List<A> list, Function<B, Function<A, B>> f) {
				// TODO: use TailCall instead
				return list.isEmpty()
				    ? acc
				    : foldLeftRec(f.apply(acc).apply(list.head()), list.tail(), f);
			}

			@Override
			public <B> B foldRight(B identity, Function<A, Function<B, B>> f) {
				return reverse().foldLeft(identity, x -> y -> f.apply(y).apply(x));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static <A> List<A> list() {
			return NIL;
		}
		
		public static <A, B> B foldRight(List<A> list, B n, Function<A, Function<B, B>> f) {
			return list.foldRight(n, f);
		}

		public static <T> List<T> cons(T t, List<T> list) {
			return list.cons(t);
		}
		  
		public static <A> List<A> concat(List<A> list1, List<A> list2) {
			return foldRight(list1, list2, x -> y -> new Cons<>(x, y));
		}
		
		public List<A> cons(A a) {
			return new Cons<>(a, this);
		}
	}
	
	public static void main(String[] args) {
		
	}

}
