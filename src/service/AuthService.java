package service;

import dao.StudentDAO;
import model.Student;
import java.sql.SQLException;

public class AuthService {
    private StudentDAO studentDAO;

    public AuthService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }
    
    /**
     * 로그인 처리
     * @return 로그인 성공한 Student 객체, 실패 시 null
     */
    public Student login(int studentId, String password) throws SQLException {
        return studentDAO.login(studentId, password);
    }
    
    /**
     * 회원가입 처리
     * @return 성공 여부
     */
    public boolean signUp(Student student) throws SQLException {
        if (studentDAO.existsById(student.getStudentId())) {
            return false;
        }

        if (student.getMaxCredits() > 0 && student.getMaxPoint() == 0) {
            student.setMaxPoint(student.getMaxCredits() * 5);
        }

        int result = studentDAO.signUp(student);
        return result > 0;
    }
}