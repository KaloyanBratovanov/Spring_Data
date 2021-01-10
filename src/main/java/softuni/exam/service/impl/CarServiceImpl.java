package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CarSeedDto;
import softuni.exam.models.entitites.Car;
import softuni.exam.repository.CarRepository;

import softuni.exam.service.CarService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
public class CarServiceImpl implements CarService {

    public static final String CAR_PAT = "src/main/resources/files/json/cars.json";

    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final CarRepository carRepository;
    private final Gson gson;

    @Autowired
    public CarServiceImpl(ModelMapper modelMapper, XmlParser xmlParser, ValidationUtil validationUtil, CarRepository carRepository, Gson gson) {
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.carRepository = carRepository;
        this.gson = gson;
    }


    @Override
    public boolean areImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsFileContent() throws IOException {
        return Files.readString( Path.of(CAR_PAT) );
    }

    @Override
    public String importCars() throws IOException {


        StringBuilder sb = new StringBuilder();

        CarSeedDto[] carSeedDtos = this.gson.fromJson(new FileReader(CAR_PAT), CarSeedDto[].class);


        for (CarSeedDto carSeedDto : carSeedDtos) {

            System.out.println();
            if (this.validationUtil.isValid(carSeedDto)){

                this.carRepository.saveAndFlush(
                        this.modelMapper.map(carSeedDto, Car.class)
                );

                sb.append(String.format("Successfully imported car - %s - %s",
                        carSeedDto.getMake(), carSeedDto.getModel())).append(System.lineSeparator());
            }else {
                sb.append("Invalid car").append(System.lineSeparator());
            }

        }

        return sb.toString();
    }

    @Override
    public String getCarsOrderByPicturesCountThenByMake() {

        StringBuilder sb = new StringBuilder();

        Set<Car> cars = this.carRepository.exportCars();

//        Car make - {make}, model - {model}
//	        Kilometers - {kilometers}
//	        Registered on - {registered on}
//	        Number of pictures - {number of pictures}

        for (Car car : cars) {
            sb.append(String.format("Car make - %s, model - %s\n" +
                            "\tKilometers - %d\n" +
                            "\tRegistered on - %s\n" +
                            "\tNumber of pictures - %d\n"
                    ,car.getMake(), car.getModel(), car.getKilometers()
            ,car.getRegisteredOn(), car.getPictures().size())).append(System.lineSeparator());
        }

        return sb.toString();
    }
}
