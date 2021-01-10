package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entitites.Car;
import softuni.exam.models.entitites.Picture;

import java.util.Optional;

@Repository
public interface PictureRepository extends JpaRepository<Picture,Integer > {


    Optional<Picture> findByName(String name);
}
