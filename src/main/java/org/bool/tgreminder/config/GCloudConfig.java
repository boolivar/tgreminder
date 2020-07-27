package org.bool.tgreminder.config;

import org.bool.tgreminder.core.TelegramBotToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;

@Profile("gcloud")
@Configuration
public class GCloudConfig {

    @Primary
    @Bean
    public TelegramBotToken gcloudToken(Firestore firestore) throws Exception {
        String value = firestore
                .collection("properties")
                .document("telegram-bot")
                .get().get().getString("token");
        return new TelegramBotToken(value);
    }
    
    @Bean
    public Firestore firestore() {
        return FirestoreOptions.getDefaultInstance().getService();
    }
}
