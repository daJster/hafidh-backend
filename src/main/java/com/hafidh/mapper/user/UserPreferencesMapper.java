package com.hafidh.mapper.user;

import com.hafidh.dto.user.UserPreferencesDTO;
import com.hafidh.entity.user.UserPreferences;
import com.hafidh.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapStructConfig.class)
public interface UserPreferencesMapper {

    UserPreferencesMapper INSTANCE = Mappers.getMapper(UserPreferencesMapper.class);
    UserPreferencesDTO toDto(UserPreferences entity);

    UserPreferences toEntity(UserPreferencesDTO dto);

}
