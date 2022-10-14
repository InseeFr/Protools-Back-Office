package com.protools.flowableDemo.services.providers;

import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public interface NomenclatureValueProvider {
    public abstract Collection<?> getNomenclatureValue(String nomenclatureId) throws Exception;
}
