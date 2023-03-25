package com.poli.internship.api.controller;

import com.poli.internship.api.auth.GraphQLAuthorization;
import com.poli.internship.api.error.CustomError;
import com.poli.internship.domain.models.UserType;
import com.poli.internship.domain.usecase.application.*;
import graphql.GraphQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.ErrorType;
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
    public GetAllApplicationsUseCase getAllApplicationsUseCase;
    @Autowired
    public GetApplicationByIdUseCase getApplicationByIdUseCase;

    @QueryMapping
    public Application getApplicationById(@Argument Map input, GraphQLContext ctx) {
        GraphQLAuthorization.checkAuthorization(ctx);

        Map data = (Map) input.get("input");
        Application application = this.getApplicationByIdUseCase.exec((String) data.get("id"));
        if(UserType.valueOf(ctx.get("userType")) == UserType.STUDENT && !((String)ctx.get("userId")).equals(application.userId())) {
            throw new CustomError("Not current student application.", ErrorType.UNAUTHORIZED);
        }

        return application;
    }

    @QueryMapping
    public List<Application> getAllApplications(GraphQLContext ctx) {
        GraphQLAuthorization.checkAuthorization(ctx);
        if(UserType.valueOf(ctx.get("userType")) == UserType.STUDENT){
            throw new CustomError("User not allowed to check applications.", ErrorType.UNAUTHORIZED);
        }

        return this.getAllApplicationsUseCase.exec((String) ctx.get("userId"));
    }

    @MutationMapping
    public Application createApplication(@Argument Map input, GraphQLContext ctx) {
        GraphQLAuthorization.checkAuthorization(ctx);
        if(UserType.valueOf(ctx.get("userType")) != UserType.STUDENT){
            throw new CustomError("User not allowed to create application.", ErrorType.UNAUTHORIZED);
        }

        Map data = (Map) input.get("input");
        return this.createApplicationUseCase.exec(
                (String)data.get("positionId"),
                (String)ctx.get("userId")
        );
    }

    @MutationMapping
    public Boolean deleteApplication(@Argument Map input, GraphQLContext ctx){
        GraphQLAuthorization.checkAuthorization(ctx);
        if(UserType.valueOf(ctx.get("userType")) != UserType.STUDENT){
            throw new CustomError("User not allowed to delete application.", ErrorType.UNAUTHORIZED);
        }
        Map data = (Map) input.get("input");
        String id = (String) data.get("id");

        Application application = this.getApplicationByIdUseCase.exec(id);
        if(!application.userId().equals((String)ctx.get("userId"))){
            throw new CustomError("This application wasn't created by the current user.", ErrorType.UNAUTHORIZED);
        }

        return this.deleteApplicationUseCase.exec(id);
    }
}
