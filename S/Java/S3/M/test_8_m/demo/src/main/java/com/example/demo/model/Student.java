package com.example.demo.model;

public class Student {

    private Long id;
    private String name;
    private String sex;
    private String age;
    private double score;

    public Student() {}

    public Student(Long id, String name, String sex, String age, double score) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.score = score;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

}
