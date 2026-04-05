package com.qpang.hub.infrastructure;

import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubRouteJpaRepository extends JpaRepository<HubRoute, UUID> {

    List<HubRoute> findBySourceHub(Hub sourceHub);

    @Modifying
    @Query("DELETE FROM HubRoute r WHERE r.sourceHub = :hub OR r.destinationHub = :hub")
    void deleteByHub(@Param("hub") Hub hub);

    @Query("SELECT r FROM HubRoute r WHERE r.sourceHub = :source AND r.destinationHub.hubType = 'CENTER'")
    List<HubRoute> findBySourceHubToCenter(@Param("source") Hub sourceHub);

    Optional<HubRoute> findBySourceHubAndDestinationHub(Hub sourceHub, Hub destinationHub);
}