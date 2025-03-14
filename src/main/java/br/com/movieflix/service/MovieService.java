package br.com.movieflix.service;

import br.com.movieflix.controller.request.MovieRequest;
import br.com.movieflix.entity.Category;
import br.com.movieflix.entity.Movie;
import br.com.movieflix.entity.Streaming;
import br.com.movieflix.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository repository;
    private final CategoryService categoryService;
    private final StreamingService streamingService;

    public Movie save(Movie movie) {
        movie.setCategories(this.findCategories(movie.getCategories()));
        movie.setStreamings(this.findStreamings(movie.getStreamings()));
        return repository.save(movie);
    }

    public List<Movie> findAll() {
        return repository.findAll();
    }

    public Optional<Movie> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Movie> update(Long id, Movie updateMovie){
        Optional<Movie> optionalMovie = repository.findById(id);
        if (optionalMovie.isPresent()){

            List<Category> categories = findCategories(updateMovie.getCategories());
            List<Streaming> streamings = findStreamings(updateMovie.getStreamings());

            Movie movie = optionalMovie.get();
            movie.setTitle(updateMovie.getTitle());
            movie.setDescription(updateMovie.getDescription());
            movie.setReleaseDate(updateMovie.getReleaseDate());
            movie.setRating(updateMovie.getRating());

            movie.getCategories().clear();
            movie.getCategories().addAll(categories);

            movie.getStreamings().clear();
            movie.getStreamings().addAll(streamings);
            repository.save(movie);

            return Optional.of(movie);
        }
        return Optional.empty();
    }

    public List<Movie> findByCategory(Long categoryId){
        return repository.findByCategories(List.of(Category.builder().id(categoryId).build()));
    }

    public void delete(Long id){
        repository.deleteById(id);
    }

    private List<Category> findCategories(List<Category> categories) {
        List<Category> categoriesFound = new ArrayList<>();
        categories.forEach(category -> {
            categoryService.findById(category.getId()).ifPresent(categoriesFound::add);
        });
        return categoriesFound;
    }

    private List<Streaming> findStreamings(List<Streaming> streamings) {
        List<Streaming> streamingsFound = new ArrayList<>();
        streamings.forEach(streaming -> {
            streamingService.findById(streaming.getId()).ifPresent(streamingsFound::add);
        });
        return streamingsFound;
    }
}
