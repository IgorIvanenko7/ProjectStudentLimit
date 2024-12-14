package cases.tests;

import cases.service.AesEncryptionUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
//@ExtendWith(SpringExtension.class)
@SpringBootTest
@Setter
@Getter
public class Root {

    @Autowired
    public AesEncryptionUtil aesEncryptionUtil;
}
