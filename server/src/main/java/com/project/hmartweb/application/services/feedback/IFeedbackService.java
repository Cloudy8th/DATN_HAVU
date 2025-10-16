package com.project.hmartweb.application.services.feedback;

import com.project.hmartweb.application.services.base.IBaseService;
import com.project.hmartweb.application.services.base.IBaseServiceMultiple;
import com.project.hmartweb.domain.dtos.FeedbackDTO;
import com.project.hmartweb.domain.entities.Feedback;
import com.project.hmartweb.domain.paginate.PaginationDTO;

import java.util.UUID;

public interface IFeedbackService extends IBaseService<Feedback, FeedbackDTO, UUID>,
        IBaseServiceMultiple<Feedback, FeedbackDTO, UUID> {
    PaginationDTO<Feedback> getAllByProduct(String id, Integer page, Integer perPage, Integer star);
}
