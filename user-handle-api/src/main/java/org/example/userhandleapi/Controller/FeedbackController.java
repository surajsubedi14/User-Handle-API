package org.example.userhandleapi.Controller;

import lombok.RequiredArgsConstructor;
import org.example.coreapi.Entities.Doctor;
import org.example.coreapi.Entities.Feedback;
import org.example.coreapi.Services.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-handle/feedback")
public class FeedbackController {
    @Autowired
    public FeedbackService feedbackService;

    @PutMapping("/update-feedback/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody String rating) {
        try {
            System.out.println(id);
            Feedback feedback = feedbackService.getFeedbacksByDoctorId(id);
            System.out.println(feedback);
            int ratingValue = Integer.parseInt(rating);
            if(ratingValue == 1){
                feedback.setOneStar(feedback.getOneStar()+1);}
            else if(ratingValue == 2){
                feedback.setTwoStar(feedback.getTwoStar()+1);}
            else if(ratingValue == 3){
                feedback.setThreeStar(feedback.getThreeStar()+1);}
            else if(ratingValue == 4){
                feedback.setFourStar(feedback.getFourStar()+1);}
            else{
                feedback.setFiveStar(feedback.getFiveStar()+1);}
            feedback.setNo_of_reviews(feedback.getNo_of_reviews()+1);
            feedbackService.addFeedback(feedback);
            return ResponseEntity.ok("Feedback  Updated successfully");
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }

    }
}
