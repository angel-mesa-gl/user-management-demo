package com.globallogic.userManagementDemo.user.mapper;

import com.globallogic.userManagementDemo.user.domain.Phone;
import com.globallogic.userManagementDemo.user.domain.User;
import com.globallogic.userManagementDemo.user.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.CollectionMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "phones", ignore = true)
    User toUser(SignUpRequest request);

    SignUpResponse toSignUpResponse(User user);

    Phone toPhone(PhoneRequest phoneRequest);

    List<Phone> toPhoneList(List<PhoneRequest> phoneRequests);

    LoginResponse toLoginResponse(User user);

    PhoneResponse toPhoneResponse(Phone phone);

    List<PhoneResponse> toPhoneResponseList(List<Phone> phones);
}