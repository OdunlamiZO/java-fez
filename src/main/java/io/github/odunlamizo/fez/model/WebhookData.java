package io.github.odunlamizo.fez.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookData {

    /** The registered webhook URL. */
    private String webhook;

    /** All webhooks currently registered for the account. */
    private List<WebhookEntry> webhooks;
}
