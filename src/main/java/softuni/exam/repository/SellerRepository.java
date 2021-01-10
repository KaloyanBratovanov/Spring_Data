package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entitites.Car;
import softuni.exam.models.entitites.Picture;
import softuni.exam.models.entitites.Seller;

import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller,Integer > {

    Optional<Seller> findByEmail(String email);

}
