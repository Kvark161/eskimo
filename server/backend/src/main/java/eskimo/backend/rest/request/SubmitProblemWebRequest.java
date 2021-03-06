package eskimo.backend.rest.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SubmitProblemWebRequest {
    private Long contestId;
    private Long problemIndex;
    private String sourceCode;
    private Long languageId;
}
