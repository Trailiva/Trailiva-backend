package com.trailiva.event;

import com.trailiva.data.model.User;
import com.trailiva.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.trailiva.data.model.TokenType.VERIFICATION;

@Component
@AllArgsConstructor
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final AuthService authService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event){
        User User = event.getUser();
        String token = UUID.randomUUID().toString();
        authService.createVerificationToken(User, token, VERIFICATION.toString());
//        authService.sendVerificationToken(User, token);
    }
}
