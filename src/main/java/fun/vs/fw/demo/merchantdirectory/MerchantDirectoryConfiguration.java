package fun.vs.fw.demo.merchantdirectory;


import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class MerchantDirectoryConfiguration {

    @Bean
    public MerchantDirectoryAdapter merchantDirectoryAdapter(MerchantDirectoryService merchantDirectoryService) {
        return new MerchantDirectoryAdapter(merchantDirectoryService);
    }
}
