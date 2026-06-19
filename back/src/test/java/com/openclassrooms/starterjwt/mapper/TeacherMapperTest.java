package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TeacherMapperTest {

    @Autowired
    private TeacherMapper teacherMapper;

    @Test
    void toDto_shouldMapTeacherToTeacherDto() {
        Teacher teacher = Teacher.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Smith")
                .build();

        TeacherDto dto = teacherMapper.toDto(teacher);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Alice");
        assertThat(dto.getLastName()).isEqualTo("Smith");
    }

    @Test
    void toEntity_shouldMapTeacherDtoToTeacher() {
        TeacherDto dto = new TeacherDto(1L, "Smith", "Alice", null, null);

        Teacher teacher = teacherMapper.toEntity(dto);

        assertThat(teacher).isNotNull();
        assertThat(teacher.getFirstName()).isEqualTo("Alice");
        assertThat(teacher.getLastName()).isEqualTo("Smith");
    }

    @Test
    void toDto_shouldMapListOfTeachersToListOfDtos() {
        Teacher t1 = Teacher.builder().id(1L).firstName("Alice").lastName("Smith").build();
        Teacher t2 = Teacher.builder().id(2L).firstName("Bob").lastName("Jones").build();

        List<TeacherDto> dtos = teacherMapper.toDto(Arrays.asList(t1, t2));

        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).getFirstName()).isEqualTo("Alice");
        assertThat(dtos.get(1).getFirstName()).isEqualTo("Bob");
    }

    @Test
    void toEntity_shouldMapListOfDtosToListOfTeachers() {
        TeacherDto dto1 = new TeacherDto(1L, "Smith", "Alice", null, null);
        TeacherDto dto2 = new TeacherDto(2L, "Jones", "Bob", null, null);

        List<Teacher> teachers = teacherMapper.toEntity(Arrays.asList(dto1, dto2));

        assertThat(teachers).hasSize(2);
        assertThat(teachers.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void toDto_shouldReturnNull_whenTeacherIsNull() {
        TeacherDto dto = teacherMapper.toDto((Teacher) null);
        assertThat(dto).isNull();
    }

    @Test
    void toEntity_shouldReturnNull_whenTeacherDtoIsNull() {
        Teacher teacher = teacherMapper.toEntity((TeacherDto) null);
        assertThat(teacher).isNull();
    }

    @Test
    void toDto_shouldReturnNull_whenListIsNull() {
        List<TeacherDto> dtos = teacherMapper.toDto((List<Teacher>) null);
        assertThat(dtos).isNull();
    }

    @Test
    void toEntity_shouldReturnNull_whenListIsNull() {
        List<Teacher> teachers = teacherMapper.toEntity((List<TeacherDto>) null);
        assertThat(teachers).isNull();
    }
}
