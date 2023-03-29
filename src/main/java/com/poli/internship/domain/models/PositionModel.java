package com.poli.internship.domain.models;

import java.time.LocalDate;
import java.util.List;

public class PositionModel {
    public static record Position(String id, String userId, String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt, List<String> steps, String benefits, String area, String location, String scholarship){};
    public static record PositionInput(String userId, String positionName, String company, String role, LocalDate startsAt, LocalDate endsAt, List<String> steps, String benefits, String area, String location, String scholarship){};
}
