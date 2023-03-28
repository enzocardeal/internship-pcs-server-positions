package com.poli.internship.domain.models;

import static com.poli.internship.domain.models.PositionModel.Position;

public class ApplicationModel {
    public static record Application(String id, Position position, String userId){};
    public static record ApplicationMessage(CurriculumAuthorizationActionType action, String companyId, String studentId){};
}
