package abheist.todo.repositories;

import abheist.todo.models.Todo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class TodoRepository {

    private final JdbcTemplate jdbcTemplate;

    public TodoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Todo> findAll() {
        String sql = "SELECT * FROM todos";
        return jdbcTemplate.query(sql, new TodoRowMapper());
    }

    public Optional<Todo> findById(Long id) {
        String sql = "SELECT * FROM todos WHERE id = ?";
        return jdbcTemplate.query(sql, new TodoRowMapper(), id).stream().findFirst();
    }

    public Todo save(Todo todo) {
        if (todo.getId() == null) {
            String sql = "INSERT INTO todos (title, description, completed, created_at) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, todo.getTitle(), todo.getDescription(), todo.isCompleted(), todo.getCreatedAt());
        } else {
            String sql = "UPDATE todos SET title = ?, description = ?, completed = ? WHERE ID = ?";
            jdbcTemplate.update(sql, todo.getTitle(), todo.getDescription(), todo.isCompleted(), todo.getId());
        }
        return todo;
    }

    public void deleteById(Long id) {
        String sql = "DELETE FROM todos WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private static class TodoRowMapper implements RowMapper<Todo> {
        @Override
        public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Todo(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getBoolean("completed"),
                    rs.getTimestamp("created_at").toLocalDateTime()
            );
        }
    }
}