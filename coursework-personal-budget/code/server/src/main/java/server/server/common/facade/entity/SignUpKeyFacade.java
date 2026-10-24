package server.server.common.facade.entity;

import org.springframework.stereotype.Service;
import server.server.common.dto.entity.key.CreateSignUpKeyRequest;
import server.server.common.dto.entity.key.SignUpKeyResponse;
import server.server.common.entity.auth.SignUpKey;
import server.server.common.entity.enums.Role;
import server.server.common.facade.BaseCreateOnlyFacade;
import server.server.common.mapper.entity.SignUpKeyMapper;
import server.server.common.service.entity.SignUpKeyService;

import java.util.List;

@Service
public class SignUpKeyFacade extends BaseCreateOnlyFacade<SignUpKey, Long, SignUpKeyMapper, SignUpKeyService, SignUpKeyResponse, CreateSignUpKeyRequest> {
    public SignUpKeyFacade(SignUpKeyService service, SignUpKeyMapper mapper) {
        super(service, mapper);
    }

    @Override
    public SignUpKeyResponse create(CreateSignUpKeyRequest request) {
        return mapper.toDto(service.create(request.getUsername(), request.getRole()));
    }

    public List<SignUpKeyResponse> findByRole(Role role) {
        return service.findByRole(role).stream().map(mapper::toDto).toList();
    }
}
