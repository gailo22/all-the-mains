package com.gailo22;

import java.util.*;

public class Main56 {
    static class MyArrayList<E> implements List<E> {

        private class MyListIterator implements Iterator<E> {

            private int progress = 0;

            @Override
            public boolean hasNext() {
                return progress < count;
            }

            @Override
            public E next() {
                return elements[progress++];
            }
        }

        private E[] elements = (E[]) new Object[10];
        private int count = 0;

        @Override
        public int size() {
            return count;
        }

        @Override
        public boolean isEmpty() {
            return count == 0;
        }

        @Override
        public boolean contains(Object o) {
            for (int i = 0; i < count; i++) {
                if (Objects.equals(o, elements[i])) return true;
            }
            return false;
        }

        @Override
        public Iterator<E> iterator() {
            return new MyListIterator();
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(E e) {
            elements[count++] = e;
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public E get(int index) {
            return elements[index];
        }

        @Override
        public E set(int index, E element) {
            return null;
        }

        @Override
        public void add(int index, E element) {

        }

        @Override
        public E remove(int index) {
            return null;
        }

        @Override
        public int indexOf(Object o) {
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            return 0;
        }

        @Override
        public ListIterator<E> listIterator() {
            return null;
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return null;
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return null;
        }
    }

    public static void main(String[] args) {
        List<Integer> list = new MyArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        for (int i : list) {
            System.out.println(i);
        }
    }
}
