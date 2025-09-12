package com.hafidh.mapper.user;

import com.hafidh.dto.user.UserDTO;
import com.hafidh.entity.user.User;
import com.hafidh.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDto(User user);

    User toEntity(UserDTO dto);
}
