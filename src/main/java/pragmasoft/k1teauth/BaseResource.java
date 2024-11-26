package pragmasoft.k1teauth;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
public class BaseResource {

    @GET
    public Response redirect() {
        return Response.seeOther(URI.create("/auth/login")).build();
    }
}
