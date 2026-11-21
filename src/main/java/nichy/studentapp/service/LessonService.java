package nichy.studentapp.service;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.EnrollmentResponse;
import nichy.studentapp.dto.LessonRequest;
import nichy.studentapp.dto.LessonResponse;
import nichy.studentapp.entity.Course;
import nichy.studentapp.entity.Enrollment;
import nichy.studentapp.entity.Lesson;
import nichy.studentapp.repository.CourseRepository;
import nichy.studentapp.repository.EnrollmentRepository;
import nichy.studentapp.repository.LessonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    // 1. បង្កើតមេរៀនថ្មី (កែ Return Type មកជា LessonResponse)
    public LessonResponse createLesson(LessonRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found!"));

        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setContent(request.getContent());
        lesson.setCourse(course);

        Lesson savedLesson = lessonRepository.save(lesson);
        return mapToLessonResponse(savedLesson);
    }

    // 2. ទាញយកមេរៀនទាំងអស់
    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
    }

    // 3. ទាញយកមេរៀនតាម ID
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found with id: " + id));
        return mapToLessonResponse(lesson);
    }

    // 4. ទាញយកមេរៀនតាម Course ID
    public List<LessonResponse> getLessonByCourseId(Long courseId) {
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        return lessons.stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
    }

    // 5. កែប្រែមេរៀន (Update Lesson)
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found with id: " + id));

        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setContent(request.getContent());

        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with id: " + request.getCourseId()));
            lesson.setCourse(course);
        }

        Lesson updatedLesson = lessonRepository.save(lesson);
        return mapToLessonResponse(updatedLesson);
    }

    // 6. លុបមេរៀន (Delete Lesson)
    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
    }

    // 7. អាប់ដេតភាគរយនៃការសិក្សារបស់សិស្ស (កែសម្រួល Logic ពិតប្រាកដ)
    public EnrollmentResponse updateProgress(Long studentId, Long courseId, Double progress) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found for this student and course"));

        enrollment.setProgress(progress);
        Enrollment updatedEnrollment = enrollmentRepository.save(enrollment);

        return EnrollmentResponse.builder()
                .id(updatedEnrollment.getId())
                .studentId(updatedEnrollment.getStudent() != null ? updatedEnrollment.getStudent().getId() : null)
                .studentName(updatedEnrollment.getStudent() != null ? updatedEnrollment.getStudent().getName() : null)
                .courseId(updatedEnrollment.getCourse() != null ? updatedEnrollment.getCourse().getId() : null)
                .courseTitle(updatedEnrollment.getCourse() != null ? updatedEnrollment.getCourse().getTitle() : null)
                .progress(updatedEnrollment.getProgress())
                .enrolledAt(updatedEnrollment.getEnrolledAt())
                .build();
    }

    // Helper Mapping Method
    private LessonResponse mapToLessonResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .videoUrl(lesson.getVideoUrl())
                .content(lesson.getContent())
                .courseId(lesson.getCourse() != null ? lesson.getCourse().getId() : null)
                .courseTitle(lesson.getCourse() != null ? lesson.getCourse().getTitle() : null)
                .build();
    }
}