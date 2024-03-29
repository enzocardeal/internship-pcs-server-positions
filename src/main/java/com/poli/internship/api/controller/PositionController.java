package com.poli.internship.api.controller;

import com.poli.internship.api.auth.GraphQLAuthorization;
import com.poli.internship.api.error.CustomError;
import com.poli.internship.domain.models.UserType;
import com.poli.internship.domain.usecase.position.*;
import graphql.GraphQLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;

import static com.poli.internship.domain.models.PositionModel.PositionInput;
import static com.poli.internship.domain.models.PositionModel.Position;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class PositionController {
    @Autowired
    public GetPositionByIdUseCase getPositionByIdUseCase;
    @Autowired
    public CreatePositionUseCase createPositionUseCase;
    @Autowired
    public DeletePositionUseCase deletePositionUseCase;

    @Autowired
    public GetAllPositionsUseCase getAllPositionsUseCase;

    @Autowired
    public GetAllPositionsByIdsUseCase getAllPositionsByIdsUseCase;

    @Autowired
    public UpdatePositionUseCase updatePositionUseCase;

    @QueryMapping
    public Position getPositionById(@Argument Map input){
        Map data = (Map)input.get("input");
        return this.getPositionByIdUseCase.exec((String) data.get("id"));
    }

    @QueryMapping
    public List<Position> getAllPositionsByIds(@Argument Map input){
        Map data = (Map)input.get("input");
        return this.getAllPositionsByIdsUseCase.exec((List<String>) data.get("ids"));
    }

    @QueryMapping
    public List<Position> getAllPositions(){
        return this.getAllPositionsUseCase.exec();
    }

    @MutationMapping
    public Position createPosition(@Argument Map input, GraphQLContext ctx){
        GraphQLAuthorization.checkAuthorization(ctx);
        if(UserType.valueOf(ctx.get("userType")) != UserType.COMPANY) {
            throw new CustomError("Wrong user type.", ErrorType.FORBIDDEN);
        }

        Map data = (Map)input.get("input");
        return this.createPositionUseCase.exec(
                new PositionInput(
                        (String)ctx.get("userId"),
                        (String)data.get("positionName"),
                        (String)data.get("company"),
                        (String)data.get("role"),
                        (String)data.get("description"),
                        (LocalDate)data.get("startsAt"),
                        (LocalDate)data.get("endsAt"),
                        (List<String>)data.get("steps"),
                        (String)data.get("benefits"),
                        (String)data.get("compensation"),
                        (String)data.get("area"),
                        (String)data.get("location"),
                        (String)data.get("scholarship")
                )

        );
    }

//    @MutationMapping
//    public Position updatePosition(@Argument Map input, GraphQLContext ctx){
//        GraphQLAuthorization.checkAuthorization(ctx);
//        if(UserType.valueOf(ctx.get("userType")) != UserType.COMPANY) {
//            throw new CustomError("Wrong user type.", ErrorType.FORBIDDEN);
//        }
//
//        Map data = (Map)input.get("input");
//        String id = (String)data.get("id");
//
//        Position positionToBeUpdated = this.getPositionByIdUseCase.exec((String)data.get("id"));
//        if(!positionToBeUpdated.userId().equals((String)ctx.get("userId"))){
//            throw new CustomError("This position wasn't created by the current user.", ErrorType.FORBIDDEN);
//        }
//        return this.updatePositionUseCase.exec(
//                new PositionInput(
//                        (String) ctx.get("userId"),
//                        data.get("positionName") != null ? (String)data.get("positionName") : positionToBeUpdated.positionName(),
//                        data.get("company") != null ? (String)data.get("company") : positionToBeUpdated.company(),
//                        data.get("role") != null ? (String)data.get("role") : positionToBeUpdated.role(),
//                        data.get("startsAt") != null ? (LocalDate)data.get("startsAt") : positionToBeUpdated.startsAt(),
//                        data.get("endsAt") != null ? (LocalDate)data.get("endsAt") : positionToBeUpdated.endsAt(),
//                        data.get("steps") != null ? (List<String>)data.get("steps") : positionToBeUpdated.steps(),
//                        data.get("benefits") != null ? (String)data.get("benefits") : positionToBeUpdated.benefits(),
//                        data.get("area") != null ? (String)data.get("area") : positionToBeUpdated.area(),
//                        data.get("location") != null ? (String)data.get("location") : positionToBeUpdated.location(),
//                        data.get("scholarship") != null ? (String)data.get("scholarship") : positionToBeUpdated.scholarship()
//                ),
//                id
//        );
//    }

    @MutationMapping
    public Boolean deletePosition(@Argument Map input, GraphQLContext ctx){
        GraphQLAuthorization.checkAuthorization(ctx);
        if(UserType.valueOf(ctx.get("userType")) != UserType.COMPANY) {
            throw new CustomError("Wrong user type.", ErrorType.FORBIDDEN);
        }

        Map data = (Map)input.get("input");
        String id = (String)data.get("id");

        Position position = this.getPositionByIdUseCase.exec(id);
        if(!position.userId().equals((String)ctx.get("userId"))){
            throw new CustomError("This position wasn't created by the current user.", ErrorType.FORBIDDEN);
        }

        return this.deletePositionUseCase.exec(id);
    }
}
