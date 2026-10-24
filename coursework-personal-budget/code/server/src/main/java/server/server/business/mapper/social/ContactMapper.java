package server.server.business.mapper.social;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import server.server.business.dto.social.ContactResponse;
import server.server.business.entity.social.Contact;
import server.server.common.mapper.BaseReadOnlyMapper;
import server.server.common.mapper.context.RelationPaths;

@Mapper(componentModel = "spring")
public abstract class ContactMapper implements BaseReadOnlyMapper<Contact, ContactResponse> {

    /**
     * Context-aware mapping: determines which side of the contact is "the other person"
     * based on currentProfileId.
     */
    public ContactResponse toDto(Contact entity, Long currentProfileId) {
        boolean isRequester = entity.getRequester().getId().equals(currentProfileId);
        var other = isRequester ? entity.getAddressee() : entity.getRequester();
        return new ContactResponse(
                entity.getId(),
                entity.getId(),
                other.getId(),
                other.getAccount().getUsername(),
                other.getFirstName(),
                other.getLastName(),
                other.getFriendCode(),
                entity.getStatus(),
                isRequester,
                entity.getCreatedAt()
        );
    }

    /** Fallback: maps from requester's perspective. Prefer {@link #toDto(Contact, Long)}. */
    @Override
    @Mapping(target = "contactId",   source = "id")
    @Mapping(target = "profileId",   source = "addressee.id")
    @Mapping(target = "email",       source = "addressee.account.username")
    @Mapping(target = "firstName",   source = "addressee.firstName")
    @Mapping(target = "lastName",    source = "addressee.lastName")
    @Mapping(target = "friendCode",  source = "addressee.friendCode")
    @Mapping(target = "isRequester", constant = "true")
    public abstract ContactResponse toDto(Contact entity);

    @Override
    public ContactResponse toDtoWithRelations(Contact entity, RelationPaths paths) {
        return toDto(entity);
    }

    @Override
    public ContactResponse toDtoWithRelations(Contact entity) {
        return toDto(entity);
    }
}
