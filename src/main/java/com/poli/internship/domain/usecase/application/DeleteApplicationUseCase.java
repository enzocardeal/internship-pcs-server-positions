package com.poli.internship.domain.usecase.application;

import com.poli.internship.data.datasource.ApplicationDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteApplicationUseCase {
    @Autowired
    private ApplicationDataSource dataSource;

    public Boolean exec(String id) {
        return this.dataSource.deleteApplication(id);
    }
}
