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
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

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

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("start", searchCriteriaDto.getStart())
                .addValue("end", searchCriteriaDto.getEnd());

        if (searchCriteriaDto.getUris() != null && !searchCriteriaDto.getUris().isEmpty()) {
            sql.append("AND uri IN (:uris) ");

            List<String> uris = searchCriteriaDto.getUris().stream()
                    .map(element -> element.replace("[", "").replace("]", ""))
                    .collect(Collectors.toList());

            parameters.addValue("uris", uris);
        }

        sql.append("GROUP BY app, uri ");
        sql.append("ORDER BY column_hits DESC");

        return namedParameterJdbcTemplate.query(sql.toString(), parameters, viewStatsRowMapper);
    }

/*    public List<Hit> findAll() {
        String sql = "SELECT * FROM hits";

        return namedParameterJdbcTemplate.query(sql.toString(), hitRowMapper);
    }*/

/*    private final RowMapper<Hit> hitRowMapper = (resultSet, rowNum) ->
            new Hit(resultSet.getLong("ID"),
                    resultSet.getString("APP"),
                    resultSet.getString("URI"),
                    resultSet.getString("IP"),
                    resultSet.getTimestamp("CREATED").toLocalDateTime());*/

    private final RowMapper<ViewStats> viewStatsRowMapper = (resultSet, rowNum) -> ViewStats.builder()
            .app(resultSet.getString("APP"))
            .uri(resultSet.getString("URI"))
            .hits(resultSet.getInt("COLUMN_HITS"))
            .build();
}