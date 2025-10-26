package com.project.tmartweb.application.services.feedback;

import com.project.tmartweb.application.services.base.IBaseService;
import com.project.tmartweb.application.services.base.IBaseServiceMultiple;
import com.project.tmartweb.domain.dtos.FeedbackDTO;
import com.project.tmartweb.domain.entities.Feedback;
import com.project.tmartweb.domain.paginate.PaginationDTO;

import java.util.UUID;

public interface IFeedbackService extends IBaseService<Feedback, FeedbackDTO, UUID>,
        IBaseServiceMultiple<Feedback, FeedbackDTO, UUID> {
    PaginationDTO<Feedback> getAllByProduct(String id, Integer page, Integer perPage, Integer star);
}
