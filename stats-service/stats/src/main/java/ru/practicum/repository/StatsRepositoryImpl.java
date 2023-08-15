package ru.practicum.repository;

import dto.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Hit save(Hit hit) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("HITS")
                .usingGeneratedKeyColumns("ID");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("APP", hit.getApp())
                .addValue("URI", hit.getUri())
                .addValue("IP", hit.getIp())
                .addValue("CREATED", hit.getTimestamp());

        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);
        hit.setId(id.longValue());

        return hit;
    }

    @Override
    public List<ViewStats> findStats(SearchCriteria searchCriteria) {
        StringBuilder sql = new StringBuilder();

        if (searchCriteria.isUnique()) {
            sql.append("SELECT app, uri, COUNT(distinct ip) AS column_hits ");
        } else {
            sql.append("SELECT app, uri, COUNT(*) AS column_hits ");
        }

        sql.append("FROM hits WHERE created >= :start AND created <= :end ");

        SqlParameterSource namedParameters;
        if (searchCriteria.getUris() != null && !searchCriteria.getUris().isEmpty()) {
            namedParameters = new MapSqlParameterSource()
                    .addValue("start", searchCriteria.getStart())
                    .addValue("end", searchCriteria.getEnd())
                    .addValue("uris", searchCriteria.getUris());

            sql.append("AND uri IN (:uris) ");
        } else {
            namedParameters = new MapSqlParameterSource()
                    .addValue("start", searchCriteria.getStart())
                    .addValue("end", searchCriteria.getEnd());
        }

        sql.append("GROUP BY app, uri");

        return namedParameterJdbcTemplate.query(sql.toString(), namedParameters, viewStatsRowMapper);
    }

    private final RowMapper<ViewStats> viewStatsRowMapper = (resultSet, rowNum) -> ViewStats.builder()
            .app(resultSet.getString("APP"))
            .uri(resultSet.getString("URI"))
            .hits(resultSet.getInt("COLUMN_HITS"))
            .build();
}