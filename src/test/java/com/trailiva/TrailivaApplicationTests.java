package com.trailiva;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TrailivaApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Test
	void canConnectToDb() throws SQLException {
		assertThat(dataSource).isNotNull();
		Connection connection = dataSource.getConnection();
		String dbName = connection.getCatalog();
		assertThat(dbName).isEqualTo("trailiva");
	}

}
