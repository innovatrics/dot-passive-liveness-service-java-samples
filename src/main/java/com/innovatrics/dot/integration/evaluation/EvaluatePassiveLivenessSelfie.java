package com.innovatrics.dot.integration.evaluation;

import com.innovatrics.dot.integrationsamples.api.ApiClient;
import com.innovatrics.dot.integration.Configuration;
import com.innovatrics.dot.integrationsamples.api.ApiException;
import com.innovatrics.dot.integrationsamples.api.model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static com.innovatrics.dot.integrationsamples.api.model.Error.NO_FACE_DETECTED;
import static com.innovatrics.dot.integrationsamples.api.model.Warning.LOW_QUALITY;
import static com.innovatrics.dot.integrationsamples.api.model.Warning.MULTIPLE_FACES_DETECTED;


/**
 * This example demonstrates usage of liveness evaluation API for liveness detection.
 */
public class EvaluatePassiveLivenessSelfie {
    private static final Logger LOG = LogManager.getLogger(EvaluatePassiveLivenessSelfie.class);
    public static void main(String[] args) throws IOException {

        final Configuration configuration = new Configuration();
        final ApiClient client = new ApiClient().setBasePath(configuration.DOT_PASSIVE_LIVENESS_SERVICE_URL);
        client.setBearerToken(configuration.DOT_AUTHENTICATION_TOKEN);

        final LivenessApi livenessApi = new LivenessApi(client);
        try{
            final PassiveLivenessEvaluationRequestImageDTO  passiveLivenessEvaluationRequestImageDTO = new PassiveLivenessEvaluationRequestImageDTO().data(configuration.EXAMPLE_BASE64_ENCODED_IMAGE_STRING);
            final PassiveLivenessEvaluationRequestSelfieDTO exampleSelfieFromBase64  = new PassiveLivenessEvaluationRequestSelfieDTO().image(passiveLivenessEvaluationRequestImageDTO);
            final PassiveLivenessEvaluationRequest passiveLivenessEvaluationRequest = new PassiveLivenessEvaluationRequest().selfie(exampleSelfieFromBase64);
            final PassiveLivenessEvaluationResponse evaluateLivenessBySelfie = livenessApi.evaluatePassiveLiveness( passiveLivenessEvaluationRequest);

            if (evaluateLivenessBySelfie.getScore() != null) {
                LOG.info("Selfie is evaluated with score" + evaluateLivenessBySelfie.getScore());
                if (evaluateLivenessBySelfie.getScore() > configuration.LIVENESS_THRESHOLD_SCORE) {
                    LOG.info("Selfie was evaluated as genuine, with score: " + evaluateLivenessBySelfie.getScore());
                } else {
                    LOG.info("Selfie was evaluated as spoof, with score: " + evaluateLivenessBySelfie.getScore());
                }
            }

            if (evaluateLivenessBySelfie.getWarnings() != null) {
                if (evaluateLivenessBySelfie.getWarnings().getSelfie().contains(MULTIPLE_FACES_DETECTED)) {
                    LOG.warn("Image added into Passive Liveness check contains more than one just face. " +
                            "Only the biggest face will be evaluated for Passive Liveness");
                }
                if (evaluateLivenessBySelfie.getWarnings().getSelfie().contains(LOW_QUALITY)) {
                    LOG.warn("Image added into Passive Liveness check has low quality and liveness evaluation may NOT be reliable. " +
                            "Please use photos satisfying image requirements: https://developers.innovatrics.com/digital-onboarding/docs/functionalities/face/passive-liveness-check/#passive-liveness-evaluation");
                }
            }

            if (evaluateLivenessBySelfie.getErrors()!= null) {
                if (evaluateLivenessBySelfie.getErrors().getSelfie().contains(NO_FACE_DETECTED)) {
                    LOG.warn("Face was not detected on image. Passive Liveness can not be evaluated on this image.");
                } else {
                    LOG.error("This should not happen.");
                }
            } else {
                LOG.info("Added image for Passive Liveness evaluation.");
            }
        } catch (ApiException exception){
            LOG.error("Request to server failed with code: " + exception.getCode() + " and response: " + exception.getResponseBody());
        }
    }
}
