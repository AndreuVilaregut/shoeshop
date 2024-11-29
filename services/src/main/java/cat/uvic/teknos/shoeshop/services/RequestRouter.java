package cat.uvic.teknos.shoeshop.services;

import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

public interface RequestRouter {
    RawHttpResponse<?> route(RawHttpRequest request);

}