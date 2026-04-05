package com.qpang.hub.application.service;

import com.qpang.common.exception.CommonErrorCode;
import com.qpang.common.exception.CustomException;
import com.qpang.hub.domain.model.Hub;
import com.qpang.hub.domain.model.HubRoute;
import com.qpang.hub.domain.model.HubType;
import com.qpang.hub.domain.repository.HubRepository;
import com.qpang.hub.domain.repository.HubRouteRepository;
import com.qpang.hub.infrastructure.KakaoRouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HubRouteManager {

    private final HubRepository hubRepository;
    private final HubRouteRepository hubRouteRepository;
    private final KakaoRouteService kakaoRouteService;

    @Transactional
    public void reassignRoutesForNewCenter(Hub newCenter) {
        List<Hub> allHubs = hubRepository.findAll();

        for (Hub target : allHubs) {
            if (target.getId().equals(newCenter.getId())) continue;

            if (target.getHubType() == HubType.CENTER) {
                createRoutePair(newCenter, target);
            } else {
                HubRoute currentRoute = hubRouteRepository.findBySourceHubToCenter(target)
                        .stream().findFirst().orElse(null);

                KakaoRouteService.RouteInfo newInfo = kakaoRouteService.getRoute(
                        target.getLongitude().doubleValue(), target.getLatitude().doubleValue(),
                        newCenter.getLongitude().doubleValue(), newCenter.getLatitude().doubleValue()
                );

                if (currentRoute == null || newInfo.distance().compareTo(currentRoute.getDistance()) < 0) {
                    hubRouteRepository.deleteByHub(target);
                    createRoutePair(target, newCenter);
                }
            }
        }
    }

    @Transactional
    public void setupRouteForNewNormal(Hub newNormal) {
        Hub centerHub = newNormal.getCenterHub();

        if (centerHub == null) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        createRoutePair(newNormal, centerHub);
    }

    private void createRoutePair(Hub source, Hub dest) {
        KakaoRouteService.RouteInfo info = kakaoRouteService.getRoute(
                source.getLongitude().doubleValue(), source.getLatitude().doubleValue(),
                dest.getLongitude().doubleValue(), dest.getLatitude().doubleValue()
        );

        saveOrUpdateRoute(source, dest, info);
        saveOrUpdateRoute(dest, source, info);
    }

    private void saveOrUpdateRoute(Hub source, Hub dest, KakaoRouteService.RouteInfo info) {
        HubRoute route = hubRouteRepository.findBySourceHubAndDestinationHub(source, dest)
                .orElseGet(() -> HubRoute.builder()
                        .sourceHub(source)
                        .destinationHub(dest)
                        .distance(info.distance())
                        .duration(info.duration())
                        .build());

        if (route.getId() == null) {
            hubRouteRepository.save(route);
            return;
        }

        route.updateRoute(info.duration(), info.distance());
    }
}