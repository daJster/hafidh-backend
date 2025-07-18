package com.hafidh.mapper;

import com.hafidh.dto.user.UserDTO;
import com.hafidh.entity.User_old;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

//    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User_old user);

    User_old toEntity(UserDTO userDTO);
}
