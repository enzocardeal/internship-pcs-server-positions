package com.poli.internship.domain.usecase;

import com.poli.internship.data.datasource.PositionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.poli.internship.domain.models.PositionModel.Position;

@Service
public class GetPositionByIdUseCase {
    @Autowired
    private PositionDataSource dataSource;

    public Position exec(String id){
        return this.dataSource.getPositionById(id);
    }
}
