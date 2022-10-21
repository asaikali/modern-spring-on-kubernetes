package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.testcontainers.DockerClientFactory.TESTCONTAINERS_LABEL;

import com.github.dockerjava.api.model.Container;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.DockerClientFactory;

@SpringBootTest
// @ActiveProfiles("test")
class QuoteRepositoryTest {

  @BeforeEach
  void debug() {
    // print a list of all the containers test containers are currently running
    var client = DockerClientFactory.instance().client();
    var containers =
        client.listContainersCmd().withLabelFilter(Map.of(TESTCONTAINERS_LABEL, "true")).exec();
    for (Container container : containers) {
      System.out.println(container.getImage());
    }
  }

  @Test
  @DisplayName("A random quote is returned")
  void testRandomQuotes(@Autowired QuoteRepository quoteRepository) {
    var quote = quoteRepository.findRandomQuote();
    assertThat(quote).isNotNull();
  }

  @Test
  @DisplayName("All quotes are returned")
  void testAllQuotes(@Autowired QuoteRepository quoteRepository) {
    var quotes = quoteRepository.findAll();
    assertThat(quotes).isNotNull();
  }

  @Test
  @DisplayName("Create a quote")
  void testCreateQuote(@Autowired QuoteRepository quoteRepository) {
    var quote = new Quote();
    quote.setId(6);
    quote.setAuthor("Confucius");
    quote.setQuote("Our greatest glory is not in never falling, but in rising every time we fall");

    var result = quoteRepository.save(quote);
    assertThat(result.getAuthor()).isEqualTo("Confucius");
  }

  @Test
  @DisplayName("Delete a quote - failed")
  void testDeleteQuote(@Autowired QuoteRepository quoteRepository) {
    var quote = new Quote();
    quote.setId(6);
    quote.setAuthor("Confucius");
    quote.setQuote("Our greatest glory is not in never falling, but in rising every time we fall");

    var result = quoteRepository.save(quote);
    assertThat(result.getAuthor()).isEqualTo("Confucius");

    assertThatThrownBy(
            () -> {
              quoteRepository.deleteById(100);
            })
        .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
  }

  @Test
  @DisplayName("Delete a quote - good")
  void testDeleteQuoteGood(@Autowired QuoteRepository quoteRepository) {
    var quote = new Quote();
    quote.setId(6);
    quote.setAuthor("Confucius");
    quote.setQuote("Our greatest glory is not in never falling, but in rising every time we fall");

    var result = quoteRepository.save(quote);
    assertThat(result.getAuthor()).isEqualTo("Confucius");

    assertDoesNotThrow(
        () -> {
          quoteRepository.deleteById(6);
        });
  }
}
