package ru.practicum.stats.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.SearchCriteriaDto;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.model.Hit;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Hit save(Hit hit) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("hits")
                .usingGeneratedKeyColumns("id");

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("app", hit.getApp())
                .addValue("uri", hit.getUri())
                .addValue("ip", hit.getIp())
                .addValue("created", hit.getTimestamp());

        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);
        hit.setId(id.longValue());

        return hit;
    }

    @Override
    public List<ViewStats> findStats(SearchCriteriaDto searchCriteriaDto) {
        StringBuilder sql = new StringBuilder();

        if (searchCriteriaDto.isUnique()) {
            sql.append("SELECT app, uri, COUNT(distinct ip) AS column_hits ");
        } else {
            sql.append("SELECT app, uri, COUNT(*) AS column_hits ");
        }

        sql.append("FROM hits WHERE created >= :start AND created <= :end ");

        SqlParameterSource namedParameters;
        if (searchCriteriaDto.getUris() != null && !searchCriteriaDto.getUris().isEmpty()) {
            namedParameters = new MapSqlParameterSource()
                    .addValue("start", searchCriteriaDto.getStart())
                    .addValue("end", searchCriteriaDto.getEnd())
                    .addValue("uris", searchCriteriaDto.getUris());

            sql.append("AND uri IN (:uris) ");
        } else {
            namedParameters = new MapSqlParameterSource()
                    .addValue("start", searchCriteriaDto.getStart())
                    .addValue("end", searchCriteriaDto.getEnd());
        }

        sql.append("GROUP BY app, uri ");
        sql.append("ORDER BY column_hits DESC");

        return namedParameterJdbcTemplate.query(sql.toString(), namedParameters, viewStatsRowMapper);
    }

    private final RowMapper<ViewStats> viewStatsRowMapper = (resultSet, rowNum) -> ViewStats.builder()
            .app(resultSet.getString("APP"))
            .uri(resultSet.getString("URI"))
            .hits(resultSet.getInt("COLUMN_HITS"))
            .build();
}