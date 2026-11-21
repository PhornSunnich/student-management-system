package nichy.studentapp.service;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.CourseProgressResponse;
import nichy.studentapp.dto.LessonProgressResponse;
import nichy.studentapp.entity.Enrollment;
import nichy.studentapp.entity.Lesson;
import nichy.studentapp.entity.Student;
import nichy.studentapp.entity.StudentLessonProgress;
import nichy.studentapp.repository.EnrollmentRepository;
import nichy.studentapp.repository.LessonRepository;
import nichy.studentapp.repository.StudentLessonProgressRepository;
import nichy.studentapp.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonProgressService {

    private final StudentLessonProgressRepository progressRepository;
    private final StudentRepository studentRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public LessonProgressResponse toggleLessonCompletion(Long studentId, Long lessonId, boolean completed) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + studentId));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found with id: " + lessonId));

        if (lesson.getCourse() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lesson is not linked to any course");
        }

        Long courseId = lesson.getCourse().getId();

        // ត្រូវប្រាកដថាសិស្សបាន Enroll វគ្គសិក្សានេះជាមុនសិន
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is not enrolled in this course"));

        StudentLessonProgress progressRecord = progressRepository
                .findByStudentIdAndLessonId(studentId, lessonId)
                .orElseGet(() -> StudentLessonProgress.builder()
                        .student(student)
                        .lesson(lesson)
                        .build());

        progressRecord.setCompleted(completed);
        progressRecord.setCompletedAt(completed ? LocalDateTime.now() : null);
        StudentLessonProgress savedProgress = progressRepository.save(progressRecord);

        long totalLessons = lessonRepository.countByCourseId(courseId);
        long completedLessons = progressRepository.countCompletedLessons(studentId, courseId);

        double percentage = totalLessons > 0
                ? Math.round(((completedLessons * 100.0) / totalLessons) * 100.0) / 100.0
                : 0.0;

        enrollment.setProgress(percentage);
        enrollmentRepository.save(enrollment);

        return LessonProgressResponse.builder()
                .progressId(savedProgress.getId())
                .studentId(studentId)
                .courseId(courseId)
                .lessonId(lessonId)
                .lessonTitle(lesson.getTitle())
                .completed(savedProgress.isCompleted())
                .completedAt(savedProgress.getCompletedAt())
                .completedLessonsCount(completedLessons)
                .totalLessonsCount(totalLessons)
                .courseProgressPercentage(percentage)
                .build();
    }

    @Transactional(readOnly = true)
    public CourseProgressResponse getCourseProgressForStudent(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Enrollment not found for studentId=" + studentId + " and courseId=" + courseId
                ));

        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByIdAsc(courseId);

        Map<Long, StudentLessonProgress> progressMap = progressRepository
                .findByStudentIdAndLesson_Course_Id(studentId, courseId)
                .stream()
                .collect(Collectors.toMap(
                        p -> p.getLesson().getId(),
                        p -> p,
                        (existing, replacement) -> existing
                ));

        long completedCount = progressMap.values().stream()
                .filter(StudentLessonProgress::isCompleted)
                .count();

        long totalLessons = lessons.size();
        double calculatedPercentage = totalLessons > 0
                ? Math.round(((completedCount * 100.0) / totalLessons) * 100.0) / 100.0
                : 0.0;

        List<CourseProgressResponse.LessonItemProgress> lessonItems = lessons.stream()
                .map(lesson -> {
                    StudentLessonProgress record = progressMap.get(lesson.getId());
                    boolean isDone = record != null && record.isCompleted();
                    return CourseProgressResponse.LessonItemProgress.builder()
                            .lessonId(lesson.getId())
                            .lessonTitle(lesson.getTitle())
                            .completed(isDone)
                            .completedAt(isDone ? record.getCompletedAt() : null)
                            .build();
                })
                .toList();

        return CourseProgressResponse.builder()
                .studentId(studentId)
                .courseId(courseId)
                .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .completedLessonsCount(completedCount)
                .totalLessonsCount(totalLessons)
                .progressPercentage(calculatedPercentage)
                .lessons(lessonItems)
                .build();
    }
}