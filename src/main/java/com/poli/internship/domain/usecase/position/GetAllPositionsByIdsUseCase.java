package com.poli.internship.domain.usecase.position;

import com.poli.internship.data.datasource.PositionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.poli.internship.domain.models.PositionModel.Position;

import java.util.List;

@Service
public class GetAllPositionsByIdsUseCase {
    @Autowired
    private PositionDataSource dataSource;

    public List<Position> exec(List<String> ids){
        return this.dataSource.getAllPositionsByIds(ids);
    }
}
