package com.hafidh.mapper.classroom;

import com.hafidh.dto.classroom.ClassroomDTO;
import com.hafidh.entity.classroom.Classroom;
import com.hafidh.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface ClassroomMapper {
        ClassroomMapper INSTANCE = Mappers.getMapper(ClassroomMapper.class);

        ClassroomDTO toDto(Classroom user);

        Classroom toEntity(ClassroomDTO dto);
}
