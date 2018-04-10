package model.presentie;

import model.persoon.Student;

public class Presentie {
    private Student student;
    private String status;

    public Presentie(Student student, String status) {
        this.student = student;
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Presentie{" +
                "student=" + student +
                ", status='" + status + '\'' +
                '}';
    }
}
