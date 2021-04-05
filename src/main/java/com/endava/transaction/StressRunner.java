package com.endava.transaction;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class StressRunner implements ApplicationListener<ContextRefreshedEvent> {
    private final ApplicationArguments applicationArguments;
    private final RestTemplate restTemplate;
    private final TaskExecutor taskExecutor;
    private final StressProperties stressProperties;

    public StressRunner(ApplicationArguments applicationArguments, RestTemplate restTemplate,
                        @Qualifier("stressTaskExecutor") TaskExecutor taskExecutor, StressProperties stressProperties) {
        this.applicationArguments = applicationArguments;
        this.restTemplate = restTemplate;
        this.taskExecutor = taskExecutor;
        this.stressProperties = stressProperties;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        String fromUserId = applicationArguments.getOptionValues("from.user.id").get(0);
        String toUserId = applicationArguments.getOptionValues("to.user.id").get(0);
        String amount = applicationArguments.getOptionValues("amount").get(0);
        String numberOfRequests = applicationArguments.getOptionValues("number.of.requests").get(0);
        TransferRequest request = new TransferRequest(fromUserId, toUserId, new BigDecimal(amount));
        for (int i = 0; i < Integer.parseInt(numberOfRequests); ++i) {
            taskExecutor.execute(() -> {
                try {
                    ResponseEntity<TransferResponse> responseEntity = restTemplate.postForEntity(getUrl(), request, TransferResponse.class);
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        log.info("Successfully transferred {} USD from user id: {} to user id {}", amount, fromUserId, toUserId);
                    } else {
                        log.info("Transfer rejected");
                    }
                } catch (Exception e) {
                    log.info("Transfer rejected");
                }
            });
        }
    }

    private String getUrl() {
        int endpointIndex = ThreadLocalRandom.current().nextInt(stressProperties.getEndpointUrls().size());
        return stressProperties.getEndpointUrls().get(endpointIndex);
    }

    public static class TransferRequest {
        private String fromUserId;
        private String toUserId;
        private BigDecimal amount;

        public TransferRequest() {
        }

        public TransferRequest(String fromUserId, String toUserId, BigDecimal amount) {
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.amount = amount;
        }

        public String getFromUserId() {
            return fromUserId;
        }

        public void setFromUserId(String fromUserId) {
            this.fromUserId = fromUserId;
        }

        public String getToUserId() {
            return toUserId;
        }

        public void setToUserId(String toUserId) {
            this.toUserId = toUserId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    public static class TransferResponse {
        private String response;

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }
}
