package by.andd3dfx.templateapp.persistence.dao;

import by.andd3dfx.templateapp.IntegrationTestInitializer;
import by.andd3dfx.templateapp.persistence.entities.Article;
import by.andd3dfx.templateapp.persistence.entities.Location;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

@ContextConfiguration(initializers = IntegrationTestInitializer.class)
@SpringBootTest
public class ArticleRepositoryTest {

    @Autowired
    private ArticleRepository repository;

    private Article entity;
    private Article entity2;
    private Article entity3;

    @BeforeEach
    public void setup() {
        entity = buildArticle("Ivan", "HD", LocalDateTime.parse("2010-12-03T10:15:30"));
        entity2 = buildArticle("Vasily", "HD", LocalDateTime.parse("2011-12-03T10:15:30"));
        entity3 = buildArticle("Ivan", "4K", LocalDateTime.parse("2012-12-03T10:15:30"));
        repository.saveAll(List.of(entity, entity2, entity3));
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll(List.of(entity, entity2, entity3));
    }

    @Test
    public void findAll() {
        var result = repository.findAll(Pageable.ofSize(2));

        assertThat("Wrong records amount", result.getNumberOfElements(), is(2));
    }

    @Test
    public void findAll_withPageNSizeNSorting() {
        var result = repository.findAll(PageRequest.of(0, 2, Sort.by("title", "summary")));

        assertThat("Wrong records amount", result.getNumberOfElements(), is(2));
        var articles = result.getContent();

        assertThat(articles.get(0).getTitle(), is(entity3.getTitle()));
        assertThat(articles.get(0).getSummary(), is(entity3.getSummary()));

        assertThat(articles.get(1).getTitle(), is(entity.getTitle()));
        assertThat(articles.get(1).getSummary(), is(entity.getSummary()));
    }

    @Test
    void getArticleByLocationByCountryNCity() {
        Article testEntity1 = buildArticle("Ivan", "HD", "FR", "Brest");
        Article testEntity2 = buildArticle("Vasily", "HD", "BY", "Brest");
        Article testEntity3 = buildArticle("Ivan", "4K", "BY", "Minsk");
        repository.saveAll(List.of(testEntity1, testEntity2, testEntity3));

        List<Article> result = repository.getArticleByCountryNCity("FR", "Brest");

        assertThat(result.size(), is(1));
        Article article = result.get(0);
        assertThat("Wrong title", article.getTitle(), is("Ivan"));
        assertThat("Wrong summary", article.getSummary(), is("HD"));
        assertThat("Wrong location.country", article.getLocation().getCountry(), is("FR"));
        assertThat("Wrong location.city", article.getLocation().getCity(), is("Brest"));

        repository.deleteAll(List.of(testEntity1, testEntity2, testEntity3));
    }

    @Test
    void getArticleByLocationByCountry() {
        Article testEntity1 = buildArticle("Ivan", "HD", "FR", "Brest");
        Article testEntity2 = buildArticle("Vasily", "HD", "BY", "Brest");
        Article testEntity3 = buildArticle("Ivan", "4K", "BY", "Minsk");
        repository.saveAll(List.of(testEntity1, testEntity2, testEntity3));

        List<Article> result = repository.getArticleByCountryNCity("BY", null);

        assertThat(result.size(), is(2));

        Article article = result.get(0);
        assertThat("Wrong [0].title", article.getTitle(), is("Vasily"));
        assertThat("Wrong [0].summary", article.getSummary(), is("HD"));
        assertThat("Wrong [0].location.country", article.getLocation().getCountry(), is("BY"));
        assertThat("Wrong [0].location.city", article.getLocation().getCity(), is("Brest"));

        Article article2 = result.get(1);
        assertThat("Wrong [1].title", article2.getTitle(), is("Ivan"));
        assertThat("Wrong [1].summary", article2.getSummary(), is("4K"));
        assertThat("Wrong [1].location.country", article2.getLocation().getCountry(), is("BY"));
        assertThat("Wrong [1].location.city", article2.getLocation().getCity(), is("Minsk"));

        repository.deleteAll(List.of(testEntity1, testEntity2, testEntity3));
    }

    @Test
    void getArticleByLocationByCity() {
        Article testEntity1 = buildArticle("Ivan", "HD", "FR", "Brest");
        Article testEntity2 = buildArticle("Vasily", "HD", "BY", "Brest");
        Article testEntity3 = buildArticle("Ivan", "4K", "BY", "Minsk");
        repository.saveAll(List.of(testEntity1, testEntity2, testEntity3));

        List<Article> result = repository.getArticleByCountryNCity(null, "Brest");

        assertThat(result.size(), is(2));

        Article article = result.get(0);
        assertThat("Wrong [0].title", article.getTitle(), is("Ivan"));
        assertThat("Wrong [0].summary", article.getSummary(), is("HD"));
        assertThat("Wrong [0].location.country", article.getLocation().getCountry(), is("FR"));
        assertThat("Wrong [0].location.city", article.getLocation().getCity(), is("Brest"));

        Article article2 = result.get(1);
        assertThat("Wrong [1].title", article2.getTitle(), is("Vasily"));
        assertThat("Wrong [1].summary", article2.getSummary(), is("HD"));
        assertThat("Wrong [1].location.country", article2.getLocation().getCountry(), is("BY"));
        assertThat("Wrong [1].location.city", article2.getLocation().getCity(), is("Brest"));

        repository.deleteAll(List.of(testEntity1, testEntity2, testEntity3));
    }

    @Test
    void getArticleByLocationWhenParamsAreNull() {
        Article testEntity1 = buildArticle("Ivan", "HD", "FR", "Brest");
        Article testEntity2 = buildArticle("Vasily", "HD", "BY", "Brest");
        Article testEntity3 = buildArticle("Ivan", "4K", "BY", "Minsk");
        repository.saveAll(List.of(testEntity1, testEntity2, testEntity3));

        var result = repository.getArticleByCountryNCity(null, null);

        assertThat(result.size(), greaterThan(2));

        repository.deleteAll(List.of(testEntity1, testEntity2, testEntity3));
    }

    private static Article buildArticle(String title, String summary, LocalDateTime timestamp) {
        var result = new Article();
        result.setTitle(title);
        result.setSummary(summary);
        result.setText("any text");
        result.setTimestamp(timestamp);
        result.setAuthor("Pushkin");
        return result;
    }

    public static Article buildArticle(String title, String summary, String country, String city) {
        Article article = buildArticle(title, summary, LocalDateTime.now());
        article.setLocation(new Location(country, city));
        return article;
    }
}
