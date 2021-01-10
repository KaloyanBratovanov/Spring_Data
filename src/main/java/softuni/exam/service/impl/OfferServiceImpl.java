package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.xml.OfferSeedDto;
import softuni.exam.models.dto.xml.OfferSeedRoodDto;
import softuni.exam.models.entitites.Car;
import softuni.exam.models.entitites.Offer;
import softuni.exam.models.entitites.Picture;
import softuni.exam.models.entitites.Seller;
import softuni.exam.repository.CarRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.repository.PictureRepository;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.OfferService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OfferServiceImpl implements OfferService {


    public static final String OFFER_PATH = "src/main/resources/files/xml/offers.xml";

    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final SellerRepository sellerRepository;
    private final PictureRepository pictureRepository;
    private final OfferRepository offerRepository;
    private final CarRepository carRepository;

    public OfferServiceImpl(ModelMapper modelMapper, XmlParser xmlParser, ValidationUtil validationUtil, SellerRepository sellerRepository, PictureRepository pictureRepository, OfferRepository offerRepository, CarRepository carRepository) {
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.sellerRepository = sellerRepository;
        this.pictureRepository = pictureRepository;
        this.offerRepository = offerRepository;
        this.carRepository = carRepository;
    }


    @Override
    public boolean areImported() {
        return this.offerRepository.count() >0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString( Path.of(OFFER_PATH) );
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();


        OfferSeedRoodDto offerSeedRoodDto = this.xmlParser.parseXml(OfferSeedRoodDto.class, OFFER_PATH);

        for (OfferSeedDto offerSeedDto : offerSeedRoodDto.getOfferSeedDtos()) {



            if (this.validationUtil.isValid(offerSeedDto)){

                Car car = this.carRepository.getOne(offerSeedDto.getCar().getId());
                Seller seller = this.sellerRepository.getOne(offerSeedDto.getSeller().getId());

                Offer offer = this.modelMapper.map(offerSeedDto, Offer.class);

                offer.setCar(car);
                offer.setSeller(seller);

                this.offerRepository.saveAndFlush(offer);

                sb.append(String.format("Successfully import offer %s - %s"
                ,offerSeedDto.getAddedOn(), offerSeedDto.isHasGoldStatus()))
                        .append(System.lineSeparator());
            }else {
                sb.append("Invalid seller").append(System.lineSeparator());
            }

        }

        return sb.toString();

    }
}
