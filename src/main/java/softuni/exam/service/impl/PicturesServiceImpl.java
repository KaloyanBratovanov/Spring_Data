package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PicturesSeedDto;
import softuni.exam.models.entitites.Car;
import softuni.exam.models.entitites.Picture;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.PictureRepository;
import softuni.exam.service.PictureService;
import softuni.exam.util.ValidationUtil;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;


@Service
public class PicturesServiceImpl implements PictureService {


    public static final String PICTURE_PATH = "src/main/resources/files/json/pictures.json";

    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final PictureRepository pictureRepository;
    private final CarRepository carRepository;

    public PicturesServiceImpl(ModelMapper modelMapper, ValidationUtil validationUtil, Gson gson, PictureRepository pictureRepository, CarRepository carRepository) {
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.pictureRepository = pictureRepository;
        this.carRepository = carRepository;
    }



    @Override
    public boolean areImported() {
        return this.pictureRepository.count() > 0;
    }

    @Override
    public String readPicturesFromFile() throws IOException {
        return Files.readString( Path.of(PICTURE_PATH) );
    }

    @Override
    public String importPictures() throws IOException {


        StringBuilder sb = new StringBuilder();

        PicturesSeedDto[] picturesSeedDtos = this.gson.fromJson(new FileReader(PICTURE_PATH), PicturesSeedDto[].class);

        for (PicturesSeedDto picturesSeedDto : picturesSeedDtos) {

            Optional<Picture> byPicture = this.pictureRepository.findByName(picturesSeedDto.getName());

            if (this.validationUtil.isValid(picturesSeedDto)  && byPicture.isEmpty()){

                Picture picture = this.modelMapper.map(picturesSeedDto, Picture.class);

                Car car = this.carRepository.getOne(picturesSeedDto.getCar());

                picture.setCar(car);

                this.pictureRepository.saveAndFlush(picture);

                sb.append(String.format("Successfully import picture - %s",
                        picturesSeedDto.getName())).append(System.lineSeparator());
            }else {
                sb.append("Invalid pictures").append(System.lineSeparator());
            }
        }

        return sb.toString();
    }
}
