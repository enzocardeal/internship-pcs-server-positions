package com.poli.internship.api.controller;

import com.poli.internship.domain.usecase.application.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

import static com.poli.internship.domain.models.ApplicationModel.Application;

@Controller
public class ApplicationController {
    @Autowired
    public CreateApplicationUseCase createApplicationUseCase;
    @Autowired
    public DeleteApplicationUseCase deleteApplicationUseCase;
    @Autowired
    public GetAllApplicationsByCurriculumIdUseCase getAllApplicationsByCurriculumIdUseCase;
    @Autowired
    public GetApplicationByIdUseCase getApplicationByIdUseCase;

    @QueryMapping
    public Application getApplicationById(@Argument Map input) {
        Map data = (Map) input.get("input");
        return this.getApplicationByIdUseCase.exec((String) data.get("id"));
    }

    @QueryMapping
    public List<Application> getAllApplicationsByCurriculumId(@Argument Map input) {
        Map data = (Map) input.get("input");
        return this.getAllApplicationsByCurriculumIdUseCase.exec((String) data.get("curriculumId"));
    }

    @MutationMapping
    public Application createApplication(@Argument Map input) {
        Map data = (Map) input.get("input");
        return this.createApplicationUseCase.exec(
                (String)data.get("positionId"),
                (String)data.get("curriculumId")
        );
    }

    @MutationMapping
    public Boolean deleteApplication(@Argument Map input){
        Map data = (Map) input.get("input");

        return this.deleteApplicationUseCase.exec( (String) data.get("id"));
    }
}
