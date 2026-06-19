package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void findAll_shouldReturnAllTeachers() {
        Teacher t1 = Teacher.builder().id(1L).firstName("Alice").lastName("Smith").build();
        Teacher t2 = Teacher.builder().id(2L).firstName("Bob").lastName("Jones").build();

        when(teacherRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Teacher> result = teacherService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void findById_shouldReturnTeacher_whenFound() {
        Teacher teacher = Teacher.builder().id(1L).firstName("Alice").lastName("Smith").build();

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_shouldReturnNull_whenNotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        Teacher result = teacherService.findById(99L);

        assertThat(result).isNull();
    }
}
