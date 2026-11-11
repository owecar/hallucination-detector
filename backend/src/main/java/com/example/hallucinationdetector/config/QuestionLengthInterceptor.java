package com.example.hallucinationdetector.config;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.example.hallucinationdetector.service.AuditLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * Input validation (security function). Refuses a question longer than MAX_LENGTH characters
 * before it reaches the controllers, the AI service or the database.
 * Applies to POST /submit and POST /verify.
 */
@Component
public class QuestionLengthInterceptor implements HandlerInterceptor {
    public static final int MAX_LENGTH = 1000;
    private final AuditLogger audit;
    public QuestionLengthInterceptor(AuditLogger audit) {
        this.audit = audit;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String question = request.getParameter("question");
        if (question != null && question.length() > MAX_LENGTH) {
            audit.log("SECURITY question rejected (too long: " + question.length() + " characters) "
                    + request.getRequestURI());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Your question is too long. Please keep it under " + MAX_LENGTH + " characters.");
            return false;
        }
        return true;
    }
}
