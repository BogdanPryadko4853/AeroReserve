package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.CityEntity;
import com.bogdan.aeroreserve.entity.RouteEntity;
import com.bogdan.aeroreserve.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;

    public Optional<RouteEntity> getRouteByCities(CityEntity departure, CityEntity arrival) {
        return routeRepository.findByDepartureCityAndArrivalCity(departure, arrival);
    }

    public RouteEntity createRoute(RouteEntity route) {
        return routeRepository.save(route);
    }

    public List<RouteEntity> getAllRoutes() {
        return routeRepository.findAll();
    }
}
