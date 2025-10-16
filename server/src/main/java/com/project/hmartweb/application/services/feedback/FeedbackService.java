package com.project.hmartweb.application.services.feedback;

import com.project.hmartweb.application.repositories.FeedbackRepository;
import com.project.hmartweb.application.services.product.IProductService;
import com.project.hmartweb.application.services.user.IUserService;
import com.project.hmartweb.config.exceptions.NotFoundException;
import com.project.hmartweb.domain.dtos.FeedbackDTO;
import com.project.hmartweb.domain.entities.Feedback;
import com.project.hmartweb.domain.entities.Product;
import com.project.hmartweb.domain.entities.User;
import com.project.hmartweb.domain.paginate.BasePagination;
import com.project.hmartweb.domain.paginate.PaginationDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService implements IFeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final IUserService userService;
    private final IProductService productService;
    private final BasePagination<Feedback, FeedbackRepository> basePagination;
    private final ModelMapper mapper;

    @Override
    public Feedback insert(FeedbackDTO feedbackDTO) {
        Feedback feedback = mapper.map(feedbackDTO, Feedback.class);
        User user = userService.getById(feedbackDTO.getUserId());
        Product product = productService.getById(feedbackDTO.getProductId());
        feedback.setUser(user);
        feedback.setProduct(product);
        return feedbackRepository.save(feedback);
    }

    @Override
    public Feedback update(UUID id, FeedbackDTO feedbackDTO) {
        Feedback feedback = getById(id);
        mapper.map(feedbackDTO, feedback);
        User user = userService.getById(feedbackDTO.getUserId());
        Product product = productService.getById(feedbackDTO.getProductId());
        feedback.setUser(user);
        feedback.setProduct(product);
        return feedbackRepository.save(feedback);
    }

    @Override
    public void delete(Feedback feedback) {
        feedbackRepository.delete(feedback);
    }

    @Override
    public PaginationDTO<Feedback> getAll(Integer page, Integer perPage) {
        if (page == null && perPage == null) {
            return new PaginationDTO<>(feedbackRepository.findAll(), null);
        }
        return basePagination.paginate(page, perPage);
    }

    @Override
    public Optional<Feedback> findById(UUID id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public Feedback getById(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Đánh giá không tồn tại!", "Feedback not found"));
    }

    @Override
    public PaginationDTO<Feedback> getAllByProduct(String id, Integer page, Integer perPage, Integer star) {
        if (page == null || perPage == null) {
            return new PaginationDTO<>(feedbackRepository.findAllByProductId(id, star), null);
        }
        Page<Feedback> feedbacks = feedbackRepository.findAllByProductId(id,
                                                                         PageRequest.of(page, perPage), star);
        return basePagination.paginate(page, perPage, feedbacks);
    }

    @Override
    public List<Feedback> insertMultiple(List<FeedbackDTO> feedbackDTOS) {
        List<Feedback> feedbacks = new ArrayList<>();
        for (FeedbackDTO feedbackDTO : feedbackDTOS) {
            feedbacks.add(insert(feedbackDTO));
        }
        return feedbacks;
    }

    @Override
    public List<Feedback> updateMultiple(List<FeedbackDTO> feedbackDTOS) {
        return List.of();
    }

    @Override
    public int deleteMultiple(List<UUID> uuids) {
        return 0;
    }
}
