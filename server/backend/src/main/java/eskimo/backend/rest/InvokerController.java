package eskimo.backend.rest;

import eskimo.backend.storage.StorageService;
import eskimo.invoker.entity.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/invoker")
public class InvokerController {

    private static final Logger logger = LoggerFactory.getLogger(InvokerController.class);

    @Autowired
    private StorageService storageService;

    @GetMapping("test-data")
    public ResponseEntity<TestData> getTestData(long contestId, long problemId, int testId, boolean needAnswer) {
        try {
            TestData testData = new TestData();
            testData.setIndex(testId);
            testData.setInputData(storageService.getTestInputData(contestId, problemId, testId));
            if (needAnswer) {
                testData.setAnswerData(storageService.getTestAnswerData(contestId, problemId, testId));
            }
            return new ResponseEntity<>(testData, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("error occurred while fetching test data contestId=" + contestId + " problemId=" + problemId + " testId=" + testId, e);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
