package com.poli.internship.domain.usecase;

import com.poli.internship.data.datasource.PositionDataSource;
import com.poli.internship.domain.models.PositionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllPositions {
    @Autowired
    private PositionDataSource dataSource;

    public List<PositionModel> exec(){
        return this.dataSource.getAllPositions();
    }
}
