package fvf4k.demo.infra.md

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable


@FeignClient(name = "merchant-directory", url = "\${merchant.directory.url}")
interface MerchantDirectoryService {

    @GetMapping("/merchant-directory/{merchantCategoryCode}")
    fun getMerchantCategoryCode(@PathVariable("merchantCategoryCode") merchantCategoryCode: String): MerchantInfo
}

data class MerchantInfo(val mcc: String, val category: String)