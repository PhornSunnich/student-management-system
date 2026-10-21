package nichy.studentapp.service;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.LessonRequest;
import nichy.studentapp.dto.LessonResponse;
import nichy.studentapp.entity.Course;
import nichy.studentapp.entity.Enrollment;
import nichy.studentapp.entity.Lesson;
import nichy.studentapp.repository.CourseRepository;
import nichy.studentapp.repository.EnrollmentRepository;
import nichy.studentapp.repository.LessonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {
    // inject lesson and course
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    // create new lesson
    public Lesson createLesson(LessonRequest request){
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(()-> new RuntimeException("Course not found!"));
        Lesson lesson = new Lesson();
        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setContent(request.getContent());
        lesson.setCourse(course);
        return lessonRepository.save(lesson);
    }
    // 1. ទាញយកមេរៀនទាំងអស់ (Get All Lessons)
    public List<LessonResponse> getAllLessons() {
        return lessonRepository.findAll().stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
    }

    // 2. ទាញយកមេរៀនតាម ID (Get Lesson by ID)
    // ទាញយកមេរៀនតាម ID និង Map ទៅជា LessonResponse
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        return mapToLessonResponse(lesson);
    }

    // get lesson by CourseId
    public List<LessonResponse> getLessonByCourseId(Long courseId){
        List<Lesson> lessons = lessonRepository.findByCourseId(courseId);
        return lessons.stream()
                .map(this::mapToLessonResponse)
                .collect(Collectors.toList());
    }

    // Progress student studying
    public Enrollment updateProgress(Long studentId, Long courseId, Double progress){
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found for this student and course"));
        enrollment.setProgress(progress);
        return enrollmentRepository.save(enrollment);
    }

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

    // 3. Update Lesson
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));

        lesson.setTitle(request.getTitle());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setContent(request.getContent());

        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));
            lesson.setCourse(course);
        }

        Lesson updatedLesson = lessonRepository.save(lesson);
        return mapToLessonResponse(updatedLesson);
    }

    // 4. Delete Lesson
    public void deleteLesson(Long id) {
        if (!lessonRepository.existsById(id)) {
            throw new RuntimeException("Lesson not found with id: " + id);
        }
        lessonRepository.deleteById(id);
    }




}
