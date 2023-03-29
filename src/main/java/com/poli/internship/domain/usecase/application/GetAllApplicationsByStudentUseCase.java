package com.poli.internship.domain.usecase.application;

import com.poli.internship.data.datasource.ApplicationDataSource;
import com.poli.internship.domain.models.ApplicationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllApplicationsByStudentUseCase {
    @Autowired
    private ApplicationDataSource dataSource;

    public List<ApplicationModel.Application> exec(String studentUserId){
        return this.dataSource.getAllApplicationsByStudent(studentUserId);
    }
}
