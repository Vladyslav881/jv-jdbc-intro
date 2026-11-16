package mate.academy.dao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import mate.academy.dao.BookDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.Book;

@Dao
public class BookDaoImpl implements BookDao {
    @Override
    public Book create(Book book) {
        String sql = "INSERT INTO books (title, price) VALUES (?, ?)";
        Properties dbProperties = new Properties();
        dbProperties.put("user","root");
        dbProperties.put("password","15vlad0705");
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/book_store", dbProperties);
                PreparedStatement preparedStatement = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setBigDecimal(2, book.getPrice());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows < 1) {
                throw new DataProcessingException(
                        "Expected to insert at least one row, but inserted 0 rows");
            }
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong(1);
                    book.setId(id);
                } else {
                    throw new DataProcessingException("Failed to get the ID of the new book");
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can't add new books", e);
        }
        return book;
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        Properties dbProperties = new Properties();
        dbProperties.put("user","root");
        dbProperties.put("password","15vlad0705");
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/book_store", dbProperties);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String title = resultSet.getString("title");
                    BigDecimal price = resultSet.getBigDecimal("price");
                    Book book = new Book();
                    book.setId(id);
                    book.setTitle(title);
                    book.setPrice(price);
                    return Optional.of(book);
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can not create a connection to the DB", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        Properties dbProperties = new Properties();
        dbProperties.put("user","root");
        dbProperties.put("password","15vlad0705");
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/book_store", dbProperties);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("id");
                    String title = resultSet.getString("title");
                    BigDecimal price = resultSet.getBigDecimal("price");
                    Book book = new Book();
                    book.setId(id);
                    book.setTitle(title);
                    book.setPrice(price);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can not create a connection to the DB", e);
        }
        return books;
    }

    @Override
    public Book update(Book book) {
        String sql = "UPDATE books SET title = ?, price = ? WHERE id = ?";
        Properties dbProperties = new Properties();
        dbProperties.put("user","root");
        dbProperties.put("password","15vlad0705");
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/book_store", dbProperties);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, book.getTitle());
            preparedStatement.setBigDecimal(2, book.getPrice());
            preparedStatement.setLong(3, book.getId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows < 1) {
                throw new DataProcessingException(
                        "Failed to update the book with id: " + book.getId());
            }
        } catch (SQLException e) {
            throw new DataProcessingException("Can not create a connection to the DB", e);
        }
        return book;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id =?";
        Properties dbProperties = new Properties();
        dbProperties.put("user","root");
        dbProperties.put("password","15vlad0705");
        try (Connection connection = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/book_store", dbProperties);
                PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Can't delete book with id: " + id, e);
        }
    }
}
