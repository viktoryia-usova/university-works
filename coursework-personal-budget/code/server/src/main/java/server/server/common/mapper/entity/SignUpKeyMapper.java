package server.server.common.mapper.entity;

import org.mapstruct.Mapper;
import server.server.common.dto.entity.key.CreateSignUpKeyRequest;
import server.server.common.dto.entity.key.SignUpKeyResponse;
import server.server.common.entity.auth.SignUpKey;
import server.server.common.mapper.BaseCreateOnlyEntityMapper;

@Mapper(componentModel = "spring")
public abstract class SignUpKeyMapper implements BaseCreateOnlyEntityMapper<SignUpKey, SignUpKeyResponse, CreateSignUpKeyRequest> {
    @Override
    public abstract SignUpKey toEntity(CreateSignUpKeyRequest createSignUpKeyRequest);

    @Override
    public abstract SignUpKeyResponse toDto(SignUpKey entity);

    @Override
    public SignUpKeyResponse toDtoWithRelations(SignUpKey entity) {
        return toDto(entity);
    }
}
