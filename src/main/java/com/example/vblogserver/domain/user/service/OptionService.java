package com.example.vblogserver.domain.user.service;

import com.example.vblogserver.domain.user.entity.Option;
import com.example.vblogserver.domain.user.repository.OptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionService {

    private final OptionRepository optionRepository;

    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }
}
