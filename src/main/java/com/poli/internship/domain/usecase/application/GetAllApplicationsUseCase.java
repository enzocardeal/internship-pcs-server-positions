package com.poli.internship.domain.usecase.application;

import com.poli.internship.data.datasource.ApplicationDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.poli.internship.domain.models.ApplicationModel.Application;

@Service
public class GetAllApplicationsUseCase {
    @Autowired
    private ApplicationDataSource dataSource;

    public List<Application> exec(String userId){
        return this.dataSource.getAllApplicationsByUserId(userId);
    }
}
