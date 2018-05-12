package com.gailo22;

import java.util.*;

public class Main55 {

    public static class Student {
        private String name;
        private String address;
        private float grade;

        public Student(String name, String address, float grade) {
            this.name = name;
            this.address = address;
            this.grade = grade;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public float getGrade() {
            return grade;
        }

        public void setGrade(float grade) {
            this.grade = grade;
        }

        @Override
        public String toString() {
            return "Student{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", grade=" + grade +
                '}';
        }

        private static final Comparator<Student> nameComparator = new NameComparator();
        private static final Comparator<Student> gradeComparator = new GradeComparator();
        public static Comparator<Student> getNameComparator() {
            return nameComparator;
        }

        private static class NameComparator implements Comparator<Student> {

            @Override
            public int compare(Student o1, Student o2) {
                return o1.name.compareTo(o2.name);
            }
        }
        private static class GradeComparator implements Comparator<Student> {

            @Override
            public int compare(Student o1, Student o2) {
                return Float.compare(o1.grade, o2.grade);
            }
        }
    }

    public static void main(String[] args) {
        Set<Student> list = new TreeSet<>(Student.getNameComparator());
        list.add(new Student("Anna", "Here", 3.4f));
        list.add(new Student("Cathy", "There", 4.0f));

        for (Student student : list) {
            System.out.println(student);
        }

    }
}

