package com.poli.internship.domain.usecase.position;

import com.poli.internship.data.datasource.PositionDataSource;
import com.poli.internship.domain.models.PositionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeletePositionUseCase {
    @Autowired
    private PositionDataSource dataSource;

    public Boolean exec(String id) {
        return this.dataSource.deletePosition(id);
    }
}
