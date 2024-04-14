package eu.phaf.news.domain;

public record NewsArticle(String title,
                          String author, String description,
                          String source, String url,
                          byte[] imageInBytes) {
}
