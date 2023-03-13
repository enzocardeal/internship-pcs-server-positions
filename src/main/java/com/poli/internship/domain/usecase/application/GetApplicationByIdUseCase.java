package com.poli.internship.domain.usecase.application;

import com.poli.internship.data.datasource.ApplicationDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.poli.internship.domain.models.ApplicationModel.Application;

@Service
public class GetApplicationByIdUseCase {
    @Autowired
    private ApplicationDataSource dataSource;

    public Application exec(String id){
        return this.dataSource.getApplicationById(id);
    }
}
