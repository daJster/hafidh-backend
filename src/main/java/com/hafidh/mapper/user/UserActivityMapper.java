package com.hafidh.mapper.user;

import com.hafidh.dto.user.UserActivityDTO;
import com.hafidh.entity.user.UserActivity;
import com.hafidh.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(config = MapStructConfig.class)
public interface UserActivityMapper {

    UserActivityMapper INSTANCE = Mappers.getMapper(UserActivityMapper.class);

    UserActivityDTO toDto(UserActivity activity);

    UserActivity toEntity(UserActivityDTO dto);
}
