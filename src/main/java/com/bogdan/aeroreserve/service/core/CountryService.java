package com.bogdan.aeroreserve.service.core;

import com.bogdan.aeroreserve.entity.CountryEntity;
import com.bogdan.aeroreserve.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public CountryEntity createCountry(String name, String code, String currency, String timezone) {
        CountryEntity country = new CountryEntity();
        country.setName(name);
        country.setCode(code);
        country.setCurrency(currency);
        country.setTimezone(timezone);
        return countryRepository.save(country);
    }

    public List<CountryEntity> getAllCountries() {
        return countryRepository.findAll();
    }

    public Optional<CountryEntity> getCountryByCode(String code) {
        return countryRepository.findByCode(code);
    }
}
