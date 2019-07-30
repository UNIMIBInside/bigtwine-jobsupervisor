package it.unimib.disco.bigtwine.services.jobsupervisor.executor;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.AnalysisInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TwitterNeelUtil {

    private static final Logger log = LoggerFactory.getLogger(TwitterNeelUtil.class);

    public static String flattifyAnalysisInput(AnalysisInfo analysis) {
        try {
            if (analysis.isQueryInputType()) {
                String joinOperator = ((String) analysis.getInput()
                    .get(AnalysisInfo.InputKeys.JOIN_OPERATOR))
                    .toLowerCase();
                @SuppressWarnings("unchecked")
                List<String> tokens = (List<String>) analysis.getInput().get(AnalysisInfo.InputKeys.TOKENS);

                if (joinOperator.equals("any")) {
                    return String.join(",", tokens);
                } else if (joinOperator.equals("all")) {
                    return String.join(" ", tokens);
                } else {
                    log.debug("Cannot flattify query input, Invalid join operator: {}", joinOperator);
                    return null;
                }
            } else if (analysis.isDatasetInputType()) {
                return (String) analysis.getInput().get(AnalysisInfo.InputKeys.DOCUMENT_ID);
            } else {
                log.debug("Invalid input type: {}", analysis.getInput().get(AnalysisInfo.InputKeys.TYPE));
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
