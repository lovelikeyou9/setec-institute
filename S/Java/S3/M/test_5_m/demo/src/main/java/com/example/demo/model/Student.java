package com.example.demo.model;

public class Student {

    private int id;
    private String name;
    private String major;
    private String sex;
    private double score;

    public Student() {}

    public Student(int id, String name, String major, String sex, double score) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.sex = sex;
        this.score = score;
    }
 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

}
