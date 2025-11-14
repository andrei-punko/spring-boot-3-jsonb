package by.andd3dfx.templateapp.persistence.dao;

import by.andd3dfx.templateapp.persistence.entities.Article;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends CrudRepository<Article, Long> {

    @Query("""
            from Article a where
                case
                    when :country is not null and :city is not null then sameLocation(a.location, :country, :city)
                    when :country is not null and :city is null then sameCountry(a.location, :country)
                    when :city is not null then sameCity(a.location, :city)
                    else true
                end = true
            """)
    List<Article> getArticleByCountryNCity(@Param("country") String country, @Param("city") String city);

    Slice<Article> findAll(Pageable pageable);
}
