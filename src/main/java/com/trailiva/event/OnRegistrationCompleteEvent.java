package com.trailiva.event;

import com.trailiva.data.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private User User;
    private Locale locale;

    public OnRegistrationCompleteEvent(User User, Locale locale, String appUrl) {
        super(User);
        this.User = User;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
