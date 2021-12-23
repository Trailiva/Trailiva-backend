package com.trailiva.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.trailiva.web.payload.request.EmailRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{
    @Override
    public void sendUserVerificationEmail(EmailRequest emailRequest) {

        // the sender email should be the same as we used to Create a Single Sender Verification
        Email from = new Email(emailRequest.getFrom());
        Email to = new Email(emailRequest.getEmail());
        Mail mail = new Mail();
        // we create an object of our static class feel free to change the class on it's own file
        // I try to keep every think simple

        // we create an object of our static class feel free to change the class on it's own file
        // I try to keep every think simple
        DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
        personalization.addTo(to);
        mail.setFrom(from);

        // This is the first_name variable that we created on the template
        personalization.addDynamicTemplateData("first_name", emailRequest.getFirstName());
        personalization.addDynamicTemplateData("last_name", emailRequest.getLastName());
        personalization.addDynamicTemplateData("token", "-NAI8dyK5zc");

        mail.addPersonalization(personalization);
        mail.setTemplateId(System.getenv("API_ID"));
        // this is the api key
        SendGrid sg = new SendGrid(System.getenv("API_KEY"));
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            log.info(response.getBody());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // This class handels the dynamic data for the template
    // Feel free to customise this class our to putted on other file
    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class DynamicTemplatePersonalization extends Personalization {

        @JsonProperty(value = "dynamic_template_data")
        private Map<String, Object> dynamic_template_data;

        @JsonProperty("dynamic_template_data")
        public Map<String, Object> getDynamicTemplateData() {
            if (dynamic_template_data == null) {
                return Collections.emptyMap();
            }
            return dynamic_template_data;
        }

        public void addDynamicTemplateData(String key, String value) {
            if (dynamic_template_data == null) {
                dynamic_template_data = new HashMap<>();
                dynamic_template_data.put(key, value);
            } else {
                dynamic_template_data.put(key, value);
            }
        }
    }

}


