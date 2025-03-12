package com.example.crm_backend.services;

import com.example.crm_backend.configurations.SecurityConfig;
import com.example.crm_backend.dtos.FeedbackDTO;
import com.example.crm_backend.entities.access_token.AccessToken;
import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.feedback.Feedback;
import com.example.crm_backend.entities.feedback.FeedbackValidator;
import com.example.crm_backend.repositories.AccountRepository;
import com.example.crm_backend.repositories.FeedbackRepository;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final FeedbackRepository feedback_repository;

    private final AccountRepository account_repository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, AccountRepository accountRepository) {
        feedback_repository = feedbackRepository;
        account_repository = accountRepository;
    }

    public String getFeedbackContent(String token) {
        String url = SecurityConfig.CLIENT_SERVER + "/public/feedback/" + token;
        return "<html><body>"
                + "<h2>Chào bạn,</h2>"
                + "<p>Hãy đánh giá trải nghiệm của bạn bằng cách nhấn vào nút bên dưới:</p>"
                + "<a href='" + url + "' style='display: inline-block; padding: 10px 20px; "
                + "font-size: 16px; text-align: center; text-decoration: none; background-color: #4CAF50; "
                + "color: white; border-radius: 5px;'>Đánh giá ngay</a>"
                + "</body></html>";
    }

    public Feedback create(AccessToken accessToken, FeedbackDTO dto) {
        Feedback feedback = new Feedback();
        Long account_id = accessToken.getObjectId();
        Account account = account_repository.findById(account_id).orElse(null);
        if (account == null) {
            throw new IllegalArgumentException("Invalid account");
        }

        ObjectMapper.mapAll(dto, feedback);
        feedback.setObjectId(account_id);
        feedback.setObjectType(accessToken.getObjectType());
        feedback.setSystemId(account.getSystemId());

        try {
            FeedbackValidator validator = new FeedbackValidator(feedback, this);
            validator.validate();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        feedback.setCreatedAt(Timer.now());
        feedback.setLastUpdate(Timer.now());

        return feedback_repository.save(feedback);
    }

    public Page<Feedback> paginateByAccount(int ipp, int page, String accountId, String query, Long start, Long end) {
        Pageable request = PageRequest.of(page, ipp, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "id"));
        if (end != null && end != 0) {
            end = Timer.endOfDay(end);
        }

        return feedback_repository.searchFeedbackByAccount(accountId, query, start, end, request);
    }

    public Long getLastByAccount(Long account_id) {
        if (account_id == null) {
            return 0L;
        }

        Feedback feedback = feedback_repository.getLastByAccount(account_id);
        if (feedback == null) {
            return 0L;
        }

        return feedback.getCreatedAt();
    }

    public Long countContact(Long account_id) {
        if (account_id == null) {
            return 0L;
        }

        return feedback_repository.countContact(account_id);
    }
}
