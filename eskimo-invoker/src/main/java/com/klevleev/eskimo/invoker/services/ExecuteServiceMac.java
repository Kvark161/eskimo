package com.klevleev.eskimo.invoker.services;

import com.klevleev.eskimo.invoker.entity.*;
import com.klevleev.eskimo.invoker.enums.CompilationVerdict;
import com.klevleev.eskimo.invoker.executers.TesterMac;
import com.klevleev.eskimo.invoker.utils.InvokerUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ExecuteServiceMac implements ExecuteService {

    private static final Logger logger = LoggerFactory.getLogger(ExecuteServiceMac.class);

    private InvokerUtils invokerUtils;

    @Autowired
    public ExecuteServiceMac(InvokerUtils invokerUtils) {
        this.invokerUtils = invokerUtils;
    }

    @Override
    public CompilationResult compile(CompilationParams compilationParams) {
        File folder = null;
        try {
            folder = invokerUtils.createTempFolder();
            File sourceFile = new File(folder.getAbsolutePath() + File.separator + compilationParams.getSourceFileName());
            File executableFile = new File(folder.getAbsolutePath() + File.separator + compilationParams.getExecutableFileName());
            FileUtils.writeStringToFile(sourceFile, compilationParams.getSourceCode());
            String command = compilationParams.prepareCompilationComman(sourceFile.getAbsolutePath(), executableFile.getAbsolutePath());
            ExecutionResult executionResult = invokerUtils.executeCommand(command, null, compilationParams.getTimelimit());
            CompilationResult compilationResult = new CompilationResult();
            compilationResult.setCompilerStdout(executionResult.getStdout());
            compilationResult.setCompilerStderr(executionResult.getStderr());
            if (executionResult.getExitCode() == 0) {
                compilationResult.setVerdict(CompilationVerdict.SUCCESS);
                compilationResult.setExecutable(FileUtils.readFileToByteArray(executableFile));
            } else {
                compilationResult.setVerdict(CompilationVerdict.COMPILATION_ERROR);
            }
            return compilationResult;
        } catch (Throwable e) {
            logger.error("Error while compiling " + e.getMessage());
            CompilationResult compilationResult = new CompilationResult();
            compilationResult.setVerdict(CompilationVerdict.INTERNAL_INVOKER_ERROR);
            return compilationResult;
        } finally {
            if (folder != null) {
                try {
                    FileUtils.deleteDirectory(folder);
                } catch (IOException e) {
                    logger.warn("Can't delete directory after compilation: " + folder.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public TestResult test(TestParams testParams) {
        return test(Collections.singletonList(testParams), false).get(0);
    }

    @Override
    public List<TestResult> test(List<TestParams> testParams, boolean stopOnFirstFail) {
        return new TesterMac(invokerUtils, testParams, stopOnFirstFail).test();
    }

}
