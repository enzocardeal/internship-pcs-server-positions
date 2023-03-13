package com.poli.internship.domain.usecase;

import com.poli.internship.data.datasource.PositionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.poli.internship.domain.models.PositionModel.Position;

import java.util.List;

@Service
public class GetAllPositionsUseCase {
    @Autowired
    private PositionDataSource dataSource;

    public List<Position> exec(){
        return this.dataSource.getAllPositions();
    }
}
