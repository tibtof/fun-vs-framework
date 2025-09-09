package fun.vs.fw.demo.merchantdirectory;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "merchant-directory", url = "${merchant.directory.url}")
public interface MerchantDirectoryService {

    @GetMapping("/merchant-directory/{merchantCategoryCode}")
    MerchantInfo getCategoryForMerchant(@PathVariable("merchantCategoryCode") String merchantCategoryCode);
}
