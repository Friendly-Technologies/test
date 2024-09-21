package com.friendly.services.settings.userinterface.orm.iotw.repository;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.services.infrastructure.base.repository.BaseJpaRepository;
import com.friendly.services.settings.userinterface.orm.iotw.model.ClientInterfaceEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * Repository to interact with persistence layer to store {@link ClientInterfaceEntity} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Repository
public interface ClientInterfaceRepository extends BaseJpaRepository<ClientInterfaceEntity, Long> {

    @Query("SELECT DISTINCT ui FROM UserEntity u INNER JOIN ClientInterfaceEntity ui ON u.clientType = ui.clientType " +
            "INNER JOIN InterfaceItemEntity ii ON ui.interfaceItem.id = ii.id " +
            "INNER JOIN InterfaceDescriptionEntity id ON u.localeId = id.localeId WHERE u.id = :userId " +
            "ORDER BY ui.id ASC")
    LinkedHashSet<ClientInterfaceEntity> getClientInterfaceItems(final Long userId);

    @Query("SELECT DISTINCT ui FROM UserEntity u INNER JOIN ClientInterfaceEntity ui ON u.clientType = ui.clientType " +
            "INNER JOIN InterfaceItemEntity ii ON ui.interfaceItem.id = ii.id " +
            "INNER JOIN InterfaceDescriptionEntity id ON u.localeId = id.localeId WHERE u.id = :userId " +
            "AND ui.interfaceItem.id = :id")
    Optional<ClientInterfaceEntity> getClientInterfaceItem(final Long userId, final String id);

    @Query("SELECT DISTINCT ui FROM ClientInterfaceEntity ui INNER JOIN InterfaceItemEntity ii ON ui.interfaceItem.id = ii.id AND ui.clientType = :clientType AND ui.interfaceItem.id = :id")
    Optional<ClientInterfaceEntity> getClientInterfaceItem(final ClientType clientType, final String id);

    @Query("SELECT ui.value FROM ClientInterfaceEntity ui " +
            "WHERE ui.clientType = :clientType AND ui.interfaceItem.id = :interfaceId")
    Optional<String> getInterfaceValue(final ClientType clientType, final String interfaceId);

    @Query("SELECT ui.defaultValue FROM InterfaceItemEntity ui " +
            "WHERE ui.id = :interfaceId")
    Optional<String> getDefaultInterfaceValue(final String interfaceId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE ClientInterfaceEntity ui SET ui.value = :value, ui.isEncrypted = :isEncrypted WHERE ui.interfaceItem.id = :id " +
            "AND ui.clientType = :clientType")
    void updateClientInterfaceEntityValue(final String id, final ClientType clientType, final String value, final boolean isEncrypted);

    Optional<ClientInterfaceEntity> findByInterfaceItem_Id(String value);
}
