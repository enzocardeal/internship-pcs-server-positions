package com.poli.internship.domain.usecase;

import com.poli.internship.data.datasource.PositionDataSource;
import com.poli.internship.domain.models.PositionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CreatePositionUseCase {
    @Autowired
    private PositionDataSource dataSource;

    public PositionModel exec(String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt){
        return this.dataSource.createPosition(positionName, company, role, startsAt, endsAt);
    }
}
