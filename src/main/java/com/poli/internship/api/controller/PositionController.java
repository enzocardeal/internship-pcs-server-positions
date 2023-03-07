package com.poli.internship.api.controller;

import com.poli.internship.domain.models.PositionModel;
import com.poli.internship.domain.usecase.CreatePositionUseCase;
import com.poli.internship.domain.usecase.DeletePositionUseCase;
import com.poli.internship.domain.usecase.GetPositionByIdUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class PositionController {
    @Autowired
    public GetPositionByIdUseCase getPositionByIdUseCase;
    @Autowired
    public CreatePositionUseCase createPositionUseCase;
    @Autowired
    public DeletePositionUseCase deletePositionUseCase;

    @QueryMapping
    public PositionModel getPositionById(@Argument Map input){
        Map data = (Map)input.get("input");
        return this.getPositionByIdUseCase.exec((String) data.get("id"));
    }

    @MutationMapping
    public PositionModel createPosition(@Argument Map input){
        Map data = (Map)input.get("input");
        return this.createPositionUseCase.exec((String)data.get("positionName"), (String)data.get("company"), (String)data.get("role"), (LocalDate)data.get("startsAt"), (LocalDate)data.get("endsAt"));
    }

    @MutationMapping
    public Boolean deletePosition(@Argument Map input){
        Map data = (Map)input.get("input");
        return this.deletePositionUseCase.exec((String)data.get("id"));
    }
}
