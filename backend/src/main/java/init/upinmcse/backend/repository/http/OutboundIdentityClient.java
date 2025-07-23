package init.upinmcse.backend.repository.http;

import feign.QueryMap;
import init.upinmcse.backend.dto.request.ExchangeTokenRequest;
import init.upinmcse.backend.dto.response.ExchangeTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "outbound-identity", url = "https://oauth2.googleapis.com")
public interface OutboundIdentityClient {
    @PostMapping(value = "/token", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest request);
    // để truyền các tham số trong request body dưới dạng application/x-www-form-urlencoded
    // sử dụng @QueryMap để ánh xạ các tham số từ ExchangeTokenRequest thành query parameters
}
