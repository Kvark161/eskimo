package eskimo.backend.dao;

import eskimo.backend.entity.Contest;
import eskimo.backend.entity.enums.ScoringSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ContestDao {

    private static final Logger logger = LoggerFactory.getLogger(ContestDao.class);

    private JdbcTemplate jdbcTemplate;

    private final ContestRowMapper rowMapper = new ContestRowMapper();

    @Autowired
    public ContestDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Contest> getAllContests() {
        String sql = "SELECT * FROM contests ORDER BY id";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public Long insertContest(Contest contest) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("contests")
                .usingGeneratedKeyColumns("id");
        Map<String, Object> params = new HashMap<>();
        params.put("name", contest.getName());
        params.put("start_time", Timestamp.from(contest.getStartTime()));
        params.put("duration_in_minutes", contest.getDuration());
        params.put("scoring_system", contest.getScoringSystem().name());
        return jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(params)).longValue();
    }

    public Contest getContestInfo(long id) {
        try {
            String sql = "SELECT * FROM contests WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("can not get contest by id = " + id, e);
            return null;
        }
    }

    public boolean contestExists(long id) {
        String sql = "SELECT count(*) FROM contests WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class) > 0;
    }

    public void editContest(Contest contest) {
        String sql = "UPDATE contests SET " +
                "name = ?, " +
                "start_time = ?, " +
                "duration_in_minutes = ?, " +
                "scoring_system = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(sql,
                contest.getName(),
                Timestamp.from(contest.getStartTime()),
                contest.getDuration(),
                contest.getScoringSystem().name(),
                contest.getId());
    }

    private static class ContestRowMapper implements RowMapper<Contest> {

        @Override
        public Contest mapRow(ResultSet resultSet, int i) throws SQLException {
            Contest contest = new Contest();
            contest.setId(resultSet.getLong("id"));
            contest.setName(resultSet.getString("name"));
            contest.setStartTime(resultSet.getTimestamp("start_time").toInstant());
            contest.setDuration(resultSet.getInt("duration_in_minutes"));
            contest.setScoringSystem(ScoringSystem.valueOf(resultSet.getString("scoring_system")));
            return contest;
        }
    }
}
