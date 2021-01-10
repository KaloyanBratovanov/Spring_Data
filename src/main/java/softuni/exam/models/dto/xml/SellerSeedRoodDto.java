package softuni.exam.models.dto.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "sellers")
@XmlAccessorType(XmlAccessType.FIELD)
public class SellerSeedRoodDto {


    @XmlElement(name = "seller")
    private List<SellersSeedDto>sellersSeedDtos;


    public SellerSeedRoodDto() {
    }

    public List<SellersSeedDto> getSellersSeedDtos() {
        return sellersSeedDtos;
    }

    public void setSellersSeedDtos(List<SellersSeedDto> sellersSeedDtos) {
        this.sellersSeedDtos = sellersSeedDtos;
    }
}
