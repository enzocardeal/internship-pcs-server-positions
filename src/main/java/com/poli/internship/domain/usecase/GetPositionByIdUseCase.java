package com.poli.internship.domain.usecase;

import com.poli.internship.data.datasource.PositionDataSource;
import com.poli.internship.domain.models.PositionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetPositionByIdUseCase {
    @Autowired
    private PositionDataSource dataSource;

    public PositionModel exec(String id){
        return this.dataSource.getPositionById(id);
    }
}
