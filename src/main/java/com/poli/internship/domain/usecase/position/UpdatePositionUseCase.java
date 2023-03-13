package com.poli.internship.domain.usecase.position;

import com.poli.internship.data.datasource.PositionDataSource;
import com.poli.internship.domain.models.PositionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.poli.internship.domain.models.PositionModel.PositionInput;
import static com.poli.internship.domain.models.PositionModel.Position;


@Service
public class UpdatePositionUseCase {
    @Autowired
    private PositionDataSource dataSource;

    public Position exec(PositionInput positionInput, String id){
        return this.dataSource.updatePosition(positionInput, Long.parseLong(id));
    }
}
