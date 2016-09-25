package com.klevleev.eskimo.server.core.services;

import com.klevleev.eskimo.server.core.domain.Contest;
import com.klevleev.eskimo.server.core.domain.Problem;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Stepan Klevleev on 25-Aug-16.
 */
public interface ContestService {

	Contest createContest(File contestRoot);

	Contest getContestById(Long contestId);

	Boolean contestExists(Long id);

	List<Contest> getAllContests();

	Problem getContestProblem(Long contestId, Long problemId);

	InputStream getStatements(Long contestId);
}
