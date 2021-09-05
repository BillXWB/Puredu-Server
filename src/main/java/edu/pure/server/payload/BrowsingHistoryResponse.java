package edu.pure.server.payload;

import edu.pure.server.model.CourseName;
import edu.pure.server.opedukg.entity.KnowledgeBaseEntityDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BrowsingHistoryResponse {
    private final KnowledgeBaseEntityDetail entity;
    private final CourseName course;
}
