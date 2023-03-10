package com.poli.internship.domain.models;

import java.time.LocalDate;

public class PositionModel {
    public static record Position(String id, String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt){};
    public static record PositionInput(String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt){};
}
