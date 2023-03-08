package com.poli.internship.domain.usecase;

import com.poli.internship.data.datasource.PositionDataSource;
import com.poli.internship.domain.models.PositionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.poli.internship.domain.models.PositionModel.PositionInput;
import static com.poli.internship.domain.models.PositionModel.Position;


@Service
public class CreatePositionUseCase {
    @Autowired
    private PositionDataSource dataSource;

    public Position exec(PositionInput positionInput){
        return this.dataSource.createPosition(positionInput);
    }
}
