package cat.teknos.shoeshop.services;

import cat.teknos.shoeshop.services.exeption.ResourceNotFoundExeption;
import cat.teknos.shoeshop.services.exeption.ServerErrorExeption;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

public class RequestRouter {

    private static RawHttp rawHttp = new RawHttp();

    public RawHttpResponse<?> execRequest(RawHttpRequest request) {

        var path = request.getUri().getPath();
        var method = request.getMethod();

        RawHttpResponse response; response = null;

        try {

            var json = ""  // clientContorller.get();
            response = rawHttp.parseResponse("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/json\r\n" +
                    "Content-Length: " + json.length() + "\r\n" +
                    "\r\n" +
                    json);

        } catch (ResourceNotFoundExeption exeption) {

            response = null;

        } catch (ServerErrorExeption exeption) {

            response = null;

        }

        return null;

    }

}
