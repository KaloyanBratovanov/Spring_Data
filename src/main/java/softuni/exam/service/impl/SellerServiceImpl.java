package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.xml.SellerSeedRoodDto;
import softuni.exam.models.dto.xml.SellersSeedDto;
import softuni.exam.models.entitites.Picture;
import softuni.exam.models.entitites.Seller;
import softuni.exam.repository.SellerRepository;
import softuni.exam.service.SellerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.swing.plaf.TableHeaderUI;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class SellerServiceImpl implements SellerService {


    public static final String SELLER_PATH = "src/main/resources/files/xml/sellers.xml";


    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final SellerRepository sellerRepository;

    public SellerServiceImpl(ModelMapper modelMapper, XmlParser xmlParser, ValidationUtil validationUtil, SellerRepository sellerRepository) {
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.sellerRepository = sellerRepository;
    }


    @Override
    public boolean areImported() {
        return this.sellerRepository.count() > 0;
    }

    @Override
    public String readSellersFromFile() throws IOException {
        return Files.readString( Path.of(SELLER_PATH) );
    }

    @Override
    public String importSellers() throws IOException, JAXBException {

        StringBuilder sb = new StringBuilder();

        SellerSeedRoodDto sellerSeedRoodDto = this.xmlParser.parseXml(SellerSeedRoodDto.class, SELLER_PATH);


        for (SellersSeedDto sellersSeedDto : sellerSeedRoodDto.getSellersSeedDtos()) {

            System.out.println();
            Optional<Seller> byEmail = this.sellerRepository.findByEmail(sellersSeedDto.getEmail());



            if (this.validationUtil.isValid(sellersSeedDto) && byEmail.isEmpty() ){

                this.sellerRepository.saveAndFlush(this.modelMapper.map(sellersSeedDto, Seller.class));

                sb.append(String.format("Successfully import seller %s - %s",
                        sellersSeedDto.getLastName(),sellersSeedDto.getEmail()
                       )).append(System.lineSeparator());
            }else {
                sb.append("Invalid seller").append(System.lineSeparator());
            }

        }


        return sb.toString();
    }
}
